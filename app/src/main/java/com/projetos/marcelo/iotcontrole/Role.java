package com.projetos.marcelo.iotcontrole;

import java.util.ArrayList;
import java.util.List;

public enum Role {

    NONE(0, "Nenhum"),
    ADMIN(1, "Administrador"),
    USUARIO(2, "Usuário"),
    SUPER(2, "Super usuário");

    Role(int i, String descricao) {
        this.valor = i;
        this.descricao = descricao;
    }

    private final int valor;
    private final String descricao;

    public static Role getEnum(Integer id) {

        for (Role item : values()) {
            if (item.getValor() == id) {
                return item;
            }
        }
        return null;
    }

    public static Role getEnumByDesc(String desc) {

        for (Role item : values()) {
            if (item.getDescricao().equals(desc)) {
                return item;
            }
        }
        return null;
    }

    public static List<String> listDescricao() {
        List<String> lista = new ArrayList<>();
        for (Role item : values()) {
            if (!item.getDescricao().equals(""))
                lista.add(item.getDescricao());
        }
        return lista;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getValor() {
        return valor;
    }

}
