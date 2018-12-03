/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladores;

/**
 *
 * @author gustavo
 */
public class Compiladores {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      // TODO code application logic here
      
      Lexer lexer = new Lexer("testeJavinha.jvn");
      Parser parser = new Parser(lexer);

      // primeiro procedimento do Javinha: Programa()
      parser.Programa();

      parser.fechaArquivos();
      
      //Imprimir a tabela de simbolos
      lexer.printTS();

      System.out.println("Compilação de Programa Realizada!");
   }
   
}
