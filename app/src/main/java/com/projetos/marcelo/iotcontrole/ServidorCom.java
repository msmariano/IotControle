package com.projetos.marcelo.iotcontrole;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class ServidorCom extends Socket
{
    private String servidor;
    private Integer porta;
    private boolean conectado;

    ServidorCom(){
        conectado = false;
    }

    ServidorCom(String servidor,Integer porta){
        this.servidor = servidor;
        this.porta = porta;
        conectado = false;
    }

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

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    void iniciar(){
        conectado = false;
        SocketAddress socketAddress = new InetSocketAddress(servidor, porta);
        try {
            this.connect(socketAddress, 5000);
            conectado = true;
        } catch (IOException e) {
            conectado = false;
        }
    }
    void interromper(){
        conectado  = false;
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String comunicar(String textJson) {
        String ret = "";
        if(conectado) {
            PrintWriter out = null;
            try {
                out = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(
                                this.getOutputStream())), true);
                out.println(textJson);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(this.getInputStream()));
                ret = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  ret;
    }
}
/*ServidorCom servidor = new ServidorCom(cfg.getServidor(),cfg.getPortaServidor());
            new Thread(new Runnable() {
                private ServidorCom servidor;
                public Runnable init(ServidorCom myParam) {
                    this.servidor = myParam;
                    return this;
                }
                @Override
                public void run() {
                    servidor.iniciar();
                    Conector con = new Conector();
                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
                    String textJson = gson.toJson(con);
                    boolean logado = false;
                    while(true) {
                        if (servidor.isConectado()) {
                            if(!logado) {
                                con.setStatus(Status.LOGIN);
                                con.setSenha("M@r0403");
                                con.setUsuario("Matinhos");
                                con.setNome("CelularMarcelo");
                                con.setTipo(TipoIOT.CONTROLELAMPADA);
                                //textJson = gson.toJson(con);
                                //String mensServidor = servidor.comunicar(textJson);
                                //con = gson.fromJson(mensServidor,Conector.class);
                                con.setStatus(Status.ALIVE);
                                textJson = gson.toJson(con);
                                logado = true;
                            }
                            try {
                                String mensServidor = servidor.comunicar(textJson);
                                con = gson.fromJson(mensServidor,Conector.class);
                                System.out.println(mensServidor);
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            try {
                                servidor.close();
                                servidor.iniciar();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }.init(servidor)).start();*/