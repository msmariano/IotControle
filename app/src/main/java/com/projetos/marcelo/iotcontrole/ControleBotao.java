package com.projetos.marcelo.iotcontrole;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ControleBotao extends AsyncTask {
    private Integer buttonID;
    private String iotDst;
    private com.projetos.marcelo.iotcontrole.Status status;
    private ConfigBotao cfg;
    private com.projetos.marcelo.iotcontrole.Status statusRetornado;
    private final String DESLIGADO = "#ffffff";
    private final String LIGADO = "#00ff00";
    private final String NAOINICIALIZADO = "#fff000";
    private ControleBotao self;
    private String nome;
    Button btn;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ControleBotao() {

    }

    public ControleBotao(String nomeLocal, ConfigBotao c, Integer id) {
        nome = nomeLocal;
        self = this;
        cfg = c;
        status = com.projetos.marcelo.iotcontrole.Status.OFF;
        buttonID = id;
        gerarBotao();
        atualizar();
    }

    public void gerarBotao() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(100, 50, 100, 50);
        btn = new Button(cfg.getAct());
        btn.setId(buttonID);
        final int id_ = btn.getId();
        btn.setText(nome);
        btn.setBackgroundColor(Color.rgb(255, 255, 255));
        cfg.getLinear().addView(btn, params);
        btn = ((Button) cfg.getAct().findViewById(id_));
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new ControleBotao().execute(self);
            }
        });

    }

    public String gerarConectorJson(com.projetos.marcelo.iotcontrole.Status sEnvio, com.projetos.marcelo.iotcontrole.Status st, String iotDst, Integer buttonId) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        Conector conector = new Conector();
        conector.setIot(gerarIot(st, iotDst, buttonId));
        conector.setId("0");
        conector.setTipo(TipoIOT.HUMAN);
        conector.setNome(cfg.getNomeIOTCom());
        conector.setSenha(cfg.getSenha());
        conector.setUsuario(cfg.getUsuario());
        conector.setStatus(sEnvio);
        return gson.toJson(conector);
    }

    public Iot gerarIot(com.projetos.marcelo.iotcontrole.Status st, String iotDst, Integer buttonId) {
        Iot iot = new Iot();
        iot.setId("0");
        iot.setName(iotDst);
        iot.setjSon(gerarBotoesJson(st, buttonId, iotDst));
        return iot;
    }

    public String gerarBotoesJson(com.projetos.marcelo.iotcontrole.Status st, Integer botaoId, String iotDst) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        ButtonIot buttonIot = new ButtonIot();
        buttonIot.setButtonID(botaoId);
        com.projetos.marcelo.iotcontrole.Status statusLocal = null;
        if (st.equals(com.projetos.marcelo.iotcontrole.Status.ACIONARBOTAO)) {
            buttonIot.setStatus(com.projetos.marcelo.iotcontrole.Status.OUT);
            if (statusRetornado.equals(com.projetos.marcelo.iotcontrole.Status.OFF)) {
                statusRetornado = com.projetos.marcelo.iotcontrole.Status.ON;
                btn.setBackgroundColor(Color.parseColor(DESLIGADO));
                statusLocal = statusRetornado;
            } else if (statusRetornado.equals(com.projetos.marcelo.iotcontrole.Status.ON)) {
                statusRetornado = com.projetos.marcelo.iotcontrole.Status.OFF;
                btn.setBackgroundColor(Color.parseColor(LIGADO));
                statusLocal = statusRetornado;
            }
            buttonIot.setTecla(statusLocal);
        } else if (st.equals(com.projetos.marcelo.iotcontrole.Status.READ)) {
            buttonIot.setStatus(com.projetos.marcelo.iotcontrole.Status.READ);
            buttonIot.setTecla(com.projetos.marcelo.iotcontrole.Status.NA);
        } else if (st.equals(com.projetos.marcelo.iotcontrole.Status.GETVALUE)) {
            buttonIot.setStatus(com.projetos.marcelo.iotcontrole.Status.GETVALUE);
            buttonIot.setTecla(com.projetos.marcelo.iotcontrole.Status.NA);
        }

        List<ButtonIot> botoes = new ArrayList<>();
        botoes.add(buttonIot);
        return gson.toJson(botoes);
    }

    public void atualizar() {
        log("Atualizando :" + buttonID);
        new Thread() {
            @Override
            public void run() {
                cfg.getAct().runOnUiThread(() -> btn.setBackgroundColor(Color.parseColor("#ff00ff")));
                envia(gerarConectorJson(com.projetos.marcelo.iotcontrole.Status.LOGINWITHCOMMAND, com.projetos.marcelo.iotcontrole.Status.READ, cfg.getNomeIot(), buttonID));
            }
        }.start();
    }

    private void log(String text) {
        //cfg.getLog().setText(cfg.getLog().getText()+"\n"+text);
    }


    public Integer getButtonID() {
        return buttonID;
    }

    public void setButtonID(Integer buttonID) {
        this.buttonID = buttonID;
    }

    public String getIotDst() {
        return iotDst;
    }

    public void setIotDst(String iotDst) {
        this.iotDst = iotDst;
    }

    public com.projetos.marcelo.iotcontrole.Status getStatusIOT() {
        return status;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        ControleBotao ct;
        Object obj = objects[0];
        ct = (ControleBotao) obj;
        com.projetos.marcelo.iotcontrole.Status loginwithcommand = com.projetos.marcelo.iotcontrole.Status.LOGINWITHCOMMAND;
        com.projetos.marcelo.iotcontrole.Status acionarbotao = com.projetos.marcelo.iotcontrole.Status.ACIONARBOTAO;
        com.projetos.marcelo.iotcontrole.Status read = com.projetos.marcelo.iotcontrole.Status.READ;
        ct.envia(ct.gerarConectorJson(loginwithcommand, acionarbotao, ct.cfg.getNomeIot(), ct.buttonID));
        ct.envia(ct.gerarConectorJson(loginwithcommand, read, ct.cfg.getNomeIot(), ct.buttonID));
        return null;
    }

    public void setStatus(com.projetos.marcelo.iotcontrole.Status status) {
        this.status = status;
    }

    public void envia(String textJson) {
        try {
            log("Conectando:" + cfg.getServidor() + ":" + String.valueOf(cfg.getPortaServidor()));
            InetAddress serverEnd = InetAddress.getByName(cfg.getServidor());
            Socket socket = new Socket(serverEnd, cfg.getPortaServidor());
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream())), true);
            log("Send:" + textJson);
            out.println(textJson);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String ret = in.readLine();
            log("recv:" + ret);
            ret = ret.trim();
            int contador = 0, inicio = 0;
            for (int i = 0; i < ret.length(); i++) {
                if (ret.charAt(i) == '{') {
                    contador++;
                } else if (ret.charAt(i) == '}') {
                    contador--;
                }
                if (contador == 0 && i != 0) {
                    trataRetorno(ret.substring(inicio, i + 1));
                    inicio = i + 1;
                }
            }
            socket.close();
        } catch (Exception e) {
            log("Erro envia:" + e.getMessage());
        }
    }

    public void trataRetorno(String jSonRetorno) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        Conector conector = gson.fromJson(jSonRetorno, Conector.class);
        if (conector != null && conector.getStatus() != null && conector.getStatus().equals(com.projetos.marcelo.iotcontrole.Status.RETORNOTRANSITORIO)) {
            if (conector.getMens().getSt().equals(com.projetos.marcelo.iotcontrole.Status.SUCESSO)) {
                //cfg.getAct().runOnUiThread(() -> Toast.makeText(cfg.getAct().getApplicationContext(), conector.getMens().getMens(), Toast.LENGTH_LONG).show());
            } else if (conector.getMens().getSt().equals(com.projetos.marcelo.iotcontrole.Status.ERRO)) {
                //cfg.getAct().runOnUiThread(() -> Toast.makeText(cfg.getAct().getApplicationContext(), conector.getMens().getMens(), Toast.LENGTH_LONG).show());
            }
        } else if (conector != null && conector.getStatus() != null && conector.getStatus().equals(com.projetos.marcelo.iotcontrole.Status.RETORNO)) {
            if (conector.getIot().getjSon() != null) {
                ButtonIot buttonIot = null;
                try {
                    buttonIot = gson.fromJson(conector.getIot().getjSon(), ButtonIot.class);

                    if (buttonIot != null && buttonIot.getFuncao() != null) {
                        if (buttonIot.getFuncao().equals(com.projetos.marcelo.iotcontrole.Status.READ)) {
                            if (buttonIot.getStatus() != null) {
                                com.projetos.marcelo.iotcontrole.Status statusLocal = buttonIot.getStatus();
                                if (statusLocal.toString().equals("OFF")) {
                                    btn.setBackgroundColor(Color.parseColor(DESLIGADO));
                                    statusRetornado = buttonIot.getStatus();
                                } else {
                                    btn.setBackgroundColor(Color.parseColor(LIGADO));
                                    statusRetornado = buttonIot.getStatus();
                                }
                            }
                        } else if (buttonIot.getFuncao().equals(com.projetos.marcelo.iotcontrole.Status.GETVALUE)) {
                            //Toast.makeText(cfg.getAct().getApplicationContext(), buttonIot.getjSon(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    //Toast.makeText(cfg.getAct().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
