package br.uefs.ecomp.analisadorSemantico.model;

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
    
    public void addErro(Object erro){
        this.errorList.add(erro);
    }
    
    public Iterator<Object> iterator(){
        return this.errorList.iterator();
    }
}
