package id.ac.ui.cs.advprog.eshop.functional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateProductFunctionalTest {

    @LocalServerPort
    private int port;

    @Test
    void createProduct_whenSubmittedFromForm_newProductAppearsInList() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String baseUrl = "http://localhost:" + port;

        HttpRequest createProductRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/product/create"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("productName=Notebook&productQuantity=5"))
                .build();

        HttpResponse<String> createProductResponse = client.send(createProductRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(302, createProductResponse.statusCode());
        assertTrue(createProductResponse.headers().firstValue("location").orElse("").contains("list"));

        HttpRequest productListRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/product/list"))
                .GET()
                .build();

        HttpResponse<String> productListResponse = client.send(productListRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, productListResponse.statusCode());
        assertTrue(productListResponse.body().contains("Notebook"));
        assertTrue(productListResponse.body().contains("5"));
    }
}
