package io.eryk.linkzone.dto;


public class PostUpdate extends PostCreate {

    private boolean locked;

    public boolean locked() {
        return locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
