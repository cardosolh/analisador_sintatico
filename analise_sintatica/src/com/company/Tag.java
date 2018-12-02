package com.company;

/**
 *
 * @author gustavo
 */
public enum Tag {

    // fim de arquivo
    EOF,

    //Operadores
    RELOP_LT,
    RELOP_LE,
    RELOP_GT,
    RELOP_GE,
    RELOP_EQ,
    RELOP_NE,
    RELOP_ASSIGN,
    RELOP_PLUS,
    RELOP_MINUS,
    RELOP_MULT,
    RELOP_DIV,

    //Simbolos
    SMB_OP,
    SMB_CP,
    SMB_SEMICOLON,
    SMB_COMMA,
    SMB_COLON,

    //identificador
    ID,

    //numeros
    NUMERICO,

    //strings
    LITERAL,

    // palavra reservada
    KW_ALGORITMO,
    KW_ATE,
    KW_DECLARE,
    KW_E,
    KW_ENQUANTO,
    KW_ESCREVA,
    KW_FACA,
    KW_FALSO,
    KW_FIM,
    KW_INICIO,
    KW_LEIA,
    KW_LITERAL,
    KW_LOGICO,
    KW_NULO,
    KW_NUMERICO,
    KW_OU,
    KW_PARA,
    KW_REPITA,
    KW_RETORNE,
    KW_SE,
    KW_SENAO,
    KW_SUBROTINA,
    KW_VERDADEIRO;
}
