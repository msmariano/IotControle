package com.projetos.marcelo.iotcontrole;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageButton IbOnOff;
    ImageButton IbOnOff2;
    ImageButton IbOnOff3;






    //TextView textView;
   //TextView textView7;
    //TextView inicioTimer;

    Button buscarB;
    Button button2;
    Button button3;
    Button button4;

    Status status;
    Status status1;
    Status status2;
    boolean bTemporizadorLigador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IbOnOff =  findViewById(R.id.btOnOff);
        IbOnOff2 =  findViewById(R.id.btOnOff2);
        IbOnOff3 =  findViewById(R.id.btOnOff3);
        buscarB =  findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);





        //textView = findViewById(R.id.textView);
        //textView7 = findViewById(R.id.textView7);
        //inicioTimer = findViewById(R.id.textView2);
        bTemporizadorLigador = false;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
        status = Status.OFF;
        status1 = Status.OFF;
        status2 = Status.OFF;


        IbOnOff.setBackgroundColor(Color.parseColor("#fff000"));
        IbOnOff2.setBackgroundColor(Color.parseColor("#fff000"));
        IbOnOff3.setBackgroundColor(Color.parseColor("#fff000"));
        IbOnOff.setEnabled(false);
        IbOnOff2.setEnabled(false);
        IbOnOff3.setEnabled(false);

        //envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.GETVALUE,"MedidorSaneparCasaMatinhos",1));

       /* new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true) {
                    envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.ALEATORIOINFO));
                    try {
                        Thread.sleep(1000*60*10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();*/

        button3.setOnClickListener(new View.OnClickListener()
        {
            public void onClick (View v)
            {
                MainActivity2 act = new MainActivity2();
                envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.ACIONARBOTAO,"CasaMatinhos",1));
            }
        });

        button4.setOnClickListener(new View.OnClickListener()
        {
            public void onClick (View v)
            {
                MainActivity2 act = new MainActivity2();
                envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.ACIONARBOTAO,"CasaMatinhos_Interno",2));
            }
        });

        button2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick (View v)
            {
                MainActivity2 act = new MainActivity2();
                envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.ACIONARBOTAO,"CasaMatinhos_Interno",1));
            }
        });


    }


    public void buscar(View view) {
        //buscarB.setText("Aguarde...");
        //buscarB.setBackgroundColor(Color.parseColor("#fff000"));

        new Thread() {
            @Override
            public void run() {
                runOnUiThread(() -> IbOnOff.setBackgroundColor(Color.parseColor("#ff00ff")));
                envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.READ,"CasaMatinhos",1));
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                runOnUiThread(() -> IbOnOff2.setBackgroundColor(Color.parseColor("#ff00ff")));
                envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.READ,"CasaMatinhos_Interno",1));
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                runOnUiThread(() -> IbOnOff3.setBackgroundColor(Color.parseColor("#ff00ff")));
                envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.READ,"CasaMatinhos_Interno",2));
            }
        }.start();



        /*envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.ALEATORIOINFO,"CasaMatinhos",1));
        envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.READ,"CasaMatinhos_Interno",1));
        //envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.ALEATORIOINFO,"CasaMatinhos_Interno",1));
        envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.READ,"CasaMatinhos_Interno",2));
        //envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.ALEATORIOINFO,"CasaMatinhos_Interno",2));
        IbOnOff.setEnabled(true);
        IbOnOff2.setEnabled(true);
        IbOnOff3.setEnabled(true);*/
       //buscarB.setBackgroundColor(Color.parseColor("#ff00ff"));
    }

    public void cozinha(View view) {

        envia(gerarConectorJson(Status.LOGINWITHCOMMAND,Status.ACIONARBOTAO,"CasaMatinhos_Interno",2));

        
    }

    public String gerarBotoesJson(Status st,Integer botaoId,String iotDst){
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        ButtonIot buttonIot = new ButtonIot();
        buttonIot.setButtonID(botaoId);
        Status statusLocal = null;
        if(st.equals(Status.ACIONARBOTAO)) {

            buttonIot.setStatus(Status.OUT);
            if (status1.equals(Status.OFF)  && "CasaMatinhos_Interno".equals(iotDst) && botaoId == 1) {
                status1 = Status.ON;
                IbOnOff2.setBackgroundColor(Color.parseColor("#00ff00"));
                statusLocal = status1;
            }
            else if (status2.equals(Status.OFF)  && "CasaMatinhos_Interno".equals(iotDst) && botaoId == 2) {
                status2 = Status.ON;
                IbOnOff3.setBackgroundColor(Color.parseColor("#00ff00"));
                statusLocal = status2;
            }
            else if (status.equals(Status.OFF)) {
                status = Status.ON;
                IbOnOff.setBackgroundColor(Color.parseColor("#00ff00"));
                statusLocal = status;
            }
            else if (status1.equals(Status.ON)  && "CasaMatinhos_Interno".equals(iotDst) && botaoId == 1) {
                status1 = Status.OFF;
                IbOnOff2.setBackgroundColor(Color.parseColor("#ffffff"));
                statusLocal = status1;
            }
            else if (status2.equals(Status.ON)  && "CasaMatinhos_Interno".equals(iotDst) && botaoId == 2) {
                status2 = Status.OFF;
                IbOnOff3.setBackgroundColor(Color.parseColor("#ffffff"));
                statusLocal = status2;
            }
            else if (status.equals(Status.ON)){
                status = Status.OFF;
                IbOnOff.setBackgroundColor(Color.parseColor("#ffffff"));
                statusLocal = status;
            }

            buttonIot.setTecla(statusLocal);
        }
        else if (st.equals(Status.READ)) {
            buttonIot.setStatus(Status.READ);
            buttonIot.setTecla(Status.NA);
        }
        else if(st.equals(Status.ALEATORIOON)){
            if( bTemporizadorLigador)
                buttonIot.setStatus(Status.ALEATORIOOFF);
            else
                buttonIot.setStatus(Status.ALEATORIOON);
            buttonIot.setTecla(Status.NA);
        }

        else if(st.equals(Status.ALEATORIOINFO)){
            buttonIot.setStatus(Status.ALEATORIOINFO);
            buttonIot.setTecla(Status.NA);
        }
        else if(st.equals(Status.GETVALUE)){
            buttonIot.setStatus(Status.GETVALUE);
            buttonIot.setTecla(Status.NA);
        }

        List<ButtonIot> botoes = new ArrayList<>();
        botoes.add(buttonIot);
        return gson.toJson(botoes);
    }

    public Iot gerarIot(Status st,String iotDst,Integer buttonId){
        Iot iot = new Iot();
        iot.setId("0");
        iot.setName(iotDst);
        iot.setjSon(gerarBotoesJson(st,buttonId,iotDst));
        return iot;
    }

    public String gerarConectorJson(Status sEnvio,Status st,String iotDst,Integer buttonId){
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        Conector conector = new Conector();
        conector.setIot(gerarIot(st,iotDst,buttonId));
        conector.setId("0");
        conector.setTipo(TipoIOT.HUMAN);
        conector.setNome("ControleCelularMarcelo");
        conector.setSenha("M@r0403");
        conector.setUsuario("Matinhos");
        conector.setStatus(sEnvio);
       return gson.toJson(conector);
    }

    public void trataRetorno(String jSonRetorno){
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        Conector conector = gson.fromJson(jSonRetorno, Conector.class);




        if(conector != null && conector.getStatus() != null && conector.getStatus().equals(Status.RETORNOTRANSITORIO)) {
            if(conector.getMens().getSt().equals(Status.SUCESSO)){
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),conector.getMens().getMens(), Toast.LENGTH_LONG).show());


                //Toast.makeText(getApplicationContext(),conector.getMens().getMens(), Toast.LENGTH_LONG).show();
            }
            else if(conector.getMens().getSt().equals(Status.ERRO)){
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),conector.getMens().getMens(), Toast.LENGTH_LONG).show());
                //Toast.makeText(getApplicationContext(),conector.getMens().getMens(), Toast.LENGTH_LONG).show();
            }
        }
        else if(conector != null && conector.getStatus() != null && conector.getStatus().equals(Status.RETORNO)) {
            if(conector.getIot().getjSon()!=null) {

                ButtonIot buttonIot = null;
                try{
                    buttonIot = gson.fromJson(conector.getIot().getjSon(), ButtonIot.class);
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }


                if (buttonIot != null && buttonIot.getFuncao() != null) {

                    if(buttonIot.getFuncao().equals(Status.ALEATORIOON)) {
                        if(buttonIot.getjSon()!=null){
                          Temporizador temporizador = gson.fromJson(buttonIot.getjSon(),Temporizador.class);
                            //inicioTimer.setText("Hora de início do Timer: "+ temporizador.getHoraTemporizador());
                            //duracao.setText("Duração do Timer: "+String.valueOf(temporizador.getDuracao()));
                            //btTemporizador.setBackgroundColor(Color.parseColor("#00ff00"));
                          bTemporizadorLigador = true;
                        }
                    }
                    else if(buttonIot.getFuncao().equals(Status.ALEATORIOOFF)) {
                        if(buttonIot.getjSon()!=null){
                            //inicioTimer.setText("Timer desativado...");
                            //duracao.setText("");
                            //btTemporizador.setBackgroundColor(Color.parseColor("#ffffff"));
                            bTemporizadorLigador = false;
                        }
                    }
                    else  if(buttonIot.getFuncao().equals(Status.ACIONARBOTAO)) {

                    }
                    else  if(buttonIot.getFuncao().equals(Status.ALEATORIOINFO)) {
                        if(buttonIot.getjSon()!=null){
                            Temporizador temporizador = gson.fromJson(buttonIot.getjSon(),Temporizador.class);
                            if(temporizador.isAtivarAleatorio()) {
                                //inicioTimer.setText("Hora de início do Timer: "+ temporizador.getHoraTemporizador());
                                //duracao.setText("Duração do Timer: "+String.valueOf(temporizador.getDuracao()));
                                //btTemporizador.setBackgroundColor(Color.parseColor("#00ff00"));
                                bTemporizadorLigador = true;
                            }
                            else {
                                //inicioTimer.setText("Timer desativado...");
                                //duracao.setText("");
                                //btTemporizador.setBackgroundColor(Color.parseColor("#ffffff"));
                                bTemporizadorLigador = false;
                            }

                        }
                    }
                    else if(buttonIot.getFuncao().equals(Status.READ)) {
                        if (buttonIot.getStatus() != null) {

                            Status statusLocal = buttonIot.getStatus();
                            if (statusLocal.toString().equals("OFF")) {
                                if(conector.getIot().getName().equals("CasaMatinhos_Interno")){
                                    if(buttonIot.getButtonID() == 1){
                                        IbOnOff2.setBackgroundColor(Color.parseColor("#ffffff"));
                                        status1 = buttonIot.getStatus();
                                    }
                                    else if(buttonIot.getButtonID() == 2){
                                        IbOnOff3.setBackgroundColor(Color.parseColor("#ffffff"));
                                        status2 = buttonIot.getStatus();
                                    }
                                }
                                else {
                                    IbOnOff.setBackgroundColor(Color.parseColor("#ffffff"));
                                    status = buttonIot.getStatus();
                                }
                            }
                            else {
                                if(conector.getIot().getName().equals("CasaMatinhos_Interno")){
                                    if(buttonIot.getButtonID() == 1){
                                        IbOnOff2.setBackgroundColor(Color.parseColor("#00ff00"));
                                        status1 = buttonIot.getStatus();
                                    }
                                    else if(buttonIot.getButtonID() == 2){
                                        IbOnOff3.setBackgroundColor(Color.parseColor("#00ff00"));
                                        status2 = buttonIot.getStatus();
                                    }
                                }
                                else {
                                    IbOnOff.setBackgroundColor(Color.parseColor("#00ff00"));
                                    status = buttonIot.getStatus();
                                }
                            }
                        }
                    }
                    else if(buttonIot.getFuncao().equals(Status.GETVALUE)) {
                        Toast.makeText(getApplicationContext(), buttonIot.getjSon(), Toast.LENGTH_LONG).show();
                    }
                    //runOnUiThread(() ->textView.setText("ID:"+conector.getId()));
                }
            }
        }
    }

    public void envia(String textJson){
        try {
            InetAddress serverEnd = InetAddress.getByName("rasp4msmariano.dynv6.net");
            //InetAddress serverEnd = InetAddress.getByName("34.69.67.128");
            //InetAddress serverEnd = InetAddress.getByName("192.168.0.103");
            Socket socket = new Socket(serverEnd,27015);
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream())), true);
            out.println(textJson);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));


            String ret = in.readLine();
            ret = ret.trim();
            int contador = 0,inicio = 0;
            for(int i =0; i < ret.length();i++) {
                if(ret.charAt(i) == '{') {
                    contador++;
                }
                else if(ret.charAt(i) == '}') {
                    contador--;
                }
                if(contador == 0 && i != 0) {
                    trataRetorno(ret.substring(inicio, i+1));
                    inicio = i+1;
                }
            }




            socket.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("NETWORK-RECEIVE", "Something goes wrong: IOException", e);
        }
    }
}