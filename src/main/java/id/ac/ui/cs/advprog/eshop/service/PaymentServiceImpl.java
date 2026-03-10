package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String VOUCHER_METHOD = "VOUCHER_CODE";
    private static final String VOUCHER_CODE_KEY = "voucherCode";
    private static final String BANK_TRANSFER_METHOD = "BANK_TRANSFER";
    private static final String BANK_NAME_KEY = "bankName";
    private static final String REFERENCE_CODE_KEY = "referenceCode";

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setPaymentData(paymentData);

        if (VOUCHER_METHOD.equals(method)) {
            applyVoucherStatus(payment, paymentData);
        } else if (BANK_TRANSFER_METHOD.equals(method)) {
            applyBankTransferStatus(payment, paymentData);
        }

        return paymentRepository.save(payment);
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);
        syncOrderStatus(payment);
        paymentRepository.save(payment);
        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    private void syncOrderStatus(Payment payment) {
        if (PaymentStatus.SUCCESS.getValue().equals(payment.getStatus())) {
            payment.getOrder().setStatus(OrderStatus.SUCCESS.getValue());
        } else if (PaymentStatus.REJECTED.getValue().equals(payment.getStatus())) {
            payment.getOrder().setStatus(OrderStatus.FAILED.getValue());
        }
    }

    private void applyVoucherStatus(Payment payment, Map<String, String> paymentData) {
        String voucherCode = paymentData == null ? null : paymentData.get(VOUCHER_CODE_KEY);
        if (isValidVoucherCode(voucherCode)) {
            payment.setStatus(PaymentStatus.SUCCESS.getValue());
        } else {
            payment.setStatus(PaymentStatus.REJECTED.getValue());
        }
    }

    private boolean isValidVoucherCode(String voucherCode) {
        if (voucherCode == null || voucherCode.length() != 16 || !voucherCode.startsWith("ESHOP")) {
            return false;
        }

        int digitCount = 0;
        for (char ch : voucherCode.toCharArray()) {
            if (Character.isDigit(ch)) {
                digitCount += 1;
            }
        }
        return digitCount == 8;
    }

    private void applyBankTransferStatus(Payment payment, Map<String, String> paymentData) {
        String bankName = paymentData == null ? null : paymentData.get(BANK_NAME_KEY);
        String referenceCode = paymentData == null ? null : paymentData.get(REFERENCE_CODE_KEY);

        if (isBlank(bankName) || isBlank(referenceCode)) {
            payment.setStatus(PaymentStatus.REJECTED.getValue());
        } else {
            payment.setStatus(PaymentStatus.SUCCESS.getValue());
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isEmpty();
    }
}
