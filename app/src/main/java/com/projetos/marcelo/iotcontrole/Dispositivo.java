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

    private String nickServidor;

    private String endServidor;

    @Expose(serialize = true)
    private TipoIOT genero;

    @Expose(serialize = true)
    private Status nivelAcionamento;

    public TipoIOT getGenero() {
        return genero;
    }

    public void setGenero(TipoIOT genero) {
        this.genero = genero;
    }

    public Status getNivelAcionamento() {
        return nivelAcionamento;
    }

    public void setNivelAcionamento(Status nivelAcionamento) {
        this.nivelAcionamento = nivelAcionamento;
    }



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

    public String getNickServidor() {
        return nickServidor;
    }

    public void setNickServidor(String arg) {
        this.nickServidor = arg;
    }

    public String getIdpool() {
        return idpool;
    }

    public void setIdpool(String idpool) {
        this.idpool = idpool;
    }
}
