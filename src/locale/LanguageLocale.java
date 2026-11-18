package locale;

import java.io.*;
import java.util.*;

/**
 * Esta classe tem por objectivo fornecer os nomes dos comandos e demais texto
 * presente nos controlos de uma aplicação. Cada texto está identificado por uma
 * chave. Assim na criação da aplicação basta dar a chave que esta classe
 * encarrega-se de lhe atribuir o texto correcto na linguagem correcta.
 * 
 * @author F. Sérgio (da parte inicial, restante deve ser feita pelos alunos)
 *
 */
public class LanguageLocale {

	private String language; // linguagem actual
	private HashMap<String, String> words = new HashMap<String, String>(); // Mapa com as relaçõess entre chaves e texto
	private static LanguageBrowser langBrowser = new LanguageBrowser(); // classe que carrega as várias linguagens

	// objecto unico 
	private static LanguageLocale localUnico = null;
	
	// ponto global de acesso metodo 
	public static LanguageLocale getLocal(){
		if (localUnico == null) {
			localUnico = new LanguageLocale();
		}
		return localUnico;
	}


	private LanguageLocale() {
		String langFileName = null; // nome do ficheiro com a linguagem

		// ler o ficheiro de configuração da linguagem
		try (BufferedReader br = new BufferedReader(new FileReader(new File("linguagens/defaultlang.cfg")))) {
			JanelaProgresso jan = new JanelaProgresso(null);
			jan.setVisible(true);
			langFileName = br.readLine();
		} catch (IOException e) {
			System.err.println("Erro na leitura do ficheiro de configuração de linguagem");
			e.printStackTrace();
			System.exit(0);
		}

		// ler o ficheiro da linguagem
		try (BufferedReader br = new BufferedReader(new FileReader(new File("linguagens/" + langFileName)))) {
			// ler a descrição da linguagem
			language = br.readLine();
			while (true) {
				String s = br.readLine();
				if (s == null) // não tem linha? termina a leitura
					break;
				if (s.equals("")) // linha vazia? continua para a linha seguinte
					continue;
				if (s.charAt(0) == '#') // é uma linha de comentário logo passa à próxima linha
					continue;
				String strs[] = s.split("=");
				// não tem duas string? dá erro... Ignora esta entrada e passa à pròxima
				if (strs.length != 2)
					continue;
				// se tem duas strings a primeira é a chave e a segunda o texto
				words.put(strs[0], strs[1]);
			}
		} catch (IOException e) {
			System.err.println("Erro na leitura do ficheiro de linguagem " + langFileName);
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * retorna o texto relacionado com um dado código
	 * 
	 * @param key o código do texto
	 * @return o texto a colocar na aplicação
	 */
	public String get(String key) {
		String val = words.get(key);
		return val == null ? "???" : val;
	}

	/**
	 * método que retorna os nomes das linguagens suportadas pela aplicação
	 * 
	 * @return um array com os nomes da linguagens suportadas
	 */
	public static String[] getLanguages() {
		return langBrowser.getLinguagens();
	}

	/**
	 * Retorna os nomes dos ficheiros onde estão definidas as linguagens
	 * 
	 * @return um array com os nomes dos ficheiros onde estão definidas as
	 *         linguagens
	 */
	public static String[] getFilenames() {
		return langBrowser.getFilenames();
	}

	/**
	 * Retorna o índice da linguagem seleccionada dentro das linguagens que estão
	 * definidas.
	 * O índice deve ser visto no retorno do método getLanguages()
	 * 
	 * @return o índice da linguagem seleccionada
	 */
	public int getSelectedLanguageIndex() {
		String[] availableLang = getLanguages();
		for (int i = 0; i < availableLang.length; i++)
			if (availableLang[i].equals(language))
				return i;

		return -1;
	}

	/**
	 * Retorna o nome da linguagem que está a ser usada
	 * 
	 * @return o nome da linguagem que está a ser usada
	 */
	public String getSelectecLanguage() {
		return language;
	}

	/**
	 * Método que define qual a nova linguagem por defeito
	 * Ou seja, qual a linguagem que deve ser carregada quando a aplicação começa
	 * Vai gravar o nome dessa linguagem no ficheiro "defaultlang.cfg"
	 * 
	 * @param lang o nome da nova linguagem por defeito
	 */
	public static void setDefaultLanguage(String lang) {

		// Get the filename
		String[] availableLang = getLanguages();
		String[] availableFiles = getFilenames();

		String strFilename = null;

		for (int i = 0; i < availableLang.length; i++) {
			if (lang.compareToIgnoreCase(availableLang[i]) == 0) {
				strFilename = availableFiles[i];
			}
		}

		try {
			File fileDefLang = new File("linguagens/defaultlang.cfg");
			BufferedWriter bw = new BufferedWriter(new PrintWriter(fileDefLang));
			bw.write(strFilename);
			bw.close();
		} catch (Exception e) {

		}
	}

	/**
	 * clase privada que vai verificar quantos ficheiros de linguagens estão
	 * definidos
	 * É static para poder ser acedida mesmo antes de se ter o objecto de Linguagem
	 * 
	 * @author F. Sérgio Barbosa
	 */
	private static class LanguageBrowser {

		String[] asLinguagens; // lista das linguagens
		String[] fileNames; // lista dos ficheiros com linguagens

		public LanguageBrowser() {

			// vai procurar em todos os ficheiros presentes na pasta "linguagens"
			// que tenham a extensão ".lang"
			File files[] = (new File("linguagens")).listFiles(
					new FilenameFilter() {
						public boolean accept(File dir, String name) {
							String extensao = name.substring(name.length() - 5);
							return extensao.compareToIgnoreCase(".lang") == 0;
						}
					});

			// vai ver para cada ficheiro qual a linguagem que suporta
			// essa informação está logo na primeira linha
			try {
				asLinguagens = new String[files.length];
				fileNames = new String[files.length];
				for (int i = 0; i < files.length; i++) {
					// ler o ficheiro da linguagem para ver a linguagem
					BufferedReader br = new BufferedReader(new FileReader(files[i]));
					asLinguagens[i] = br.readLine();
					fileNames[i] = files[i].getName();
					br.close();
					System.out.println(asLinguagens[i]);
				}
			} catch (Exception e) {
				asLinguagens = new String[1];
				asLinguagens[0] = "Sem linguagens";
			}
		}

		public String[] getLinguagens() {
			return asLinguagens;
		}

		public String[] getFilenames() {
			return fileNames;
		}
	}
}
