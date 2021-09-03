package com.projetos.marcelo.iotcontrole;
import com.orm.SugarRecord;

import java.util.List;


public class ParametroDAO {



    public ParametroDAO (){

    }

    public void setChave(String parametro,String campo1, String campo2){

        Parametro chave = null;

        chave = buscarParamento(parametro);

        if(chave == null) {
            chave = new Parametro();
            chave.setParametro(parametro);
            chave.setCampo1(campo1);
            chave.setCampo2(campo2);
            chave.save();
        }
        else
        {
            chave.delete();
            chave.setParametro(parametro);
            chave.setCampo1(campo1);
            chave.setCampo2(campo2);
            chave.save();

        }


    }



    Parametro buscarParamento (String argParametro){
        try {
            List<Parametro> listaParametros = SugarRecord.find(Parametro.class, "parametro = ? ", argParametro);
            if (listaParametros.size() > 0)
                return listaParametros.get(0);
            else
                return null;
        }
        catch (Exception e){

        }
        return null;
    }
    List<Parametro> listar(){

        List<Parametro> listaParametros = Parametro.listAll(Parametro.class);
        return listaParametros;

    }
    public void salvar(Parametro parametro){
        parametro.save();

    }

}
