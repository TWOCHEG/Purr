package com.purr.events.impl;

import com.purr.events.Event;

public class EventMouse extends Event {
    int button;

    public int getButton() {
        return button;
    }

    public int getAction() {
        return action;
    }

    int action;

    public EventMouse(int b,int action) {
        button = b;
        this.action = action;
    }
}
