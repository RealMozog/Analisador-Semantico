/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uefs.ecomp.analisadorSemantico.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author sandr
 */
public class TabelaDeSimbolos {
    private List<List<Simbolo>> tabela;
    
    public TabelaDeSimbolos(){
        this.tabela = new ArrayList<LinkedList<Simbolo>>();
    }
    
    public boolean addElement(Simbolo symbol){
        boolean c = this.tabela.contains(symbol);
        
        if(!c){
            this.tabela.add(symbol);
        }
        
        return c;
    }
    
    public boolean contains (String id){
        
    }
    
    public void updateValue(String lexema, String value){
        
    }
    
    public Object[] lista(){
           return this.tabela.toArray();
    }
}
