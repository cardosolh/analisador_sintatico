package com.company;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gustavo
 */
public class TS {
    
    private HashMap<String, Token> tabelaSimbolos; // Tabela de símbolos do ambiente

    public TS() {
        tabelaSimbolos = new HashMap();
        Token word;

        // Inserindo as palavras reservadas
        this.tabelaSimbolos.put("algoritmo",(new Token(Tag.KW,"algoritmo",0,0)));
        this.tabelaSimbolos.put("ate",(new Token(Tag.KW,"ate",0,0)));
        this.tabelaSimbolos.put("declare",(new Token(Tag.KW,"declare",0,0)));
        this.tabelaSimbolos.put("e",(new Token(Tag.KW,"e",0,0)));
        this.tabelaSimbolos.put("enquanto",(new Token(Tag.KW,"enquanto",0,0)));
        this.tabelaSimbolos.put("escreva",(new Token(Tag.KW,"escreva",0,0)));
        this.tabelaSimbolos.put("faca",(new Token(Tag.KW,"faca",0,0)));
        this.tabelaSimbolos.put("falso",(new Token(Tag.KW,"falso",0,0)));
        this.tabelaSimbolos.put("fim",(new Token(Tag.KW,"fim",0,0)));
        this.tabelaSimbolos.put("inicio",(new Token(Tag.KW,"inicio",0,0)));
        this.tabelaSimbolos.put("leia",(new Token(Tag.KW,"leia",0,0)));
        this.tabelaSimbolos.put("literal",(new Token(Tag.KW,"literal",0,0)));
        this.tabelaSimbolos.put("logico",(new Token(Tag.KW,"logico",0,0)));
        this.tabelaSimbolos.put("nulo",(new Token(Tag.KW,"nulo",0,0)));
        this.tabelaSimbolos.put("numerico",(new Token(Tag.KW,"numerico",0,0)));
        this.tabelaSimbolos.put("ou",(new Token(Tag.KW,"ou",0,0)));
        this.tabelaSimbolos.put("para",(new Token(Tag.KW,"para",0,0)));
        this.tabelaSimbolos.put("repita",(new Token(Tag.KW,"repita",0,0)));
        this.tabelaSimbolos.put("retorne",(new Token(Tag.KW,"retorne",0,0)));
        this.tabelaSimbolos.put("se",(new Token(Tag.KW,"se",0,0)));
        this.tabelaSimbolos.put("senao",(new Token(Tag.KW,"senao",0,0)));
        this.tabelaSimbolos.put("subrotina",(new Token(Tag.KW,"subrotina",0,0)));
        this.tabelaSimbolos.put("verdadeiro",(new Token(Tag.KW,"verdadeiro",0,0)));
        
        


//----------------------------------------------------------------------------------------------------------

// PALAVRAS JAVINHA

//----------------------------------------------------------------------------------------------------------

        /*
        word = new Token(Tag.KW_PUBLIC, "public", 0, 0);
        this.tabelaSimbolos.put("public", word);

        word = new Token(Tag.KW_CLASS, "class", 0, 0);
        this.tabelaSimbolos.put("class", word);

        word = new Token(Tag.KW_SYSTEMOUTDISPLN, "SystemOutDispln", 0, 0);
        this.tabelaSimbolos.put("SystemOutDispln", word);

        word = new Token(Tag.KW_END, "end", 0, 0);
        this.tabelaSimbolos.put("end", word);

        word = new Token(Tag.KW_INTEGER, "integer", 0, 0);
        this.tabelaSimbolos.put("integer", word);

        word = new Token(Tag.KW_DOUBLE, "double", 0, 0);
        this.tabelaSimbolos.put("double", word);

        word = new Token(Tag.KW_STRING, "string", 0, 0);
        this.tabelaSimbolos.put("string", word);*/
    }
    
    public void put(String s, Token w) {
        tabelaSimbolos.put(s, w);
    }

    // Pesquisa na tabela de símbolos se há algum token com determinado lexema
    // vamos usar esse metodo somente para diferenciar ID e KW
    public Token retornaToken(String lexema) {
		  Token token = tabelaSimbolos.get(lexema); 
		  return token;   	
    }
    
    @Override
    public String toString() {
        String saida = "";
        int i = 1;
        
        for(Map.Entry<String, Token> entry : tabelaSimbolos.entrySet()) {
            saida += ("posicao " + i + ": \t" + entry.getValue().toString()) + "\n";
            i++;
        }
        return saida;
    }
}
