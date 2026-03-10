package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/create")
    public String createOrderPage(Model model) {
        model.addAttribute("order", new OrderForm());
        return "createOrder";
    }

    @PostMapping("/create")
    public String createOrderPost(@ModelAttribute("order") OrderForm orderForm) {
        String orderId = UUID.randomUUID().toString();
        List<Product> products = buildDefaultProducts();
        Order order = new Order(orderId, products, orderForm.getOrderTime(), orderForm.getAuthor());
        orderService.createOrder(order);
        return "redirect:/order/history";
    }

    @GetMapping("/history")
    public String historyFormPage() {
        return "orderHistory";
    }

    @PostMapping("/history")
    public String historyResultPage(@RequestParam("author") String author, Model model) {
        List<Order> orders = orderService.findAllByAuthor(author);
        model.addAttribute("author", author);
        model.addAttribute("orders", orders);
        return "orderList";
    }

    @GetMapping("/pay/{orderId}")
    public String payOrderPage(@PathVariable String orderId, Model model) {
        Order order = orderService.findById(orderId);
        model.addAttribute("order", order);
        return "orderPay";
    }

    @PostMapping("/pay/{orderId}")
    public String payOrderPost(
            @PathVariable String orderId,
            @RequestParam("method") String method,
            @RequestParam(required = false) String voucherCode,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String referenceCode,
            Model model
    ) {
        Order order = orderService.findById(orderId);
        Map<String, String> paymentData = new HashMap<>();
        if ("VOUCHER_CODE".equals(method)) {
            paymentData.put("voucherCode", voucherCode);
        } else if ("BANK_TRANSFER".equals(method)) {
            paymentData.put("bankName", bankName);
            paymentData.put("referenceCode", referenceCode);
        }

        var payment = paymentService.addPayment(order, method, paymentData);
        model.addAttribute("payment", payment);
        return "orderPayResult";
    }

    private List<Product> buildDefaultProducts() {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName("Default Product");
        product.setProductQuantity(1);
        products.add(product);
        return products;
    }

    public static class OrderForm {
        private String author;
        private Long orderTime;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public Long getOrderTime() {
            return orderTime;
        }

        public void setOrderTime(Long orderTime) {
            this.orderTime = orderTime;
        }
    }
}
