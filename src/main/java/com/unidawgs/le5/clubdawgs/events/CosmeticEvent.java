package com.unidawgs.le5.clubdawgs.events;

import javafx.event.Event;
import javafx.event.EventType;

public class CosmeticEvent extends Event {
    private final int cosmetic;

    public CosmeticEvent(EventType<? extends CosmeticEvent> eventType, int cosmetic) {
        super(eventType);
        this.cosmetic = cosmetic;
    }

    public int getCosmetic() {
        return this.cosmetic;
    }
}
