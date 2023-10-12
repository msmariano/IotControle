package com.projetos.marcelo.iotcontrole;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class MainActivity extends AppCompatActivity implements IMqttMessageListener {

    SQLiteDatabase mydatabase;
    AppCompatActivity activity;
    ListView simpleList;
    List<Dispositivo> dispositivos = new ArrayList<>();
    ClienteMQTT clienteMQTT;
    boolean inserido = false;
    boolean inicializado = false;
    public CustomAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Controle");
        activity = this;
        //calendar.setTime(new Date());
        FloatingActionButton btCfg = findViewById(R.id.bCfg);
        btCfg.setOnClickListener(view -> {});
        inicializarMqtt();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private SSLContext getSslContext(Context context) throws Exception {
        // Charger le certificat CA
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = context.getResources().openRawResource(R.raw.ca);
        X509Certificate caCertificate;
        try {
            caCertificate = (X509Certificate) cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", caCertificate);

        // Créer un TrustManager qui fait confiance au keystore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);



        // Créer un KeyManager à partir du keystore du client
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, null);

        // Créer un SSLContext qui utilise notre TrustManager et KeyManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext;
    }

    public void inicializarMqtt(){
        try {
            if (!inicializado) {
                inicializado = true;

                SSLContext sslContext;
                sslContext = getSslContext(activity);




                UUID uniqueKey = UUID.randomUUID();
                String idGerado = uniqueKey.toString();
                StrictMode.ThreadPolicy gfgPolicy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(gfgPolicy);
                clienteMQTT = new ClienteMQTT("ssl://73cd8514e7c447ff91d697b4b02f88c5.s1.eu.hivemq.cloud:8883","neuverse1","M@r040370");
                clienteMQTT.mqttOptions.setSocketFactory(sslContext.getSocketFactory());
                clienteMQTT.iniciar();

                clienteMQTT.subscribe(0, this, "br/com/neuverse/servidores/events");
                clienteMQTT.subscribe(0, this, "br/com/neuverse/geral/lista");
                simpleList = (ListView) findViewById(R.id.simpleListView);

                clienteMQTT.publicar("br/com/neuverse/geral/info", idGerado.getBytes(), 1);
                arrayAdapter = new CustomAdapter(activity, dispositivos);
                simpleList.setAdapter(arrayAdapter);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println(new String(message.getPayload()));
        if (topic.equals("br/com/neuverse/geral/lista")) {
            acrescentarServidorIOT(new String(message.getPayload()));
        } else if (topic.equals("br/com/neuverse/servidores/events")) {
            handleEvent(new String(message.getPayload()));
        }
    }

    public synchronized void handleEvent(String json) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        Type listType = new TypeToken<ArrayList<Pool>>() {
        }.getType();
        List<Pool> pools = gson.fromJson(json, listType);
        for (Pool pool : pools) {
            for (Dispositivo dispositivo : pool.getDispositivos()) {
                Button btn = arrayAdapter.getItemByIdPoolIdDisp(dispositivo.getNick());
                if (btn != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn.setVisibility(View.VISIBLE);
                            Drawable img;
                            if (dispositivo.getStatus().equals(Status.ON)) {
                                if(dispositivo.getNivelAcionamento().equals(Status.HIGH)){
                                    if (!dispositivo.getNick().toString().toLowerCase().contains("luz"))
                                        img = activity.getResources().getDrawable(R.drawable.intligado);
                                    else
                                        img = activity.getResources().getDrawable(R.drawable.lacessa);
                                }
                                else{
                                    if (!dispositivo.getNick().toString().toLowerCase().contains("luz"))
                                        img = activity.getResources().getDrawable(R.drawable.intdesligado);
                                    else
                                        img = activity.getResources().getDrawable(R.drawable.b983w);
                                }

                            } else {
                                if(dispositivo.getNivelAcionamento().equals(Status.LOW)){
                                    if (!dispositivo.getNick().toString().toLowerCase().contains("luz"))
                                        img = activity.getResources().getDrawable(R.drawable.intligado);
                                    else
                                        img = activity.getResources().getDrawable(R.drawable.lacessa);
                                }
                                else{
                                    if (!dispositivo.getNick().toString().toLowerCase().contains("luz"))
                                        img = activity.getResources().getDrawable(R.drawable.intdesligado);
                                    else
                                        img = activity.getResources().getDrawable(R.drawable.b983w);
                                }

                            }
                            img.setBounds(0, 0, 60, 60);
                            btn.setCompoundDrawables(img, null, null, null);
                        }
                    });
                }
            }
        }
    }

    public synchronized void acrescentarServidorIOT(String jSon) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                    Type listType = new TypeToken<ArrayList<Pool>>() {
                    }.getType();
                    List<Pool> pools = gson.fromJson(jSon, listType);
                    for (Pool pool : pools) {
                        for (Dispositivo dispositivo : pool.getDispositivos()) {
                            dispositivo.setIdpool(pool.getId());
                            if (pool.getNick() != null && pool.getNick().trim().length() > 0)
                                dispositivo.setNickServidor(pool.getNick());
                            else
                                dispositivo.setNickServidor("Sem nome");
                            boolean naLista = false;
                            for (Dispositivo dsp : dispositivos) {
                                if (dsp.getIdpool().equals(pool.getId()) && dsp.getId().equals(dispositivo.getId())) {
                                    naLista = true;
                                    break;
                                }
                            }
                            if (!naLista)
                                dispositivos.add(dispositivo);
                        }
                    }
                    activity.runOnUiThread(() -> arrayAdapter.notifyDataSetChanged());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String lerCfgServidor() {
        String fileName = "Cfg";
        FileInputStream inputStream = null;
        String s = "";
        try {
            inputStream = openFileInput(fileName);

            int i = inputStream.read();
            if (i != -1)
                s = s + (char) i;
            while (i != -1) {
                i = inputStream.read();
                if (i != -1)
                    s = s + (char) i;
            }
        } catch (Exception e) {
        }
        return s;
    }

    public void salvarCfgServidor(String endServidor) {
        String fileName = "Cfg";
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(endServidor.getBytes());
            outputStream.close();
        } catch (Exception e) {
            ;
        }
    }

}