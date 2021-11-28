package ru.skillbox.diplom.websocket;

import com.corundumstudio.socketio.SocketIOClient;
import org.apache.logging.log4j.LogManager;
import ru.skillbox.diplom.controller.api.UsersControllerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


//STORAGE for "pairs" USER ID AND SocketIOClient that are currently active
//it solves problem "several simultaneous connections for the same user" with the same userId.
public class ClientSocketIOTempStorage {

    private HashMap<Long, List<SocketIOClient>> map = new HashMap<>();

    private final static Logger LOGGER = LogManager.getLogger(ClientSocketIOTempStorage.class);

    public void saveClient(Long clientId, SocketIOClient socketIOClient){
        if (map.containsKey(clientId)){
            List<SocketIOClient> list = map.get(clientId);
            list.add(socketIOClient);
        }else{
            List<SocketIOClient> list = new ArrayList<>();
            list.add(socketIOClient);
            map.put(clientId,list);
        }
    }

    public void deleteClient(Long clientId){
        if (map.containsKey(clientId)){
            map.remove(clientId);
        }
    }

    public void deleteClient(SocketIOClient socketIOClient){

        for (Long k: map.keySet()){
            List<SocketIOClient> list = map.get(k);
            for (SocketIOClient sc: list){
                if (
//                //first small often changed part
//                sc.getHandshakeData().getAddress().toString().equals(socketIOClient.getHandshakeData().getAddress().toString())
//                //last part to compare
//                &&  sc.getHandshakeData().getHttpHeaders().toString().equals(socketIOClient.getHandshakeData().getHttpHeaders().toString())

                        sc==socketIOClient //TODO: maybe changed
                ){
                    if (list.size() == 1){
                        map.remove(k);
                        return;
                    }else{
                        list.remove(sc);
                        return;
                    }
                }
            }
        }
        LOGGER.info("it`s strange: no socketIoClient here");
    }

    public List<SocketIOClient> getClients(Long clientId){
        return map.get(clientId);
    }


    public Long  getUserId(SocketIOClient client){
        for (Long k: map.keySet()){
            List<SocketIOClient> list = map.get(k);
            for (SocketIOClient sc: list){
                if (
//                //first small often changed part
//                sc.getHandshakeData().getAddress().toString().equals(socketIOClient.getHandshakeData().getAddress().toString())
//                //last part to compare
//                &&  sc.getHandshakeData().getHttpHeaders().toString().equals(socketIOClient.getHandshakeData().getHttpHeaders().toString())
                        sc==client //TODO: maybe changed
                ){
                    return k;
                }
            }
        }
        LOGGER.info("it is strange.... error getUserId");
        return null;
    }


    public void clear(){
        map.clear();
    }

//    public void getAll(){
//
//        System.out.println("-------------------------------start");
//        for (Long k: map.keySet()){
//            List<SocketIOClient> list = map.get(k);
//            for (SocketIOClient sc: list){
//                System.out.println("id= "+k+" sc: "+sc.toString());
//            }
//        }
//        System.out.println("-------------------------------finish");
//    }
}
