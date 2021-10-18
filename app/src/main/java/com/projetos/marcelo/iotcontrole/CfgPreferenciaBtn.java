package com.projetos.marcelo.iotcontrole;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import java.security.acl.Group;
import java.util.List;

public class CfgPreferenciaBtn extends EditTextPreference {

    private Configuracao cfg = new Configuracao();

    public CfgPreferenciaBtn(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CfgPreferenciaBtn(Context context, PreferenceScreen ps){
        super(context);
        setIcon(android.R.drawable.ic_lock_power_off);
        setDialogIcon(android.R.drawable.ic_lock_power_off);
        alterarCfgBtn();
        ps.addPreference(this);
    }

    public CfgPreferenciaBtn(Context context){
        super(context);

    }

    public Configuracao getCfg() {
        return cfg;
    }

    public void setCfg(Configuracao cfg) {
        this.cfg = cfg;
        setKey("EditTextPreferenceBtn_" + cfg.getIdbotao().byteValue());
        setTitle(cfg.getNomeiot() + "_ButtonID_" + cfg.getIdbotao().byteValue());
        setDefaultValue(cfg.getNomebotao());
        setText(cfg.getNomebotao());
        setSummary(cfg.getNomebotao());

    }
    public void remove(){
        PreferenceGroup layout = (PreferenceGroup) this.getParent();
        if(layout!=null)
            layout.removePreference(this);
    }
    public void alterarCfgBtn(){
        setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    EditTextPreference btn = getPreferenceManager().findPreference(preference.getKey());
                    btn.setSummary((String)newValue);
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

    public void add(String servidor,String porta,String usuario,String senha,String iot){

    }
}
