package com.projetos.marcelo.iotcontrole;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.GsonBuilder;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter  implements IMqttMessageListener {

    private Context context;
    ClienteMQTT clienteMQTT;
    private final List<Dispositivo> dispositivos;
    private final String uuid;
    private final AppCompatActivity activity;

    public CustomAdapter(Context context, List<Dispositivo> disps, ClienteMQTT clienteMQTTArg, String uuidArg, AppCompatActivity activity) {
        clienteMQTT = clienteMQTTArg;
        this.context = context;
        dispositivos = disps;
        uuid = uuidArg;
        this.activity = activity;
    }
    @Override
    public int getCount() {
        return dispositivos.size();
    }

    public void atualizaEstadoDispositivo(List<Dispositivo> dsps, String idpool) {
        for (Dispositivo dspAtualizar : dsps) {
            for (Dispositivo dispositivo : dispositivos) {
                if (dispositivo.getId().equals(dspAtualizar.getId()) && dispositivo.getIdpool().equals(idpool)) {
                    if (!dispositivo.getStatus().equals(dspAtualizar.getStatus()))
                    {
                        dispositivo.setStatus(dspAtualizar.getStatus());
                        activity.runOnUiThread(this::notifyDataSetChanged);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public Object getItem(int position) {
        return dispositivos.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void putImagem(Dispositivo dispositivo, View view, Button button) {
        Drawable img = null;

        if(button.getText().toString().trim().toLowerCase().contains("portão")){
            img = view.getResources().getDrawable(R.drawable.pushbutton);
            if(dispositivo.getStatus().equals(Status.PUSHOFF))
                button.setEnabled(true);
        }
        else  if(button.getText().toString().trim().toLowerCase().contains("nivel")){
            img = (dispositivo.getStatus().equals(Status.ON) && dispositivo.getNivelAcionamento().equals(Status.HIGH)) ||
                    (dispositivo.getStatus().equals(Status.OFF) && dispositivo.getNivelAcionamento().equals(Status.LOW))
                    ? view.getResources().getDrawable(R.drawable.notificacao)
                    : view.getResources().getDrawable(R.drawable.semnotificacao);
            button.setEnabled(false);
        }
        else {
            switch (dispositivo.getGenero().getValor()) {
                case 1:
                    img = (dispositivo.getStatus().equals(Status.ON) && dispositivo.getNivelAcionamento().equals(Status.HIGH)) ||
                            (dispositivo.getStatus().equals(Status.OFF) && dispositivo.getNivelAcionamento().equals(Status.LOW))
                            ? view.getResources().getDrawable(R.drawable.lacessa)
                            : view.getResources().getDrawable(R.drawable.b983w);
                    break;
                case 15:
                    img = view.getResources().getDrawable(R.drawable.pushbutton);
                    break;
                case 11:
                default:
                    img = (dispositivo.getStatus().equals(Status.ON) && dispositivo.getNivelAcionamento().equals(Status.HIGH)) ||
                            (dispositivo.getStatus().equals(Status.OFF) && dispositivo.getNivelAcionamento().equals(Status.LOW))
                            ? view.getResources().getDrawable(R.drawable.intligado)
                            : view.getResources().getDrawable(R.drawable.intdesligado);
                    break;
            }
        }

        img.setBounds(0, 0, 60, 60);
        button.setBackgroundColor(Color.rgb(255, 255, 255));
        button.setCompoundDrawables(img, null, null, null);
        button.refreshDrawableState();
        //activity.runOnUiThread(() -> Toast.makeText(activity, button.getText().toString() + " " + dispositivo.getStatus(), Toast.LENGTH_LONG).show());
        dispositivo.setImg(img);
    }
    @SuppressLint({"UseCompatLoadingForDrawables", "InflateParams", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.activity_list_view, null);
        TextView txt = convertView.findViewById(R.id.txt);
        txt.setText(dispositivos.get(position).getNickServidor());
        Button button = convertView.findViewById(R.id.btn);
        button.setText(dispositivos.get(position).getNick());
        putImagem(dispositivos.get(position), convertView, button);
        dispositivos.get(position).setButton(button);
        dispositivos.get(position).setView(convertView);
        View finalConvertView = convertView;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dispositivo dispositivo = dispositivos.get(position);
                List<Pool> pools = new ArrayList<>();
                Pool pool = new Pool();
                pool.setOrigemID(uuid);
                pool.setId(dispositivo.getIdpool());
                pool.setDispositivos(new ArrayList<>());
                pool.getDispositivos().add(dispositivo);
                pools.add(pool);
                if (dispositivo.getStatus().equals(Status.ON))
                    dispositivo.setStatus(Status.OFF);
                else
                    dispositivo.setStatus(Status.ON);
                dispositivo.setStatus(dispositivo.getNick().trim().toLowerCase().contains("portão")
                        ? Status.PUSHON : dispositivo.getStatus());
                if(button.getText().toString().trim().toLowerCase().contains("portão"))
                    button.setEnabled(false);
                putImagem(dispositivo, finalConvertView, button);
                button.setPressed(true);
                String jSon = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("dd/MM/yyyy HH:mm:ss").create().toJson(pools);
                clienteMQTT.publicar("br/com/neuverse/servidores/" + pool.getId() + "/atualizar", jSon.getBytes(), 0);

            }
        });
        return convertView;
    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }
}