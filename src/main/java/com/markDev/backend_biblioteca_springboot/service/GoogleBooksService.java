package com.markDev.backend_biblioteca_springboot.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markDev.backend_biblioteca_springboot.dto.BookSearchResultDTO;
import com.markDev.backend_biblioteca_springboot.service.googlebooks.model.GoogleBooksResponse;
import com.markDev.backend_biblioteca_springboot.service.googlebooks.model.VolumeItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Serviço que atua como um proxy seguro para a API do Google Books. Esta versão
 * utiliza uma estratégia de busca refinada para maximizar a relevância dos
 * resultados.
 */
@Service
public class GoogleBooksService {

	private static final Logger LOGGER = Logger.getLogger(GoogleBooksService.class.getName());
	private static final String API_URL = "https://www.googleapis.com/books/v1/volumes";

	@Value("${google.books.api.key}")
	private String apiKey;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	/**
	 * Busca livros na API do Google e retorna uma lista limpa de DTOs, já
	 * reordenada para máxima relevância.
	 *
	 * @param titulo O termo de busca para o título.
	 * @param autor  O termo de busca para o autor.
	 * @return Uma lista de BookSearchResultDTO com os resultados.
	 */
	public List<BookSearchResultDTO> buscarLivrosExterno(String titulo, String autor) {
		String query = buildAdvancedQuery(titulo, autor);
		if (query.isBlank()) {
			return Collections.emptyList();
		}

		try {
			String urlFinal = UriComponentsBuilder.fromHttpUrl(API_URL).queryParam("q", query).queryParam("key", apiKey)
					.queryParam("maxResults", 40).queryParam("printType", "books").toUriString();

			LOGGER.log(Level.INFO, "URL de busca final: {0}", urlFinal);

			String respostaJson = restTemplate.getForObject(urlFinal, String.class);
			GoogleBooksResponse response = objectMapper.readValue(respostaJson, GoogleBooksResponse.class);

			if (response == null || response.getItems() == null) {
				return Collections.emptyList();
			}

			List<BookSearchResultDTO> resultados = response.getItems().stream()
					.filter(item -> item != null && item.getVolumeInfo() != null).map(this::convertToStandardDTO)
					.collect(Collectors.toList());

			// Reordena a nossa lista de DTOs com base nos critérios de relevância.
			resultados.sort(Comparator
					.comparing((BookSearchResultDTO dto) -> "pt".equalsIgnoreCase(dto.getIdioma()) ? 0 : 1)
					.thenComparing(
							dto -> titulo != null && !titulo.isBlank() && titulo.equalsIgnoreCase(dto.getTitulo()) ? 0
									: 1));

			return resultados;

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erro ao chamar ou processar a API do Google Books para a consulta: " + query, e);
			return Collections.emptyList();
		}
	}

	/**
	 * Constrói a string de consulta para a API do Google. Esta versão combina os
	 * termos de forma mais genérica para permitir que o algoritmo de relevância do
	 * Google encontre os melhores resultados.
	 */
	private String buildAdvancedQuery(String titulo, String autor) {
		StringBuilder queryBuilder = new StringBuilder();
		if (titulo != null && !titulo.isBlank()) {
			// Apenas adiciona o título como termo de busca geral.
			queryBuilder.append(titulo);
		}
		if (autor != null && !autor.isBlank()) {
			if (queryBuilder.length() > 0) {
				queryBuilder.append(" ");
			}
			// O operador 'inauthor:' continua a ser muito eficaz.
			queryBuilder.append("inauthor:").append(autor);
		}
		return queryBuilder.toString();
	}

	private BookSearchResultDTO convertToStandardDTO(VolumeItem item) {
		var volumeInfo = item.getVolumeInfo();
		BookSearchResultDTO dto = new BookSearchResultDTO();

		dto.setTitulo(volumeInfo.getTitle() != null ? volumeInfo.getTitle() : "Sem título");
		dto.setAutor(volumeInfo.getAuthors() != null && !volumeInfo.getAuthors().isEmpty()
				? String.join(", ", volumeInfo.getAuthors())
				: "Autor desconhecido");
		dto.setSinopse(volumeInfo.getDescription());
		dto.setNumeroPaginas(volumeInfo.getPageCount());
		dto.setIdioma(volumeInfo.getLanguage());

		if (volumeInfo.getPublishedDate() != null && volumeInfo.getPublishedDate().matches("\\d{4}.*")) {
			try {
				dto.setAno(Integer.parseInt(volumeInfo.getPublishedDate().substring(0, 4)));
			} catch (NumberFormatException e) {
				/* ignora */ }
		}

		if (volumeInfo.getImageLinks() != null && volumeInfo.getImageLinks().getThumbnail() != null) {
			String capaUrl = volumeInfo.getImageLinks().getThumbnail().replace("&zoom=1", "&zoom=0")
					.replace("&edge=curl", "");
			dto.setCapaUrl(capaUrl);
		}
		return dto;
	}
}
