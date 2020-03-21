package br.uefs.ecomp.analisadorSintatico.view;

import br.uefs.ecomp.AnalisadorLexico.model.Token;
import br.uefs.ecomp.analisadorSintatico.model.Error;
import br.uefs.ecomp.analisadorSintatico.controller.AnalisadorSintaticoController;
import br.uefs.ecomp.analisadorSintatico.model.AnaliseLexica;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Alessandro Costa
 */
public class AnalisadorSintático {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        AnaliseLexica al = new AnaliseLexica();        
        AnalisadorSintaticoController controller = new AnalisadorSintaticoController();
        Iterator<Token> it; 
        Iterator<Error> e;
        String output = "";
        int count = 1;
        
        for (List<Token> arq: al.getArqs()){
            it = arq.iterator();
            controller.analiseArq(it);
            e = controller.iteratorErrors();

            if(!e.hasNext()){
                output = "Nenhum erro sintático encontrado no arquivo de entrada" + count;
                System.out.print(output + "\n");
            } else {
                while(e.hasNext()){
                    output += e.next().toString() + "\n";
                }
            }
            
            write_output(output, count);
            output = "";
            count++;
        }
    }
    
    public static void write_output(String erros, int count) throws IOException{
        
        Path path = Paths.get("output\\saida" + count + ".txt");
        
        byte[] strToBytes = erros.getBytes();
        
        Files.write(path, strToBytes);
    }   
}
