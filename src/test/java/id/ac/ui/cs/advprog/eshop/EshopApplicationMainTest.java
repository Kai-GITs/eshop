package id.ac.ui.cs.advprog.eshop;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class EshopApplicationMainTest {

    @Test
    void main_shouldRunWithNonWebConfiguration() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () ->
                EshopApplication.main(new String[]{
                        "--spring.main.web-application-type=none",
                        "--spring.main.banner-mode=off"
                })
        );
    }
}
