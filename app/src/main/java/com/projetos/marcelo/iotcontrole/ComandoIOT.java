package com.projetos.marcelo.iotcontrole;


public class ComandoIOT {
    private String acao;
    private String dispositivo;
    private String retorno;
    private String estadoAtual;
    private String resultado;
    private Integer retardo;
    private Integer tipo;
    private String ssid;
    private String password;
    private String conteudo;

    public String getAcao() {
        return acao;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Integer getRetardo() {
        return retardo;
    }

    public void setRetardo(Integer retardo) {
        this.retardo = retardo;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getEstadoAtual() {
        return estadoAtual;
    }

    public void setEstadoAtual(String estadoAtual) {
        this.estadoAtual = estadoAtual;
    }

    public String getRetorno() {
        return retorno;
    }

    public void setRetorno(String retorno) {
        this.retorno = retorno;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

}
