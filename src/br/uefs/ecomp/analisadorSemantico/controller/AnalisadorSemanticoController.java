/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uefs.ecomp.analisadorSemantico.controller;

import br.uefs.ecomp.AnalisadorLexico.model.Token;
import br.uefs.ecomp.analisadorSemantico.model.ErrorList;
import br.uefs.ecomp.analisadorSemantico.model.TokensReader;
import java.util.Iterator;

/**
 *
 * @author Alessandro Costa
 */
public class AnalisadorSemanticoController {
    TokensReader tr;
    ErrorList list;
    
    public void analiseArq (Iterator<Token> arq){
        
        this.tr = new TokensReader(arq);
        
        this.list = this.tr.stateZero();
    }
    
    public Iterator<Object> iteratorErrors(){
        return this.list.iterator();
    }
    
    public Iterator<Object> iteratorSemanticErrors(){
        return this.tr.getSemanticErrors().iterator();
    }
}
