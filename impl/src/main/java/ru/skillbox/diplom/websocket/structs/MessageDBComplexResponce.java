package ru.skillbox.diplom.websocket.structs;

// interface for getting complex response from DB
// it represents Message table fields + count(UNREAD) for every author
// I use this way because of native query
public interface MessageDBComplexResponce {
    long getXID();
    java.sql.Timestamp  getXTIME();
    long getXAID();
    long getXRID();
    String getXMT();
    String getXRS();
    Long getXURI();
    Long getXURO();
    String getXFN();
    String getXPH();
    java.sql.Timestamp getXLONI();
    java.sql.Timestamp getXLONO();
}
