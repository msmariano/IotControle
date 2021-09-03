package com.projetos.marcelo.iotcontrole;

import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigBotao {

    private AppCompatActivity act;
    private String servidor;
    private Integer portaServidor;
    private EditText log;
    private LinearLayout linear;

    public LinearLayout getLinear() {
        return linear;
    }

    public void setLinear(LinearLayout linear) {
        this.linear = linear;
    }

    public EditText getLog() {
        return log;
    }

    public void setLog(EditText log) {
        this.log = log;
    }

    public AppCompatActivity getAct() {
        return act;
    }

    public void setAct(AppCompatActivity act) {
        this.act = act;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public Integer getPortaServidor() {
        return portaServidor;
    }

    public void setPortaServidor(Integer portaServidor) {
        this.portaServidor = portaServidor;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNomeIOTCom() {
        return nomeIOTCom;
    }

    public void setNomeIOTCom(String nomeIOTCom) {
        this.nomeIOTCom = nomeIOTCom;
    }

    private String usuario;
    private String senha;
    private String nomeIOTCom;

    public String getNomeIot() {
        return nomeIot;
    }

    public void setNomeIot(String nomeIot) {
        this.nomeIot = nomeIot;
    }

    private String nomeIot;
}
