package com.projetos.marcelo.iotcontrole;

import static com.google.common.collect.ComparisonChain.start;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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

public class MainActivity2 extends AppCompatActivity {

    EditText edEndServidor;
    EditText edPortaServidor;
    ListView lvOptIots;
    List<String> opcoes;
    ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        lvOptIots = findViewById(R.id.lvOpIots);
        opcoes = new ArrayList<String>();
        opcoes.add("Selecione...");
        List<Parametro> optIots = Parametro.findWithQuery(Parametro.class, "SELECT * FROM Parametro WHERE parametro = 'NomeIot'");
        if(optIots.size()>0){
            for(Parametro p : optIots){
                opcoes.add(p.getCampo1());
            }
        }

        adaptador = new ArrayAdapter<String>(MainActivity2.this, android.R.layout.simple_list_item_1, opcoes);
        lvOptIots.setAdapter(adaptador);
        lvOptIots.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Parametro> parametros = Parametro.findWithQuery(Parametro.class, "SELECT * FROM Parametro WHERE parametro = 'NomeIotSel'");
                if (parametros.size() > 0) {
                    if(position>1) {
                        parametros.get(0).setCampo1(opcoes.get(position));
                        parametros.get(0).save();
                    }
                } else {
                    if(position>1) {
                        Parametro parametro = new Parametro("NomeIotSel", opcoes.get(position), "");
                        parametro.save();
                    }
                }

            }
        });

        edEndServidor = findViewById(R.id.endServidor);
        edEndServidor.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    List<Parametro> parametros = Parametro.findWithQuery(Parametro.class, "SELECT * FROM Parametro WHERE parametro = 'EndServidor'");
                    if (parametros.size() > 0) {
                        parametros.get(0).setCampo1(edEndServidor.getText().toString());
                        parametros.get(0).save();
                    } else {
                        Parametro parametro = new Parametro("EndServidor", edEndServidor.getText().toString(), "");
                        parametro.save();
                    }
                    return true;
                }
                return false;
            }
        });

        edPortaServidor = findViewById(R.id.edPortaServidor);
        edPortaServidor.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    List<Parametro> parametros = Parametro.findWithQuery(Parametro.class, "SELECT * FROM Parametro WHERE parametro = 'EndServidor'");
                    if (parametros.size() > 0) {
                        parametros.get(0).setCampo1(edEndServidor.getText().toString());
                        parametros.get(0).setCampo2(edPortaServidor.getText().toString());
                        parametros.get(0).save();
                    } else {
                        Parametro parametro = new Parametro("EndServidor", edEndServidor.getText().toString(), edPortaServidor.getText().toString());
                        parametro.save();
                    }
                    //Preencher lista Iot
                    try {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    if (edPortaServidor.getText().toString().trim().length() > 0
                                            && edEndServidor.getText().toString().trim().length() > 0) {
                                        List<String> iots = retornaIots(edEndServidor.getText().toString(),
                                                Integer.parseInt(edPortaServidor.getText().toString()));
                                        if (iots != null) {
                                            opcoes.clear();
                                            opcoes.add("Selecione...");
                                            opcoes.addAll(iots);
                                            Parametro incOptIot = new Parametro();
                                            Parametro.executeQuery("DELETE FROM Parametro WHERE parametro = 'NomeIot'");
                                            for(String opt : opcoes){
                                                incOptIot.setParametro("NomeIot");
                                                incOptIot.setCampo1(opt);
                                                incOptIot.save();
                                            }

                                        }
                                    }
                                }
                                catch (Exception e){
                                }
                            }
                        }.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });


        List<Parametro> parametros = Parametro.findWithQuery(Parametro.class, "SELECT * FROM Parametro");
        for (Parametro parametro : parametros) {
            if (parametro.getParametro().equals("EndServidor")) {
                if (parametro.getCampo1() != null && parametro.getCampo1().trim().length() > 0) {
                    edEndServidor.setText(parametro.getCampo1());
                }
                if (parametro.getCampo2() != null && parametro.getCampo2().trim().length() > 0) {
                    edPortaServidor.setText(parametro.getCampo2());
                }
            }
        }
    }

    List<String> retornaIots(String endServidor, Integer portaServidor) throws IOException {

        InetAddress serverEnd = InetAddress.getByName(endServidor);
        Socket socket = new Socket(serverEnd, portaServidor);
        PrintWriter out = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream())), true);

        out.println("{\"status\":\"LISTA_IOT\"}");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        String ret = in.readLine();
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        List<String> lista = gson.fromJson(ret, List.class);

        return lista;
    }
}