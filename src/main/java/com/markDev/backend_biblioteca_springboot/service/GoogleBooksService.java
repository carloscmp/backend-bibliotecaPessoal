package com.markDev.backend_biblioteca_springboot.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.logging.Logger;
import java.util.logging.Level;

@Service
public class GoogleBooksService {

	private static final Logger LOGGER = Logger.getLogger(GoogleBooksService.class.getName());
	private static final String API_URL = "https://www.googleapis.com/books/v1/volumes";

	/**
	 * Chave de API para o serviço Google Books, injetada a partir do ficheiro
	 * application.properties. Manter a chave no backend é uma prática de segurança
	 * crucial para evitar a sua exposição no cliente.
	 */
	@Value("${google.books.api.key}")
	private String apiKey;

	/**
	 * Cliente RestTemplate para realizar chamadas HTTP. É uma ferramenta padrão do
	 * Spring para comunicação com outras APIs REST. É declarado como 'final' para
	 * ser thread-safe.
	 */
	private final RestTemplate restTemplate = new RestTemplate();

	/**
	 * Busca livros na API do Google usando uma consulta avançada e, em seguida,
	 * manipula a resposta para solicitar imagens de capa de maior resolução.
	 *
	 * @param titulo O termo de busca fornecido pelo utilizador.
	 * @return Uma String contendo o JSON da resposta do Google, com as URLs das
	 *         imagens modificadas para alta resolução.
	 */
	public String buscarLivrosExterno(String titulo) {
		try {
			// Passo 1: Codificar o termo de busca do utilizador.
			// Isto é essencial para garantir que caracteres especiais (como espaços,
			// acentos, etc.)
			// sejam transmitidos corretamente na URL, evitando erros de HTTP 400 (Bad
			// Request).
			String encodedTitulo = URLEncoder.encode(titulo, StandardCharsets.UTF_8);

			// Passo 2: Construir a string da consulta.
			// Usamos o operador "intitle:" para dizer à API do Google para focar a busca
			// no campo do título. Isto aumenta drasticamente a relevância dos resultados
			// para buscas de títulos específicos.
			String query = "intitle:" + encodedTitulo;

			// Passo 3: Montar a URL final manualmente.
			// Esta abordagem dá-nos controlo total sobre a formatação da query, o que é
			// necessário para que os operadores especiais da API funcionem corretamente.
			String urlFinal = String.format("%s?q=%s&key=%s&maxResults=40&langRestrict=pt&printType=books", API_URL,
					query, apiKey);

			LOGGER.log(Level.INFO, "Buscando na API externa com consulta de título aprimorada: {0}", urlFinal);

			// Passo 4: Fazer a chamada de rede para a API do Google.
			String respostaJson = restTemplate.getForObject(urlFinal, String.class);

			// Passo 5: Refinar a resposta para o frontend.
			// Esta é a correção para o problema de baixa resolução da capa.
			if (respostaJson != null) {
				// A API do Google, por padrão, retorna URLs de thumbnail com o parâmetro
				// "&zoom=1".
				// Ao substituir por "&zoom=0", solicitamos a maior resolução de imagem
				// disponível.
				// Também removemos "&edge=curl", que por vezes adiciona um efeito de página
				// curvada indesejado.
				// Esta transformação de dados é uma responsabilidade valiosa do nosso
				// backend-proxy.
				String respostaRefinada = respostaJson.replace("&zoom=1", "&zoom=0").replace("&edge=curl", "");
				LOGGER.info("URLs de imagem refinadas para alta resolução (zoom=0).");
				return respostaRefinada;
			}

			// Se a resposta for nula por algum motivo, retornamos um JSON de itens vazios.
			return "{\"items\":[]}";

		} catch (Exception e) {
			// Em caso de qualquer erro (ex: falha na rede, erro de codificação),
			// registamos o erro e retornamos uma resposta JSON vazia e segura.
			// Isto garante que o frontend não quebre, mesmo que a API externa falhe.
			LOGGER.log(Level.SEVERE, "Falha ao buscar livros externamente para o termo: " + titulo, e);
			return "{\"items\":[]}";
		}
	}
}
