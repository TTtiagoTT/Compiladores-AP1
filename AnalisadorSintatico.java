import java.util.List;

public class AnalisadorSintatico {
    private List<Token> tokens;   // Lista de tokens gerados pelo analisador léxico
    private int posicaoAtual;     // Índice do token atual

    // Construtor
    public AnalisadorSintatico(List<Token> tokens) {
        this.tokens = tokens;
        this.posicaoAtual = 0;  // Começa no primeiro token
    }

    // Método principal para iniciar a análise
    public void analisar() {
        programa();  // Chama o método do símbolo inicial
        if (posicaoAtual < tokens.size() && lookahead().getTipo() != TipoToken.EOF) {
            erro("Tokens não consumidos corretamente. Esperado EOF.");
        }
        System.out.println("Análise Sintática concluída com sucesso!");
    }


    // Método para verificar o próximo token e avançar
    private void match(TipoToken tipoEsperado) {
        if (lookahead().getTipo().equals(tipoEsperado)) {
            posicaoAtual++;  // Avança para o próximo token
        } else {
            erro("Token inesperado. Esperado: " + tipoEsperado);
        }
    }

    // Método para o símbolo não terminal Programa
    private void programa() {
        match(TipoToken.Delim);    // Delimitador
        match(TipoToken.PCDec);   // Declaração
        listaDeclaracoes();
        match(TipoToken.Delim);
        match(TipoToken.PCProg);
        listaComandos();
        //match(TipoToken.PCFim);    // "fim"
        match(TipoToken.EOF);    // "fim"

    }

    // Método para processar lista de declarações
    private void listaDeclaracoes() {
        while (lookahead().getTipo() == TipoToken.Var) {
            declaracao();
        }
    }

    // Método para processar uma única declaração
    private void declaracao() {
        match(TipoToken.Var);
        match(TipoToken.Delim);
        tipoVariavel();
    }

    // Método para processar tipos de variáveis
    private void tipoVariavel() {
        if (lookahead().getTipo() == TipoToken.PCInt) {
            match(TipoToken.PCInt);
        } else if (lookahead().getTipo() == TipoToken.PCReal) {
            match(TipoToken.PCReal);
        } else {
            erro("Esperado tipo de variável.");
        }
    }

    // Método para processar lista de comandos
    private void listaComandos() {
        while (isInicioDeComando()) {
            comando();
        }
    }

    // Método para verificar se o token atual pode iniciar um comando
    private boolean isInicioDeComando() {
        TipoToken tipo = lookahead().getTipo();
        return tipo == TipoToken.PCLer ||
               tipo == TipoToken.PCImprimir ||
               tipo == TipoToken.PCSe ||
               tipo == TipoToken.Var ||
               tipo == TipoToken.PCEnqto ||
               tipo == TipoToken.PCIni;
    }

    // Método para processar um comando
    private void comando() {
        if (lookahead().getTipo() == TipoToken.PCLer) {
            comandoEntrada();
        } else if (lookahead().getTipo() == TipoToken.PCImprimir) {
            comandoSaida();
        } else if (lookahead().getTipo() == TipoToken.PCSe) {
            comandoCondicao();
        } else if (lookahead().getTipo() == TipoToken.Var) {
            comandoAtribuicao();
        } else if(lookahead().getTipo() == TipoToken.PCEnqto){
        	comandoRepeticao();
        } else if(lookahead().getTipo() == TipoToken.PCIni){
        	subAlgoritmo();
        }else if(lookahead().getTipo() == TipoToken.PCImprimir){
        	comandoSaida();
        }  else {
            erro("Comando inválido.");
        }
    }
    
    private void comandoCondicaoRest() {
        if(lookahead().getTipo() == TipoToken.PCSenao) {
        	match(TipoToken.PCSenao);
        	comando();
        	// Caso contrário, "ε" (não faz nada).
        }else {
        	
        }
    }

    // Comando de entrada
    private void comandoEntrada() {
        match(TipoToken.PCLer);
        match(TipoToken.Var);
    }

    // Comando de saída
    private void comandoSaida() {
        match(TipoToken.PCImprimir);
        comandoSaidaRest();
        
    }
    
    private void comandoSaidaRest() {
    	if(lookahead().getTipo() == TipoToken.Var) {
    		match(TipoToken.Var);
    	} else if(lookahead().getTipo() == TipoToken.Cadeia) {
    		match(TipoToken.Cadeia);
    	} else {
    		erro("Esperado variavel ou cadeia.");
    	} 
    }

    // Comando de atribuição
    private void comandoAtribuicao() {
        match(TipoToken.Var);
        match(TipoToken.Atrib);
        expressaoAritmetica();
    }

    // Comando condicional (SE)
    private void comandoCondicao() {
        match(TipoToken.PCSe);
        if (isInicioDeExpressaoRelacional()) {
            expressaoRelacional();
            match(TipoToken.PCEntao);
            comando();
            comandoCondicaoRest();
        } else {
            erro("Esperado uma expressão relacional após 'se'.");
        }
    }
    
    private void comandoRepeticao() {
   
    		match(TipoToken.PCEnqto);
    		expressaoRelacional();
    		comando();
  
    }


    // Método para processar expressões relacionais
    private void expressaoRelacional() {
        termoRelacional();
        expressaoRelacionalRest();
    }

    private void expressaoRelacionalRest() {
        if (isOperadorBooleano()) {
            operadorBooleano();
            termoRelacional();
            expressaoRelacionalRest();
        }
        // ε: Não faz nada
    }

    private void operadorBooleano() {
        if (lookahead().getTipo() == TipoToken.OpBoolE) {
            match(TipoToken.OpBoolE);
        } else if (lookahead().getTipo() == TipoToken.OpBoolOu) {
            match(TipoToken.OpBoolOu);
        } else {
            erro("Esperado operador booleano.");
        }
    }

    private boolean isOperadorBooleano() {
        TipoToken tipo = lookahead().getTipo();
        return tipo == TipoToken.OpBoolE || tipo == TipoToken.OpBoolOu;
    }

    private void termoRelacional() {
        if (lookahead().getTipo() == TipoToken.AbrePar) {
            match(TipoToken.AbrePar);
            expressaoRelacional();
            match(TipoToken.FechaPar);
        } else {
            expressaoAritmetica();
            opRel();
            expressaoAritmetica();
        }
    }

    private void opRel() {
        switch (lookahead().getTipo()) {
            case OpRelMenor -> match(TipoToken.OpRelMenor);
            case OpRelMenorIgual -> match(TipoToken.OpRelMenorIgual);
            case OpRelMaior -> match(TipoToken.OpRelMaior);
            case OpRelMaiorIgual -> match(TipoToken.OpRelMaiorIgual);
            case OpRelIgual -> match(TipoToken.OpRelIgual);
            case OpRelDif -> match(TipoToken.OpRelDif);
            default -> erro("Esperado operador relacional.");
        }
    }

    private void expressaoAritmetica() {
        termoAritmetico();
        expressaoAritmeticaRest();
    }

    private void expressaoAritmeticaRest() {
        if (lookahead().getTipo() == TipoToken.OpAritSoma || lookahead().getTipo() == TipoToken.OpAritSub) {
            match(lookahead().getTipo());
            termoAritmetico();
            expressaoAritmeticaRest();
        }else {
        	
        }
    }

    private void termoAritmetico() {
        fatorAritmetico();
        termoAritmeticoRest();
    }

    private void termoAritmeticoRest() {
        if (lookahead().getTipo() == TipoToken.OpAritMult || lookahead().getTipo() == TipoToken.OpAritDiv) {
            match(lookahead().getTipo());
            fatorAritmetico();
            termoAritmeticoRest();
        }
    }

    private void fatorAritmetico() {
        if (lookahead().getTipo() == TipoToken.NumInt ||
            lookahead().getTipo() == TipoToken.NumReal ||
            lookahead().getTipo() == TipoToken.Var) {
            match(lookahead().getTipo());
        } else if (lookahead().getTipo() == TipoToken.AbrePar) {
            match(TipoToken.AbrePar);
            expressaoAritmetica();
            match(TipoToken.FechaPar);
        } else {
            erro("Esperado fator aritmético.");
        }
    }

    private void subAlgoritmo() {
    	match(TipoToken.PCIni);
    	listaComandos();
    	match(TipoToken.PCFim);

    }
    
    // Método para verificar início de expressão relacional
    private boolean isInicioDeExpressaoRelacional() {
        TipoToken tipo = lookahead().getTipo();
        return tipo == TipoToken.Var || tipo == TipoToken.AbrePar ||
               tipo == TipoToken.NumInt || tipo == TipoToken.NumReal;
    }
    

    // Método para obter o próximo token
    private Token lookahead() {
        return tokens.get(posicaoAtual);
    }

    // Método para lançar erros
    private void erro(String mensagem) {
        Token atual = lookahead();
        throw new RuntimeException("Erro Sintático: " + mensagem +
                                   ". Encontrado: " + atual.getTipo() +
                                   " (" + atual.getLexema() + ") na linha " + atual.getLinha() + ".");
    }
}
