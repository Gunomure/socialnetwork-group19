package ru.skillbox.diplom.websocket;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.config.security.jwt.JwtTokenProvider;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.mappers.MessageMapper;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.response.dialogs.*;
import ru.skillbox.diplom.repository.MessageRepository;
import ru.skillbox.diplom.repository.UserRepository;
import ru.skillbox.diplom.util.TimeUtil;
import ru.skillbox.diplom.util.specification.SpecificationUtil;
import ru.skillbox.diplom.websocket.structs.MessageDBComplexResponce;
import ru.skillbox.diplom.websocket.structs.MyPair;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


//HTTP REQUESTS for Messaging
//CacheLoader: User by jwt

@Service
public class SocketIOService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    public final ClientSocketIOTempStorage clientStorage = new ClientSocketIOTempStorage();
    private final MessageMapper messageMapper = Mappers.getMapper(MessageMapper.class);

    public SocketIOService(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, MessageRepository messageRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }



    private final CacheLoader<String, Optional<User>> loader = new CacheLoader<String, Optional<User>>() {
        @Override
        public final Optional<User> load(final String jwt) {
            Optional<User> user = gerUserByToken(jwt);
            return user;
        }
    };
    private final LoadingCache<String, Optional<User>> cache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build(loader);

    public Optional<User> gerUserByToken(String jwt){
        String email;
        try {
            if (!jwtTokenProvider.validateToken(jwt)) return Optional.empty();
            email = jwtTokenProvider.getUsername(jwt);
        } catch (Exception e) {
            return Optional.empty();
        }
        Optional<User>  user = Optional.of(userRepository.findByEmail(email).orElse(null));
        return user;
    }

    public Optional<User> gerCachedUserByToken(String jwt){
        return cache.getUnchecked(jwt);
    }

    public DialogMessagesResponse getDialogMessages(Long interlocutorId, String query,
                                                    int offset, int itemPerPage){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email))
        );

        long authorId = user.getId();
        Pageable pageable = PageRequest.of(offset, itemPerPage, Sort.by("time").descending());
        SpecificationUtil<Message> mesSpec = new SpecificationUtil<>();
        Specification<Message> s1 = mesSpec.equals("authorId", authorId);
        Specification<Message> s1inv = mesSpec.equals("authorId", interlocutorId);
        Specification<Message> s2 = mesSpec.equals("recipientId", interlocutorId);
        Specification<Message> s2inv = mesSpec.equals("recipientId", authorId );
        Specification<Message> noDel = mesSpec.equals("isDeleted", false );
        List<Message> messages ;
        if (query == null){
            messages = messageRepository.findAll(
                            Specification.
                                    where( (s1.and(s2)).or(s1inv.and(s2inv)) ).and(noDel),
                            pageable)
                    .getContent();
        }else{
            Specification<Message> s3 = mesSpec.contains("messageText", query);
            messages = messageRepository.findAll(
                            Specification.
                                    where( (s1.and(s2)).or(s1inv.and(s2inv)) ).
                                    and(s3).and(noDel),
                            pageable)
                    .getContent();
        }

        DialogMessagesResponse dialogMessagesResponse = new DialogMessagesResponse();
        dialogMessagesResponse.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        dialogMessagesResponse.setError("getDialogMessages request is successful");
        dialogMessagesResponse.setOffset(offset);
        dialogMessagesResponse.setPerPage(itemPerPage);
        List<MessageDto> mesDtos = new ArrayList<>();
        for (Message temp:  messages) {
            MessageDto messageDto = messageMapper.toMessageDTO(temp);
            mesDtos.add(messageDto);
        }
        dialogMessagesResponse.setData(mesDtos);
        dialogMessagesResponse.setTotal(mesDtos.size());
        return dialogMessagesResponse ;
    }

    public OneLastMessagesResponseDto getOneLastMessageFromEverybodyFull(
            int offset,
            int itemPerPage
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email))
        );

        long currentUserId = user.getId();

        int offsetReal = offset;
        int itemPerPageReal = itemPerPage * 2;

        Pageable pageable = PageRequest.of(offsetReal, itemPerPageReal, Sort.by("time").descending());
        List<MessageDBComplexResponce> lastMessages =
                messageRepository.getOneLastMessageFromEverybodyNativeFull(currentUserId, pageable);


        HashSet<MyPair> dublicates = new HashSet<>();//author, recipient

        //parsing response from DB

        int counterFact = 0;
        //int offsetFact = 0;


        List<OneLastMessageResponseDtoBody> allLastMessagesDtoBodiesFull = new ArrayList<>();
        for (MessageDBComplexResponce temp:  lastMessages)  {

        Long authorIdResponce = temp.getXAID();
        Long recipientIdResponce = temp.getXRID();
        if (counterFact >= itemPerPage) break;
            counterFact++;
        if (dublicates.contains(new MyPair(recipientIdResponce, authorIdResponce))){
                continue;
            }
        dublicates.add(new MyPair(authorIdResponce, recipientIdResponce));

            OneLastMessageResponseDtoBody oneLastMessageResponseDtoBody = new OneLastMessageResponseDtoBody();
            LastMessageDto lastMessage = new LastMessageDto();

            boolean isItInMessage = false;
            if (temp.getXLONI() != null){
                isItInMessage = true;
            }


            PersonInfoShortDto author = new PersonInfoShortDto(
                    temp.getXAID(), temp.getXPH(), temp.getXFN(),
                    isItInMessage ? temp.getXLONI(): temp.getXLONO()
            );
            lastMessage.setAuthor(author);
            lastMessage.setId(temp.getXID());
            ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(temp.getXTIME().getTime()),
                    TimeUtil.ZONE_UTC);
            lastMessage.setTime(
                    ZonedDateTime.ofInstant(zdt.toInstant(), TimeUtil.ZONE_UTC).toEpochSecond());
            lastMessage.setRecipientId(temp.getXRID());
            lastMessage.setMessageText(temp.getXMT());
            lastMessage.setStatus(temp.getXRS());//status
            oneLastMessageResponseDtoBody.setLastMessage(lastMessage);
            oneLastMessageResponseDtoBody.setId(Long.valueOf(0));//Todo: decide what to do, may be remove.
            oneLastMessageResponseDtoBody.setUnreadCount(
                    isItInMessage ? (temp.getXURI()==null ? 0: temp.getXURI()):( temp.getXURO()==null ? 0: temp.getXURO()));///////////
            allLastMessagesDtoBodiesFull.add(oneLastMessageResponseDtoBody);

        }

        OneLastMessagesResponseDto oneLastMessagesResponseDto = new OneLastMessagesResponseDto();
        oneLastMessagesResponseDto.setData(allLastMessagesDtoBodiesFull);
        oneLastMessagesResponseDto.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        oneLastMessagesResponseDto.setError("getOneLastMessageFromEverybody request is successful");
        oneLastMessagesResponseDto.setOffset(offset);
        oneLastMessagesResponseDto.setPerPage(counterFact);//itemPerPage
        oneLastMessagesResponseDto.setTotal(allLastMessagesDtoBodiesFull.size());
        oneLastMessagesResponseDto.setCurrentUserId(currentUserId);


        return oneLastMessagesResponseDto;
    }
}



