package com.unidawgs.le5.clubdawgs.events;

import com.unidawgs.le5.clubdawgs.overlays.Overlay;
import javafx.event.Event;
import javafx.event.EventType;

public class OverlayEvent extends Event {
    private final Overlay overlay;

    public OverlayEvent(EventType<? extends OverlayEvent> eventType,  Overlay overlay) {
        super(eventType);
        this.overlay = overlay;
    }

    public Overlay getOverlay() {
        return this.overlay;
    }
}
