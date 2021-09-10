package com.projetos.marcelo.iotcontrole;

import com.orm.SugarRecord;


public class Configuracao extends SugarRecord<Configuracao> {

    private Long id;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    private String servidor;
    private Integer portaservidor;
    private String usuario;
    private String senha;
    private String nomeiotcom;
    private String nomeiot;
    private String nomebotao;
    private Integer idbotao;


    public Configuracao(){

    }
    public Configuracao(String servidor, Integer portaServidor, String usuario, String senha,
                        String nomeIOTCom, String nomeIot, String nomeBotao, Integer idBotao) {
        this.servidor = servidor;
        this.portaservidor = portaServidor;
        this.usuario = usuario;
        this.senha = senha;
        this.nomeiotcom = nomeIOTCom;
        this.nomeiot = nomeIot;
        this.nomebotao = nomeBotao;
        this.idbotao = idBotao;

    }

    public String getNomebotao() {
        return nomebotao;
    }

    public void setNomebotao(String nomebotao) {
        this.nomebotao = nomebotao;
    }

    public Integer getIdbotao() {
        return idbotao;
    }

    public void setIdbotao(Integer idbotao) {
        this.idbotao = idbotao;
    }


    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public Integer getPortaservidor() {
        return portaservidor;
    }

    public void setPortaservidor(Integer portaservidor) {
        this.portaservidor = portaservidor;
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

    public String getNomeiotcom() {
        return nomeiotcom;
    }

    public void setNomeiotcom(String nomeiotcom) {
        this.nomeiotcom = nomeiotcom;
    }

    public String getNomeiot() {
        return nomeiot;
    }

    public void setNomeiot(String nomeiot) {
        this.nomeiot = nomeiot;
    }


}
