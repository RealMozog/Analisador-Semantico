package br.uefs.ecomp.analisadorSemantico.view;

import br.uefs.ecomp.analisadorSemantico.model.AnaliseSintatica;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 *
 * @author Alessandro Costa
 */
public class AnalisadorSemantico {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        AnaliseSintatica as = new AnaliseSintatica();
    }
    
    public static void write_output(String erros, int count) throws IOException{
        
        Path path = Paths.get("output-sintatico\\saida" + count + ".txt");
        
        byte[] strToBytes = erros.getBytes();
        
        Files.write(path, strToBytes);
    }   
}
