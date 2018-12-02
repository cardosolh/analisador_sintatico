package com.company;

public class Main {

    public static void main(String[] args) {
        // TODO code application logic here

        Lexer lexer = new Lexer("primeiro_portugolo_certo.ptgl");


        Parser parser = new Parser(lexer);

        // primeiro procedimento do Javinha: Programa()

        parser.Compilador();

        parser.fechaArquivos();

        //Imprimir a tabela de simbolos
        lexer.printTS();

        System.out.println("Compilação de Programa Realizada!");
    }
}
