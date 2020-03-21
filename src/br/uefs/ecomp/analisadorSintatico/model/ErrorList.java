package br.uefs.ecomp.analisadorSintatico.model;

import java.util.ArrayList;
import java.util.Iterator;


/**
 *
 * @author sandr
 */
public class ErrorList {
    private ArrayList errorList;
    
    public ErrorList(){
        this.errorList = new ArrayList<>();
    }
    
    public void addErro(Error erro){
        this.errorList.add(erro);
    }
    
    public Iterator<Error> iterator(){
        return this.errorList.iterator();
    }
}
