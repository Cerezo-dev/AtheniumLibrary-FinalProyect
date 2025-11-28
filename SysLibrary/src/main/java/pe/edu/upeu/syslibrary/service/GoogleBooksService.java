package pe.edu.upeu.syslibrary.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleBooksService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<GoogleBookDto> buscarLibros(String query) {
        List<GoogleBookDto> libros = new ArrayList<>();
        try {
            // 1. Codificar la búsqueda (ej: "Harry Potter" -> "Har ry+Potter")
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://www.googleapis.com/books/v1/volumes?q=" + encodedQuery + "&maxResults=10";

            // 2. Hacer la petición HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 3. Parsear el JSON
            JsonNode root = mapper.readTree(response.body());
            JsonNode items = root.path("items");

            if (items.isArray()) {
                for (JsonNode item : items) {
                    JsonNode volumeInfo = item.path("volumeInfo");

                    String titulo = volumeInfo.path("title").asText("Sin título");
                    String autor = "Desconocido";
                    if (volumeInfo.path("authors").isArray()) {
                        autor = volumeInfo.path("authors").get(0).asText();
                    }

                    // Intentamos obtener el ISBN_13 para cruzar info con tu BD local
                    String isbn = "";
                    JsonNode industryIdentifiers = volumeInfo.path("industryIdentifiers");
                    if (industryIdentifiers.isArray()) {
                        for (JsonNode id : industryIdentifiers) {
                            if ("ISBN_13".equals(id.path("type").asText())) {
                                isbn = id.path("identifier").asText();
                                break;
                            }
                        }
                    }

                    // Imagen de portada
                    String imagenUrl = volumeInfo.path("imageLinks").path("thumbnail").asText(null);

                    libros.add(new GoogleBookDto(titulo, autor, isbn, imagenUrl));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return libros;
    }

    // DTO Interno para transportar datos limpios
    @Data
    public static class GoogleBookDto {
        private String titulo;
        private String autor;
        private String isbn;
        private String imagenUrl;

        public GoogleBookDto(String titulo, String autor, String isbn, String imagenUrl) {
            this.titulo = titulo;
            this.autor = autor;
            this.isbn = isbn;
            this.imagenUrl = imagenUrl;
        }
    }
}