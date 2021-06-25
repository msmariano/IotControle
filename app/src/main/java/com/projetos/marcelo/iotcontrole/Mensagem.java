package com.projetos.marcelo.iotcontrole;


public class Mensagem {
    private String id;
    private Integer cod;
    private Status st;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Integer getCod() {
        return cod;
    }
    public void setCod(Integer cod) {
        this.cod = cod;
    }
    public String getMens() {
        return mens;
    }
    public void setMens(String mens) {
        this.mens = mens;
    }
    public Status getSt() {
        return st;
    }
    public void setSt(Status st) {
        this.st = st;
    }
    private String mens;

}