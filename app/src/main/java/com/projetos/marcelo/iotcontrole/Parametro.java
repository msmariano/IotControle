package com.projetos.marcelo.iotcontrole;


import com.orm.SugarRecord;

public class Parametro extends SugarRecord<Parametro> {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Parametro(){

    }
    public Parametro(String par,String c1,String c2){
        parametro = par;
        campo1 = c1;
        campo2 = c2;
    }

    private Long id;
    private String parametro;
    private String campo1;
    private String campo2;

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    public String getCampo1() {
        return campo1;
    }

    public void setCampo1(String campo1) {
        this.campo1 = campo1;
    }

    public String getCampo2() {
        return campo2;
    }

    public void setCampo2(String campo2) {
        this.campo2 = campo2;
    }





}