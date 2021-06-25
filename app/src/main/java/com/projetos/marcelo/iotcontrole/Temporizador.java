package com.projetos.marcelo.iotcontrole;

public class Temporizador {
    private String horaTemporizador;
    private String horaInicializacao;
    private long duracao;
    private long horaInicioTemporizador;
    private long horaFinalTemporizador;

    private boolean acionarTemporizador;
    private boolean aguardandoTemporizador;
    private boolean ativarAleatorio;

    public String getHoraTemporizador() {
        return horaTemporizador;
    }
    public void setHoraTemporizador(String horaTemporizador) {
        this.horaTemporizador = horaTemporizador;
    }
    public String getHoraInicializacao() {
        return horaInicializacao;
    }
    public void setHoraInicializacao(String horaInicializacao) {
        this.horaInicializacao = horaInicializacao;
    }
    public long getDuracao() {
        return duracao;
    }
    public void setDuracao(long duracao) {
        this.duracao = duracao;
    }
    public long getHoraInicioTemporizador() {
        return horaInicioTemporizador;
    }
    public void setHoraInicioTemporizador(long horaInicioTemporizador) {
        this.horaInicioTemporizador = horaInicioTemporizador;
    }
    public long getHoraFinalTemporizador() {
        return horaFinalTemporizador;
    }
    public void setHoraFinalTemporizador(long horaFinalTemporizador) {
        this.horaFinalTemporizador = horaFinalTemporizador;
    }
    public boolean isAcionarTemporizador() {
        return acionarTemporizador;
    }
    public void setAcionarTemporizador(boolean acionarTemporizador) {
        this.acionarTemporizador = acionarTemporizador;
    }
    public boolean isAguardandoTemporizador() {
        return aguardandoTemporizador;
    }
    public void setAguardandoTemporizador(boolean aguardandoTemporizador) {
        this.aguardandoTemporizador = aguardandoTemporizador;
    }
    public boolean isAtivarAleatorio() {
        return ativarAleatorio;
    }
    public void setAtivarAleatorio(boolean ativarAleatorio) {
        this.ativarAleatorio = ativarAleatorio;
    }

}
