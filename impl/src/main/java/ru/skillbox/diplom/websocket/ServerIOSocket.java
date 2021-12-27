package ru.skillbox.diplom.websocket;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.skillbox.diplom.controller.api.AccountControllerImpl;
import ru.skillbox.diplom.mappers.MessageMapper;
import ru.skillbox.diplom.model.Message;
import ru.skillbox.diplom.model.MessageDto;
import ru.skillbox.diplom.model.User;
import ru.skillbox.diplom.model.enums.ReadStatus;
import ru.skillbox.diplom.repository.MessageRepository;
import ru.skillbox.diplom.util.TimeUtil;
import ru.skillbox.diplom.util.specification.SpecificationUtil;
import ru.skillbox.diplom.websocket.structs.MessageBody;
import ru.skillbox.diplom.websocket.structs.MessageResponse;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Main runner for Socket IO Engine AS independent part of APP
// + Socket IO EVENTS (messaging)

@Component
public class ServerIOSocket implements CommandLineRunner {

    private final static Logger LOGGER = LogManager.getLogger(AccountControllerImpl.class);
    private final SocketIOServer server;
    private final SocketIOService socketIOService;
    private final MessageRepository messageRepository;

    private final MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);

    @Autowired
    public ServerIOSocket(SocketIOServer server, SocketIOService socketIOService, MessageRepository messageRepository) {
        this.server = server;
        this.socketIOService = socketIOService;
        this.messageRepository = messageRepository;
    }

    private boolean saveToDb(){
        return true;
    }


    @Override
    public void run(String... args) throws Exception {

        server.addConnectListener(new ConnectListener() {// add listener

            //Connection function
            @Override
            public void onConnect(SocketIOClient client) {
                LOGGER.info("onConnect");

                //get JWT-token
                boolean isValidClient = false;
                HandshakeData hsd = client.getHandshakeData();
                String jwt_ = hsd.getUrlParams().get("jwt").toString();
                String jwt = jwt_.substring(1, jwt_.length() - 1);


                Optional<User> user = socketIOService.gerCachedUserByToken(jwt);
                if (user.isPresent()) {
                    //save client into bi-dir map
                    try {
                        socketIOService.getClientStorage().saveClient(user.get().getId(), client);
                        //LOGGER.info("id:  "+user.get().getId()+"  "+socketIOService.clientStorage.getUserId(client));
                    }
                    catch(Exception e){
                        LOGGER.info("Only one client for one browser on you computer");
                    }
                } else {
                    client.disconnect();
                    LOGGER.info("user isn`t identified. Disconnection.");
                }
            }
        });

        //Disconnection function
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                socketIOService.getClientStorage().deleteClient(client);
                LOGGER.info("Client has disconnected -> {}", client.getSessionId());
            }
        });

        //basic message event
        server.addEventListener("chat_server", MessageBody.class, new DataListener<MessageBody>() {

            //get message-event from client
                    @Override
                    public void onData(final SocketIOClient client, MessageBody data, AckRequest ackRequest) {
                        LOGGER.info("chat: -> {}", data.toString());

                        Long iam =socketIOService.getClientStorage().getUserId(client);
                        Long forWhom = data.getRecipientId();
                        if (iam.equals(forWhom)){//
                            if (ackRequest.isAckRequested()) {
                                MessageResponse messageResponse = new MessageResponse();
                                messageResponse.setMessage("Incorrect message");
                                ackRequest.sendAckData(messageResponse);
                            }
                            return;
                        }

                        //create data class for this message
                        Message message = new Message();
                        //LOGGER.info("set in message:: -> {}", socketIOService.clientStorage.getUserId(client));
                        message.setAuthorId(iam);
                        message.setMessageText(data.getMessage());
                        message.setRecipientId(data.getRecipientId());
                        message.setStatus(ReadStatus.SENT);
                        message.setTime(ZonedDateTime.now());
                        message.setIsDeleted(false);

                        if (message.getMessageText().isEmpty()){
                            message.setMessageText("empty message");
                        }

                        List<SocketIOClient> recipients = socketIOService.getClientStorage().getClients(data.getRecipientId());
                        Message fromDb = messageRepository.save(message);
                        if (recipients == null || recipients.isEmpty()){
                            LOGGER.info("{}  NO Active recipients ", message);
                        }else{
                            MessageDto messageDTO = messageMapper.toMessageDTO(fromDb);
                            for (SocketIOClient recipient: recipients) {
                                recipient.sendEvent("chat_client", new AckCallback<String>(String.class) {
                                    @Override
                                    public void onSuccess(String result) {
                                        //LOGGER.info("Message is delivered");
                                    }
                                }, messageDTO);
                            }
                        }
                        MessageResponse messageResponse = new MessageResponse();
                        messageResponse.setMessage(message.getMessageText());
                        messageResponse.setMessageId(message.getId());
                        messageResponse.setTime(TimeUtil.zonedDateTimeToLong(message.getTime()));//Time
                        messageResponse.setRecipientId(message.getRecipientId());
                        if (ackRequest.isAckRequested()) {
                            //ackRequest.sendAckData(messageResponse);
                        }
                        ////
                        List<SocketIOClient> authors = socketIOService.getClientStorage().getClients(iam);
                        if (authors == null || authors.isEmpty()){
                            LOGGER.info("{} NO Active authors ", message);
                        }else{
                            for (SocketIOClient author: authors) {
                                author.sendEvent("author_response_client", new AckCallback<String>(String.class) {
                                    @Override
                                    public void onSuccess(String result) {
                                       // LOGGER.info("Message for author is delivered");
                                    }
                                }, messageResponse);
                            }
                        }
                    }
                });

        //confirmation delivery of messages
        server.addEventListener("delivery_confirmation_server", String.class, new DataListener<String>() {

            //get message-event from client
            @Override
            public void onData(final SocketIOClient client, String data, AckRequest ackRequest) {
                LOGGER.info("  delivery_confirmation_server: {} ", data.toString());

                List<Long> ids = new ArrayList<>();
                for (String ids_ : data.split(",")) {
                    try {
                        Long id = Long.parseLong(ids_);
                        ids.add(id);
                    }catch (Exception e){
                        LOGGER.info("error parsing to Long:  ",ids_);
                    }
                }
                if (ids.isEmpty()) return;

                SpecificationUtil<Message> mesSpec = new SpecificationUtil<>();
                Specification<Message> s1 = mesSpec.in("id", ids);
                Specification<Message> s1one = mesSpec.equals("id", ids.get(0));
                Specification<Message> noDel = mesSpec.equals("isDeleted", false );
                List<Message>    messages;
                if (ids.size() > 1){
                                    messages = messageRepository.findAll(
                                    Specification.
                                            where(s1).and(noDel));
                }else{
                    messages = messageRepository.findAll(
                            Specification.
                                    where(s1one).and(noDel));
                }

                Long id = socketIOService.getClientStorage().getUserId(client);
                for (Message mes: messages){
                    if (mes.getStatus() == ReadStatus.SENT && mes.getRecipientId() == id){
                        mes.setStatus(ReadStatus.READ);
                        messageRepository.save(mes);
                    }
                }

                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData("Done");
                }
            }
        });

        //confirmation delivery of messages
        server.addEventListener("delete_server", String.class, new DataListener<String>() {

            //get message-event from client
            @Override
            public void onData(final SocketIOClient client, String data, AckRequest ackRequest) {
                LOGGER.info("delete_server:  {}  ", data.toString());

                List<Long> ids = new ArrayList<>();
                for (String ids_ : data.split(",")) {
                    try {
                        Long id = Long.parseLong(ids_);
                        ids.add(id);
                    }catch (Exception e){
                        LOGGER.info("error parsing to Long: {} ", ids_);
                    }
                }
                if (ids.isEmpty()) {
                    LOGGER.info("is Empty");
                    if (ackRequest.isAckRequested()) {
                        ackRequest.sendAckData("Empty");
                    }
                    return;
                }

                SpecificationUtil<Message> mesSpec = new SpecificationUtil<>();
                Specification<Message> s1 = mesSpec.in("id", ids);
                Specification<Message> s1one = mesSpec.equals("id", ids.get(0));
                Specification<Message> noDel = mesSpec.equals("isDeleted", false );

                List<Message>    messages;
                if (ids.size() > 1){
                    messages = messageRepository.findAll(
                            Specification.
                                    where(s1));
                }else{
                    messages = messageRepository.findAll(
                            Specification.
                                    where(s1one));
                }

                Long id = socketIOService.getClientStorage().getUserId(client);
                for (Message mes: messages){
                    if (mes.getIsDeleted() == false && mes.getAuthorId() == id){
                        mes.setIsDeleted(true);
                        messageRepository.save(mes);
                    }
                }

                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData("Done");
                }
            }
        });


        server.start();
        Thread.sleep(Integer.MAX_VALUE);
        server.stop();

    }
}

