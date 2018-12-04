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
        this.tabelaSimbolos.put("algoritmo",(new Token(Tag.KW_ALGORITMO,"algoritmo",0,0)));
        word = new Token(Tag.KW_DECLARE, "declare", 0, 0);
        this.tabelaSimbolos.put("declare", word);
        this.tabelaSimbolos.put("ate",(new Token(Tag.KW_ATE,"ate",0,0)));
        //this.tabelaSimbolos.put("declare",(new Token(Tag.KW_DECLARE,"declare",0,0)));
        this.tabelaSimbolos.put("e",(new Token(Tag.KW_E,"e",0,0)));
        this.tabelaSimbolos.put("enquanto",(new Token(Tag.KW_ENQUANTO,"enquanto",0,0)));
        this.tabelaSimbolos.put("escreva",(new Token(Tag.KW_ESCREVA,"escreva",0,0)));
        this.tabelaSimbolos.put("faca",(new Token(Tag.KW_FACA,"faca",0,0)));
        this.tabelaSimbolos.put("falso",(new Token(Tag.KW_FALSO,"falso",0,0)));
        this.tabelaSimbolos.put("fim",(new Token(Tag.KW_FIM,"fim",0,0)));
        this.tabelaSimbolos.put("inicio",(new Token(Tag.KW_INICIO,"inicio",0,0)));
        this.tabelaSimbolos.put("leia",(new Token(Tag.KW_LEIA,"leia",0,0)));
        this.tabelaSimbolos.put("literal",(new Token(Tag.KW_LITERAL,"literal",0,0)));
        this.tabelaSimbolos.put("logico",(new Token(Tag.KW_LOGICO,"logico",0,0)));
        this.tabelaSimbolos.put("nao",(new Token(Tag.KW_NAO,"nao",0,0)));
        this.tabelaSimbolos.put("nulo",(new Token(Tag.KW_NULO,"nulo",0,0)));
        this.tabelaSimbolos.put("numerico",(new Token(Tag.KW_NUMERICO,"numerico",0,0)));
        this.tabelaSimbolos.put("ou",(new Token(Tag.KW_OU,"ou",0,0)));
        this.tabelaSimbolos.put("para",(new Token(Tag.KW_PARA,"para",0,0)));
        this.tabelaSimbolos.put("repita",(new Token(Tag.KW_REPITA,"repita",0,0)));
        this.tabelaSimbolos.put("retorne",(new Token(Tag.KW_RETORNE,"retorne",0,0)));
        this.tabelaSimbolos.put("se",(new Token(Tag.KW_SE,"se",0,0)));
        this.tabelaSimbolos.put("senao",(new Token(Tag.KW_SENAO,"senao",0,0)));
        this.tabelaSimbolos.put("subrotina",(new Token(Tag.KW_SUBROTINA,"subrotina",0,0)));
        this.tabelaSimbolos.put("verdadeiro",(new Token(Tag.KW_VERDADEIRO,"verdadeiro",0,0)));

        


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
