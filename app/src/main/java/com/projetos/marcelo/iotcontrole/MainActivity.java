package com.projetos.marcelo.iotcontrole;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase mydatabase;
    List<ControleBotao> listaCb = new ArrayList<>();
    private ImageView mProgressBar;
    MainActivity instancia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instancia = this;
        this.setTitle("Controle Marcelo");
        FloatingActionButton btCfg = findViewById(R.id.bCfg);
        btCfg.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        //Configuracao.deleteAll(Configuracao.class);
        mydatabase = openOrCreateDatabase("cfg.db", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Configuracao(ID INTEGER,SERVIDOR VARCHAR," +
                "PORTASERVIDOR INTEGER,USUARIO VARCHAR,SENHA VARCHAR,NOMEIOTCOM VARCHAR," +
                "NOMEIOT VARCHAR, NOMEBOTAO VARCHAR,IDBOTAO INTEGER );");

        mProgressBar = findViewById(R.id.mProgressBar);
        Glide.with(mProgressBar)
                .load(R.drawable.progress)
                .into(mProgressBar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    mProgressBar.setVisibility(View.INVISIBLE);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


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
                        isCriar = false;
                        break;
                    }
                }
                if (isCriar) {
                    listaCb.add(new ControleBotao(cfg.getNomebotao(), cfg, cfg.getIdbotao(), this,
                            (LinearLayout) findViewById(R.id.lLayout)));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}