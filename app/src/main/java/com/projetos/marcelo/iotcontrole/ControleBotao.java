package com.projetos.marcelo.iotcontrole;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ControleBotao extends AsyncTask {
    private final String DESLIGADO = "#ffffff";
    private final String LIGADO = "#00ff00";
    private final String NAOINICIALIZADO = "#fff000";
    private Socket socketEnviaSemFechar;
    private AppCompatActivity act;
    private Integer buttonID;
    private String iotDst;
    private LinearLayout linear;
    private Button btn;
    private com.projetos.marcelo.iotcontrole.Status status = com.projetos.marcelo.iotcontrole.Status.NA;
    private Configuracao cfg;
    private com.projetos.marcelo.iotcontrole.Status statusRetornado = com.projetos.marcelo.iotcontrole.Status.NA;
    private ControleBotao self;
    private String nome;
    private EditText log;
    private List<Integer> idsBt = new ArrayList<Integer>();
    public ControleBotao() {

    }
    public ControleBotao(String nomeLocal, Configuracao c, Integer id, AppCompatActivity actLocal, LinearLayout linearLocal) throws IOException {
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

    public Button getBtn() {
        return btn;
    }

    public void setBtn(Button btn) {
        this.btn = btn;
    }

    public Configuracao getCfg() {
        return cfg;
    }

    public void setCfg(Configuracao cfg) {
        this.cfg = cfg;
    }

    public EditText getLog() {
        return log;
    }

    public void setLog(EditText log) {
        this.log = log;
    }

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

    public void gerarBotao() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(100, 50, 100, 50);
        btn = new Button(act);
        btn.setId(buttonID);
        final int id_ = btn.getId();
        btn.setTooltipText(nome);
        Drawable img = act.getResources().getDrawable(R.drawable.b983w);
        img.setBounds(0, 0, 60, 60);
        btn.setCompoundDrawables(img, null, null, null);
        btn.setBackgroundColor(Color.rgb(255, 255, 255));
        btn.setText(nome);
        linear.addView(btn, params);
        btn = act.findViewById(id_);
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
        conector.setNome(cfg.getNomeiotcom());
        conector.setSenha(cfg.getSenha());
        conector.setUsuario(cfg.getUsuario());
        conector.setStatus(sEnvio);
        return gson.toJson(conector);
    }

    public Iot gerarIot(com.projetos.marcelo.iotcontrole.Status st, String iotDst, Integer buttonId) {
        Iot iot = new Iot();
        iot.setId("0");
        iot.setName(iotDst);
        iot.setTipoIOT(TipoIOT.SERVIDOR);
        iot.setjSon(gerarBotoesJson(st, buttonId, iotDst));
        return iot;
    }

    public String gerarConectorJsonSemTratamento(com.projetos.marcelo.iotcontrole.Status sEnvio, com.projetos.marcelo.iotcontrole.Status st, String iotDst, Integer buttonId) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        Conector conector = new Conector();
        conector.setIot(gerarIotSemTratamento(st, iotDst, buttonId));
        conector.setId("0");
        conector.setTipo(TipoIOT.HUMAN);
        conector.setNome(cfg.getNomeiotcom());
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

    void atualizaImagemLigada() {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Drawable img = act.getResources().getDrawable(R.drawable.lacessa);
                img.setBounds(0, 0, 60, 60);
                act.runOnUiThread(() -> btn.setCompoundDrawables(img, null, null, null));
            }
        });
    }

    void atualizaImagemDesLigada() {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Drawable img = act.getResources().getDrawable(R.drawable.b983w);
                img.setBounds(0, 0, 60, 60);
                act.runOnUiThread(() -> btn.setCompoundDrawables(img, null, null, null));
            }
        });
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
                //btn.setBackgroundColor(Color.parseColor(DESLIGADO));
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Drawable img = act.getResources().getDrawable(R.drawable.lacessa);
                        img.setBounds(0, 0, 60, 60);
                        act.runOnUiThread(() -> btn.setCompoundDrawables(img, null, null, null));
                    }
                });

                statusLocal = statusRetornado;
            } else if (statusRetornado.equals(com.projetos.marcelo.iotcontrole.Status.ON)) {
                statusRetornado = com.projetos.marcelo.iotcontrole.Status.OFF;
                //btn.setBackgroundColor(Color.parseColor(LIGADO));
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Drawable img = act.getResources().getDrawable(R.drawable.b983w);
                        img.setBounds(0, 0, 60, 60);
                        act.runOnUiThread(() -> btn.setCompoundDrawables(img, null, null, null));
                    }
                });
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

    public void atualizar() throws IOException {


        new Thread() {
            @Override
            public void run() {
                try {
                    socketEnviaSemFechar = new Socket();
                    socketEnviaSemFechar.setSoTimeout(5000);
                    SocketAddress socketAddress = new InetSocketAddress(cfg.getServidor(), cfg.getPortaservidor());
                    socketEnviaSemFechar.connect(socketAddress, 5000);
                }
                catch (Exception e){

                }
                while (socketEnviaSemFechar.isConnected()) {
                    try {
                        //Teste de exaustao
                        //if(buttonID == 1)
                        //    new ControleBotao().execute(self);
                        String jSon = gerarConectorJson(com.projetos.marcelo.iotcontrole.Status.LOGINWITHCOMMAND, com.projetos.marcelo.iotcontrole.Status.READ, cfg.getNomeiot(), buttonID);
                        enviaSemFechar(jSon);
                        Thread.sleep(500);
                    } catch (Exception e) {
                        System.err.println("Erro atualizar: " + e.getMessage());
                    }
                }
                try {
                    socketEnviaSemFechar.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void log(String text) {
        //System.out.println(text);
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
        //com.projetos.marcelo.iotcontrole.Status acionarbotao = com.projetos.marcelo.iotcontrole.Status.OUT;
        if(statusRetornado == null)
            statusRetornado = com.projetos.marcelo.iotcontrole.Status.OFF;
        com.projetos.marcelo.iotcontrole.Status read = com.projetos.marcelo.iotcontrole.Status.READ;
        String jSon = ct.gerarConectorJson(loginwithcommand, acionarbotao, ct.cfg.getNomeiot(), ct.buttonID);
        ct.envia(jSon);
        ct.envia(ct.gerarConectorJson(loginwithcommand, read, ct.cfg.getNomeiot(), ct.buttonID));
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

        /*for (int j = 0; j < 8; j++) {
            String ret = enviaServidor(gerarConectorJsonSemTratamento(com.projetos.marcelo.iotcontrole.Status.LOGINWITHCOMMAND,
                    com.projetos.marcelo.iotcontrole.Status.READ, cfg.getNomeiot(), j));
            if(ret!=null) {
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
        }*/

        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();

        Conector conector = new Conector();
        conector.setId("0");
        conector.setTipo(TipoIOT.HUMAN);
        conector.setNome(cfg.getNomeiot());
        conector.setSenha(cfg.getSenha());
        conector.setUsuario(cfg.getUsuario());
        conector.setStatus(com.projetos.marcelo.iotcontrole.Status.LOGINWITHCOMMAND);
        Iot iot = new Iot();
        iot.setId("0");
        iot.setTipoIOT(TipoIOT.SERVIDOR);
        iot.setName(cfg.getNomeiot());
        conector.setIot(iot);


        List<ButtonIot> botoes = new ArrayList<>();
        for (int j = 0; j < 8; j++) {

            ButtonIot buttonIot = new ButtonIot();
            buttonIot.setButtonID(j);
            buttonIot.setStatus(com.projetos.marcelo.iotcontrole.Status.READ);
            buttonIot.setTecla(com.projetos.marcelo.iotcontrole.Status.NA);
            botoes.add(buttonIot);

        }
        Type listType1 = new TypeToken<ArrayList<ButtonIot>>(){}.getType();
        iot.setjSon(gson.toJson(botoes,listType1));
        String jSonEnvia = gson.toJson(conector,Conector.class);
        String ret1 = enviaServidor(jSonEnvia);


        Conector con  = gson.fromJson(ret1, Conector.class);
        Type listType = new TypeToken<ArrayList<ButtonIot>>(){}.getType();
        List<ButtonIot> listaBiot = gson.fromJson(con.getIot().getjSon(),listType);
        for (ButtonIot buttonIot: listaBiot) {
            if (buttonIot != null && buttonIot.getFuncao() != null) {
                if (buttonIot.getFuncao().equals(com.projetos.marcelo.iotcontrole.Status.INTERRUPTOR)) {
                    if (buttonIot.getStatus() != null) {
                        System.out.println("ButtonID:" + buttonIot.getButtonID());
                        idsBt.add(buttonIot.getButtonID());
                    }
                }
            }
        }

    }

    public String enviaServidor(String textJson) {
        String ret = null;
        try {

            Socket socket = new Socket();
            socket.setSoTimeout(5000);
            SocketAddress socketAddress = new InetSocketAddress(cfg.getServidor(), cfg.getPortaservidor());
            socket.connect(socketAddress, 5000);
            //InetAddress serverEnd = InetAddress.getByName(cfg.getServidor());
            //Socket socket = new Socket(serverEnd, cfg.getPortaservidor());
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

    public boolean enviaSemFechar(String textJson) {
        try {


            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(
                            socketEnviaSemFechar.getOutputStream())), true);
            out.println(textJson);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socketEnviaSemFechar.getInputStream()));
            String ret = in.readLine();
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

        } catch (SocketTimeoutException ste) {
            System.err.println("Erro timeout envia: " + ste.getMessage());
            return false;

        } catch (Exception e) {
            System.err.println("Erro envia:" + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean envia(String textJson) {
        try {
            Socket socket = new Socket();
            socket.setSoTimeout(5000);
            SocketAddress socketAddress = new InetSocketAddress(cfg.getServidor(), cfg.getPortaservidor());
            socket.connect(socketAddress, 5000);
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream())), true);
            out.println(textJson);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String ret = in.readLine();
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
        } catch (SocketTimeoutException ste) {
            System.err.println("Erro timeout envia: " + ste.getMessage());
            return false;

        } catch (Exception e) {
            System.err.println("Erro envia:" + e.getMessage());
            return false;
        }
        return true;
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

                try {
                    Type listType = new TypeToken<ArrayList<ButtonIot>>() {}.getType();
                    List<ButtonIot> listaBiot = gson.fromJson(conector.getIot().getjSon(), listType);

                    for( ButtonIot buttonIot : listaBiot){
                        if (buttonIot != null && buttonIot.getFuncao() != null) {
                            if (buttonIot.getFuncao().equals(com.projetos.marcelo.iotcontrole.Status.INTERRUPTOR)) {
                                if (buttonIot.getStatus() != null) {
                                    com.projetos.marcelo.iotcontrole.Status statusLocal = buttonIot.getStatus();
                                    if (statusLocal.toString().equals("OFF")) {
                                        //btn.setBackgroundColor(Color.parseColor(DESLIGADO));
                                        atualizaImagemDesLigada();
                                        statusRetornado = buttonIot.getStatus();
                                    } else {
                                        atualizaImagemLigada();
                                        //btn.setBackgroundColor(Color.parseColor(LIGADO));
                                        statusRetornado = buttonIot.getStatus();
                                    }
                                }
                            } else if (buttonIot.getFuncao().equals(com.projetos.marcelo.iotcontrole.Status.GETVALUE)) {
                                //Toast.makeText(cfg.getAct().getApplicationContext(), buttonIot.getjSon(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println();
                }
            }
        }
    }
}
