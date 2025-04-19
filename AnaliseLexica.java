import java.io.IOException;

public class AnaliseLexica {
    LeitorArquivo ldat;
    int linhaAtual = 0;  // Posição da linha atual (persistente)
    int colunaAtual = 0; // Posição da coluna atual (persistente)

    public AnaliseLexica(String arquivo) throws IOException {
        this.ldat = new LeitorArquivo(arquivo);
    }

    public Token proximoToken() {
        String[] linhas = ldat.getLinhas();  // Obtém todas as linhas do arquivo

        // Percorre o arquivo enquanto houver linhas para processar
        while (linhaAtual < linhas.length) {
            String linha = linhas[linhaAtual];

            // Verifica se a coluna atual está dentro dos limites da linha
            if (colunaAtual >= linha.length()) {
                // Avança para a próxima linha se todas as colunas da linha atual foram processadas
                linhaAtual++;
                colunaAtual = 0;  // Reinicia a coluna para 0 na nova linha
                continue;
            }

            char c = linha.charAt(colunaAtual);  // Obtém o caractere atual

            // Ignorar espaços em branco e atualizar a coluna
            while (colunaAtual < linha.length() && Character.isWhitespace(c)) {
                colunaAtual++;
                if (colunaAtual < linha.length()) {
                    c = linha.charAt(colunaAtual);  // Atualiza c se a coluna não terminar
                }
            }

            // Verifica novamente após ignorar espaços
            if (colunaAtual >= linha.length()) {
                linhaAtual++;
                colunaAtual = 0;
                continue;
            }

            // Se o caractere atual for '#', ignorar o resto da linha
            if (c == '#') {
                linhaAtual++;  // Pula para a próxima linha
                colunaAtual = 0;  // Reinicia a coluna para 0 na nova linha
                continue;
            }

            // Atualiza c após espaço em branco
            c = linha.charAt(colunaAtual);
            StringBuilder lexema = new StringBuilder();

            // Identificar tokens
            if (Character.isLetter(c)) {
                // Identifica um token baseado em letras
                while (colunaAtual < linha.length() && Character.isLetterOrDigit(c)) {
                    lexema.append(c);
                    colunaAtual++;
                    if (colunaAtual < linha.length()) {
                        c = linha.charAt(colunaAtual);  // Atualiza c se ainda não terminou
                    }
                }

                // Verifica as palavras reservadas e retorna o token correspondente
                String lexemaStr = lexema.toString();
                switch (lexemaStr) {
                    case "DEC":
                        return new Token("DEC", TipoToken.PCDec, linhaAtual+1);
                    case "PROG":
                        return new Token("PROG", TipoToken.PCProg, linhaAtual+1);
                    case "INT":
                        return new Token("INT", TipoToken.PCInt, linhaAtual+1);
                    case "REAL":
                        return new Token("REAL", TipoToken.PCReal, linhaAtual+1);
                    case "LER":
                        return new Token("LER", TipoToken.PCLer, linhaAtual+1);
                    case "IMPRIMIR":
                        return new Token("IMPRIMIR", TipoToken.PCImprimir, linhaAtual+1);
                    case "SE":
                        return new Token("SE", TipoToken.PCSe, linhaAtual+1);
                    case "SENAO":
                        return new Token("SENAO", TipoToken.PCSenao, linhaAtual+1);
                    case "ENTAO":
                        return new Token("ENTAO", TipoToken.PCEntao, linhaAtual+1);
                    case "ENQTO":
                        return new Token("ENQTO", TipoToken.PCEnqto, linhaAtual+1);
                    case "INI":
                        return new Token("INI", TipoToken.PCIni, linhaAtual+1);
                    case "FIM":
                        return new Token("FIM", TipoToken.PCFim, linhaAtual+1);
                    case "E":
                    	return new Token("E", TipoToken.OpBoolE, linhaAtual+1);
                    case "OU":
                    	return new Token("OU", TipoToken.OpBoolOu, linhaAtual+1);
                    default:
                        // Verifica se a primeira letra do lexema é minúscula
                        if (Character.isLowerCase(lexemaStr.charAt(0))) {
                            // Retorna um token de variável se não for uma palavra reservada
                            return new Token(lexemaStr, TipoToken.Var, linhaAtual+1);
                        } else {
                            // Retorna um token de erro se a variável não começar com letra minúscula
                            return new Token("Erro: Token Não Reconhecido", TipoToken.ERRO, linhaAtual+1);
                        }
                }
            } else if (Character.isDigit(c)) {
                // Identifica um número (inteiro ou real)
                while (colunaAtual < linha.length() && Character.isDigit(c)) {
                    lexema.append(c);
                    colunaAtual++;
                    if (colunaAtual < linha.length()) {
                        c = linha.charAt(colunaAtual);
                    }
                }

                // Verifica se é um número real
                if (colunaAtual < linha.length() && linha.charAt(colunaAtual) == '.') {
                    lexema.append('.');
                    colunaAtual++;
                    while (colunaAtual < linha.length() && Character.isDigit(linha.charAt(colunaAtual))) {
                        lexema.append(linha.charAt(colunaAtual));
                        colunaAtual++;
                    }
                    return new Token(lexema.toString(), TipoToken.NumReal, linhaAtual+1);
                }

                // Retorna um token de número inteiro
                return new Token(lexema.toString(), TipoToken.NumInt, linhaAtual+1);
            } else {
                // Identifica tokens baseados em símbolos
                switch (c) {
                    case ':':
                        // Verifica se é uma atribuição ":="
                        if (colunaAtual + 1 < linha.length() && linha.charAt(colunaAtual + 1) == '=') {
                            colunaAtual += 2;  // Avança a coluna além de ":="
                            return new Token(":=", TipoToken.Atrib, linhaAtual+1);
                        } else {
                            // Ignora ':' se não for atribuição
                            colunaAtual++;
                            return new Token(":", TipoToken.Delim, linhaAtual+1);
                        }
                    case '*':
                        colunaAtual++;
                        return new Token("*", TipoToken.OpAritMult, linhaAtual+1);
                    case '/':
                        colunaAtual++;
                        return new Token("/", TipoToken.OpAritDiv, linhaAtual+1);
                    case '+':
                        colunaAtual++;
                        return new Token("+", TipoToken.OpAritSoma, linhaAtual+1);
                    case '-':
                        colunaAtual++;
                        return new Token("-", TipoToken.OpAritSub, linhaAtual+1);
                    case '<':
                    	if (colunaAtual + 1 < linha.length() && linha.charAt(colunaAtual + 1) == '=') {
                            colunaAtual += 2; 
                            return new Token("<=", TipoToken.OpRelMenorIgual, linhaAtual + 1);
                        }else {
                        	colunaAtual++;
                        	return new Token("<", TipoToken.OpRelMenor, linhaAtual+1);
                        }
                    case '>':
                    	if (colunaAtual + 1 < linha.length() && linha.charAt(colunaAtual + 1) == '=') {
                            colunaAtual += 2;
                            return new Token(">=", TipoToken.OpRelMaiorIgual, linhaAtual + 1);
                        }else {
                        	colunaAtual++;
                        	return new Token("<", TipoToken.OpRelMaior, linhaAtual+1);
                        }
                    case '=':
                    	// Verifica se o próximo caractere também é '=' para identificar '=='
                        if (colunaAtual + 1 < linha.length() && linha.charAt(colunaAtual + 1) == '=') {
                            colunaAtual += 2;  // Avança além de '=='
                            return new Token("==", TipoToken.OpRelIgual, linhaAtual + 1);
                        }else {
                        	return new Token("Erro: Caractere desconhecido '" + c + "'", TipoToken.ERRO, linhaAtual + 1);
                        }
                    case '!':
                    	if (colunaAtual + 1 < linha.length() && linha.charAt(colunaAtual + 1) == '=') {
                            colunaAtual += 2;  // Avança além de '=='
                            return new Token("!=", TipoToken.OpRelDif, linhaAtual + 1);
                        }else {
                        	return new Token("Erro: Caractere desconhecido '" + c + "'", TipoToken.ERRO, linhaAtual + 1);
                        }
                    case '(':
                        colunaAtual++;
                        return new Token("(", TipoToken.AbrePar, linhaAtual+1);
                    case ')':
                        colunaAtual++;
                        return new Token(")", TipoToken.FechaPar, linhaAtual+1);
                    case '"':
                        // Identifica uma cadeia de caracteres
                        colunaAtual++;
                        StringBuilder cadeia = new StringBuilder();

                        while (colunaAtual < linha.length() && linha.charAt(colunaAtual) != '"') {
                            cadeia.append(linha.charAt(colunaAtual));
                            colunaAtual++;
                        }

                        // Verifica se a string foi encerrada corretamente com uma aspa dupla
                        if (colunaAtual < linha.length() && linha.charAt(colunaAtual) == '"') {
                            colunaAtual++;  // Fecha as aspas
                            return new Token(cadeia.toString(), TipoToken.Cadeia, linhaAtual + 1);
                        } else {
                            // Caso a string não tenha aspas de fechamento
                            System.err.println("Erro léxico na linha " + (linhaAtual + 1) + ": string não fechada.");
                        }
                    default:
                        colunaAtual++;
                        return new Token("Erro: Caractere desconhecido '" + c + "'", TipoToken.ERRO, linhaAtual + 1);
                }
            }
        }

        // Retorna <EOF> ao final do arquivo
        return new Token("EOF", TipoToken.EOF, linhaAtual);
    }
}
