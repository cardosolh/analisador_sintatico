/**
 *
 * @author gustavo
 *
 * Implementacao com Modo Panico
 * [TODO]: tratar retorno 'null' do Lexer.
 *
 *
 * Modo Pânico: 
    * para tomar a decisao de escolher uma das regras (quando mais de uma disponivel),
    * temos que olhar para o FIRST(). Caso o token da entrada nao esteja no FIRST()
    * calculado, informamos uma mensagem de erro e inicia-se o Modo Panico:
    * [1] calculamos o FOLLOW do NAO-TERMINAL (a esquerda) da regra atual;
    * [2] se o token da entrada esta neste FOLLOW, desempilha-se o nao-terminal atual;
    * [3] caso contrario, avancamos a entrada para nova comparacao 
    * e mantemos o nao-terminal no topo da pilha (recursiva).
    * 
    * O Modo Panico encerra-se, automagicamente, quando um token esperado (FIRST)
    * ou (FOLLOW) aparece.
    * 
**/

public class Parser {

   private final Lexer lexer;
   private Token token;

   public Parser(Lexer lexer) {
      this.lexer = lexer;
      token = lexer.getToken(); // Leitura inicial obrigatoria do primeiro simbolo
      //System.out.println("[DEBUG]" + token.toString());
   }

   // Fecha os arquivos de entrada e de tokens
   public void fechaArquivos() {
      lexer.fechaArquivo();
   }

   public void erroSintatico(String mensagem) {

      System.out.print("[Erro Sintatico] na linha " + token.getLinha() + " e coluna " + token.getColuna() + ": ");
      System.out.println(mensagem + "\n");
   }
    
   public void advance() {
   	token = lexer.getToken();
   	//System.out.println("[DEBUG]" + token.toString());
   }
    
	public void skip(String mensagem) {
    	erroSintatico(mensagem);
    	advance();
	}

	// verifica token esperado t
   public boolean eat(Tag t) {
		if(token.getClasse() == t) {
			advance();
			return true;
		} 
		else {
			return false;
		}
   }
      
    /*
    // Todas as decisoes do Parser, sao guiadas
    // pela Tabela Preditiva.
    //
    */

   // Programa --> Classe
   public void Programa() {
   	//System.out.println("[DEBUG] Programa()");
    	
      // OBS.: vimos que para o Nao-Terminal Inicial, eh melhor chamar o metodo skip()
      // para nao prejudicar a leitura no restante do codigo.
    	if(token.getClasse() != Tag.KW_PUBLIC) // FISRT(Programa)
			skip("Esperado \"public\", encontrado " + "\"" + token.getLexema() + "\"");

      Classe();
   }

   // Classe --> "public" "class" ID ListaDeclaraVar ListaCmd "end"
   public void Classe() {
		//System.out.println("[DEBUG] Classe()");
		
      /* OBS.: vimos que para o primeiro, eh melhor chamar o metodo skip()
      * para nao prejudicar a leitura no restante do codigo.
      * Se percebermos na TP, 'Programa' e 'Classe' possuem os mesmos
      * FIRST e FOLLOW. Entao a regra para se analisar a sincronizacao no 
      * primeiro instante em que entra nesses metodos eh a mesma.
      */
		if(!eat(Tag.KW_PUBLIC)) {
			skip("Esperado \"public\", encontrado "  + "\"" + token.getLexema() + "\"");
      }

		if(!eat(Tag.KW_CLASS)) { // espera "class"
			
         /* ATENCAO: no caso 'terminal esperado' vs 'terminal na entrada', de acordo com vimos em sala:
			// o terminal esperado não casou com o terminal da entrada,
			// dai vamos simular o 'desempilha terminal',
			// isto eh, continue a varredura, mantendo a entrada.
			*/
      	erroSintatico("Esperado \"class\", encontrado "  + "\"" + token.getLexema() + "\"");
      }
      	
      if(!eat(Tag.ID)) { // espera "ID"
      	erroSintatico("Esperado um \"ID\", encontrado "  + "\"" + token.getLexema() + "\"");
      } 
      
     	ListaDeclaraVar();
      ListaCmd();
      
      if(!eat(Tag.KW_END)) { // espera "end"
      	erroSintatico("Esperado \"end\", encontrado "  + "\"" + token.getLexema() + "\"");
     	}
	}
    
    //ListaDeclaraVar --> TipoPrimitivo ID ";" ListaDeclaraVar | epsilon
    public void ListaDeclaraVar() {
    	//System.out.println("[DEBUG] ListaDeclaraVar()");
    	
    	//ListaDeclaraVar --> TipoPrimitivo ID ";" ListaDeclaraVar
    	if(token.getClasse() == Tag.KW_INTEGER || token.getClasse() == Tag.KW_DOUBLE || token.getClasse() == Tag.KW_STRING) {
      	
        	TipoPrimitivo();
        	
        	if(!eat(Tag.ID)) erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
        	if(!eat(Tag.SMB_SEMICOLON)) erroSintatico("Esperado \";\", encontrado "  + "\"" + token.getLexema() + "\"");
        	
        	ListaDeclaraVar();
      }
      //ListaDeclaraVar --> epsilon
      else if(token.getClasse() == Tag.KW_END || token.getClasse() == Tag.KW_SYSTEMOUTDISPLN || token.getClasse() == Tag.ID) {
			return;		      
      }
      else {
         /* Percebemos na TP que o unico metodo a ser chamado no caso de erro, seria o skip(). 
         * Mas a ideia do skip() eh avancar a entrada sem retirar ListaDeclaraVar() da pilha
         * (recursiva). So que chegamos ao fim do metodo ListaDeclaraVar(). Como podemos
         * mante-lo na pilha recursiva? 
         * Simples, chamamos skip() e o proprio metodo ListaDeclaraVar().
         */
         
      	skip("Esperado \"Integer, Double, String, end, SystemOutDispln, ID\", encontrado " + "\"" + token.getLexema() + "\"");
         if(token.getClasse() != Tag.EOF) ListaDeclaraVar();
      }
    }
    
    // TipoPrimitivo --> integer | string | double
    public void TipoPrimitivo() {
    	//System.out.println("[DEBUG] TipoPrimitivo()");
	
		if(!eat(Tag.KW_INTEGER) && !eat(Tag.KW_DOUBLE) && !eat(Tag.KW_STRING)) {  
			
         // synch: FOLLOW(TipoPrimitivo): tira 'TipoPrimitivo()' da pilha recursiva
         if(token.getClasse() == Tag.ID) {
            erroSintatico("Esperado \"Integer, Double, String\", encontrado "  + "\"" + token.getLexema() + "\"");
            // return;
         }
         else {
            skip("Esperado \"Integer, Double, String\", encontrado "  + "\"" + token.getLexema() + "\"");
            TipoPrimitivo(); // mantem 'TipoPrimitivo()' na pilha recursiva
         }
      }
    }
    
    // ListaCmd --> CmdDispln ListaCmd | CmdAtrib ListaCmd | epsilon 
    public void ListaCmd() {
     //System.out.println("[DEBUG] ListaCmd()");
      
    	// ListaCmd --> CmdDispln ListaCmd
      if(token.getClasse() == Tag.KW_SYSTEMOUTDISPLN) {
      	CmdDispln();
         ListaCmd();
      }
      // ListaCmd --> CmdAtrib ListaCmd
      else if(token.getClasse() == Tag.ID) {
			CmdAtrib();
			ListaCmd();	        
      }
      // ListaCmd --> epsilon
      else if(token.getClasse() == Tag.KW_END) return;
      else {
        skip("Esperado \"SystemOutDispln, ID, end\", encontrado "  + "\"" + token.getLexema() + "\"");
        if(token.getClasse() != Tag.EOF) ListaCmd(); // mantem 'ListaCmd()' na pilha recursiva
      }
    }

	// CmdDispln --> "SystemOutDispln" "(" Expressao ")" ";"
   public void CmdDispln() {	
		//System.out.println("[DEBUG] CmdDispln()");
		  
		if(eat(Tag.KW_SYSTEMOUTDISPLN)) {
		
			if(!eat(Tag.SMB_OP)) {
				erroSintatico("Esperado \"(\", encontrado "  + "\"" + token.getLexema() + "\"");
			}
			Expressao();
			if(!eat(Tag.SMB_CP)) {
				erroSintatico("Esperado \")\", encontrado "  + "\"" + token.getLexema() + "\"");
			}
			if(!eat(Tag.SMB_SEMICOLON)) {
				erroSintatico("Esperado \";\", encontrado "  + "\"" + token.getLexema() + "\"");
			}	
		} 
		else {
         // synch: FOLLOW(CmdDispln)
         if(token.getClasse() == Tag.KW_SYSTEMOUTDISPLN || token.getClasse() == Tag.ID || 
              token.getClasse() == Tag.KW_END ) {
            erroSintatico("Esperado \"SystemOutDispln\", encontrado "  + "\"" + token.getLexema() + "\"");
            //return;
         }	
         else {
            skip("Esperado \"SystemOutDispln\", encontrado "  + "\"" + token.getLexema() + "\"");
            if(token.getClasse() != Tag.EOF) ListaCmd(); // mantem 'ListaCmd()' na pilha recursiva
         } 
		}
	}

	// CmdAtrib --> ID "=" Expressao ";"
   public void CmdAtrib() {
   	//System.out.println("[DEBUG] CmdAtrib()");
   	
   	if(eat(Tag.ID)) {
   		if(!eat(Tag.RELOP_ASSIGN)) erroSintatico("Esperado \"=\", encontrado "  + "\"" + token.getLexema() + "\"");
			Expressao();
			if(!eat(Tag.SMB_SEMICOLON)) erroSintatico("Esperado \";\", encontrado "  + "\"" + token.getLexema() + "\"");
   	}
   	else {
         // synch: FOLLOW(CmdAtrib)
         if(token.getClasse() == Tag.KW_SYSTEMOUTDISPLN || token.getClasse() == Tag.ID 
                 || token.getClasse() == Tag.KW_END) {
            erroSintatico("Esperado \"ID\", encontrado "  + "\"" + token.getLexema() + "\"");
            // return;
         }
         else {
            skip("Esperado \"ID\", encontrado "  + "\"" + token.getLexema() + "\"");
            if(token.getClasse() != Tag.EOF) CmdAtrib();
         }
   	}
   }
    
	// Expressao --> Expressao1 Expressao'
	public void Expressao() {
		//System.out.println("[DEBUG] Expressao()");
		
		if(token.getClasse() == Tag.INTEGER || token.getClasse() == Tag.DOUBLE || 
			token.getClasse() == Tag.STRING || token.getClasse() == Tag.ID) {
			Expressao1();
			ExpressaoLinha();
		}
		else {
         // synch: FOLLOW(Expressao)
         if(token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON) {
            erroSintatico("Esperado \"Inteito, Double, String, ID\", encontrado "  + "\"" + token.getLexema() + "\"");
            // return;
         }
         else {
            skip("Esperado \"Inteito, Double, String, ID\", encontrado "  + "\"" + token.getLexema() + "\"");
            if(token.getClasse() != Tag.EOF) Expressao();
         }
		}
	}
    
	// Expressao' --> ">" Expressao1 Expressao'  | "<" Expressao1 Expressao' | 
	//						">=" Expressao1 Expressao' | "<=" Expressao1 Expressao'| 
   //						"==" Expressao1 Expressao' | "!=" Expressao1 Expressao' | epsilon
   public void ExpressaoLinha() {
   	//System.out.println("[DEBUG] Expressao'()");
   	
   	if(eat(Tag.RELOP_GT) || eat(Tag.RELOP_LT) || eat(Tag.RELOP_GE) || 
   		eat(Tag.RELOP_LE) || eat(Tag.RELOP_EQ) || eat(Tag.RELOP_NE)) {
			Expressao1();
			ExpressaoLinha();   	
   	}
   	else if(token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON) return;
		else {
			skip("Esperado \">, <, >=, <=, ==, !=, ), ;\", encontrado "  + "\"" + token.getLexema() + "\"");
			if(token.getClasse() != Tag.EOF) ExpressaoLinha();	
		}
   }
    
	// Expressao1 --> Expressao2 Expressao1'
	public void Expressao1() {
  		//System.out.println("[DEBUG] Expressao1()");
		if(token.getClasse() == Tag.INTEGER || token.getClasse() == Tag.DOUBLE || 
			token.getClasse() == Tag.STRING || token.getClasse() == Tag.ID)  {		
  		
			Expressao2();
			Expressao1Linha();
		}
		else {
         // synch: FOLLOW(Expressao1)
         if(token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON || 
   			token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT || 
   			token.getClasse() == Tag.RELOP_GE || token.getClasse() == Tag.RELOP_LE || 
   			token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE) {
            
            erroSintatico("Esperado \"Inteito, Double, String ou ID\", encontrado "  + "\"" + token.getLexema() + "\"");
            //return;
         }
         else {
            skip("Esperado \"Inteito, Double, String ou ID\", encontrado "  + "\"" + token.getLexema() + "\"");
            if(token.getClasse() != Tag.EOF) Expressao1();
         }	
		}
    }
    
	// Expressao1' --> "+" Expressao2 Expressao1' | "-" Expressao2 Expressao1' | epsilon
	public void Expressao1Linha() {
		//System.out.println("[DEBUG] Expressao1'()");
		
   	if(eat(Tag.RELOP_PLUS) || eat(Tag.RELOP_MINUS)) {
			Expressao2();
			Expressao1Linha();   	
   	}
   	else if(token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON || 
   			token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT || 
   			token.getClasse() == Tag.RELOP_GE || token.getClasse() == Tag.RELOP_LE || 
   			token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE) return;
		else {
			skip("Esperado \"+, -, ), ;, >, <, >=, <=, ==, !=\", encontrado "  + "\"" + token.getLexema() + "\"");
			if(token.getClasse() != Tag.EOF) Expressao1Linha();		
		}     
	}
    
   // Expressao2 --> Expressao3 Expressao2'
   public void Expressao2() {
      //System.out.println("[DEBUG] Expressao2()");
      if(token.getClasse() == Tag.INTEGER || token.getClasse() == Tag.DOUBLE || 
			token.getClasse() == Tag.STRING || token.getClasse() == Tag.ID)  {		
  		
			Expressao3();
			Expressao2Linha(); 
		}
		else {
			// synch: FOLLOW(Expressao2)
         if(token.getClasse() == Tag.RELOP_PLUS || token.getClasse() == Tag.RELOP_MINUS || 
				token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON || 
				token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT || 
				token.getClasse() == Tag.RELOP_GE || token.getClasse() == Tag.RELOP_LE || 
				token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE) {
            
            erroSintatico("Esperado \"Inteito, Double, String ou ID\", encontrado "  + "\"" + token.getLexema() + "\"");
            // return;
         }
         else {
            skip("Esperado \"Inteito, Double, String ou ID\", encontrado "  + "\"" + token.getLexema() + "\"");
            if(token.getClasse() != Tag.EOF) Expressao2();
         }	
		}
	}
    
	// Expressao2' --> "*" Expressao3 Expressao2' | "/" Expressao3 Expressao2' | epsilon
	public void Expressao2Linha() {
		//System.out.println("[DEBUG] Expressao2'()");
		
		if(eat(Tag.RELOP_MULT) || eat(Tag.RELOP_DIV)) {
			Expressao3();
			Expressao2Linha();
		}
		else if(token.getClasse() == Tag.RELOP_PLUS || token.getClasse() == Tag.RELOP_MINUS || 
				token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON || 
				token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT || 
				token.getClasse() == Tag.RELOP_GE || token.getClasse() == Tag.RELOP_LE || 
				token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE) return;
		else {
			skip("Esperado \"*, /, +, -, ), ;, >, <, >=, <=, ==, !=\", encontrado "  + "\"" + token.getLexema() + "\"");
         if(token.getClasse() != Tag.EOF) Expressao2Linha();
		}
	}
	
	// Expressao3 --> ConstNumInt | ConstNumDouble | ConstString | ID 
	public void Expressao3() {
		//System.out.println("[DEBUG] Expressao3()");
		
		if(!eat(Tag.INTEGER) && !eat(Tag.DOUBLE) && !eat(Tag.STRING) && !eat(Tag.ID)) {
			
			// synch: FOLLOW(Expressao3)
         if(token.getClasse() == Tag.RELOP_MULT || token.getClasse() == Tag.RELOP_DIV ||
            token.getClasse() == Tag.RELOP_PLUS || token.getClasse() == Tag.RELOP_MINUS || 
				token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON || 
				token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT || 
				token.getClasse() == Tag.RELOP_GE || token.getClasse() == Tag.RELOP_LE || 
				token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE) {
               
            erroSintatico("Esperado \"Inteito, Double, String ou ID\", encontrado "  + "\"" + token.getLexema() + "\"");
            // return;
         }
         else {
            skip("Esperado \"Inteito, Double, String ou ID\", encontrado "  + "\"" + token.getLexema() + "\"");
            if(token.getClasse() != Tag.EOF) Expressao3();
         }
		}
	}
}
