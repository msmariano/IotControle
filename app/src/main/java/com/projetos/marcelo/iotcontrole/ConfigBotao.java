package com.projetos.marcelo.iotcontrole;

import com.orm.SugarRecord;

public class ConfigBotao extends SugarRecord<ConfigBotao> {


    private String servidor;
    private Integer portaServidor;
    private String usuario;
    private String senha;
    private String nomeIOTCom;
    private String nomeIot;
    private String nomeBotao;
    private Integer idBotao;

    public String getNomeBotao() {
        return nomeBotao;
    }

    public void setNomeBotao(String nomeBotao) {
        this.nomeBotao = nomeBotao;
    }

    public Integer getIdBotao() {
        return idBotao;
    }

    public void setIdBotao(Integer idBotao) {
        this.idBotao = idBotao;
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

    public String getNomeIot() {
        return nomeIot;
    }

    public void setNomeIot(String nomeIot) {
        this.nomeIot = nomeIot;
    }


}
