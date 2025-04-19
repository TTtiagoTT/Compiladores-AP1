
public class Token {
	public String lexema;
	public TipoToken tipo;
	public int linha; //chave para tabela hash/tabela de simbolos
	
	
	public Token(String lexema, TipoToken tipo, int linha) {
		super();
		this.lexema = lexema;
		this.tipo = tipo;
		this.linha = linha;
	}
	
	@Override
	public String toString() {
		return "<"+tipo+", "+lexema+", "+linha+">";
	}
	
	
	public String getLexema() {
		return lexema;
	}
	public void setLexema(String lexema) {
		this.lexema = lexema;
	}
	public TipoToken getTipo() {
		return tipo;
	}
	public void setTipo(TipoToken tipo) {
		this.tipo = tipo;
	}
	public int getLinha() {
		return linha;
	}
	public void setLinha(int linha) {
		this.linha = linha;
	}
	
	
}
