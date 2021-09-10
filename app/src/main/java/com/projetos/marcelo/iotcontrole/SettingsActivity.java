package com.projetos.marcelo.iotcontrole;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    SQLiteDatabase mydatabase;
    static final Set<String> opcoesNomeIot = new HashSet<>();
    static final Set<String> opcoesNomeIotBtns = new HashSet<>();

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public static List<String> retornaIots(String endServidor, Integer portaServidor) throws IOException {

        InetAddress serverEnd = InetAddress.getByName(endServidor);
        Socket socket = new Socket(serverEnd, portaServidor);
        PrintWriter out = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream())), true);

        out.println("{\"status\":\"LISTA_IOT\"}");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        String ret = in.readLine();
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        List<String> lista = gson.fromJson(ret, List.class);
        if (lista.size() > 0) {

        }
        return lista;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        try {
            mydatabase = openOrCreateDatabase("cfg.db", MODE_PRIVATE, null);
            Cursor c = mydatabase.rawQuery("SELECT DISTINCT NOMEIOT FROM Configuracao", null);
            while(c.moveToNext()){
                opcoesNomeIot.add(c.getString(0));
            }
            c.close();
            //c = mydatabase.rawQuery("SELECT * FROM Configuracao", null);
        }
        catch (Exception e){
            System.out.println("Erro ao listar nomes de iot da tabela configuracao: "+ e.getMessage());
        }
        setTitle("Configuração");
    }



    public static class SettingsFragment extends PreferenceFragmentCompat {

        public void alterarCfgBtn(EditTextPreference preference){
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        EditTextPreference btn = getPreferenceManager().findPreference(preference.getKey());
                        assert btn != null;
                        Integer inicio = btn.getTitle().toString().indexOf("ButtonID_");
                        Integer fim = inicio + "ButtonID_".length();
                        String id = btn.getTitle().toString().substring(fim);
                        List<Configuracao> cbs = Configuracao.findWithQuery(Configuracao.class,
                                "SELECT * FROM Configuracao WHERE idBotao =" + id);
                        if (cbs != null && cbs.size() > 0) {
                            Configuracao cf = cbs.get(0);
                            cf.setNomebotao((String) newValue);
                            String query = "UPDATE Configuracao SET NOMEBOTAO = '" + (String) newValue
                                    + "' WHERE IDBOTAO =" + id;
                            Configuracao.executeQuery(query);
                            return true;
                        }
                        return false;
                    } catch (Exception e) {
                        System.out.println("Erro salvando nome do botao:" + e.getMessage());
                        return false;
                    }
                }
            });
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Set<String> opcoes = opcoesNomeIot;// new HashSet<>();

            if(opcoes.size()==0) {
                opcoes.add("Nenhum");
            }
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            MultiSelectListPreference listaIots = getPreferenceManager().findPreference("iots");
            CharSequence[] cs = opcoes.toArray(new CharSequence[0]);
            assert listaIots != null;
            listaIots.setEntries(cs);
            listaIots.setEntryValues(cs);
            if(opcoes.size()==0)
                listaIots.setVisible(false);

            Iterator<Configuracao> configs =  Configuracao.findAll(Configuracao.class);
            while ( configs.hasNext()) {
                Configuracao cf = configs.next();
                PreferenceScreen screen = getPreferenceManager().getPreferenceScreen();
                setPreferenceScreen(screen);
                EditTextPreference preference = new EditTextPreference(screen.getContext());
                preference.setKey("EditTextPreferenceBtn_" + cf.getIdbotao().byteValue());
                preference.setTitle(cf.getNomeiot() + "_ButtonID_" + cf.getIdbotao().byteValue());
                preference.setDefaultValue(cf.getNomebotao());
                screen.addPreference(preference);
                alterarCfgBtn(preference);
            }




            MultiSelectListPreference iots = this.getPreferenceManager().findPreference("iots");
            iots.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Set<String> opts = (Set) newValue;
                    EditTextPreference edEndServidor = getPreferenceManager().findPreference("endereco");
                    EditTextPreference edPortaServidor = getPreferenceManager().findPreference("porta");
                    EditTextPreference usuario = getPreferenceManager().findPreference("usuario");
                    EditTextPreference senha = getPreferenceManager().findPreference("senha");
                    for (String opt : opts) {
                        try {
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        Configuracao cfg = new Configuracao();
                                        cfg.setNomeiot(opt);
                                        cfg.setNomeiotcom(getDeviceName());
                                        assert usuario != null;
                                        cfg.setUsuario(usuario.getText());
                                        assert senha != null;
                                        cfg.setSenha(senha.getText());
                                        ControleBotao cb = new ControleBotao();
                                        if (edEndServidor.getText().trim().length() > 0) {
                                            assert edPortaServidor != null;
                                            if (edPortaServidor.getText().trim().length() > 0) {
                                                cfg.setServidor(edEndServidor.getText());
                                                cfg.setPortaservidor(Integer.parseInt(edPortaServidor.getText()));
                                                cb.setCfg(cfg);
                                                cb.testaBotoes();
                                                if (cb.getIdsBt().size() > 0) {
                                                    for (Integer i : cb.getIdsBt()) {

                                                        PreferenceScreen screen = getPreferenceManager().getPreferenceScreen();
                                                        setPreferenceScreen(screen);
                                                        EditTextPreference preference = new EditTextPreference(screen.getContext());
                                                        preference.setKey("EditTextPreferenceBtn_" + i);
                                                        preference.setTitle(opt + "_ButtonID_" + i);
                                                        Configuracao configuracao = null;
                                                        List<Configuracao> cbs = null;
                                                        try {
                                                            cbs = Configuracao.findWithQuery(Configuracao.class,
                                                                    "SELECT * FROM Configuracao WHERE idBotao =" + i);
                                                        } catch (Exception e) {
                                                        }

                                                        if (cbs != null && cbs.size() > 0) {
                                                            configuracao = cbs.get(0);
                                                            preference.setText(configuracao.getNomebotao());
                                                        } else {
                                                            preference.setText("Lamp_" + i);
                                                            configuracao = new Configuracao(edEndServidor.getText(),
                                                                    Integer.parseInt(edPortaServidor.getText()),
                                                                    usuario.getText(),
                                                                    senha.getText(),
                                                                    getDeviceName(), opt, "Lamp_" + i, i);
                                                            configuracao.save();
                                                        }
                                                        preference.setDefaultValue("Lamp_" + i);
                                                        screen.addPreference(preference);
                                                        alterarCfgBtn(preference);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                }
                            }.start();
                        } catch (Exception e) {

                        }

                    }

                    return true;
                }
            });

            CheckBoxPreference chkCon = this.getPreferenceManager().findPreference("conectar");
            chkCon.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean bCon = (Boolean) newValue;
                    MultiSelectListPreference listaIots = getPreferenceManager().findPreference("iots");
                    EditTextPreference edEndServidor = getPreferenceManager().findPreference("endereco");
                    EditTextPreference edPortaServidor = getPreferenceManager().findPreference("porta");
                    //Preencher lista Iot
                    if (bCon) {
                        try {
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        assert edPortaServidor != null;
                                        if (edPortaServidor.getText().trim().length() > 0) {
                                            assert edEndServidor != null;
                                            if (edEndServidor.getText().trim().length() > 0) {
                                                List<String> iots = retornaIots(edEndServidor.getText(),
                                                        Integer.parseInt(edPortaServidor.getText()));
                                                if (iots != null) {
                                                    opcoes.clear();
                                                    opcoes.addAll(iots);
                                                    CharSequence[] cs = opcoes.toArray(new CharSequence[0]);
                                                    listaIots.setEntries(cs);
                                                    listaIots.setEntryValues(cs);
                                                    listaIots.setVisible(true);
                                                    listaIots.saveHierarchyState(savedInstanceState);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else
                        return true;

                    return opcoes.size() > 0;
                }
            });
        }

    }
}