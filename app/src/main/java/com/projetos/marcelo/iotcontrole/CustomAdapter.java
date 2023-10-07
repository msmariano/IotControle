package com.projetos.marcelo.iotcontrole;

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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CustomAdapter  extends BaseAdapter {

    private Context context;

    private List<DispositivoButton> listaCb = new ArrayList<>();

    private List<Dispositivo> dispositivos;

    public CustomAdapter(Context context, List<Dispositivo> disps ) {

        this.context = context;
        dispositivos = disps;
        monitora();
    }

    public void monitora(){

    }
    @Override
    public int getCount() {
        return dispositivos.size();
    }

    @Override
    public Object getItem(int position) {
        return dispositivos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_list_view, null);
        }

        //Handle TextView and display string from your list
        TextView tvContact= (TextView)view.findViewById(R.id.tvContact);
        //tvContact.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button callbtn= (Button)view.findViewById(R.id.btn);
        DispositivoButton dispbutton = new DispositivoButton();
        dispbutton.setBtn(callbtn);
        dispbutton.setDipositivoId(dispositivos.get(position).getId());
        dispbutton.setPoolId(dispositivos.get(position).getIdpool());
        dispbutton.setDispositivo(dispositivos.get(position));
        listaCb.add(dispbutton);
        Drawable img ;
        callbtn.setBackgroundColor(Color.rgb(255, 255, 255));
        callbtn.setText(dispositivos.get(position).getNick());
        if(dispositivos.get(position).getStatus().equals(Status.ON)){
            img = view.getResources().getDrawable(R.drawable.lacessa);
        }
        else {
            img = view.getResources().getDrawable(R.drawable.b983w);
        }
        img.setBounds(0, 0, 60, 60);
        callbtn.setCompoundDrawables(img, null, null, null);
        callbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button btnParent = (Button) v;
                /*Rest rest = new Rest();
                rest.setIp("192.168.18.58");
                rest.setPorta("27016");*/
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                for (DispositivoButton dispbutton : listaCb){
                    if(dispbutton.getBtn().equals(btnParent)){
                        List<Pool> listaPool = new ArrayList<>();
                        Pool pool = new Pool();
                        pool.setId(dispbutton.getPoolId());
                        pool.setDispositivos(new ArrayList<>());
                        pool.getDispositivos().add(dispbutton.getDispositivo());
                        listaPool.add(pool);
                        Drawable img;
                        if(dispbutton.getDispositivo().getStatus().equals(Status.ON)){
                            dispbutton.getDispositivo().setStatus(Status.OFF);
                            img = context.getResources().getDrawable(R.drawable.b983w);
                        }
                        else {
                            dispbutton.getDispositivo().setStatus(Status.ON);
                            img = context.getResources().getDrawable(R.drawable.lacessa);
                        }
                        img.setBounds(0, 0, 60, 60);
                        btnParent.setCompoundDrawables(img, null, null, null);
                        String jSon = gson.toJson(listaPool);
                        System.out.println(jSon);


                        ClienteMQTT clienteMQTTSend = new ClienteMQTT("tcp://broker.mqttdashboard.com:1883", "neuverse",
                                "M@r040370");
                        clienteMQTTSend.iniciar();
                        clienteMQTTSend.publicar("br/com/neuverse/servidores/" + pool.getId() + "/atualizar", jSon.getBytes(), 0);
                        clienteMQTTSend.finalizar();

                        break;
                    }
                }

            }
        });


        return view;

    }
}
