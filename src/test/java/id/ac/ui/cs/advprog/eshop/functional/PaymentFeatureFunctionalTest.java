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
class PaymentFeatureFunctionalTest {

    @LocalServerPort
    private int port;

    @Test
    void paymentPages_shouldBeAccessible() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String baseUrl = "http://localhost:" + port;

        HttpRequest listRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/payment/admin/list"))
                .GET()
                .build();
        HttpResponse<String> listResponse = client.send(listRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, listResponse.statusCode());
        assertTrue(listResponse.body().contains("Payment"));

        HttpRequest detailRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/payment/detail/payment-unknown"))
                .GET()
                .build();
        HttpResponse<String> detailResponse = client.send(detailRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, detailResponse.statusCode());
        assertTrue(detailResponse.body().contains("Payment Detail"));

        HttpRequest adminDetailRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/payment/admin/detail/payment-unknown"))
                .GET()
                .build();
        HttpResponse<String> adminDetailResponse = client.send(adminDetailRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, adminDetailResponse.statusCode());
        assertTrue(adminDetailResponse.body().contains("Admin Payment Detail"));
    }

    @Test
    void setStatusEndpoint_shouldAcceptPost() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String baseUrl = "http://localhost:" + port;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/payment/admin/set-status/payment-unknown"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("status=SUCCESS"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(302, response.statusCode());
    }
}
