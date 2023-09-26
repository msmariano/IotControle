package com.projetos.marcelo.iotcontrole;

import com.google.gson.annotations.Expose;

public class Dispositivo {

    @Expose(serialize = true)
    private Status status;

    public String getEndServidor() {
        return endServidor;
    }

    public void setEndServidor(String endServidor) {
        this.endServidor = endServidor;
    }

    @Expose(serialize = true)
    private Integer id;

    @Expose(serialize = true)
    private String nick;

    private String idpool;

    private String endServidor;

    public boolean on() {
        return false;
    }

    public boolean off() {
        return false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status st) {
        status = st;
    }

    public void updateStatus(Status st) {

    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIdpool() {
        return idpool;
    }

    public void setIdpool(String idpool) {
        this.idpool = idpool;
    }
}
