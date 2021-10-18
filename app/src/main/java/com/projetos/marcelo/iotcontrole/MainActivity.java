package com.projetos.marcelo.iotcontrole;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.projetos.marcelo.iotcontrole.ui.login.LoginActivity;

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
            Iterator<Configuracao> configs = Configuracao.findAll(Configuracao.class);
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
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}