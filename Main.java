//Tiago Fernades Soucek - 2453312	Vitor Luiz De Castro Viana - 2503298
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main{
	public static void main(String[] args) throws IOException {
	    AnaliseLexica analisadorLexico = new AnaliseLexica(args[0]);
	    List<Token> tokens = new ArrayList<>();

	
	    Token token;
	    do {
	        token = analisadorLexico.proximoToken();
	        tokens.add(token);	        
	        System.out.println(token);
	        
	        // Verifica se encontrou um token de erro e encerra a execução
	        if (token.getTipo() == TipoToken.ERRO) {
	            System.err.println("Erro léxico encontrado. Programa encerrado.");
	            break;
	        }
	    } while (token.getTipo() != TipoToken.EOF);
	    
	    AnalisadorSintatico analisadorSintatico = new AnalisadorSintatico(tokens);
	    analisadorSintatico.analisar();
	    
}
}