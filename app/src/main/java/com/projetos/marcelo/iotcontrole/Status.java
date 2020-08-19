package com.projetos.marcelo.iotcontrole;

public enum Status {
    CONECTADO(1),LOGIN(2),VOID(3),FAIL(4),ALIVE(5),JSON(6),ON(7),
        OFF(8),IN(9),OUT(10),CONTROLLERCOMMAND(11),LOGINWITHCOMMAND(12),
        RETORNO(13),READ(14),NA(15);

    private final int valor;

    Status(int i) {
        this.valor = i;
    }
    public int getValor() {
        return valor;
    }

}
