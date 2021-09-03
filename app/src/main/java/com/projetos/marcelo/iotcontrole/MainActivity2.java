package com.projetos.marcelo.iotcontrole;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    EditText edEndServidor;
    EditText edPortaServidor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        edEndServidor = findViewById(R.id.endServidor);
        edEndServidor.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    List<Parametro> parametros = Parametro.findWithQuery(Parametro.class,"SELECT * FROM Parametro WHERE parametro = 'EndServidor'");
                    if(parametros.size()>0) {
                        parametros.get(0).setCampo1(edEndServidor.getText().toString());
                        parametros.get(0).save();
                    }
                    else{
                        Parametro parametro = new Parametro("EndServidor",edEndServidor.getText().toString(),"");
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
                    List<Parametro> parametros = Parametro.findWithQuery(Parametro.class,"SELECT * FROM Parametro WHERE parametro = 'EndServidor'");
                    if(parametros.size()>0) {
                        parametros.get(0).setCampo1(edEndServidor.getText().toString());
                        parametros.get(0).setCampo2(edPortaServidor.getText().toString());
                        parametros.get(0).save();
                    }
                    else{
                        Parametro parametro = new Parametro("EndServidor",edEndServidor.getText().toString(),edPortaServidor.getText().toString());
                        parametro.save();
                    }
                    return true;
                }
                return false;
            }
        });



        List<Parametro> parametros = Parametro.findWithQuery(Parametro.class,"SELECT * FROM Parametro");
        for (Parametro parametro : parametros) {
            if (parametro.getParametro().equals("EndServidor")) {
                if(parametro.getCampo1()!=null&&parametro.getCampo1().trim().length()>0) {
                    edEndServidor.setText( parametro.getCampo1());
                }
                if(parametro.getCampo2()!=null&&parametro.getCampo2().trim().length()>0) {
                   edPortaServidor.setText(parametro.getCampo2());
                }
            }
        }
    }
}