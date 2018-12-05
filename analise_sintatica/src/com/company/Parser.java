package com.company;

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
      System.out.println("[DEBUG]" + token.toString());
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
   	System.out.println("[DEBUG]" + token.toString());
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


    //Compilador →Programa $ [1]
	public void Compilador(){
        System.out.println("[DEBUG] Compilador()");
	    if(token.getClasse() != Tag.KW_ALGORITMO) // FISRT(Programa)
            skip("Esperado \"algoritmo\", encontrado " + "\"" + token.getLexema() + "\"");
        System.out.println("[DEBUG] Compilador()");
        Programa();

	}

	//Programa → "algoritmo" RegexDeclVar ListaCmd "fim" "algoritmo" ListaRotina [2]
	public void Programa() {
    System.out.println("[DEBUG] Programa()");

        if(eat(Tag.KW_ALGORITMO)) {RegexDeclVar();
            ListaCmd();

            if(!eat(Tag.KW_FIM)) { // espera "fim"
                erroSintatico("Esperado \"fim\", encontrado "  + "\"" + token.getLexema() + "\"");
            }

            if(!eat(Tag.KW_ALGORITMO)) {
                skip("Esperado \"algoritmo\", encontrado "  + "\"" + token.getLexema() + "\"");
            }
            ListaRotina();

        }


	}

    //RegexDeclVar → “declare” Tipo ListaID";" DeclaraVar  [3] | ε [4]
	public void RegexDeclVar() {

        System.out.println("[DEBUG] RegexDeclVar()");
        //RegexDeclVar → “declare” Tipo ListaID";" DeclaraVar  [3]
        if(token.getClasse() == Tag.KW_DECLARE) {
            if(!eat(Tag.KW_DECLARE)) {
                skip("Esperado \"declare\", encontrado "  + "\"" + token.getLexema() + "\"");
            }
            Tipo();
			ListaID();
            if(!eat(Tag.SMB_SEMICOLON)) {
                skip("Esperado \";\", encontrado "  + "\"" + token.getLexema() + "\"");
            }
            DeclaraVar();
        }
        //RegexDeclVar → ε [4]
        else if(token.getClasse() == Tag.KW_FIM || token.getClasse() == Tag.ID ||
                token.getClasse() == Tag.KW_RETORNE || token.getClasse() == Tag.KW_SE ||
                token.getClasse() == Tag.KW_ENQUANTO||
                token.getClasse() == Tag.KW_PARA || token.getClasse() == Tag.KW_REPITA ||
                token.getClasse() == Tag.KW_ESCREVA || token.getClasse() == Tag.KW_LEIA) {
            return;
        }
        else {
            skip("Esperado \"fim, ID, retorne, se, enquanto, para, repita, escreva, leia\", encontrado " + "\"" + token.getLexema() + "\"");
            if(token.getClasse() != Tag.EOF) RegexDeclVar();
        }
	}

	//DeclaraVar → Tipo ListaID ";" DeclaraVar  [5] | ε [6]
	public void DeclaraVar() {
		System.out.println("[DEBUG] DeclaraVar()");
		//DeclaraVar → Tipo ListaID ";" DeclaraVar  [5]
        if( token.getClasse() == Tag.KW_LOGICO || token.getClasse() == Tag.KW_NUMERICO ||
            token.getClasse() == Tag.KW_LITERAL || token.getClasse() == Tag.KW_NULO) { // FIRST(Tipo)
            Tipo();
            ListaID();
			if(!eat(Tag.SMB_SEMICOLON)) {
				skip("Esperado \";\", encontrado "  + "\"" + token.getLexema() + "\"");
			}
            DeclaraVar();
        }
        //DeclaraVar → ε [6]
        else if(token.getClasse() == Tag.KW_FIM || token.getClasse() == Tag.ID ||
                token.getClasse() == Tag.KW_RETORNE || token.getClasse() == Tag.KW_SE ||
                token.getClasse() == Tag.KW_ENQUANTO||
                token.getClasse() == Tag.KW_PARA || token.getClasse() == Tag.KW_REPITA ||
                token.getClasse() == Tag.KW_ESCREVA || token.getClasse() == Tag.KW_LEIA) {
            return;
        }
        else {
            skip("Esperado \"fim, ID, retorne, se, enquanto, para, repita, escreva, leia\", encontrado " + "\"" + token.getLexema() + "\"");
            if(token.getClasse() != Tag.EOF) DeclaraVar();
        }
	}

	//ListaRotina → ListaRotinaLinha [7]
	public void ListaRotina() {
		System.out.println("[DEBUG] ListaRotina()");
		if(token.getClasse() != Tag.KW_SUBROTINA || token.getClasse() != Tag.EOF) // FISRT(ListaRotinaLinha)
			ListaRotinaLinha();

	}

	//ListaRotinaLinha → Rotina ListaRotinaLinha [8] | ε [9]
	public void ListaRotinaLinha() {
		System.out.println("[DEBUG] ListaRotinaLinha()");

		//ListaRotinaLinha → Rotina ListaRotinaLinha [8]
		if(token.getClasse() == Tag.KW_SUBROTINA){ // FIRST(Rotina)

			Rotina();
			ListaRotinaLinha();
		}
		//ListaRotinaLinha → ε [9]
		else if(token.getClasse() == Tag.EOF)
			if(!eat(Tag.EOF)) {
				skip("Esperado \"EOF\", encontrado "  + "\"" + token.getLexema() + "\"");
			}

	}

	//Rotina → "subrotina" ID "(" ListaParam ")" RegexDeclVar ListaCmd Retorno "fim" "subrotina" [10]
	public void Rotina() {
        System.out.println("[DEBUG] Rotina()");
        if(eat(Tag.KW_SUBROTINA)){
            if(!eat(Tag.ID))
                erroSintatico("Esperado \"ID\", encontrado "  + "\"" + token.getLexema() + "\"");

            if(!eat(Tag.SMB_OP))
                erroSintatico("Esperado \"(\", encontrado "  + "\"" + token.getLexema() + "\"");
            ListaParam();
            if(!eat(Tag.SMB_CP))
                erroSintatico("Esperado \")\", encontrado "  + "\"" + token.getLexema() + "\"");
            RegexDeclVar();
            ListaCmd();
            Retorno();
            if(!eat(Tag.KW_FIM))
                erroSintatico("Esperado \"fim\", encontrado "  + "\"" + token.getLexema() + "\"");
            if(!eat(Tag.KW_SUBROTINA))
                erroSintatico("Esperado \"subrotina\", encontrado "  + "\"" + token.getLexema() + "\"");
        }else{
            if(token.getClasse() == Tag.KW_SUBROTINA || token.getClasse() == Tag.EOF) {
                erroSintatico("Esperado \"subrotina, EOF\", encontrado "  + "\"" + token.getLexema() + "\"");
                // return;
            }
            else {
                skip("Esperado \"subrotina\", encontrado "  + "\"" + token.getLexema() + "\"");
                if(token.getClasse() != Tag.EOF) Rotina();
            }
        }

	}

	//ListaParam → Param ListaParamLinha [11]
	public void ListaParam() {
        System.out.println("[DEBUG] ListaParam()");
        if(token.getClasse() != Tag.ID){ // FISRT(Param)
            Param();
            ListaParamLinha();
	    }

	}

	//ListaParamLinha → "," ListaParam [12] | ε [13]
	public void ListaParamLinha() {
        System.out.println("[DEBUG] ListaParamLinha()");
	    if(eat(Tag.SMB_COMMA))
	        ListaParam();
	    else if(token.getClasse() == Tag.SMB_CP)
	        return;
	}

	//Param → ListaID Tipo [14]
	public void Param() {
        System.out.println("[DEBUG] Param()");
	    if(token.getClasse() == Tag.ID){
	        ListaID();
	        Tipo();
        }

	}

	public void ListaID() {
        System.out.println("[DEBUG] ListaID()");

        if (eat(Tag.ID)) {
            ListaIDLinha();
        } else {
            if (token.getClasse() == Tag.SMB_SEMICOLON || token.getClasse() == Tag.KW_LOGICO
                    || token.getClasse() == Tag.NUMERICO || token.getClasse() == Tag.LITERAL
                    || token.getClasse() == Tag.KW_NULO) {
                erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
            } else {
                skip("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
                if (token.getClasse() != Tag.EOF) {
                    ListaID();
                }
                if (token.getClasse() == Tag.EOF) {
                    System.exit(0);
                }
            }
        }
    }

	//ListaIDLinha → "," ListaID [16] | ε [17]
	public void  ListaIDLinha(){
		System.out.println("[DEBUG] ListaIDLinha()");

		//ListaIDLinha → "," ListaID [16]
		if(eat(Tag.SMB_COMMA)) {
			ListaID();
		}
		//ListaIDLinha → ε [17]
		else if(token.getClasse() == Tag.SMB_SEMICOLON || token.getClasse() == Tag.KW_LOGICO ||
				token.getClasse() == Tag.NUMERICO || token.getClasse() == Tag.LITERAL ||
				token.getClasse() == Tag.KW_NULO) {
			return;
		}
		else {
			skip("Esperado \"--,--, ;, logico, numerico, literal, nulo\", encontrado " + "\"" + token.getLexema() + "\"");
			if(token.getClasse() != Tag.EOF) ListaIDLinha();
		}


	}

	//Retorno → "retorne" Expressao [18] | ε [19]
	public void Retorno(){
        System.out.println("[DEBUG] Retorno()");

        //Retorno → "retorne" Expressao [18]
        if(token.getClasse() == Tag.KW_RETORNE){
            if(!eat(Tag.KW_RETORNE)) {
                skip("Esperado \"Retorne\", encontrado "  + "\"" + token.getLexema() + "\"");
            }
            Expressao();
        }
        //Retorno → ε [19]
        else if(token.getClasse() == Tag.KW_FIM)
            return;
        else{
            skip("Esperado \"retorne, fim\", encontrado " + "\"" + token.getLexema() + "\"");
            if(token.getClasse() != Tag.EOF) Retorno();
        }


	}

	//Tipo → 	"logico" [20] | "numerico" [21] |
	// 			"literal" [22] | "nulo" [23]
	public void Tipo() {
		System.out.println("[DEBUG] Tipo()");

		if(	!eat(Tag.KW_LOGICO) && !eat(Tag.KW_NUMERICO) &&
			!eat(Tag.KW_LITERAL) && !eat(Tag.KW_NULO)) {

			// synch: FOLLOW(Tipo)
			if(token.getClasse() == Tag.ID || token.getClasse() == Tag.SMB_COMMA ||
					token.getClasse() == Tag.SMB_CP) {

				erroSintatico("Esperado \"ID, --,-- , )\", encontrado "  + "\"" + token.getLexema() + "\"");
				//return;
			}
			else {
				skip("Esperado \"Esperado \"ID, --,-- , )\", encontrado  "  + "\"" + token.getLexema() + "\"");
				if(token.getClasse() != Tag.EOF) Tipo();
			}
		}
	}

	//ListaCmd → ListaCmdLinha [24]
	public void ListaCmd() {
        System.out.println("[DEBUG] ListaCmd()");
        if( token.getClasse() == Tag.KW_FIM || token.getClasse() == Tag.ID ||
            token.getClasse() == Tag.KW_RETORNE || token.getClasse() == Tag.KW_SE ||
            token.getClasse() == Tag.KW_ENQUANTO || token.getClasse() == Tag.KW_PARA ||
            token.getClasse() == Tag.KW_ATE || token.getClasse() == Tag.KW_REPITA ||
            token.getClasse() == Tag.KW_ESCREVA || token.getClasse() == Tag.KW_LEIA)
                ListaCmdLinha();
        else {

            if( token.getClasse() == Tag.KW_FIM || token.getClasse() == Tag.KW_RETORNE ||
                token.getClasse() == Tag.KW_ATE) {
                erroSintatico("Esperado \"fim, ID, retorne, se, enquanto, para, ate, repita, escreva, leia\", encontrado "  + "\"" + token.getLexema() + "\"");
                // return;
            }
            else {
                skip("Esperado \"fim, ID, retorne, se, enquanto, para, ate, repita, escreva, leia\", encontrado "  + "\"" + token.getLexema() + "\"");
                if(token.getClasse() != Tag.EOF) ListaCmd();
            }
        }
	}

	//ListaCmdLinha → Cmd ListaCmdLinha [25] | ε [26]
	public void ListaCmdLinha(){
        System.out.println("[DEBUG] ListaCmdLinha()");
        //ListaCmdLinha → Cmd ListaCmdLinha [25]
        if( token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_SE ||
            token.getClasse() == Tag.KW_ENQUANTO || token.getClasse() == Tag.KW_PARA ||
            token.getClasse() == Tag.KW_REPITA ||
            token.getClasse() == Tag.KW_ESCREVA || token.getClasse() == Tag.KW_LEIA){
            Cmd();
            ListaCmdLinha();

        }//ListaCmdLinha → ε [26]
        else if( token.getClasse() == Tag.KW_FIM || token.getClasse() == Tag.KW_RETORNE ||
                token.getClasse() == Tag.KW_ENQUANTO || token.getClasse() == Tag.KW_ATE)
            return;
        else {
            skip("Esperado \"ID, se, enquanto, para, repita, escreva, leia, fim, retorne, enquanto, ate\", encontrado " + "\"" + token.getLexema() + "\"");
            if(token.getClasse() != Tag.EOF) ListaCmdLinha();
        }

	}
	//Cmd →	CmdSe [27] | CmdEnquanto [28] |
	// 		CmdPara [29] | CmdRepita [30] |
	//		ID Cmdlinha [31] | CmdEscreva [32] |
	//		CmdLeia [33]

	public void Cmd() {
        System.out.println("[DEBUG] Cmd()");
        //Cmd →	CmdSe [27]
        if(token.getClasse()==Tag.KW_SE)
            CmdSe();
        //Cmd →	CmdEnquanto [28]
        else if(token.getClasse()==Tag.KW_ENQUANTO)
            CmdEnquanto();
        //Cmd → CmdPara [29]
        else if(token.getClasse()==Tag.KW_PARA)
            CmdPara();
        //Cmd →	CmdRepita [30]
        else if(token.getClasse()==Tag.KW_REPITA)
            CmdRepita();
        //Cmd →	Cmdlinha [31]
        else if(eat(Tag.ID))
            CmdLinha();
        //Cmd →	CmdEscreva [32]
        else if(token.getClasse()==Tag.KW_ESCREVA)
            CmdEscreva();
        //Cmd →	CmdLeia [33]
        else if(token.getClasse()==Tag.KW_LEIA)
            CmdLeia();
        else{
            if (token.getClasse() == Tag.KW_FIM || token.getClasse() ==Tag.ID ||
                    token.getClasse() == Tag.KW_RETORNE || token.getClasse() == Tag.KW_SE ||
                    token.getClasse() == Tag.KW_ENQUANTO || token.getClasse() == Tag.KW_PARA ||
                    token.getClasse() == Tag.KW_ATE || token.getClasse() == Tag.KW_REPITA ||
                    token.getClasse() == Tag.KW_ESCREVA || token.getClasse() == Tag.KW_LEIA) {
                erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
            }
        }

	}

	//CmdLinha → CmdAtrib [34] | CmdChamaRotina [35]
	public void CmdLinha() {
        System.out.println("[DEBUG] CmdAtrib()");
        //CmdLinha → CmdAtrib [34]
        if (token.getClasse() == Tag.RELOP_ASSIGN)
            CmdAtrib();
        //CmdLinha → CmdChamaRotina [35]
	    else if (token.getClasse() == Tag.SMB_OP)
            CmdChamaRotina();
	}

	//CmdSe → "se" "(" Expressao ")" "inicio" ListaCmd "fim" CmdSeLinha [36]
	public void CmdSe(){
        System.out.println("[DEBUG] CmdSe()");
	    if(eat(Tag.KW_SE)){
            if(!eat(Tag.SMB_OP))
                erroSintatico("Esperado \"(\", encontrado " + "\"" + token.getLexema() + "\"");
            Expressao();
            if(!eat(Tag.SMB_CP))
                erroSintatico("Esperado \")\", encontrado " + "\"" + token.getLexema() + "\"");
            if(!eat(Tag.KW_INICIO))
                erroSintatico("Esperado \"inicio\", encontrado " + "\"" + token.getLexema() + "\"");
            ListaCmd();
            if(!eat(Tag.KW_FIM))
                erroSintatico("Esperado \"fim\", encontrado " + "\"" + token.getLexema() + "\"");
            CmdSeLinha();
	    }else{
            if (token.getClasse() == Tag.KW_FIM || token.getClasse() ==Tag.ID ||
                    token.getClasse() == Tag.KW_RETORNE || token.getClasse() == Tag.KW_SE ||
                    token.getClasse() == Tag.KW_ENQUANTO || token.getClasse() == Tag.KW_PARA ||
                    token.getClasse() == Tag.KW_ATE || token.getClasse() == Tag.KW_REPITA ||
                    token.getClasse() == Tag.KW_ESCREVA || token.getClasse() == Tag.KW_LEIA) {
                erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
            }
        }

	}

	//CmdSeLinha → "senao" "inicio" ListaCmd "fim" [37] | ε [38]
	public void CmdSeLinha() {
        System.out.println("[DEBUG] CmdSeLinha()");
	    //CmdSeLinha → "senao" "inicio" ListaCmd "fim" [37]
	    if(token.getClasse() == Tag.KW_SENAO){
	        if(!eat(Tag.KW_SENAO))
                erroSintatico("Esperado \"senao\", encontrado " + "\"" + token.getLexema() + "\"");
            if(!eat(Tag.KW_INICIO))
                erroSintatico("Esperado \"inicio\", encontrado " + "\"" + token.getLexema() + "\"");
            ListaCmd();
            if(!eat(Tag.KW_FIM))
                erroSintatico("Esperado \"fim\", encontrado " + "\"" + token.getLexema() + "\"");
        }
        //CmdSeLinha → ε [38]
	    else if(    token.getClasse() == Tag.KW_FIM || token.getClasse() ==Tag.ID ||
                    token.getClasse() == Tag.KW_RETORNE || token.getClasse() == Tag.KW_SE ||
                    token.getClasse() == Tag.KW_ENQUANTO || token.getClasse() == Tag.KW_PARA ||
                    token.getClasse() == Tag.KW_ATE || token.getClasse() == Tag.KW_REPITA ||
                    token.getClasse() == Tag.KW_ESCREVA || token.getClasse() == Tag.KW_LEIA) return;
	    else{
            skip("Esperado \"senao\", encontrado " + "\"" + token.getLexema() + "\"");
            if(token.getClasse() != Tag.EOF) CmdSeLinha();
        }
	}

	//CmdEnquanto → "enquanto" "(" Expressao ")" "faca" "inicio" ListaCmd "fim" [39]
	public void CmdEnquanto(){
        System.out.println("[DEBUG] CmdEnquanto()");

            if(eat(Tag.KW_ENQUANTO)) {
                erroSintatico("Esperado \"enquanto\", encontrado " + "\"" + token.getLexema() + "\"");
                if (!eat(Tag.SMB_OP))
                    erroSintatico("Esperado \"(\", encontrado " + "\"" + token.getLexema() + "\"");
                Expressao();
                if (!eat(Tag.SMB_CP))
                    erroSintatico("Esperado \")\", encontrado " + "\"" + token.getLexema() + "\"");
                if (!eat(Tag.KW_FACA))
                    erroSintatico("Esperado \"faca\", encontrado " + "\"" + token.getLexema() + "\"");
                if (!eat(Tag.KW_INICIO))
                    erroSintatico("Esperado \"inicio\", encontrado " + "\"" + token.getLexema() + "\"");
                ListaCmd();
                if (!eat(Tag.KW_FIM))
                    erroSintatico("Esperado \"fim\", encontrado " + "\"" + token.getLexema() + "\"");
            } else
                {
                    if (token.getClasse() == Tag.KW_FIM || token.getClasse() ==Tag.ID ||
                    token.getClasse() == Tag.KW_RETORNE || token.getClasse() == Tag.KW_SE ||
                    token.getClasse() == Tag.KW_ENQUANTO || token.getClasse() == Tag.KW_PARA ||
                    token.getClasse() == Tag.KW_ATE || token.getClasse() == Tag.KW_REPITA ||
                    token.getClasse() == Tag.KW_ESCREVA || token.getClasse() == Tag.KW_LEIA)
                        {
                        erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
                    }
                }
            }

	//CmdPara → "para" ID CmdAtrib "ate" Expressao "faca" "inicio" ListaCmd "fim" [40]
    public void CmdPara() {
        System.out.println("[DEBUG] CmdPara()");
        if (eat(Tag.KW_PARA)) {
            if(!eat(Tag.ID))
                erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
            CmdAtrib();
            if(!eat(Tag.KW_ATE))
                erroSintatico("Esperado \"ate\", encontrado " + "\"" + token.getLexema() + "\"");
            Expressao();
            if(!eat(Tag.KW_FACA))
                erroSintatico("Esperado \"faca\", encontrado " + "\"" + token.getLexema() + "\"");
            if(!eat(Tag.KW_INICIO))
                erroSintatico("Esperado \"inicio\", encontrado " + "\"" + token.getLexema() + "\"");
            ListaCmd();
            if(!eat(Tag.KW_FIM))
                erroSintatico("Esperado \"fim\", encontrado " + "\"" + token.getLexema() + "\"");
        } else {
            if (token.getClasse() == Tag.KW_FIM || token.getClasse() ==Tag.ID ||
                    token.getClasse() == Tag.KW_RETORNE || token.getClasse() == Tag.KW_SE ||
                    token.getClasse() == Tag.KW_ENQUANTO || token.getClasse() == Tag.KW_PARA ||
                    token.getClasse() == Tag.KW_ATE || token.getClasse() == Tag.KW_REPITA ||
                    token.getClasse() == Tag.KW_ESCREVA || token.getClasse() == Tag.KW_LEIA) {
                erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
            }
        }
    }

//CmdRepita → "repita" ListaCmd "ate" Expressao [41]
    public void CmdRepita() {
        System.out.println("[DEBUG] CmdRepita()");
        if (eat(Tag.KW_REPITA) || eat(Tag.KW_ATE)) {
            ListaCmd();
            CmdRepita();
        } else {
            if (token.getClasse() == Tag.KW_ATE || token.getClasse() == Tag.KW_REPITA) {
                erroSintatico("Esperado \"KW\", encontrado " + "\"" + token.getLexema() + "\"");
                return;
            } else {
                skip("Esperado \"KW\", encontrado " + "\"" + token.getLexema() + "\"");
                if (token.getClasse() != Tag.EOF) {
                    CmdRepita();
                }
            }
        }
    }

//CmdAtrib → "<--" Expressao ";" [42]
    public void CmdAtrib() {
        System.out.println("[DEBUG] CmdAtrib()");


        if (eat(Tag.RELOP_ASSIGN)) {

            Expressao();
            if (!eat(Tag.SMB_SEMICOLON)) {
                erroSintatico("Esperado \";\", encontrado " + "\"" + token.getLexema() + "\"");
            }
        } else {
            // synch: FOLLOW(CmdAtrib)
            if (token.getClasse() == Tag.KW_FIM || token.getClasse() ==Tag.ID ||
                    token.getClasse() == Tag.KW_RETORNE || token.getClasse() == Tag.KW_SE ||
                    token.getClasse() == Tag.KW_ENQUANTO || token.getClasse() == Tag.KW_PARA ||
                    token.getClasse() == Tag.KW_ATE || token.getClasse() == Tag.KW_REPITA ||
                    token.getClasse() == Tag.KW_ESCREVA || token.getClasse() == Tag.KW_LEIA) {
                erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
                //return;
            } else {
                skip("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
                if (token.getClasse() != Tag.EOF) {
                    CmdAtrib();
                }
            }
        }
    }

//CmdChamaRotina → "(" RegexExp ")" ";" [43]
    public void CmdChamaRotina() {
        System.out.println("[DEBUG] CmdChamadaRotina()");
        if (eat(Tag.SMB_OP)) {
            RegexExp();
            if(!eat(Tag.SMB_CP))
                erroSintatico("Esperado \")\", encontrado" + "\"" + token.getLexema() + "\"");

        } else {
            if (token.getClasse() == Tag.KW_FIM || token.getClasse() ==Tag.ID ||
                    token.getClasse() == Tag.KW_RETORNE || token.getClasse() == Tag.KW_SE ||
                    token.getClasse() == Tag.KW_ENQUANTO || token.getClasse() == Tag.KW_PARA ||
                    token.getClasse() == Tag.KW_ATE || token.getClasse() == Tag.KW_REPITA ||
                    token.getClasse() == Tag.KW_ESCREVA || token.getClasse() == Tag.KW_LEIA) {
                erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
            }
        }
    }

//RegexExp → Expressao RegexExpLinha [44] | ε [45]
    public void RegexExp() {
        System.out.println("[DEBUG] RegexExp()");
        if (token.getClasse() == Tag.ID || token.getClasse() == Tag.SMB_OP ||
            token.getClasse() == Tag.KW_NAO || token.getClasse() == Tag.KW_VERDADEIRO ||
            token.getClasse() == Tag.KW_FALSO || token.getClasse() == Tag.NUMERICO ||
            token.getClasse() == Tag.LITERAL) {
            Expressao();
            RegexExpLinha();
        } else if(token.getClasse() == Tag.SMB_CP){
            return;}
        else{
            skip("Esperado \"KW\", encontrado " + "\"" + token.getLexema() + "\"");
            if (token.getClasse() != Tag.EOF) {
                ListaCmd();
            }

        }
    }

//RegexExpLinha → , Expressao RegexExpLinha [46] | ε [47]
public void RegexExpLinha() {
    System.out.println("[DEBUG] RegexExpLinha()");
	if (eat(Tag.SMB_COMMA)) {
		Expressao();
		RegexExpLinha();
	} else {

		skip("Esperado \"KW\", encontrado " + "\"" + token.getLexema() + "\"");
		if (token.getClasse() != Tag.EOF) {
			Expressao();
		}

	}
}

//CmdEscreva → "escreva" "(" Expressao ")" ";" [48]
public void CmdEscreva() {
    System.out.println("[DEBUG] CmdEscreva()");
	if (eat(Tag.KW_ESCREVA)) {
		if (!eat(Tag.SMB_OP)) {
			erroSintatico("Esperado \"(\", encontrado " + "\"" + token.getLexema() + "\"");
		}
		Expressao();
		if (!eat(Tag.SMB_CP)) {
			erroSintatico("Esperado \")\", encontrado " + "\"" + token.getLexema() + "\"");
		}
		if (!eat(Tag.SMB_SEMICOLON)) {
			erroSintatico("Esperado \";\", encontrado " + "\"" + token.getLexema() + "\"");
		}

	} else {
		if (token.getClasse() == Tag.KW_ESCREVA || token.getClasse() == Tag.ID
				|| token.getClasse() == Tag.KW_FIM) {
			erroSintatico("Esperado \"SystemOutDispln\", encontrado " + "\"" + token.getLexema() + "\"");
			return;
		} else {
			skip("Esperado \"SystemOutDispln\", encontrado " + "\"" + token.getLexema() + "\"");
			if (token.getClasse() != Tag.EOF) {
				ListaCmd();
			}
		}
	}
}


//CmdLeia → "leia" "(" ID ")" ";" [49]
public void CmdLeia() {
    System.out.println("[DEBUG] CmdLeia()");
	if (eat(Tag.KW_LEIA)) {
		if (!eat(Tag.SMB_OP)) {
			erroSintatico("Esperado \"(\", encontrado " + "\"" + token.getLexema() + "\"");
		}
		Expressao();
		if (!eat(Tag.ID)) {
			erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
		}
		if (!eat(Tag.SMB_CP)) {
			erroSintatico("Esperado \")\", encontrado " + "\"" + token.getLexema() + "\"");
		}
		if (!eat(Tag.SMB_SEMICOLON)) {
			erroSintatico("Esperado \";\", encontrado " + "\"" + token.getLexema() + "\"");
		}
	} else {
		skip("Esperado \"SystemOutDispln\", encontrado " + "\"" + token.getLexema() + "\"");
		if (token.getClasse() != Tag.EOF) {
			ListaCmd();
		}
	}
}

//Expressao → Exp1 ExpLinha [50]
public void Expressao() {
    System.out.println("[DEBUG] Expressao()");
	if (    token.getClasse() == Tag.ID || token.getClasse() == Tag.SMB_OP ||
            token.getClasse() == Tag.KW_NAO || token.getClasse() == Tag.KW_VERDADEIRO ||
            token.getClasse() == Tag.KW_FALSO || token.getClasse() == Tag.NUMERICO ||
            token.getClasse() == Tag.LITERAL) {
		Exp1();
		ExpLinha();

	} else {

		if (token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON) {
			erroSintatico("Esperado \"Numérico, Literal, ID\", encontrado " + "\"" + token.getLexema() + "\"");
			return;
		} else {
			skip("Esperado \"Numérico, Literal, ID\", encontrado " + "\"" + token.getLexema() + "\"");
			if (token.getClasse() != Tag.EOF) {
				Expressao();
			}
		}
	}
}

//ExpLinha →	< Exp1 ExpLinha [51] | <= Exp1 ExpLinha [52] |
// 				> Exp1 ExpLinha [53] | >= Exp1 ExpLinha [54] |
//		 		= Exp1 ExpLinha [55] | <> Exp1 ExpLinha [56] | ε [57]
public void ExpLinha() {
    System.out.println("[DEBUG] ExpLinha()");
	if (eat(Tag.RELOP_GT) || eat(Tag.RELOP_LT) || eat(Tag.RELOP_GE)
			|| eat(Tag.RELOP_LE) || eat(Tag.RELOP_EQ) || eat(Tag.RELOP_NE)) {
		Exp1();
		ExpLinha();
	} else if (token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON) {
		return;
	} else {
		skip("Esperado \"<, <=, >, >=, =, <>, ), ;\", encontrado " + "\"" + token.getLexema() + "\"");
		if (token.getClasse() != Tag.EOF) {
			ExpLinha();
		}
	}
}

//Exp1 → Exp2 Exp1Linha [58]
public void Exp1() {
	System.out.println("[DEBUG] Exp1()");
	if (token.getClasse() == Tag.NUMERICO
			|| token.getClasse() == Tag.LITERAL
			|| token.getClasse() == Tag.ID) {

		Exp2();
		Exp1Linha();
	} else {
		// synch: FOLLOW(Expressao1)
		if (token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON
				|| token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT
				|| token.getClasse() == Tag.RELOP_GE || token.getClasse() == Tag.RELOP_LE
				|| token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE) {

			erroSintatico("Esperado \"Inteito, Double, String ou ID\", encontrado " + "\"" + token.getLexema() + "\"");
			return;
		} else {
			skip("Esperado \"Inteito, Double, String ou ID\", encontrado " + "\"" + token.getLexema() + "\"");
			if (token.getClasse() != Tag.EOF) {
				Exp1();
			}
		}
	}
}

//Exp1Linha → E Exp2 Exp1Linha [59] | Ou Exp2 Exp1Linha [60]| ε [61]
public void Exp1Linha() {
	System.out.println("[DEBUG] ExpLinha'()");

	if (eat(Tag.RELOP_PLUS) || eat(Tag.RELOP_MINUS)) {
		Exp2();
		Exp1Linha();	
	} else if (token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON
			|| token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT
			|| token.getClasse() == Tag.RELOP_GE || token.getClasse() == Tag.RELOP_LE
			|| token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE) {
		return;
        }else {
		skip("Esperado \"E, OU\", encontrado " + "\"" + token.getLexema() + "\"");
		if (token.getClasse() != Tag.EOF) {
			Exp1Linha();
		}
	}
}

//Exp2 → Exp3 Exp2Linha [62]
public void Exp2() {
	System.out.println("[DEBUG] Exp2()");
	if (token.getClasse() == Tag.NUMERICO
			|| token.getClasse() == Tag.LITERAL
			|| token.getClasse() == Tag.ID) {

		Exp3();
		Exp2Linha();
	} else {
		// synch: FOLLOW(Expressao2)
		if (token.getClasse() == Tag.RELOP_PLUS || token.getClasse() == Tag.RELOP_MINUS
				|| token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON
				|| token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT
				|| token.getClasse() == Tag.RELOP_GE || token.getClasse() == Tag.RELOP_LE
				|| token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE) {

			erroSintatico("Esperado \"Numérico, Literal ou ID\", encontrado " + "\"" + token.getLexema() + "\"");
			return;
		} else {
			skip("Esperado \"Numérico, Literal ou ID\", encontrado " + "\"" + token.getLexema() + "\"");
			if (token.getClasse() != Tag.EOF) {
				Exp2();
			}
		}
	}
}

//Exp2Linha → + Exp3 Exp2Linha [63] | - Exp3 Exp2Linha [64] | ε [65]
public void Exp2Linha() {
	System.out.println("[DEBUG] Exp2Linha()");

	if (eat(Tag.RELOP_PLUS) || eat(Tag.RELOP_MINUS)) {
		Exp3();
		Exp2Linha();
	} else if ( token.getClasse() == Tag.KW_FIM || token.getClasse() == Tag.SMB_CP ||
            token.getClasse() == Tag.SMB_SEMICOLON || token.getClasse() == Tag.ID ||
            token.getClasse() == Tag.SMB_COMMA || token.getClasse() == Tag.KW_RETORNE ||
            token.getClasse() == Tag.KW_SE || token.getClasse() == Tag.KW_ENQUANTO ||
            token.getClasse() == Tag.KW_PARA || token.getClasse() == Tag.KW_ATE ||
            token.getClasse() == Tag.KW_REPITA || token.getClasse() == Tag.KW_ESCREVA ||
            token.getClasse() == Tag.KW_LEIA || token.getClasse() == Tag.KW_E ||
            token.getClasse() == Tag.KW_OU  
            || token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT
            || token.getClasse() == Tag.RELOP_GE || token.getClasse() == Tag.RELOP_LE
            || token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE) {
		return;
	} else {
		skip("Esperado \" +, -, ), ;, >, <, >=, <=, ==, !=\", encontrado " + "\"" + token.getLexema() + "\"");
		if (token.getClasse() != Tag.EOF) {
			Exp2Linha();
		}
	}
}

//Exp3 → Exp4 Exp3Linha [66]
public void Exp3() {
	System.out.println("[DEBUG] Exp3()");

	if (token.getClasse() == Tag.ID || token.getClasse() == Tag.SMB_OP ||
            token.getClasse() == Tag.KW_NAO || token.getClasse() == Tag.KW_VERDADEIRO ||
            token.getClasse() == Tag.KW_FALSO || token.getClasse() == Tag.NUMERICO ||
            token.getClasse() == Tag.LITERAL) {
		Exp4();
		Exp3Linha();
	}
	// synch: FOLLOW(Expressao3)
	if (token.getClasse() == Tag.RELOP_MULT || token.getClasse() == Tag.RELOP_DIV
			|| token.getClasse() == Tag.RELOP_PLUS || token.getClasse() == Tag.RELOP_MINUS
			|| token.getClasse() == Tag.SMB_CP || token.getClasse() == Tag.SMB_SEMICOLON
			|| token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT
			|| token.getClasse() == Tag.RELOP_GE || token.getClasse() == Tag.RELOP_LE
			|| token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE) {

		erroSintatico("Esperado \"NUMERICO, LITERAL ou ID\", encontrado " + "\"" + token.getLexema() + "\"");
		return;
	} else {
		skip("Esperado \"NUMERICO, LITERAL ou ID\", encontrado " + "\"" + token.getLexema() + "\"");
		if (token.getClasse() != Tag.EOF) Exp3();
	}

}

//Exp3Linha →* Exp4 Exp3Linha [67] | / Exp4 Exp3Linha [68] | ε [69]
public void Exp3Linha() {
	System.out.println("[DEBUG] Exp3Linha()");

	if (eat(Tag.RELOP_MULT) || eat(Tag.RELOP_DIV)) {
		Exp4();
		Exp3Linha();
	} else if ( token.getClasse() == Tag.KW_FIM || token.getClasse() == Tag.SMB_CP ||
                token.getClasse() == Tag.SMB_SEMICOLON || token.getClasse() == Tag.ID ||
                token.getClasse() == Tag.SMB_COMMA || token.getClasse() == Tag.KW_RETORNE ||
                token.getClasse() == Tag.KW_SE || token.getClasse() == Tag.KW_ENQUANTO ||
                token.getClasse() == Tag.KW_PARA || token.getClasse() == Tag.KW_ATE ||
                token.getClasse() == Tag.KW_REPITA || token.getClasse() == Tag.KW_ESCREVA ||
                token.getClasse() == Tag.KW_LEIA || token.getClasse() == Tag.KW_E ||
                token.getClasse() == Tag.KW_OU || token.getClasse() == Tag.RELOP_PLUS ||
                token.getClasse() == Tag.RELOP_MINUS
			|| token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT
			|| token.getClasse() == Tag.RELOP_GE || token.getClasse() == Tag.RELOP_LE
			|| token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE) {
		return;
	} else {
		skip("Esperado \"*, /, +, -, ), ;, >, <, >=, <=, ==, !=\", encontrado " + "\"" + token.getLexema() + "\"");
		if (token.getClasse() != Tag.EOF) {
			Exp3Linha();
		}
	}
}

//Exp4 → 	id Exp4Linha [70] | Numerico [71] |
// 			Litetal [72] | “verdadeiro” [73] |
// 			“falso” [74] | OpUnario Expressao [75]|
// 			“(“ Expressao “)” [76]
public void Exp4() {
	System.out.println("[DEBUG] Exp4()");

	if(token.getClasse() == Tag.ID || token.getClasse() == Tag.SMB_OP ||
            token.getClasse() == Tag.KW_NAO || token.getClasse() == Tag.KW_VERDADEIRO ||
            token.getClasse() == Tag.KW_FALSO || token.getClasse() == Tag.NUMERICO ||
            token.getClasse() == Tag.LITERAL){
        if (eat(Tag.NUMERICO) || eat(Tag.LITERAL) || eat(Tag.KW_VERDADEIRO)
                || eat(Tag.KW_FALSO)) {
            return;
        } else if(eat(Tag.ID)){
            Exp4Linha();

        }else if (token.getClasse() == Tag.SMB_OP || token.getClasse() == Tag.SMB_SEMICOLON) {
            Expressao();
            return;
    }

	} else {
		skip("Esperado \"ID, Numerico, Litetal, verdadeiro, falso,  OpUnario, ), ;\", encontrado " + "\"" + token.getLexema() + "\"");
		if (token.getClasse() != Tag.EOF) {
			Exp4Linha();
		}
	}
}

//Exp4Linha → “(“ RegexExp ”)” [77] | ε [78]
public void Exp4Linha() {
	System.out.println("[DEBUG] Exp4Linha()");

	if (eat(Tag.SMB_OP)) {

		RegexExp();
		if(!eat(Tag.SMB_CP))
            erroSintatico("Esperado \")\", encontrado " + "\"" + token.getLexema() + "\"");
	} else if ( token.getClasse() == Tag.KW_DECLARE || token.getClasse() == Tag.SMB_CP ||
            token.getClasse() == Tag.SMB_SEMICOLON || token.getClasse() == Tag.ID ||
            token.getClasse() == Tag.SMB_COMMA || token.getClasse() == Tag.KW_RETORNE ||
            token.getClasse() == Tag.KW_SE || token.getClasse() == Tag.KW_ENQUANTO ||
            token.getClasse() == Tag.KW_PARA || token.getClasse() == Tag.KW_ATE ||
            token.getClasse() == Tag.KW_REPITA || token.getClasse() == Tag.KW_ESCREVA ||
            token.getClasse() == Tag.KW_LEIA || token.getClasse() == Tag.KW_E ||
            token.getClasse() == Tag.KW_OU || token.getClasse() == Tag.RELOP_PLUS ||
            token.getClasse() == Tag.RELOP_MINUS || token.getClasse() == Tag.RELOP_MULT ||
            token.getClasse() == Tag.RELOP_DIV
            || token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT
            || token.getClasse() == Tag.RELOP_GE || token.getClasse() == Tag.RELOP_LE
            || token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE)  {
		return;
	} else {
		skip("Esperado \"( , )\", encontrado " + "\"" + token.getLexema() + "\"");
		if (token.getClasse() != Tag.EOF) {
			RegexExp();
		}
	}
}

//OpUnario → "Nao" [79]
public void OpUnario() {
	System.out.println("[DEBUG] OpUnario()");

	if (eat(Tag.SMB_OP)) {

	} else if (token.getClasse() == Tag.SMB_SEMICOLON
			|| token.getClasse() == Tag.RELOP_GT || token.getClasse() == Tag.RELOP_LT
			|| token.getClasse() == Tag.RELOP_MULT || token.getClasse() == Tag.RELOP_LE
			|| token.getClasse() == Tag.RELOP_EQ || token.getClasse() == Tag.RELOP_NE) {
		return;
	} else {
		skip("Esperado \"Não \", encontrado " + "\"" + token.getLexema() + "\"");
		if (token.getClasse() != Tag.EOF) {
			OpUnario();
		}
	}
}

//----------------------------------------------------------------------------------------------------------
// CLASSES JAVINHA
//----------------------------------------------------------------------------------------------------------
// Programa --> Classe

/*   public void Programa() {
   //System.out.println("[DEBUG] Programa()");

  // OBS.: vimos que para o Nao-Terminal Inicial, eh melhor chamar o metodo skip()
  // para nao prejudicar a leitura no restante do codigo.
	if(token.getClasse() != Tag.KW_ALGORITMO) // FISRT(Programa)
		skip("Esperado \"public\", encontrado " + "\"" + token.getLexema() + "\"");

  Classe();
}*/
// Classe --> "public" "class" ID ListaDeclaraVar ListaCmd "end"
/*public void Classe() {
	//System.out.println("[DEBUG] Classe()");
	
   OBS.: vimos que para o primeiro, eh melhor chamar o metodo skip()
  * para nao prejudicar a leitura no restante do codigo.
  * Se percebermos na TP, 'Programa' e 'Classe' possuem os mesmos
  * FIRST e FOLLOW. Entao a regra para se analisar a sincronizacao no 
  * primeiro instante em que entra nesses metodos eh a mesma.

	if(!eat(Tag.KW_PUBLIC)) {
		skip("Esperado \"public\", encontrado "  + "\"" + token.getLexema() + "\"");
  }

	if(!eat(Tag.KW_CLASS)) { // espera "class"
		
	  ATENCAO: no caso 'terminal esperado' vs 'terminal na entrada', de acordo com vimos em sala:
		// o terminal esperado não casou com o terminal da entrada,
		// dai vamos simular o 'desempilha terminal',
		// isto eh, continue a varredura, mantendo a entrada.

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
}*//*

//ListaDeclaraVar --> TipoPrimitivo ID ";" ListaDeclaraVar | epsilon
public void ListaDeclaraVar() {
	//System.out.println("[DEBUG] ListaDeclaraVar()");
	
	//ListaDeclaraVar --> TipoPrimitivo ID ";" ListaDeclaraVar
	if(token.getClasse() == Tag.KW_INTEGER || token.getClasse() == Tag.KW_DOUBLE || token.getClasse() == Tag.KW_STRING) {
	  
		TipoPrimitivo();
		
		if(!eat(Tag.ID))
			erroSintatico("Esperado \"ID\", encontrado " + "\"" + token.getLexema() + "\"");
		if(!eat(Tag.SMB_SEMICOLON)) erroSintatico("Esperado \";\", encontrado "  + "\"" + token.getLexema() + "\"");
		
		ListaDeclaraVar();
  }
  //ListaDeclaraVar --> epsilon
  else if(token.getClasse() == Tag.KW_END || token.getClasse() == Tag.KW_SYSTEMOUTDISPLN || token.getClasse() == Tag.ID) {
		return;		      
  }
  else {
 *//* Percebemos na TP que o unico metodo a ser chamado no caso de erro, seria o skip().
	 * Mas a ideia do skip() eh avancar a entrada sem retirar ListaDeclaraVar() da pilha
	 * (recursiva). So que chegamos ao fim do metodo ListaDeclaraVar(). Como podemos
	 * mante-lo na pilha recursiva? 
	 * Simples, chamamos skip() e o proprio metodo ListaDeclaraVar().
 *//*
	 
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
}*/
}
