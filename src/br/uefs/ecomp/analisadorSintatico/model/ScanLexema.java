/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uefs.ecomp.analisadorSintatico.model;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author sandr
 */
public class ScanLexema {
    private final String[] type = { "int", "string", "real", "boolean" };
    private final String[] commands = { "if", "while", "read", "print" } ;
    private final String[] modifiers = { "global", "local" };
    private final String[] boleans = { "true", "false" };
    private final String[] unary_op = { "++", "--" };
    private final String[] operators = { "+", "-", "/", "*", "&&", "||", ">", 
        "<", "==", "!=", ">=", "<="  };
    private final String[] logicaOp = { "&&", "||" };
    private final String[] RelationalOpStronger = { ">", "<", ">=", "<=" };
    private final String[] RelationalOpWeaker = { "==", "!=" };
    private final String[] plus_minus = { "+", "-" };
    private final String[] mult_div = { "*", "/" };
            
    public boolean isType(String lexema){
        List<String> types = Arrays.asList(this.type);
        
        return lexema != null? types.contains(lexema): null;
    }
    
    public boolean isCommands(String lexema) {
        List<String> command = Arrays.asList(this.commands);
        
        return lexema != null? command.contains(lexema): false;
    }
    
    public boolean isModifiers(String lexema) {
        List<String> modifier = Arrays.asList(this.modifiers);
        
        return lexema != null? modifier.contains(lexema): false;
    }
    
    public boolean isBooleans(String lexema) {
        List<String> bolean = Arrays.asList(this.boleans);
        
        return lexema != null? bolean.contains(lexema): false;
    }
    
    public boolean isUnaryOp(String lexema) {
        List<String> unary = Arrays.asList(this.unary_op);
        
        return lexema != null? unary.contains(lexema): false;
    }
    
    public boolean isOperators(String lexema) {
        List<String> operator = Arrays.asList(this.operators);
        
        return lexema != null? operator.contains(lexema): false;
    }
    
    public boolean isLogicalOp(String lexema) {
        List<String> logical_op = Arrays.asList(this.logicaOp);
        
        return lexema != null? logical_op.contains(lexema): false;
    }
    
    public boolean isRelationalOpStronger(String lexema) {
        List<String> relation_op = Arrays.asList(this.RelationalOpStronger);
        
        return lexema != null? relation_op.contains(lexema): false;
    }
    
    public boolean isRelationalOpWeaker(String lexema) {
        List<String> relation_op = Arrays.asList(this.RelationalOpWeaker);
        
        return lexema != null? relation_op.contains(lexema): false;
    }
    
    public boolean isPlusMinus(String lexema) {
        List<String> plus_minu = Arrays.asList(this.plus_minus);
        
        return lexema != null? plus_minu.contains(lexema): false;
    }
    
    public boolean isTimesDiv(String lexema) {
        List<String> times_div = Arrays.asList(this.mult_div);
        
        return lexema != null? times_div.contains(lexema): false;
    }
    
    public boolean isNoLogicalOperators(String lexema) {
        List<String> nologicalOp = Arrays.asList(this.operators);
        
        nologicalOp.remove("&&");
        nologicalOp.remove("||");
        
        return lexema != null? nologicalOp.contains(lexema): false;
    }
}
