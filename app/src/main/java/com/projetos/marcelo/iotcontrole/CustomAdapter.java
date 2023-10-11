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

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomAdapter  extends BaseAdapter {

    private Context context;

    private List<DispositivoButton> listaCb;

    private List<Dispositivo> dispositivos;

    private String  atualNickServidor = "";

    public CustomAdapter(Context context, List<Dispositivo> disps ) {
        listaCb = null;
        listaCb = new ArrayList<>();
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

    public Button getItemByIdPoolIdDisp(String nick){
        for (DispositivoButton dispbutton : listaCb){
            if(dispbutton.getBtn().getText().equals(nick)){
                return dispbutton.getBtn();
            }
        }
        return null;
    }

    public void addItem(Dispositivo dsp){

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
        TextView txt = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_list_view, null);


            /*if(!atualNickServidor.equals(dispositivos.get(position).getNickServidor())){
                atualNickServidor = dispositivos.get(position).getNickServidor();
                txt.setText(dispositivos.get(position).getNickServidor());
                txt.setVisibility(View.VISIBLE);
            }
            else {
                txt.setVisibility(View.INVISIBLE);
            }*/

        }
        String texto = dispositivos.get(position).getNickServidor();

        /*try {
            if (!atualNickServidor.equals(dispositivos.get(position).getNickServidor())) {
                atualNickServidor = dispositivos.get(position).getNickServidor();
                if(dispositivos.get(position).getNickServidor()!=null)
                    texto = dispositivos.get(position).getNickServidor();
            }
        }
        catch (Exception e){

        }*/

        txt = view.findViewById(R.id.txt);
        txt.setText(texto);



        //Handle buttons and add onClickListeners
        Button callbtn= view.findViewById(R.id.btn);
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
            if(dispositivos.get(position).getNivelAcionamento().equals(Status.HIGH)){
                if (!dispositivos.get(position).getNick().toString().toLowerCase().contains("luz"))
                    img = view.getResources().getDrawable(R.drawable.intligado);
                else
                    img = view.getResources().getDrawable(R.drawable.lacessa);
            }
            else {
                if (!dispositivos.get(position).getNick().toString().toLowerCase().contains("luz"))
                    img = view.getResources().getDrawable(R.drawable.intdesligado);
                else
                    img = view.getResources().getDrawable(R.drawable.b983w);
            }
        }
        else {
            if(dispositivos.get(position).getNivelAcionamento().equals(Status.LOW)){
                if (!dispositivos.get(position).getNick().toString().toLowerCase().contains("luz"))
                    img = view.getResources().getDrawable(R.drawable.intligado);
                else
                    img = view.getResources().getDrawable(R.drawable.lacessa);
            }
            else{
                if (!dispositivos.get(position).getNick().toString().toLowerCase().contains("luz"))
                    img = view.getResources().getDrawable(R.drawable.intdesligado);
                else
                    img = view.getResources().getDrawable(R.drawable.b983w);
            }

        }
        img.setBounds(0, 0, 60, 60);
        callbtn.setCompoundDrawables(img, null, null, null);
        callbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button btnParent = (Button) v;

                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                for (DispositivoButton dispbutton : listaCb){
                    if(dispbutton.getDispositivo().getNick().equals(btnParent.getText())){
                        List<Pool> listaPool = new ArrayList<>();
                        Pool pool = new Pool();
                        pool.setId(dispbutton.getPoolId());
                        pool.setDispositivos(new ArrayList<>());
                        pool.getDispositivos().add(dispbutton.getDispositivo());
                        listaPool.add(pool);
                        Drawable img = context.getResources().getDrawable(R.drawable.branco1);;
                        if(dispbutton.getDispositivo().getStatus().equals(Status.ON)){
                            dispbutton.getDispositivo().setStatus(Status.OFF);
                            /*if (dispbutton.getDispositivo().getNick().toString().toLowerCase().equals("bomba do filtro"))
                                img = context.getResources().getDrawable(R.drawable.intligado);
                            else
                                img = context.getResources().getDrawable(R.drawable.b983w);*/
                        }
                        else {
                            dispbutton.getDispositivo().setStatus(Status.ON);
                            /*if (dispbutton.getDispositivo().getNick().toString().toLowerCase().equals("bomba do filtro"))
                                img = context.getResources().getDrawable(R.drawable.intdesligado);
                            else
                                img = context.getResources().getDrawable(R.drawable.lacessa);*/
                        }
                        img.setBounds(0, 0, 60, 60);
                        btnParent.setCompoundDrawables(img, null, null, null);
                        String jSon = gson.toJson(listaPool);
                        //System.out.println(jSon);


                        ClienteMQTT clienteMQTTSend = new ClienteMQTT("tcp://broker.mqttdashboard.com:1883", "neuverse",
                                "M@r040370");
                        clienteMQTTSend.iniciar();
                        String topic = "br/com/neuverse/servidores/" + pool.getId() + "/atualizar";
                        clienteMQTTSend.publicar(topic, jSon.getBytes(), 0);
                        clienteMQTTSend.finalizar();

                        break;
                    }
                }

            }
        });


        return view;

    }
}
