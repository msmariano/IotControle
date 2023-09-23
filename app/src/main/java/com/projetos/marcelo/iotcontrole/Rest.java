package com.projetos.marcelo.iotcontrole;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;
import javax.ws.rs.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Rest {
    private String ip;
    private String porta = "80";
    private String uri;

    static {
        disableSslVerification();
    }


    private static void disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public String sendRest(String jSon) throws Exception {


        StringBuilder sb = new StringBuilder(jSon);
        byte[] input = sb.toString().getBytes(StandardCharsets.UTF_8);
        String urlS = "https://"+ip+":"+porta+uri;
        URL url = new URL(urlS);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(500);
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
