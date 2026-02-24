package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService service;

    @InjectMocks
    private ProductController controller;

    @Test
    void createProductPage_shouldReturnFormPage() {
        Model model = new ExtendedModelMap();

        String view = controller.createProductPage(model);

        assertEquals("createProduct", view);
        assertNotNull(model.getAttribute("product"));
    }

    @Test
    void createProductPost_shouldSetIdAndRedirect() {
        Product product = new Product();
        product.setProductName("Notebook");
        product.setProductQuantity(5);

        String view = controller.createProductPost(product, new ExtendedModelMap());

        assertEquals("redirect:list", view);
        assertNotNull(product.getProductId());
        verify(service).create(product);
    }

    @Test
    void productListPage_shouldReturnListPage() {
        Product product = new Product();
        product.setProductId("p-1");
        when(service.findAll()).thenReturn(List.of(product));
        Model model = new ExtendedModelMap();

        String view = controller.productListPage(model);

        assertEquals("productList", view);
        assertEquals(1, ((List<?>) model.getAttribute("products")).size());
        verify(service).findAll();
    }

    @Test
    void editProductPage_shouldAddSelectedProduct() {
        Product product = new Product();
        product.setProductId("p-1");
        when(service.findById("p-1")).thenReturn(product);
        Model model = new ExtendedModelMap();

        String view = controller.editProductPage("p-1", model);

        assertEquals("editProduct", view);
        assertEquals(product, model.getAttribute("product"));
        verify(service).findById("p-1");
    }

    @Test
    void editProductPost_shouldDelegateAndRedirect() {
        Product product = new Product();
        product.setProductId("p-1");

        String view = controller.editProductPost(product);

        assertEquals("redirect:list", view);
        verify(service).edit(product);
    }

    @Test
    void deleteProduct_shouldDelegateAndRedirect() {
        String view = controller.deleteProduct("p-1");

        assertEquals("redirect:/product/list", view);
        verify(service).delete("p-1");
    }
}
