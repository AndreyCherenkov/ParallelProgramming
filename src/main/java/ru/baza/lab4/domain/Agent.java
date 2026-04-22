package ru.baza.lab4.domain;

public abstract class Agent {

    private Position position;

    public void setPosition(int x, int y) {
        position.setX(x); //todo synchronization on the object layer?
        position.setY(y);
    }

    public Position getPosition() {
        return position;
    }
}
