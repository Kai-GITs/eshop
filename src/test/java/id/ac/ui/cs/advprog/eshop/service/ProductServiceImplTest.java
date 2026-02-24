package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void create_shouldDelegateToRepositoryAndReturnProduct() {
        Product product = buildProduct("p-1", "Book", 3);

        Product created = productService.create(product);

        verify(productRepository).create(product);
        assertSame(product, created);
    }

    @Test
    void findAll_shouldConvertIteratorToList() {
        Product first = buildProduct("p-1", "Book", 3);
        Product second = buildProduct("p-2", "Pen", 7);
        when(productRepository.findAll()).thenReturn(List.of(first, second).iterator());

        List<Product> products = productService.findAll();

        assertEquals(2, products.size());
        assertEquals("p-1", products.get(0).getProductId());
        assertEquals("p-2", products.get(1).getProductId());
        verify(productRepository).findAll();
    }

    @Test
    void findById_shouldReturnRepositoryResult() {
        Product product = buildProduct("p-1", "Book", 3);
        when(productRepository.findById("p-1")).thenReturn(product);

        Product found = productService.findById("p-1");

        assertSame(product, found);
        verify(productRepository).findById("p-1");
    }

    @Test
    void edit_shouldReturnRepositoryResult() {
        Product product = buildProduct("p-1", "Book", 10);
        when(productRepository.edit(product)).thenReturn(product);

        Product edited = productService.edit(product);

        assertSame(product, edited);
        verify(productRepository).edit(product);
    }

    @Test
    void delete_shouldDelegateToRepository() {
        productService.delete("p-1");

        verify(productRepository).delete("p-1");
    }

    private Product buildProduct(String id, String name, int quantity) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setProductQuantity(quantity);
        return product;
    }
}
