/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uefs.ecomp.analisadorSintatico.model;

/**
 *
 * @author sandr
 */
public class Error {
    private int line;
    private String expected;
    private String found;
    
    public Error(int line, String expected, String found){
        this.line = line;
        this.expected = expected;
        this.found = found;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getFound() {
        return found;
    }

    public void setFound(String found) {
        this.found = found;
    }

    @Override
    public String toString() {
        return "Error: line= " + line + "; Expected=" + expected + '"'  + 
                ", Found=" + '"' + found + '"' ;
    }
    
    
}
