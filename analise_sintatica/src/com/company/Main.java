package com.company;

public class Main {

    public static void main(String[] args) {
        // TODO code application logic here

        Lexer lexer = new Lexer("primeiro_portugolo_2.ptgl");


        Parser parser = new Parser(lexer);

        //Imprimir a tabela de simbolos
        lexer.printTS();

        // primeiro procedimento do Javinha: Programa()

        parser.Compilador();

        parser.fechaArquivos();



        System.out.println("Compilação de Programa Realizada!");
    }
}
