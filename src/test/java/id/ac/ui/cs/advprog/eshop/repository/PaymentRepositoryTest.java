package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentRepositoryTest {
    private PaymentRepository paymentRepository;
    private List<Payment> payments;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();

        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);

        Order order = new Order(
                "13652556-012a-4c07-b546-54eb1396d79b",
                List.of(product),
                1708560000L,
                "Safira Sudrajat"
        );

        Payment payment1 = new Payment();
        payment1.setId("payment-1");
        payment1.setOrder(order);
        payment1.setMethod("VOUCHER_CODE");
        payment1.setStatus(PaymentStatus.SUCCESS.getValue());
        Map<String, String> paymentData1 = new HashMap<>();
        paymentData1.put("voucherCode", "ESHOP1234ABC5678");
        payment1.setPaymentData(paymentData1);

        Payment payment2 = new Payment();
        payment2.setId("payment-2");
        payment2.setOrder(order);
        payment2.setMethod("BANK_TRANSFER");
        payment2.setStatus(PaymentStatus.REJECTED.getValue());
        Map<String, String> paymentData2 = new HashMap<>();
        paymentData2.put("bankName", "BCA");
        paymentData2.put("referenceCode", "INV-1234");
        payment2.setPaymentData(paymentData2);

        payments = List.of(payment1, payment2);
    }

    @Test
    void testSaveCreateAndFindById() {
        Payment payment = payments.get(0);
        Payment result = paymentRepository.save(payment);
        Payment findResult = paymentRepository.findById(payment.getId());

        assertEquals(payment.getId(), result.getId());
        assertEquals(payment.getId(), findResult.getId());
        assertEquals(payment.getMethod(), findResult.getMethod());
        assertEquals(payment.getStatus(), findResult.getStatus());
    }

    @Test
    void testFindByIdNotFound() {
        Payment findResult = paymentRepository.findById("unknown-payment");
        assertNull(findResult);
    }

    @Test
    void testFindAllPayments() {
        paymentRepository.save(payments.get(0));
        paymentRepository.save(payments.get(1));

        List<Payment> results = paymentRepository.findAll();
        assertEquals(2, results.size());
        assertEquals("payment-1", results.get(0).getId());
        assertEquals("payment-2", results.get(1).getId());
    }
}
