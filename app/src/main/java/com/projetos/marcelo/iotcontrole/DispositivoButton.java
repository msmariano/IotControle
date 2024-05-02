package com.projetos.marcelo.iotcontrole;

import android.widget.Button;

public class DispositivoButton {
    private Button btn;

    private Dispositivo dispositivo;

    private String ObjectID;

    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectID) {
        ObjectID = objectID;
    }

    public Dispositivo getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }

    public Button getBtn() {
        return btn;
    }

    public void setBtn(Button btn) {
        this.btn = btn;
    }

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }

    public Integer getDipositivoId() {
        return dipositivoId;
    }

    public void setDipositivoId(Integer dipositivoId) {
        this.dipositivoId = dipositivoId;
    }

    private String poolId;
    private Integer dipositivoId;
}
