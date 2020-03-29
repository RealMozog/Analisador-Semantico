/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uefs.ecomp.analisadorSemantico.model;

import br.uefs.ecomp.AnalisadorLexico.model.Token;

/**
 *
 * @author sandr
 */
public class AnaliseSemantica {
    
    
    public boolean checkTypes(String type, Token token){
        if(type.equals("int")){
            if(token.getCodigo().equals("NRO")){
                if(token.getLexema().contains(".")){
                    return false;
                }
            }
        }
        
        if(type.equals("real")){
            if(token.getCodigo().equals("NRO")){
                if(!token.getLexema().contains(".")){
                    return false;
                }
            }
        }
        
        if(type.equals("boolean")){
            if(!token.getLexema().equals("true") && !token.getLexema().equals("false")){
                return false;
            }
        }
        
        if(type.equals("string")){
            if(!token.getCodigo().equals("CDC")){
                return false;
            }
        }
        
        return true;
    }
}
