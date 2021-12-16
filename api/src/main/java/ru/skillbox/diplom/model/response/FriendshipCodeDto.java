package ru.skillbox.diplom.model.response;

// there is only REQUEST status in DB, this DTO wraps it to _FROM and _TO
public enum FriendshipCodeDto {
    REQUEST_FROM,
    REQUEST_TO,
    FRIEND,
    BLOCKED,
    DECLINED,
    SUBSCRIBED
}