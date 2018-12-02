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

   // Obtém próximo token: Esse metodo eh a implementacao do AFD
   public Token getToken() {

	StringBuilder lexema = new StringBuilder();
	int estado = 1;
	char c;
		
	while(true) {
      c = '\u0000'; // null char
            
      // avanca caractere
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
            
      // movimentacao do automato
      switch(estado) {
         case 1:
            if(lookahead == END_OF_FILE) {
               return new Token(Tag.EOF, "EOF", n_line, n_column); // fim de arquivo: fim do lexer
            }
            else if (c == ' ' ||  c == '\b' || c == '\t' || c == '\n') {
               // Permance no estado = 1
               if(c == '\n')  {
               	n_line++;  
               	n_column = 1;           
               }
               else if(c == '\t') {
               	n_column += 2; // ja avancou uma coluna anteriormente, completar mais duas
               }
            }
            else if(Character.isLetter(c)) {
               lexema.append(c);
               estado = 17;
            }
            else if(Character.isDigit(c)) {
               lexema.append(c);
               estado = 19;
            }
            else if(c == '<') {
               estado = 6;
            }
            else if(c == '>') {
               estado = 9;
            }
            else if(c == '=') {
               estado = 12;
            }
            else if(c == '!') {
               estado = 15;
            }
            else if(c == '/') {
               //estado = 5;
               return new Token(Tag.RELOP_DIV, "/", n_line, n_column);
            }
            else if(c == '*') {
               //estado = 4;
               return new Token(Tag.RELOP_MULT, "*", n_line, n_column);
            }
            else if(c == '+') {
               //estado = 2;
               return new Token(Tag.RELOP_PLUS, "+", n_line, n_column);
            }
            else if(c == '-') {
               //estado = 3;
               return new Token(Tag.RELOP_MINUS, "-", n_line, n_column);
            }
            else if(c == ';') {
               //estado = 27;
               return new Token(Tag.SMB_SEMICOLON, ";", n_line, n_column);
            }
            else if(c == '(') {
               //estado = 28;
               return new Token(Tag.SMB_OP, "(", n_line, n_column);
            }
            else if(c == ')') {
               //estado = 29;
               return new Token(Tag.SMB_CP, ")", n_line, n_column);
            }
            else if(c == '"') {
               estado = 24;
            }
            else {
               sinalizaErro("Caractere invalido " + c + " na linha " + n_line + " e coluna " + n_column);
               return null;
            }
            break;
         case 6:
            if(c == '=') {
               //estado = 7;
			      return new Token(Tag.RELOP_LE, "<=", n_line, n_column);
            }
            else {
               //estado = 8;
			      retornaPonteiro();
			      return new Token(Tag.RELOP_LT, "<", n_line, n_column);
            }
         case 9:
            if(c == '=') {
               //estado = 10;
               return new Token(Tag.RELOP_GE, ">=", n_line, n_column);
            }
            else {
               //estado = 11;
               retornaPonteiro();
               return new Token(Tag.RELOP_GT, ">", n_line, n_column);
            }
         case 12:
            if(c == '=') {
               //estado = 13;
               return new Token(Tag.RELOP_LE, "==", n_line, n_column);
            }
            else {
            	//estado = 14
               retornaPonteiro();
               return new Token(Tag.RELOP_ASSIGN, "=", n_line, n_column);
            }
		   case 15:
            if(c == '=') {
               //estado = 16;
			      return new Token(Tag.RELOP_NE, "!=", n_line, n_column);
            }
            else {
               retornaPonteiro();
               sinalizaErro("Token incompleto para o caractere ! na linha " + n_line + " e coluna " + n_column);
			      return null;
            }
         case 17:
            if(Character.isLetterOrDigit(c) || c == '_') {
               lexema.append(c);
			      // Permanece no estado 17
             }
             else {
              //estado = 18;
			      retornaPonteiro();  
               Token token = tabelaSimbolos.retornaToken(lexema.toString()); // consulta TS
                        
               if(token == null) {
                  token = new Token(Tag.ID, lexema.toString(), n_line, n_column);
                  tabelaSimbolos.put(lexema.toString(), token);
               }
               else {
                  token.setLinha(n_line);
                  token.setColuna(n_column);
               }
               return token;
             }
             break;
         case 19:
            if(Character.isDigit(c)) {
               lexema.append(c);
               // Permanece no estado 19
            }
            else if(c == '.') {
               lexema.append(c);
               estado = 21;
            }
            else {
               //estado = 20;
               retornaPonteiro();						
               return new Token(Tag.INTEGER, lexema.toString(), n_line, n_column);
            }
            break;
         case 21:
            if(Character.isDigit(c)) {
               lexema.append(c);
               estado = 22;
            }
            else {
               sinalizaErro("Padrao para double invalido na linha " + n_line + " coluna " + n_column);
               return null;
            }
            break;
         case 22:
            if(Character.isDigit(c)) {
               lexema.append(c);
               // permanece no esrado 22
            }
            else {
            	//estado = 23;
               retornaPonteiro();						
               return new Token(Tag.DOUBLE, lexema.toString(), n_line, n_column);
            }
            break;
         case 24:
            if(c == '"') {
               sinalizaErro("String deve conter pelo menos um caractere. Erro na linha " + n_line + " coluna " + n_column);
               return null;
            }
            else if(c == '\n') {
               n_column--; // como nao ha modo panico, apenas nao deixa avancar a coluna
               sinalizaErro("Padrao para [ConstString] invalido na linha " + n_line + " coluna " + n_column);
               return null;
            }
            else if(lookahead == END_OF_FILE) {
               sinalizaErro("String deve ser fechada com \" antes do fim de arquivo");
               return null;
            }
            else {
               lexema.append(c);
               estado = 25;
            }
            break;
         case 25:
            if(c == '"') {
               //estado = 26;
               return new Token(Tag.STRING, lexema.toString(), n_line, n_column);
            }
            else if(c == '\n') {
               n_column--; // como nao ha modo panico, apenas nao deixa avancar a coluna
               sinalizaErro("Padrao para [ConstString] invalido na linha " + n_line + " coluna " + n_column);
               return null;
            }
            else if(lookahead == END_OF_FILE) {
               sinalizaErro("String deve ser fechada com \" antes do fim de arquivo");
               return null;
            }
            else {
               lexema.append(c);
               // permanece no estado 25
            }
            break;
         } // fim switch
      } // fim while
   } // fim getToken
}
