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
class OrderFeatureFunctionalTest {

    @LocalServerPort
    private int port;

    @Test
    void createOrderAndSearchHistory_shouldWork() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String baseUrl = "http://localhost:" + port;

        HttpRequest createPageRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/order/create"))
                .GET()
                .build();
        HttpResponse<String> createPageResponse = client.send(createPageRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, createPageResponse.statusCode());
        assertTrue(createPageResponse.body().contains("Create New Order"));

        HttpRequest createOrderRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/order/create"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("author=Safira+Sudrajat&orderTime=1708560000"))
                .build();
        HttpResponse<String> createOrderResponse = client.send(createOrderRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(302, createOrderResponse.statusCode());

        HttpRequest historyPageRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/order/history"))
                .GET()
                .build();
        HttpResponse<String> historyPageResponse = client.send(historyPageRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, historyPageResponse.statusCode());
        assertTrue(historyPageResponse.body().contains("Search Order History"));

        HttpRequest searchHistoryRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/order/history"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("author=Safira+Sudrajat"))
                .build();
        HttpResponse<String> searchHistoryResponse = client.send(searchHistoryRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, searchHistoryResponse.statusCode());
        assertTrue(searchHistoryResponse.body().contains("Safira Sudrajat"));
    }
}
