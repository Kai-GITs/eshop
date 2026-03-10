package id.ac.ui.cs.advprog.eshop.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerTest {

    @Test
    void home_shouldRedirectToProductList() {
        HomeController controller = new HomeController();

        String view = controller.home();

        assertEquals("redirect:/product/list", view);
    }
}
