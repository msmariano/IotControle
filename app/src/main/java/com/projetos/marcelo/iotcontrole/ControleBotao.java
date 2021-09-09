package com.projetos.marcelo.iotcontrole;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private AppCompatActivity act;
    private Integer buttonID;
    private String iotDst;
    private LinearLayout linear;


    public ConfigBotao getCfg() {
        return cfg;
    }

    public void setCfg(ConfigBotao cfg) {
        this.cfg = cfg;
    }

    private com.projetos.marcelo.iotcontrole.Status status;
    private ConfigBotao cfg;
    private com.projetos.marcelo.iotcontrole.Status statusRetornado;
    private final String DESLIGADO = "#ffffff";
    private final String LIGADO = "#00ff00";
    private final String NAOINICIALIZADO = "#fff000";
    private ControleBotao self;
    private String nome;
    private EditText log;

    public EditText getLog() {
        return log;
    }

    public void setLog(EditText log) {
        this.log = log;
    }

    Button btn;
    private List<Integer> idsBt = new ArrayList<Integer>();

    public List<Integer> getIdsBt() {
        return idsBt;
    }

    public void setIdsBt(List<Integer> idsBt) {
        this.idsBt = idsBt;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ControleBotao() {

    }

    public ControleBotao(String nomeLocal, ConfigBotao c, Integer id, AppCompatActivity actLocal, LinearLayout linearLocal) {
        linear = linearLocal;
        act = actLocal;
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
        btn = new Button(act);
        btn.setId(buttonID);
        final int id_ = btn.getId();
        btn.setText(nome);
        btn.setBackgroundColor(Color.rgb(255, 255, 255));
        linear.addView(btn, params);
        btn = ((Button) act.findViewById(id_));
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

    public String gerarConectorJsonSemTratamento(com.projetos.marcelo.iotcontrole.Status sEnvio, com.projetos.marcelo.iotcontrole.Status st, String iotDst, Integer buttonId) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        Conector conector = new Conector();
        conector.setIot(gerarIotSemTratamento(st, iotDst, buttonId));
        conector.setId("0");
        conector.setTipo(TipoIOT.HUMAN);
        conector.setNome(cfg.getNomeIOTCom());
        conector.setSenha(cfg.getSenha());
        conector.setUsuario(cfg.getUsuario());
        conector.setStatus(sEnvio);
        return gson.toJson(conector);
    }

    public Iot gerarIotSemTratamento(com.projetos.marcelo.iotcontrole.Status st, String iotDst, Integer buttonId) {
        Iot iot = new Iot();
        iot.setId("0");
        iot.setName(iotDst);
        iot.setjSon(gerarBotoesJsonSemTratamento(st, buttonId, iotDst));
        return iot;
    }

    public String gerarBotoesJsonSemTratamento(com.projetos.marcelo.iotcontrole.Status st, Integer botaoId, String iotDst) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        ButtonIot buttonIot = new ButtonIot();
        buttonIot.setButtonID(botaoId);
        buttonIot.setStatus(st);
        buttonIot.setTecla(com.projetos.marcelo.iotcontrole.Status.NA);
        List<ButtonIot> botoes = new ArrayList<>();
        botoes.add(buttonIot);
        return gson.toJson(botoes);
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
               act.runOnUiThread(() -> btn.setBackgroundColor(Color.parseColor("#ff00ff")));
               while(true) {
                   envia(gerarConectorJson(com.projetos.marcelo.iotcontrole.Status.LOGINWITHCOMMAND, com.projetos.marcelo.iotcontrole.Status.READ, cfg.getNomeIot(), buttonID));
                   try {
                       Thread.sleep(1000);
                   }
                   catch (Exception e){

                   }
               }
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

    public void pegarIds(String jSonRetorno) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        Conector conector = gson.fromJson(jSonRetorno, Conector.class);
        if (conector != null && conector.getStatus() != null && conector.getStatus().equals(com.projetos.marcelo.iotcontrole.Status.RETORNO)) {
            if (conector.getIot().getjSon() != null) {
                ButtonIot buttonIot = null;
                try {
                    buttonIot = gson.fromJson(conector.getIot().getjSon(), ButtonIot.class);

                    if (buttonIot != null && buttonIot.getFuncao() != null) {
                        if (buttonIot.getFuncao().equals(com.projetos.marcelo.iotcontrole.Status.READ)) {
                            if (buttonIot.getStatus() != null) {
                                System.out.println("ButtonID:" + buttonIot.getButtonID());
                                idsBt.add(buttonIot.getButtonID());
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }
    public void testaBotoes() {

        for (int j = 0; j < 8; j++) {
            String ret = enviaServidor(gerarConectorJsonSemTratamento(com.projetos.marcelo.iotcontrole.Status.LOGINWITHCOMMAND,
                    com.projetos.marcelo.iotcontrole.Status.READ, cfg.getNomeIot(), j));
            ret = ret.trim();
            int contador = 0, inicio = 0;
            for (int i = 0; i < ret.length(); i++) {
                if (ret.charAt(i) == '{') {
                    contador++;
                } else if (ret.charAt(i) == '}') {
                    contador--;
                }
                if (contador == 0 && i != 0) {
                    pegarIds(ret.substring(inicio, i + 1));
                    inicio = i + 1;
                }
            }
        }

    }

    public String enviaServidor(String textJson) {
        String ret = null;
        try {

            InetAddress serverEnd = InetAddress.getByName(cfg.getServidor());
            Socket socket = new Socket(serverEnd, cfg.getPortaServidor());
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                    true);

            out.println(textJson);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ret = in.readLine();
            socket.close();

        } catch (Exception e) {

        }
        return ret;
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
