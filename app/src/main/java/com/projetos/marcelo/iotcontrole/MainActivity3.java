package com.projetos.marcelo.iotcontrole;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Intent it = getIntent();
        List<Integer> ids =   it.getIntegerArrayListExtra("idBotoes");
        LinearLayout linear = findViewById(R.id.lvoptbt);

        for(Integer id : ids) {

            Button btn;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(100, 50, 100, 50);
            btn = new Button(this);
            btn.setId(id);
            final int id_ = btn.getId();
            btn.setText("button_ID: " +id);
            btn.setBackgroundColor(Color.rgb(255, 255, 255));
            linear.addView(btn, params);
            btn = ((Button) this.findViewById(id_));
        }
    }
}