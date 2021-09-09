package com.projetos.marcelo.iotcontrole;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<ControleBotao> botoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String nomes[] = {"Garagem","Cozinha"};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Controle Marcelo");
        Button btCfg = findViewById(R.id.bCfg);
        btCfg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        List<Parametro> parametros = Parametro.findWithQuery(Parametro.class,"SELECT * FROM Parametro");
        if(parametros.size()>0) {
            botoes = new ArrayList<ControleBotao>();
            ConfigBotao cfg = new ConfigBotao();
            Integer tParametros = 0;
            for (Parametro parametro : parametros) {
                if (parametro.getParametro().equals("EndServidor")) {
                    if(parametro.getCampo1()!=null&&parametro.getCampo1().trim().length()>0) {
                        cfg.setServidor(parametro.getCampo1());
                        tParametros++;
                    }
                    if(parametro.getCampo2()!=null&&parametro.getCampo2().trim().length()>0) {
                        try {
                            cfg.setPortaServidor (Integer.parseInt(parametro.getCampo2())) ;
                            tParametros++;
                        }
                        catch (Exception e){
                        }
                    }
                }
                if (parametro.getParametro().equals("NomeIotSel")) {
                    cfg.setNomeIot(parametro.getCampo1());
                    tParametros++;
                }
            }

            if(tParametros==3) {
                cfg.setNomeIot("CasaMatinhos_Interno");
                cfg.setNomeIOTCom("CelularMarcelo");
                cfg.setUsuario("Matinhos");
                cfg.setSenha("M@r0403");
                for (int i = 0; i < nomes.length; i++) {
                    ControleBotao botao = new ControleBotao(nomes[i], cfg, i + 1,this,
                            (LinearLayout) findViewById(R.id.lLayout));
                    botoes.add(botao);
                }
            }
        }
    }


    public void buscar(View view) {
        for (ControleBotao botao : botoes) {
            botao.atualizar();
        }
    }
}