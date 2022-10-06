package com.projetos.marcelo.iotcontrole;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ControleRest {

    private String ip;
    private Integer porta;

    public Integer getPorta(){return this.porta;}
    public void setPorta(Integer porta) {this.porta = porta;}

    public ComandoIOT sendRest(String jSon) throws Exception {

        StringBuilder sb = new StringBuilder(jSon);
        URL url = new URL("http://"+ip+":"+porta);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(HttpMethod.POST);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Host", ip);
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Content-Length", String.valueOf(sb.toString().length()));
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = sb.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, jSon.length());
        }
        int responseCode = con.getResponseCode();

        System.out.println("Response code:"+responseCode);

        if(responseCode == HttpURLConnection.HTTP_OK){
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                response.append(inputLine);
            }
            in.close();

            String json = response.toString().substring(0,con.getContentLength());
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
            ComandoIOT ciot = gson.fromJson(json, ComandoIOT.class);
            return ciot;
        }
        return null;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
