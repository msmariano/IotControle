package com.projetos.marcelo.iotcontrole;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import javax.ws.rs.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Rest {
    private String ip;
    private String porta = "80";
    private String uri;

    public String sendRest(String jSon) throws Exception {


        StringBuilder sb = new StringBuilder(jSon);
        byte[] input = sb.toString().getBytes(StandardCharsets.UTF_8);
        String urlS = "http://"+ip+":"+porta+uri;
        URL url = new URL(urlS);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(HttpMethod.POST);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Host", ip);
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Content-Length", String.valueOf(input.length));
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setDoOutput(true);
        int tam = jSon.length();
        try(OutputStream os = con.getOutputStream()) {
            os.write(input, 0, input.length);
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

            return response.toString();
        }
        return null;
    }

    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getUri() {
        return uri;
    }
    public void setUri(String p) {
        this.uri = p;
    }
    public String getPorta(){
        return porta;
    }
    public void setPorta(String p){
        porta = p;
    }
}
