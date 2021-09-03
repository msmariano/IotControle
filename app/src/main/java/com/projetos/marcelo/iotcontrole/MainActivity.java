package com.projetos.marcelo.iotcontrole;

import androidx.appcompat.app.AppCompatActivity;

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
        botoes = new ArrayList<ControleBotao>();
        ConfigBotao cfg = new ConfigBotao();
        cfg.setAct(this);
        cfg.setNomeIot("CasaMatinhos_Interno");
        cfg.setNomeIOTCom("CelularMarcelo");
        cfg.setServidor("192.168.0.254");
        cfg.setPortaServidor(27015);
        cfg.setUsuario("Matinhos");
        cfg.setSenha("M@r0403");
        cfg.setLog(null);
        cfg.setLinear((LinearLayout) findViewById(R.id.lLayout));
        for(int i=0 ;i < nomes.length;i++){
            ControleBotao botao = new ControleBotao(nomes[i],cfg,i+1);
            botoes.add(botao);
        }
    }


    public void buscar(View view) {
        for (ControleBotao botao : botoes) {
            botao.atualizar();
        }
    }
}