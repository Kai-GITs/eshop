package id.ac.ui.cs.advprog.eshop.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentStatusTest {

    @Test
    void contains_shouldReturnTrueForValidStatuses() {
        assertTrue(PaymentStatus.contains("SUCCESS"));
        assertTrue(PaymentStatus.contains("REJECTED"));
    }

    @Test
    void contains_shouldReturnFalseForInvalidStatus() {
        assertFalse(PaymentStatus.contains("UNKNOWN"));
    }

    @Test
    void getValue_shouldReturnExpectedString() {
        assertEquals("SUCCESS", PaymentStatus.SUCCESS.getValue());
        assertEquals("REJECTED", PaymentStatus.REJECTED.getValue());
    }
}
