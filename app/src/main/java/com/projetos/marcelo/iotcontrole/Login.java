package com.projetos.marcelo.iotcontrole;

import java.util.List;

public class Login {
    private Integer id;
    private String user;
    private String pass;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getResultado() {
        return resultado;
    }

    public void setResultado(Boolean resultado) {
        this.resultado = resultado;
    }

    public List<String> getUuidIOTs() {
        return uuidIOTs;
    }

    public void setUuidIOTs(List<String> uuidIOTs) {
        this.uuidIOTs = uuidIOTs;
    }

    public List<Login> getLogins() {
        return logins;
    }

    public void setLogins(List<Login> logins) {
        this.logins = logins;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    private String uuid;
    private Boolean resultado;
    private List<String> uuidIOTs;
    private List<Login> logins;
    private Role role;

}
