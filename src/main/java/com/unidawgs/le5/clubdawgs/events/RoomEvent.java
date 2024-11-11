package com.unidawgs.le5.clubdawgs.events;

import javafx.event.Event;
import javafx.event.EventType;

public class RoomEvent extends Event {
    private String roomId;

    public RoomEvent(EventType<? extends Event> eventType, String roomId) {
        super(eventType);
        this.roomId = roomId;
    }

    public String getRoomId() {
        return this.roomId;
    }
}
