package com.example.reddit.dto;


public class PostUpdate extends PostCreate {

    private boolean locked;

    public boolean locked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
