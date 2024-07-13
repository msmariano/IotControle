package com.projetos.marcelo.iotcontrole;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import com.google.gson.annotations.Expose;

public class Dispositivo {
    public void copyDispositivo(Status status, TipoIOT genero, Status nivelAcionamento, Integer id, String nick, String idpool, String nickServidor, String endServidor) {
        this.status = status;
        this.genero = genero;
        this.nivelAcionamento = nivelAcionamento;
        this.id = id;
        this.nick = nick;
        this.idpool = idpool;
        this.nickServidor = nickServidor;
        this.endServidor = endServidor;
    }

    @Expose(serialize = true)
    private Status status;

    @Expose(serialize = true)
    private TipoIOT genero;

    @Expose(serialize = true)
    private Status nivelAcionamento;

    @Expose(serialize = true)
    private Integer id;

    @Expose(serialize = true)
    private String nick;

    private String buttoAddress;

    private String idpool;

    private String nickServidor;

    private String endServidor;

    private Button button;

    private Drawable img;

    private Boolean criado = false;

    private View view;

    public Boolean getCriado() {
        return criado;
    }

    public void setCriado(Boolean criado) {
        this.criado = criado;
    }

    public Drawable getImg() {
        return img;
    }

    public void setImg(Drawable img) {
        this.img = img;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public String getButtoAddress() {
        return buttoAddress;
    }

    public void setButtoAddress(String buttoAddress) {
        this.buttoAddress = buttoAddress;
    }


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

    public String getEndServidor() {
        return endServidor;
    }

    public void setEndServidor(String endServidor) {
        this.endServidor = endServidor;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
