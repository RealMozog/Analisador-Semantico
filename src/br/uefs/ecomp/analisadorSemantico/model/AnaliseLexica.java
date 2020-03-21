/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uefs.ecomp.analisadorSemantico.model;

import br.uefs.ecomp.AnalisadorLexico.controller.AnalisadorLexicoController;
import br.uefs.ecomp.AnalisadorLexico.model.Token;
import br.uefs.ecomp.AnalisadorLexico.model.TokenError;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sandr
 */
public class AnaliseLexica {
    private final ArrayList<List<Token>> arqs;
    
    
    public AnaliseLexica(){
        this.arqs = new ArrayList<>();
        
        readArqs();
    }
    
    private void readArqs(){
        AnalisadorLexicoController controller = new AnalisadorLexicoController();
        Iterator<Token> iterator;
        Iterator<TokenError> iteratorErr;
        int count = 1;
        String w = "";
        
        try{
            String path = "input\\entrada" + count + ".txt";
            System.out.print(path);
            String arq = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            
            while (!arq.isEmpty()){
                
                controller.analiseArq(arq);
                
                iterator = controller.listTokens().iterator();
                w += tokensValidos(iterator);
                
                iteratorErr = controller.iteratorErros();
                
                if(!iteratorErr.hasNext()){
                    System.out.printf("Nenhum erro léxico encontrado no arquivo de entrada" + count + "\n");
                    this.addArq(controller.listTokens());
                } else {
                    w += tokensInValidos(iteratorErr);
                }
                
                // System.out.print(w + "\n");
                
                writeOutput (w, count);
                
                arq = null;
                w = "";
                count++;
                path = "input\\entrada" + count + ".txt";
                arq = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            }
            
        } catch (IOException e) {
             System.err.printf("Erro na abertura do arquivo: %s.\n",e.getMessage());
        }
    }
    
    private void writeOutput(String token, int count) 
        throws IOException {
        
        Path path = Paths.get("output-lexico\\saida" + count + ".txt");
        
        byte[] strToBytes = token.getBytes();
        
        Files.write(path, strToBytes);
    }
    
    private String tokensValidos(Iterator<Token> iterator){
        String w = "";
        
        while(iterator.hasNext()){
            Token t = iterator.next();
            w += t.toString() + '\n';
        }
        
        return w;
    }
    
    private String tokensInValidos(Iterator<TokenError> iterator){
        String err = "\n\n";
        
        while(iterator.hasNext()){
            TokenError e = iterator.next();
            err += e.toString() + '\n';
        }
        
        return err;
    }
    
    private void addArq(List<Token> arq){
        this.arqs.add(arq);
    }

    public ArrayList<List<Token>> getArqs(){
        return this.arqs;
    }
}
