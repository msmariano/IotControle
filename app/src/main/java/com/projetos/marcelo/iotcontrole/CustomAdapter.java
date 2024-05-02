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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class CustomAdapter  extends BaseAdapter {

    private Context context;

    private List<DispositivoButton> listaCb;

    ClienteMQTT clienteMQTT;

    private List<Dispositivo> dispositivos;

    private String atualNickServidor = "";

    public CustomAdapter(Context context, List<Dispositivo> disps, ClienteMQTT clienteMQTTArg) {
        listaCb = null;
        listaCb = new ArrayList<>();
        clienteMQTT = clienteMQTTArg;
        this.context = context;
        dispositivos = disps;
        monitora();
    }

    public void monitora() {

    }

    @Override
    public int getCount() {
        return dispositivos.size();
    }

    public Button getItemByIdPoolIdDisp(String idpool, Integer id) {

        for(Dispositivo dispositivo : dispositivos){
            if(dispositivo.getId().equals(id) && dispositivo.getIdpool().equals(idpool)){
                return dispositivo.getButton();
            }
        }

        /*for (DispositivoButton dispbutton : listaCb) {
            if (dispbutton.getBtn().getText().equals(nick)) {
                return dispbutton.getBtn();
            }
        }*/
        return null;
    }

    public void addItem(Dispositivo dsp) {

    }

    @Override
    public Object getItem(int position) {
        return dispositivos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Dispositivo disp = null;
        View view = convertView;
        TextView txt = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_list_view, null);
        }
        txt = view.findViewById(R.id.txt);
        txt.setText(dispositivos.get(position).getNickServidor());
        Button callbtn = view.findViewById(R.id.btn);
        dispositivos.get(position).setButtoAddress(callbtn.toString());
        dispositivos.get(position).setButton(callbtn);
        Drawable img;
        callbtn.setBackgroundColor(Color.rgb(255, 255, 255));
        callbtn.setText(dispositivos.get(position).getNick());
        if(dispositivos.get(position).getGenero().equals(TipoIOT.PUSHBUTTON)){
            img = view.getResources().getDrawable(R.drawable.pushbutton);
            //callbtn.setEnabled(true);
        }
        else if(dispositivos.get(position).getGenero().equals(TipoIOT.NOTIFICACAO)){
            img = view.getResources().getDrawable(R.drawable.semnotificacao);
            //callbtn.setEnabled(false);
        }
        else if (dispositivos.get(position).getStatus().equals(Status.ON)) {
            //callbtn.setEnabled(true);
            if (dispositivos.get(position).getNivelAcionamento().equals(Status.HIGH)) {
                if (!dispositivos.get(position).getNick().toLowerCase().contains("luz"))
                    img = view.getResources().getDrawable(R.drawable.intligado);
                else
                    img = view.getResources().getDrawable(R.drawable.lacessa);
            } else {
                if (!dispositivos.get(position).getNick().toLowerCase().contains("luz"))
                    img = view.getResources().getDrawable(R.drawable.intdesligado);
                else
                    img = view.getResources().getDrawable(R.drawable.b983w);
            }
        }
        else {
            if (dispositivos.get(position).getNivelAcionamento().equals(Status.LOW)) {
                if (!dispositivos.get(position).getNick().toLowerCase().contains("luz"))
                    img = view.getResources().getDrawable(R.drawable.intligado);
                else
                    img = view.getResources().getDrawable(R.drawable.lacessa);
            } else {
                if (!dispositivos.get(position).getNick().toLowerCase().contains("luz"))
                    img = view.getResources().getDrawable(R.drawable.intdesligado);
                else
                    img = view.getResources().getDrawable(R.drawable.b983w);
            }

        }
        img.setBounds(0, 0, 60, 60);
        callbtn.setCompoundDrawables(img, null, null, null);
        callbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Dispositivo dispositivo = dispositivos.get(position);
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                List<Pool> pools = new ArrayList<>();
                Pool pool = new Pool();
                pool.setId(dispositivo.getIdpool());
                pool.setDispositivos(new ArrayList<>());
                pool.getDispositivos().add(dispositivo);
                pools.add(pool);
                if (dispositivo.getStatus().equals(Status.ON))
                    dispositivo.setStatus(Status.OFF);
                else
                    dispositivo.setStatus(Status.ON);
                String jSon = gson.toJson(pools);
                clienteMQTT.publicar("br/com/neuverse/servidores/" + pool.getId() + "/atualizar", jSon.getBytes(), 0);
            }
        });
        return view;
    }
}