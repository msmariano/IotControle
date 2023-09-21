package com.projetos.marcelo.iotcontrole;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
    List<DispositivoButton> listaCb = new ArrayList<>();

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
            rest.setIp("192.168.18.58");
            rest.setPorta("27016");
            rest.setUri("/ServidorIOT/listar");
            try {
                String jSon = rest.sendRest("");
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                Type listType = new TypeToken<ArrayList<Pool>>(){}.getType();
                List<Pool> listaConectores = gson.fromJson(jSon,listType);
                for (Pool con:listaConectores) {
                    for(Dispositivo dispositivo : con.getDispositivos()){
                        DispositivoButton dispbutton = null;
                        for(DispositivoButton ctrl : listaCb){
                            if(dispositivo.getId().equals(ctrl.getDipositivoId())
                                &&con.getId().equals(ctrl.getPoolId())){
                                dispbutton = ctrl;
                                break;
                            }
                        }

                        if(dispbutton==null) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(100, 50, 100, 50);
                            Button btn = new Button(this);
                            btn.setId(dispositivo.getId());
                            final int id_ = btn.getId();
                            btn.setTooltipText(dispositivo.getNick());
                            Drawable img = this.getResources().getDrawable(R.drawable.b983w);
                            img.setBounds(0, 0, 60, 60);
                            btn.setCompoundDrawables(img, null, null, null);
                            btn.setBackgroundColor(Color.rgb(255, 255, 255));
                            btn.setText(dispositivo.getNick());
                            LinearLayout linear = findViewById(R.id.lLayout);
                            linear.addView(btn, params);
                            btn = this.findViewById(id_);
                            dispbutton = new DispositivoButton();
                            dispbutton.setBtn(btn);
                            dispbutton.setDipositivoId(dispositivo.getId());
                            dispbutton.setPoolId(con.getId());
                            dispbutton.setDispositivo(dispositivo);
                            listaCb.add(dispbutton);
                            btn.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                    Button btnParent = (Button) view;
                                    for (DispositivoButton dispbutton : listaCb){
                                        if(dispbutton.getBtn().equals(btnParent)){
                                            List<Pool> listaPool = new ArrayList<>();
                                            Pool pool = new Pool();
                                            pool.setId(dispbutton.getPoolId());
                                            pool.setDispositivos(new ArrayList<>());
                                            pool.getDispositivos().add(dispbutton.getDispositivo());
                                            listaPool.add(pool);
                                            if(dispbutton.getDispositivo().getStatus().equals(Status.ON)){
                                                dispbutton.getDispositivo().setStatus(Status.OFF);
                                            }
                                            else
                                                dispbutton.getDispositivo().setStatus(Status.ON);
                                            String jSon = gson.toJson(listaPool);
                                            System.out.println(jSon);
                                            rest.setUri("/ServidorIOT/atualizar");
                                            try {
                                                jSon = rest.sendRest(jSon);
                                            } catch (Exception e) {
                                                System.out.println(e.getMessage());
                                            }
                                            break;
                                        }
                                    }
                                }
                            });
                        }
                        AppCompatActivity act = this;
                        Drawable img;
                        if(dispositivo.getStatus().equals(Status.ON)){
                            img = act.getResources().getDrawable(R.drawable.lacessa);
                        }
                        else{
                            img = act.getResources().getDrawable(R.drawable.b983w);
                        }
                        img.setBounds(0, 0, 60, 60);
                        dispbutton.getBtn().setCompoundDrawables(img, null, null, null);

                    }
                }

                /*Type listType = new TypeToken<ArrayList<Conector>>(){}.getType();
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
                }*/
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