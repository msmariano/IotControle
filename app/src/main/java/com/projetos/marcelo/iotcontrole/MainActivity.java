package com.projetos.marcelo.iotcontrole;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.projetos.marcelo.iotcontrole.ui.login.LoginActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase mydatabase;
    List<ControleBotao> listaCb = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Controle");
        FloatingActionButton btCfg = findViewById(R.id.bCfg);
        btCfg.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        //Configuracao.deleteAll(Configuracao.class);

        mydatabase = openOrCreateDatabase("cfg.db", MODE_PRIVATE, null);
        //mydatabase.execSQL("DROP tABLE IF EXISTS PARAMETRO");
        //mydatabase.execSQL("DROP TABLE IF EXISTS CONFIGURACAO");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Configuracao(ID INTEGER,SERVIDOR VARCHAR," +
                "PORTASERVIDOR INTEGER,USUARIO VARCHAR,SENHA VARCHAR,NOMEIOTCOM VARCHAR," +
                "NOMEIOT VARCHAR, NOMEBOTAO VARCHAR,IDBOTAO INTEGER );");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS PARAMETRO(ID INTEGER,PARAMETRO VARCHAR,CAMPO1 VARCHAR,CAMPO2 VARCHAR);");
        //Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        //startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            //ControleRest controleRest = new ControleRest();
            //controleRest.setIp("192.168.0.10");


            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
            Rest rest = new Rest();
            rest.setIp("192.168.0.254");
            rest.setPorta("8080");
            rest.setUri("/ServidorIOT/listarIOTs");
            try {
                String jSon = rest.sendRest("");
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                Type listType = new TypeToken<ArrayList<Conector>>(){}.getType();
                List<Conector> listaConectores = gson.fromJson(jSon,listType);
                for (Conector con:listaConectores) {
                    for(ButtonIot bIot : con.getButtons()){
                        ControleBotao cb = null;
                        Boolean iInserir = Boolean.TRUE;
                        for(ControleBotao ctrl : listaCb){
                            if(bIot.getButtonID().equals(ctrl.getButtonID())
                                && con.getIdConector().equals(ctrl.getIdConector())
                                && con.getNome().equals(ctrl.getNomeServidor())){
                                iInserir = Boolean.FALSE;
                                cb = ctrl;
                                break;
                            }
                        }


                        Configuracao cfg = new Configuracao();
                        cfg.setServidor("192.168.0.254");
                        cfg.setPortaservidor(27015);

                        if(cb==null) {
                            cb = new ControleBotao(bIot.getNick(), cfg, bIot.getButtonID(), this,
                                    findViewById(R.id.lLayout));
                            cb.setConectores(listaConectores);
                            cb.comm(con);
                            if(iInserir)
                                listaCb.add(cb);
                        }
                        cb.setListaCbGlobal(listaCb);
                        cb.setStatus(bIot.getStatus());
                        cb.setIdConector(con.getIdConector());
                        cb.setNomeServidor(con.getNome());
                        cb.setNomeIot(con.getIot().getName());
                        cb.setServidor("192.168.0.254");
                        cb.setPorta(8080);
                        if(bIot.getStatus().equals(Status.ON))
                            cb.atualizaImagemLigada();
                        else
                            cb.atualizaImagemDesLigada();


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }





            /*Iterator<Configuracao> configs = Configuracao.findAll(Configuracao.class);
            while (configs.hasNext()) {
                Configuracao cfg = configs.next();
                boolean isCriar = true;
                for (ControleBotao cb : listaCb) {
                    if (cfg.getIdbotao().equals(cb.getButtonID())) {
                        cb.setNome(cfg.getNomebotao());
                        cb.getBtn().setText(cfg.getNomebotao());
                        cb.getCfg().setServidor(cfg.getServidor());
                        cb.getCfg().setPortaservidor(cfg.getPortaservidor());
                        cb.getCfg().setUsuario(cfg.getUsuario());
                        cb.getCfg().setSenha(cfg.getSenha());
                        isCriar = false;
                        break;
                    }
                }
                if (isCriar) {
                    listaCb.add(new ControleBotao(cfg.getNomebotao(), cfg, cfg.getIdbotao(), this,
                            findViewById(R.id.lLayout)));
                }
            }*/
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}