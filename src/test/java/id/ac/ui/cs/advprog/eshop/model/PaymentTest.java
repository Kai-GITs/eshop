package id.ac.ui.cs.advprog.eshop.model;

import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PaymentTest {
    private Order order;
    private Map<String, String> paymentData;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);

        order = new Order(
                "13652556-012a-4c07-b546-54eb1396d79b",
                List.of(product),
                1708560000L,
                "Safira Sudrajat"
        );

        paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
    }

    @Test
    void testCreatePaymentModel() {
        Payment payment = new Payment();
        payment.setId("payment-1");
        payment.setOrder(order);
        payment.setMethod("VOUCHER_CODE");
        payment.setStatus(PaymentStatus.SUCCESS.getValue());
        payment.setPaymentData(paymentData);

        assertEquals("payment-1", payment.getId());
        assertSame(order, payment.getOrder());
        assertEquals("VOUCHER_CODE", payment.getMethod());
        assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
        assertSame(paymentData, payment.getPaymentData());
    }

    @Test
    void testUpdatePaymentStatusAndData() {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod("BANK_TRANSFER");
        payment.setStatus(PaymentStatus.REJECTED.getValue());

        Map<String, String> bankTransferData = new HashMap<>();
        bankTransferData.put("bankName", "BCA");
        bankTransferData.put("referenceCode", "INV-1234");
        payment.setPaymentData(bankTransferData);

        assertEquals("BANK_TRANSFER", payment.getMethod());
        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
        assertEquals("BCA", payment.getPaymentData().get("bankName"));
        assertEquals("INV-1234", payment.getPaymentData().get("referenceCode"));
    }
}
