package pon.purr.events.impl;

import pon.purr.events.Event;

public class EventJumpHeight extends Event {
    private float jumpHeight;

    public EventJumpHeight(float jumpHeight) {
        this.jumpHeight = jumpHeight;
    }

    public float getJumpHeight() {
        return jumpHeight;
    }

    public void  setJumpHeight(float value) {
        this.jumpHeight = value;
    }
}
