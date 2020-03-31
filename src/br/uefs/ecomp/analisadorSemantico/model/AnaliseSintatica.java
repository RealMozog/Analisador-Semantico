/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uefs.ecomp.analisadorSemantico.model;

import br.uefs.ecomp.AnalisadorLexico.model.Token;
import br.uefs.ecomp.analisadorSemantico.controller.AnalisadorSemanticoController;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sandr
 */
public class AnaliseSintatica {
    
    public AnaliseSintatica() throws IOException{
        AnaliseLexica al = new AnaliseLexica();        
        AnalisadorSemanticoController controller = new AnalisadorSemanticoController();
        Iterator<Token> it; 
        Iterator<Object> e;
        Iterator<Object> s;
        String output = "";
        int count = 1;
        
        for (List<Token> arq: al.getArqs()){
            it = arq.iterator();
            controller.analiseArq(it);
            e = controller.iteratorErrors();
            s = controller.iteratorSemanticErrors();

            if(!e.hasNext()){
                output = "Nenhum erro sintático encontrado no arquivo de entrada" + count;
                System.out.print(output + "\n");
            } else {
                while(e.hasNext()){
                    output += e.next().toString() + "\n";
                }
            }
            
            write_output(output, count, false);
            output = "";
            
            if(!s.hasNext()){
                output = "Nenhum erro semântico encontrado no arquivo de entrada" + count;
                System.out.print(output + "\n");
            } else {
                while(s.hasNext()){
                    output += s.next().toString() + "\n";
                }
            }
            
            write_output(output, count, true);

            count++;
        }
    }
    
    private void write_output(String erros, int count, boolean isSemantic) throws IOException{
        String caminho;
        
        if(isSemantic){
            caminho = "output\\saida";
        } else {
            caminho = "output-sintatico\\saida";
        }
        
        Path path = Paths.get(caminho + count + ".txt");
        
        byte[] strToBytes = erros.getBytes();
        
        Files.write(path, strToBytes);
    }  
}
