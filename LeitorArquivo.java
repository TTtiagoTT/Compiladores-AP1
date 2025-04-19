import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LeitorArquivo {
    public String[] linhas;

    public LeitorArquivo(String arquivo) throws IOException {
        lerArquivo(arquivo);
    }

    private void lerArquivo(String arquivo) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(arquivo));
        String linha;
        int numLinhas = 0;

        // Primeiro, conte quantas linhas existem
        while ((linha = br.readLine()) != null) {
            numLinhas++;
        }

        // Volte para o início do arquivo
        br.close();
        br = new BufferedReader(new FileReader(arquivo));

        // Crie o vetor de strings
        linhas = new String[numLinhas];
        int index = 0;

        while ((linha = br.readLine()) != null) {
            linhas[index] = linha;
            index++;
        }

        br.close();
    }

    public String[] getLinhas() {
        return linhas;
    }

}

