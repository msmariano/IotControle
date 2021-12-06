package com.projetos.marcelo.iotcontrole;

public class Iot {
    private String id;
    private String name;
    private String jSon;
    private TipoIOT tipoIOT;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getjSon() {
        return jSon;
    }
    public void setjSon(String jSon) {
        this.jSon = jSon;
    }
    public TipoIOT getTipoIOT() {
        return tipoIOT;
    }
    public void setTipoIOT(TipoIOT tipoIOT) {
        this.tipoIOT = tipoIOT;
    }
}
