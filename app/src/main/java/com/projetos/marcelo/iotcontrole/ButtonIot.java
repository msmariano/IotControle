package com.projetos.marcelo.iotcontrole;

public class ButtonIot {
    private Status tecla;
    private Integer buttonID;
    private Status status;

    public Status getTecla() {
        return tecla;
    }

    public void setTecla(Status tecla) {
        this.tecla = tecla;
    }

    public Integer getButtonID() {
        return this.buttonID;
    }

    public void setButtonID(Integer buttonID) {
        this.buttonID = buttonID;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
