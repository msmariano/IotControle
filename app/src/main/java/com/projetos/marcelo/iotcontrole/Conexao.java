package com.projetos.marcelo.iotcontrole;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Conexao {

    private String servidor;
    private Integer porta;

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public Integer getPorta() {
        return porta;
    }

    public void setPorta(Integer porta) {
        this.porta = porta;
    }

    public List<Conector> getListaConector() {
        return listaConector;
    }

    public void setListaConector(List<Conector> listaConector) {
        this.listaConector = listaConector;
    }

    private List<Conector> listaConector = new ArrayList<>();

    public boolean envia(Conector con) {
        try {

            listaConector.clear();
            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
            String textJson = gson.toJson(con,Conector.class);


            Socket socket = new Socket();
            socket.setSoTimeout(5000);
            SocketAddress socketAddress = new InetSocketAddress(servidor, porta);
            socket.connect(socketAddress, 5000);
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream())), true);
            out.println(textJson);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String ret = in.readLine();
            ret = ret.trim();
            int contador = 0, inicio = 0;
            for (int i = 0; i < ret.length(); i++) {
                if (ret.charAt(i) == '{') {
                    contador++;
                } else if (ret.charAt(i) == '}') {
                    contador--;
                }
                if (contador == 0 && i != 0) {
                    String jSonRetorno = ret.substring(inicio, i + 1);
                    Conector conectorTratar = gson.fromJson(jSonRetorno, Conector.class);
                    listaConector.add(conectorTratar);
                    inicio = i + 1;
                }
            }
            socket.close();
        } catch (SocketTimeoutException ste) {
            System.err.println("Erro timeout envia: " + ste.getMessage());
            return false;

        } catch (Exception e) {
            System.err.println("Erro envia:" + e.getMessage());
            return false;
        }
        return true;
    }

}
