package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductRepositoryTest {

    private ProductRepository productRepository;
    private Product firstProduct;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository();

        firstProduct = new Product();
        firstProduct.setProductId("product-1");
        firstProduct.setProductName("Indomie");
        firstProduct.setProductQuantity(10);
        productRepository.create(firstProduct);
    }

    @Test
    void editProduct_existingId_updatesProductData() {
        Product updatedProduct = new Product();
        updatedProduct.setProductId("product-1");
        updatedProduct.setProductName("Mie Sedaap");
        updatedProduct.setProductQuantity(20);

        Product result = productRepository.edit(updatedProduct);
        Product savedProduct = productRepository.findById("product-1");

        assertNotNull(result);
        assertEquals("Mie Sedaap", savedProduct.getProductName());
        assertEquals(20, savedProduct.getProductQuantity());
    }

    @Test
    void editProduct_unknownId_returnsNull() {
        Product unknownProduct = new Product();
        unknownProduct.setProductId("unknown-id");
        unknownProduct.setProductName("Unknown");
        unknownProduct.setProductQuantity(1);

        Product result = productRepository.edit(unknownProduct);

        assertNull(result);
    }

    @Test
    void deleteProduct_existingId_removesProduct() {
        productRepository.delete("product-1");

        Product deletedProduct = productRepository.findById("product-1");
        Iterator<Product> iterator = productRepository.findAll();

        assertNull(deletedProduct);
        assertFalse(iterator.hasNext());
    }

    @Test
    void deleteProduct_unknownId_keepsExistingData() {
        Product secondProduct = new Product();
        secondProduct.setProductId("product-2");
        secondProduct.setProductName("Susu");
        secondProduct.setProductQuantity(4);
        productRepository.create(secondProduct);

        productRepository.delete("unknown-id");
        List<Product> allProducts = toList(productRepository.findAll());

        assertEquals(2, allProducts.size());
        assertTrue(allProducts.stream().anyMatch(product -> "product-1".equals(product.getProductId())));
        assertTrue(allProducts.stream().anyMatch(product -> "product-2".equals(product.getProductId())));
    }

    private List<Product> toList(Iterator<Product> iterator) {
        List<Product> products = new ArrayList<>();
        iterator.forEachRemaining(products::add);
        return products;
    }
}
