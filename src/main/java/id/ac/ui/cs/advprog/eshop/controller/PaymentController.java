package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/detail")
    @ResponseBody
    public String paymentDetailFormPage() {
        return "<html><body><h3>Payment Detail Form</h3></body></html>";
    }

    @GetMapping("/detail/{paymentId}")
    @ResponseBody
    public String paymentDetailPage(@PathVariable String paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return "<html><body><h3>Payment Detail</h3><p>Payment not found</p></body></html>";
        }
        return "<html><body><h3>Payment Detail</h3><p>" + payment.getId() + "</p></body></html>";
    }

    @GetMapping("/admin/list")
    @ResponseBody
    public String paymentAdminListPage() {
        List<Payment> payments = paymentService.getAllPayments();
        return "<html><body><h3>Payment Admin List</h3><p>Total Payment: " + payments.size() + "</p></body></html>";
    }

    @GetMapping("/admin/detail/{paymentId}")
    @ResponseBody
    public String paymentAdminDetailPage(@PathVariable String paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return "<html><body><h3>Admin Payment Detail</h3><p>Payment not found</p></body></html>";
        }
        return "<html><body><h3>Admin Payment Detail</h3><p>" + payment.getId() + "</p></body></html>";
    }

    @PostMapping("/admin/set-status/{paymentId}")
    public String setPaymentStatus(
            @PathVariable String paymentId,
            @RequestParam("status") String status
    ) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment != null) {
            paymentService.setStatus(payment, status);
        }
        return "redirect:/payment/admin/detail/" + paymentId;
    }
}
