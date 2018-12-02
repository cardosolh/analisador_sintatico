package com.company;

import java.io.*;

/**
 *
 * @author gustavo
 */
public class Lexer {
    
   private static final int END_OF_FILE = -1; // contante para fim do arquivo
   private static int lookahead = 0; // armazena o último caractere lido do arquivo	
   public static int n_line = 1; // contador de linhas
   public static int n_column = 1; // contador de linhas
   public static int n_errors = 0;
   private RandomAccessFile instance_file; // referencia para o arquivo
   private static TS tabelaSimbolos; // tabela de simbolos
    
   public Lexer(String input_data) {
		
      // Abre instance_file de input_data
      try {
         instance_file = new RandomAccessFile(input_data, "r");
      }
	   catch(IOException e) {
         System.out.println("Erro de abertura do arquivo " + input_data + ": " + e);
         System.exit(1);
      }
	   catch(Exception e) {
         System.out.println("Erro do programa ou falha da tabela de simbolos: " + e);
         System.exit(2);
	   }

      tabelaSimbolos = new TS(); // tabela de simbolos
   }
   
   public void printTS() {
		System.out.println("");
      System.out.println("--------Tabela de Simbolos--------");
      System.out.println(tabelaSimbolos.toString());  
      System.out.println();
   }
    
   // Fecha instance_file de input_data
   public void fechaArquivo() {

      try {
         instance_file.close();
      }
	   catch (IOException errorFile) {
         System.out.println ("Erro ao fechar arquivo: " + errorFile);
         System.exit(3);
	   }
   }
    
    //Reporta erro para o usuário
    public void sinalizaErro(String mensagem) {
      System.out.println("[Erro Lexico]: " + mensagem + "\n");
    }
    
    //Volta uma posição do buffer de leitura
    public void retornaPonteiro(){

      try {
         // Não é necessário retornar o ponteiro em caso de Fim de Arquivo
         if(lookahead != END_OF_FILE) {
            instance_file.seek(instance_file.getFilePointer() - 1);
            n_column--;
         }    
      }
      catch(IOException e) {
         System.out.println("Falha ao retornar a leitura: " + e);
         System.exit(4);
      }
    }
    public char leCaractere(){
       char c;
        c = '\u0000';
        try {
            lookahead = instance_file.read();
            if(lookahead != END_OF_FILE) {
                c = (char) lookahead;
                n_column++;
            }
        }
        catch(IOException e) {
            System.out.println("Erro na leitura do arquivo");
            System.exit(3);
        }
        return c;

   }

   // Obtém próximo token: Esse metodo eh a implementacao do AFD
   public Token getToken() {

	StringBuilder lexema = new StringBuilder();
	int estado = 0;
	char c ;

		
	while(true) {
       // null char
       c = leCaractere();


            
      // avanca caractere

            
      // movimentacao do automato
       switch (estado) {

          case 0:
             if (lookahead == END_OF_FILE) // fim de arquivo. hora de parar
             {

                return new Token(Tag.EOF, "EOF", n_line, n_column);

             } else if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {//ok
                if (c == '\t') {
                   n_column = +3;
                } else if (c == '\n') {
                   n_line++;
                   n_column = 1;
                }
             } //Estado q24
             else if (c == '(') {
                return new Token(Tag.SMB_OP, "(", n_line, n_column);//ok
             } //Estado q25
             else if (c == ')') {
                return new Token(Tag.SMB_CP, ")", n_line, n_column);//ok
             } //Estado q26
             else if (c == '=') {
                lexema.append(c);
                return new Token(Tag.RELOP_EQ, "=", n_line, n_column);//ok
             }//Estado q27
             else if (c == ',') {
                lexema.append(c);
                return new Token(Tag.SMB_COMMA, ",", n_line, n_column);//ok
             }//Estado q28
             else if (c == ';') {
                return new Token(Tag.SMB_SEMICOLON, ";", n_line, n_column);//ok
             }//Estado q29
             else if (c == ':') {
                return new Token(Tag.SMB_COLON, ":", n_line, n_column);//ok
             }//Estado q30
             else if (c == '-') {
                return new Token(Tag.RELOP_MINUS, "-", n_line, n_column);//ok
             }//Estado q31
             else if (c == '+') {
                return new Token(Tag.RELOP_PLUS, "+", n_line, n_column);//ok
             }//Estado q32
             else if (c == '*') {
                return new Token(Tag.RELOP_MULT, "*", n_line, n_column);//ok
             } else if (c == '<') {//ok
                lexema.append(c);
                estado = 8;
             } else if (c == '>') {//ok
                lexema.append(c);
                estado = 17;
             } else if (c == '/') {
                lexema.append(c);
                estado = 4;
             } else if (Character.isLetter(c)) {
                lexema.append(c);
                estado = 6; // ok
             } else if (Character.isDigit(c)) {
                lexema.append(c); //
                estado = 20; //
             } else if (c == '"') {
                //lexema.append(c);
                estado = 14;
             } else {
                sinalizaErro("Não foi possível reconhecer o símbolo "+ c +
                        " na linha " + n_line +  " e coluna " + n_column + " o token será ignorado");
                lexema.delete(0, lexema.length());
                n_errors=0;
                estado = 0;

             }
             break;

          case 1:
             switch (c) {
                case '/':
                   lexema.append(c);
                   estado = 4;
                   break;
                case '*':
                   lexema.append(c);
                   estado = 3;
                   break;
                default:
                   retornaPonteiro();
                   return new Token(Tag.RELOP_DIV, "/", n_line, n_column);
             }
             break;

          case 3:
             if (c == '*') {
                lexema.append(c);
                estado = 5;
             } else if (lookahead == END_OF_FILE) {
                retornaPonteiro();
                sinalizaErro("Comentário multilinha (/**/) não fechado antes do final do programa.");
                return null;
             } else {
                lexema.append(c);
             }
             break;

          case 4:
             if (c == '\n' || c == '\r' || lookahead == END_OF_FILE) {
                lexema.delete(0, lexema.length());
                estado = 0;
             } else {
                lexema.append(c);
             }
             break;

          case 5:
             if (c == '/') {
                lexema.delete(0, lexema.length());
                estado = 0;
             } else if (lookahead == END_OF_FILE) {
                retornaPonteiro();
                sinalizaErro("Comentário multilinha (/**/) não fechado antes do final do programa.");
                return null;
             } else {
                lexema.append(c);
                estado = 3;
             }
             break;

          case 6:
             if (Character.isLetterOrDigit(c)) {
                lexema.append(c);
             } else {
                retornaPonteiro();
                if (tabelaSimbolos.retornaToken(lexema.toString()) == null) {
                   return new Token(Tag.ID, lexema.toString(), n_line, n_column);
                } else {
                   return tabelaSimbolos.retornaToken(lexema.toString());
                }
             }
             break;

          case 8:
             if (c == '=') {
                return new Token(Tag.RELOP_LE, "<=", n_line, n_column);
             } else if (c == '-') {
                lexema.append(c);
                estado = 13;
                break;
             } else if (c == '>') {
                return new Token(Tag.RELOP_LT, "<>", n_line, n_column);
             } else {
                retornaPonteiro();
                return new Token(Tag.RELOP_LT, "<", n_line, n_column);
             }

          case 13:
             if (c == '-') {
                return new Token(Tag.RELOP_ASSIGN, "<--", n_line, n_column);
             } else {
                n_errors++;
                sinalizaErro("Lexema " + lexema + " invalido na linha " + n_line
                        + " e coluna " + n_column + " era esperado \" <-- \"");
                if (n_errors<1){
                   lexema.delete(lexema.length()-1, 1);
                   estado = 0;
                   retornaPonteiro();
                }else {
                   sinalizaErro("Não foi possível corrigir o erro léxico," +
                           " da linha " + n_line +  " e coluna " + n_column + " o token será ignorado");
                   lexema.delete(0, lexema.length());
                   retornaPonteiro();
                   n_errors=0;
                   estado = 0;
                }
             }
             break;

          case 14:
             if (c == '"' && lookahead != '"') {
                lexema.append(c);
                //estado = 15;
             }
             else if(c =='"' && lexema.length()==1){

                sinalizaErro("Literal vazio identificado na linha " + n_line
                        + " e coluna " + n_column + ", os caracteres subsequentes montarão o novo Literal");
                lexema.delete(lexema.length()-1, 1);
                estado = 15;

             } else if (c == '\n' || c == '\r') {
                sinalizaErro("Literal "+ lexema +"não fechado na linha " + n_line
                        + " e coluna " + n_column + ", o token não será formado");
                lexema.delete(0, lexema.length());
                estado = 0;

             } else if (lookahead == END_OF_FILE) {
                sinalizaErro("Literal \""+ lexema +" não fechado antes do final do arquivo na linha " + n_line
                        + " e coluna " + n_column + ", o token não será formado");
                lexema.delete(0, lexema.length());

                estado = 0;
             } else {
                lexema.append(c);
                estado = 15;
             }
             break;
          case 15:
             if (c == '"' && lexema.length()>2) {
                return new Token(Tag.LITERAL, lexema.toString(), n_line,n_column) ;
             } else if (c=='"' && lexema.length()<=2){
                sinalizaErro("Literal vazio identificado na linha " + n_line
                        + " e coluna " + n_column + ", os caracteres subsequentes montarão o novo Literal");
                lexema.delete(lexema.length()-1, 1);
                lexema.append(c);
             }else if (c == '\n' || c == '\r') {
                sinalizaErro("Literal "+ lexema +" não fechado na linha " + n_line
                        + " e coluna " + n_column + ", o token não será formado");
                lexema.delete(0, lexema.length());
                estado = 0;
                retornaPonteiro();

             } else if (lookahead == END_OF_FILE) {
                retornaPonteiro();

                sinalizaErro("Literal "+ lexema +" não fechado antes do final do arquivo na linha " + n_line
                        + " e coluna " + n_column + ", o token não será formado");
                lexema.delete(lexema.length(), 0);
                estado = 0;
             } else { // Se vier outro, permanece no estado
                lexema.append(c);

             }
             break;

          case 17:
             if (c == '=') {
                return new Token(Tag.RELOP_GE, ">=", n_line, n_column);
             } else {
                retornaPonteiro();
                return new Token(Tag.RELOP_GT, ">", n_line, n_column);
             }


          case 20:
             if (Character.isDigit(c)) {
                lexema.append(c);
             } else if (c == '.') {
                lexema.append(c);
                estado = 21;
             } else {
                retornaPonteiro();
                return new Token(Tag.NUMERICO, lexema.toString(), n_line, n_column);
             }
             break;

          case 21:
             if (Character.isDigit(c)) {
                lexema.append(c);
                estado = 23; // movimento para o estado 21
             } else {
                n_errors++;
                sinalizaErro("Simbolo " + c + " invalido na linha " + n_line
                        + " e coluna " + n_column + " era esperado um numeral");
                if (n_errors<1){
                   lexema.delete(lexema.length()-1, 1);
                   estado = 0;
                }else {
                   lexema.delete(lexema.length()-1, 2);
                   retornaPonteiro();
                   n_errors=0;
                   return new Token(Tag.NUMERICO, lexema.toString(), n_line, n_column);
                }
             }
             break;

          case 23:
             if (Character.isDigit(c)) {
                lexema.append(c);

             } else {
                return new Token(Tag.NUMERICO, lexema.toString(), n_line, n_column);
             }

       } // fim switch
      } // fim while
   } // fim getToken
}
