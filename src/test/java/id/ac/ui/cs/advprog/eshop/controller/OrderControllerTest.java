package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderController controller;

    @Test
    void createOrderPage_shouldReturnFormPage() {
        Model model = new ExtendedModelMap();

        String view = controller.createOrderPage(model);

        assertEquals("createOrder", view);
        assertNotNull(model.getAttribute("order"));
    }

    @Test
    void createOrderPost_shouldCreateOrderAndRedirect() {
        OrderController.OrderForm form = new OrderController.OrderForm();
        form.setAuthor("Safira Sudrajat");
        form.setOrderTime(1708560000L);

        String view = controller.createOrderPost(form);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderService).createOrder(captor.capture());
        Order capturedOrder = captor.getValue();
        assertEquals("Safira Sudrajat", capturedOrder.getAuthor());
        assertEquals(1708560000L, capturedOrder.getOrderTime());
        assertNotNull(capturedOrder.getId());
        assertEquals(1, capturedOrder.getProducts().size());
        assertEquals("Default Product", capturedOrder.getProducts().get(0).getProductName());
        assertEquals("redirect:/order/history", view);
    }

    @Test
    void historyFormPage_shouldReturnHistoryForm() {
        assertEquals("orderHistory", controller.historyFormPage());
    }

    @Test
    void historyResultPage_shouldReturnOrderList() {
        Product product = new Product();
        product.setProductId("p-1");
        product.setProductName("Notebook");
        product.setProductQuantity(1);
        Order order = new Order("o-1", List.of(product), 1708560000L, "Safira Sudrajat");
        when(orderService.findAllByAuthor("Safira Sudrajat")).thenReturn(List.of(order));
        Model model = new ExtendedModelMap();

        String view = controller.historyResultPage("Safira Sudrajat", model);

        assertEquals("orderList", view);
        assertEquals("Safira Sudrajat", model.getAttribute("author"));
        assertEquals(1, ((List<?>) model.getAttribute("orders")).size());
        verify(orderService).findAllByAuthor("Safira Sudrajat");
    }

    @Test
    void payOrderPage_shouldReturnPayPage() {
        Product product = new Product();
        product.setProductId("p-1");
        product.setProductName("Notebook");
        product.setProductQuantity(1);
        Order order = new Order("o-1", List.of(product), 1708560000L, "Safira Sudrajat");
        when(orderService.findById("o-1")).thenReturn(order);
        Model model = new ExtendedModelMap();

        String view = controller.payOrderPage("o-1", model);

        assertEquals("orderPay", view);
        assertEquals(order, model.getAttribute("order"));
        verify(orderService).findById("o-1");
    }

    @Test
    void payOrderPost_withVoucher_shouldPassVoucherData() {
        Product product = new Product();
        product.setProductId("p-1");
        product.setProductName("Notebook");
        product.setProductQuantity(1);
        Order order = new Order("o-1", List.of(product), 1708560000L, "Safira Sudrajat");
        Payment payment = new Payment();
        payment.setId("payment-1");
        when(orderService.findById("o-1")).thenReturn(order);
        when(paymentService.addPayment(eq(order), eq("VOUCHER_CODE"), anyMap())).thenReturn(payment);
        Model model = new ExtendedModelMap();

        String view = controller.payOrderPost("o-1", "VOUCHER_CODE", "ESHOP1234ABC5678", null, null, model);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(paymentService).addPayment(eq(order), eq("VOUCHER_CODE"), mapCaptor.capture());
        Map<String, String> map = mapCaptor.getValue();
        assertEquals("ESHOP1234ABC5678", map.get("voucherCode"));
        assertEquals("orderPayResult", view);
        assertEquals(payment, model.getAttribute("payment"));
    }

    @Test
    void payOrderPost_withBankTransfer_shouldPassBankData() {
        Product product = new Product();
        product.setProductId("p-1");
        product.setProductName("Notebook");
        product.setProductQuantity(1);
        Order order = new Order("o-1", List.of(product), 1708560000L, "Safira Sudrajat");
        Payment payment = new Payment();
        payment.setId("payment-2");
        when(orderService.findById("o-1")).thenReturn(order);
        when(paymentService.addPayment(eq(order), eq("BANK_TRANSFER"), anyMap())).thenReturn(payment);
        Model model = new ExtendedModelMap();

        String view = controller.payOrderPost("o-1", "BANK_TRANSFER", null, "BCA", "INV-1234", model);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(paymentService).addPayment(eq(order), eq("BANK_TRANSFER"), mapCaptor.capture());
        Map<String, String> map = mapCaptor.getValue();
        assertEquals("BCA", map.get("bankName"));
        assertEquals("INV-1234", map.get("referenceCode"));
        assertEquals("orderPayResult", view);
        assertEquals(payment, model.getAttribute("payment"));
    }

    @Test
    void payOrderPost_withUnknownMethod_shouldPassEmptyDataMap() {
        Product product = new Product();
        product.setProductId("p-1");
        product.setProductName("Notebook");
        product.setProductQuantity(1);
        Order order = new Order("o-1", List.of(product), 1708560000L, "Safira Sudrajat");
        Payment payment = new Payment();
        payment.setId("payment-3");
        when(orderService.findById("o-1")).thenReturn(order);
        when(paymentService.addPayment(eq(order), eq("OTHER"), anyMap())).thenReturn(payment);
        Model model = new ExtendedModelMap();

        String view = controller.payOrderPost("o-1", "OTHER", null, null, null, model);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(paymentService).addPayment(eq(order), eq("OTHER"), mapCaptor.capture());
        assertTrue(mapCaptor.getValue().isEmpty());
        assertEquals("orderPayResult", view);
    }

    @Test
    void orderForm_shouldStoreAndReturnValues() {
        OrderController.OrderForm form = new OrderController.OrderForm();

        form.setAuthor("Safira Sudrajat");
        form.setOrderTime(1708560000L);

        assertEquals("Safira Sudrajat", form.getAuthor());
        assertEquals(1708560000L, form.getOrderTime());
    }
}
