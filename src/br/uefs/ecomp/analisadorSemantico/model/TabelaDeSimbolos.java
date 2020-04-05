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
    private int tamanho = 0;
    
    public TabelaDeSimbolos(){
        this.tabela = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            LinkedList<Simbolo> lista = new LinkedList<>();
            this.tabela.add(lista);
        }
    }
    
     public void addElement(Simbolo symbol) {
        if(!this.contem(symbol)) {
            this.verificaCarga();
            int indice = this.calculaIndiceDaTabela(symbol);
            List<Simbolo> lista = this.tabela.get(indice);
            lista.add(symbol);
            this.tamanho++;
        }
      }
    
    public void removeElement(Simbolo symbol){
        if(!this.contem(symbol)){
            
            int indice = this.calculaIndiceDaTabela(symbol);
            List<Simbolo> lista = this.tabela.get(indice);
            lista.remove(symbol);
            this.tamanho--;
            this.verificaCarga();
        }
    }
    
    private int calculaIndiceDaTabela(Simbolo symbol) {
        int codigoDeEspalhamento = symbol.hashCode();
        codigoDeEspalhamento = Math.abs(codigoDeEspalhamento);
        return codigoDeEspalhamento % tabela.size();
    }
    
    public void removeAll(){
        for (List<Simbolo> lista: this.tabela){
            lista.clear();
        }
    }
    
    public void removeSymbolsByScope(String scope){
        for (List<Simbolo> lista: this.tabela){
            lista.forEach(element -> {
                if(element.getScope().equals(scope)){
                    lista.remove(element);
                }
            });
        }
    }
    
    public int tamanho() {
        return this.tamanho;
    }
    
    public boolean contem(Simbolo symbol) {
        int indice = this.calculaIndiceDaTabela(symbol);
        // System.out.print("\n"+ indice + "\n" + symbol.toString());
        List<Simbolo> lista = this.tabela.get(indice);
        
        return lista.contains(symbol);
    }
    
    public Simbolo findById(Simbolo Id){
        int indice = this.calculaIndiceDaTabela(Id);
        // System.out.print("\n"+ indice + "\n" + Id.toString());
        List<Simbolo> lista = this.tabela.get(indice);
        
        for(Simbolo item: lista){
            if(item.getId().equals(Id.getId()) && item.getScope().equals(Id.getScope())){
                if(Id.getStruct_id() != null){
                    if(item.getStruct_id() != null){
                        if(item.getStruct_id().equals(Id.getStruct_id())){
                            return item;
                        }
                    }
                } else {
                    return item;
                }
            }
        }
        
        return null;
    }
    
    public void updateValue(String lexema, String value){
        
    }
    
    private void redimensionaTabela(int novaCapacidade){
        List<Simbolo> simbolos = this.pegaTodos();
        this.tabela.clear();

        for (int i = 0; i < novaCapacidade; i++) {
            this.tabela.add(new LinkedList<>());
        }

        for (Simbolo symbol : simbolos) {
            this.addElement(symbol);
        }
    }
    
    private void verificaCarga() {
        int capacidade = this.tabela.size();
        double carga = (double) this.tamanho / capacidade;

        if (carga > 0.75) {
            this.redimensionaTabela(capacidade * 2);
        } else if (carga < 0.25) {
            this.redimensionaTabela(Math.max(capacidade / 2, 10));
        }
    }
    
    public List<Simbolo> pegaTodos() {
        List<Simbolo> simbolos = new ArrayList<>();
        for (List<Simbolo> tabela1 : this.tabela) {
            simbolos.addAll(tabela1);
        }
        return simbolos;
      }
}
