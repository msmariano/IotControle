package com.projetos.marcelo.iotcontrole;

public class ButtonIot {
    private Status tecla;
    private Integer buttonID;
    private Status status;
    private String jSon;
    private Status funcao;
    private String nick;

    public Status getFuncao() {
        return funcao;
    }

    public void setFuncao(Status funcao) {
        this.funcao = funcao;
    }

    public String getjSon() {
        return jSon;
    }

    public void setjSon(String jSon) {
        this.jSon = jSon;
    }


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

    public String getNick(){return this.nick;}

    public void setNick(String nick) {this.nick = nick;}
}
