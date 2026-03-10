package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController controller;

    @Test
    void paymentDetailFormPage_shouldReturnHtmlForm() {
        String response = controller.paymentDetailFormPage();
        assertTrue(response.contains("Payment Detail Form"));
    }

    @Test
    void paymentDetailPage_whenNotFound_shouldReturnNotFoundHtml() {
        when(paymentService.getPayment("p-404")).thenReturn(null);

        String response = controller.paymentDetailPage("p-404");

        assertTrue(response.contains("Payment Detail"));
        assertTrue(response.contains("Payment not found"));
        verify(paymentService).getPayment("p-404");
    }

    @Test
    void paymentDetailPage_whenFound_shouldReturnPaymentId() {
        Payment payment = new Payment();
        payment.setId("payment-1");
        when(paymentService.getPayment("payment-1")).thenReturn(payment);

        String response = controller.paymentDetailPage("payment-1");

        assertTrue(response.contains("Payment Detail"));
        assertTrue(response.contains("payment-1"));
        verify(paymentService).getPayment("payment-1");
    }

    @Test
    void paymentAdminListPage_shouldShowCount() {
        Payment payment = new Payment();
        payment.setId("payment-1");
        when(paymentService.getAllPayments()).thenReturn(List.of(payment));

        String response = controller.paymentAdminListPage();

        assertTrue(response.contains("Payment Admin List"));
        assertTrue(response.contains("Total Payment: 1"));
        verify(paymentService).getAllPayments();
    }

    @Test
    void paymentAdminDetailPage_whenNotFound_shouldReturnNotFoundHtml() {
        when(paymentService.getPayment("p-404")).thenReturn(null);

        String response = controller.paymentAdminDetailPage("p-404");

        assertTrue(response.contains("Admin Payment Detail"));
        assertTrue(response.contains("Payment not found"));
        verify(paymentService).getPayment("p-404");
    }

    @Test
    void paymentAdminDetailPage_whenFound_shouldReturnPaymentId() {
        Payment payment = new Payment();
        payment.setId("payment-2");
        when(paymentService.getPayment("payment-2")).thenReturn(payment);

        String response = controller.paymentAdminDetailPage("payment-2");

        assertTrue(response.contains("Admin Payment Detail"));
        assertTrue(response.contains("payment-2"));
        verify(paymentService).getPayment("payment-2");
    }

    @Test
    void setPaymentStatus_whenPaymentFound_shouldDelegateAndRedirect() {
        Payment payment = new Payment();
        payment.setId("payment-1");
        when(paymentService.getPayment("payment-1")).thenReturn(payment);

        String view = controller.setPaymentStatus("payment-1", "SUCCESS");

        assertEquals("redirect:/payment/admin/detail/payment-1", view);
        verify(paymentService).getPayment("payment-1");
        verify(paymentService).setStatus(payment, "SUCCESS");
    }

    @Test
    void setPaymentStatus_whenPaymentNotFound_shouldOnlyRedirect() {
        when(paymentService.getPayment("p-404")).thenReturn(null);

        String view = controller.setPaymentStatus("p-404", "REJECTED");

        assertEquals("redirect:/payment/admin/detail/p-404", view);
        verify(paymentService).getPayment("p-404");
        verify(paymentService, never()).setStatus(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString());
    }
}
