package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void paymentDetailFormPage_withoutQuery_shouldReturnFormView() {
        Model model = new ExtendedModelMap();

        String view = controller.paymentDetailFormPage(null, model);

        assertEquals("paymentDetail", view);
        assertEquals(null, model.getAttribute("payment"));
        assertEquals(null, model.getAttribute("paymentId"));
    }

    @Test
    void paymentDetailFormPage_withQuery_shouldRedirectToDetailPage() {
        Model model = new ExtendedModelMap();

        String view = controller.paymentDetailFormPage("payment-1", model);

        assertEquals("redirect:/payment/detail/payment-1", view);
    }

    @Test
    void paymentDetailFormPage_withBlankQuery_shouldReturnFormView() {
        Model model = new ExtendedModelMap();

        String view = controller.paymentDetailFormPage("", model);

        assertEquals("paymentDetail", view);
        assertEquals(null, model.getAttribute("payment"));
        assertEquals(null, model.getAttribute("paymentId"));
    }

    @Test
    void paymentDetailPage_whenNotFound_shouldReturnDetailViewWithNullPayment() {
        when(paymentService.getPayment("p-404")).thenReturn(null);
        Model model = new ExtendedModelMap();

        String view = controller.paymentDetailPage("p-404", model);

        assertEquals("paymentDetail", view);
        assertEquals(null, model.getAttribute("payment"));
        assertEquals("p-404", model.getAttribute("paymentId"));
        verify(paymentService).getPayment("p-404");
    }

    @Test
    void paymentDetailPage_whenFound_shouldReturnDetailViewWithPayment() {
        Payment payment = new Payment();
        payment.setId("payment-1");
        when(paymentService.getPayment("payment-1")).thenReturn(payment);
        Model model = new ExtendedModelMap();

        String view = controller.paymentDetailPage("payment-1", model);

        assertEquals("paymentDetail", view);
        assertEquals(payment, model.getAttribute("payment"));
        assertEquals("payment-1", model.getAttribute("paymentId"));
        verify(paymentService).getPayment("payment-1");
    }

    @Test
    void paymentAdminListPage_shouldReturnListViewAndModel() {
        Payment payment = new Payment();
        payment.setId("payment-1");
        when(paymentService.getAllPayments()).thenReturn(List.of(payment));
        Model model = new ExtendedModelMap();

        String view = controller.paymentAdminListPage(model);

        assertEquals("paymentAdminList", view);
        assertEquals(1, ((List<?>) model.getAttribute("payments")).size());
        verify(paymentService).getAllPayments();
    }

    @Test
    void paymentAdminDetailPage_whenNotFound_shouldReturnDetailViewWithNullPayment() {
        when(paymentService.getPayment("p-404")).thenReturn(null);
        Model model = new ExtendedModelMap();

        String view = controller.paymentAdminDetailPage("p-404", model);

        assertEquals("paymentAdminDetail", view);
        assertEquals(null, model.getAttribute("payment"));
        verify(paymentService).getPayment("p-404");
    }

    @Test
    void paymentAdminDetailPage_whenFound_shouldReturnDetailViewWithPayment() {
        Payment payment = new Payment();
        payment.setId("payment-2");
        when(paymentService.getPayment("payment-2")).thenReturn(payment);
        Model model = new ExtendedModelMap();

        String view = controller.paymentAdminDetailPage("payment-2", model);

        assertEquals("paymentAdminDetail", view);
        assertEquals(payment, model.getAttribute("payment"));
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
