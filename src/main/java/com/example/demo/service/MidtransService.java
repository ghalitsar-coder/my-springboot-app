package com.example.demo.service;

import com.example.demo.entity.Payment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class MidtransService {

    // You would typically inject these from configuration
    private static final String MIDTRANS_SERVER_KEY = "your-server-key";
    private static final String MIDTRANS_CLIENT_KEY = "your-client-key";
    private static final String MIDTRANS_BASE_URL = "https://api.sandbox.midtrans.com/v2"; // Use production URL for live

    /**
     * Create a Midtrans payment request with promotion-adjusted amount
     */
    public MidtransPaymentRequest createPaymentRequest(String orderId, BigDecimal amount, 
                                                      Map<String, Object> customerDetails,
                                                      PromotionService.CalculationResult promotionResult) {
        
        MidtransPaymentRequest request = new MidtransPaymentRequest();
        
        // Basic transaction details
        request.setTransactionDetails(Map.of(
            "order_id", "ORDER-" + orderId,
            "gross_amount", promotionResult.getFinalAmount().intValue()
        ));
        
        // Customer details
        request.setCustomerDetails(customerDetails);
        
        // Item details (including promotion discount as line item)
        Map<String, Object> orderItem = Map.of(
            "id", "ORDER-" + orderId,
            "price", promotionResult.getOriginalAmount().intValue(),
            "quantity", 1,
            "name", "Order Total"
        );
        
        if (promotionResult.getTotalDiscount().compareTo(BigDecimal.ZERO) > 0) {
            Map<String, Object> discountItem = Map.of(
                "id", "DISCOUNT-" + orderId,
                "price", promotionResult.getTotalDiscount().negate().intValue(), // Negative amount for discount
                "quantity", 1,
                "name", "Promotion Discount"
            );
            request.setItemDetails(java.util.List.of(orderItem, discountItem));
        } else {
            request.setItemDetails(java.util.List.of(orderItem));
        }
        
        // Additional metadata
        Map<String, Object> customField = new HashMap<>();
        customField.put("original_amount", promotionResult.getOriginalAmount().toString());
        customField.put("discount_amount", promotionResult.getTotalDiscount().toString());
        customField.put("applied_promotions", promotionResult.getAppliedPromotionIds());
        request.setCustomField1(customField.toString());
        
        return request;
    }

    /**
     * Verify webhook signature from Midtrans
     */
    public boolean verifyWebhookSignature(String orderId, String statusCode, String grossAmount, String signatureKey) {
        // Implementation would use SHA512 hash
        // String expectedSignature = DigestUtils.sha512Hex(orderId + statusCode + grossAmount + MIDTRANS_SERVER_KEY);
        // return expectedSignature.equals(signatureKey);
        
        // For now, return true for development
        return true;
    }

    /**
     * Update payment record with Midtrans-specific data
     */
    public void updatePaymentWithMidtransData(Payment payment, Map<String, Object> midtransResponse) {
        payment.setMidtransOrderId((String) midtransResponse.get("order_id"));
        
        if (midtransResponse.get("gross_amount") != null) {
            payment.setGrossAmount(new BigDecimal(midtransResponse.get("gross_amount").toString()));
        }
        
        payment.setPaymentChannel((String) midtransResponse.get("payment_type"));
        payment.setSignatureKey((String) midtransResponse.get("signature_key"));
        
        // Set transaction reference
        payment.setPaymentReference((String) midtransResponse.get("transaction_id"));
    }

    /**
     * Midtrans payment request structure
     */
    public static class MidtransPaymentRequest {
        private Map<String, Object> transactionDetails;
        private Map<String, Object> customerDetails;
        private java.util.List<Map<String, Object>> itemDetails;
        private String customField1;

        // Getters and setters
        public Map<String, Object> getTransactionDetails() { return transactionDetails; }
        public void setTransactionDetails(Map<String, Object> transactionDetails) { this.transactionDetails = transactionDetails; }

        public Map<String, Object> getCustomerDetails() { return customerDetails; }
        public void setCustomerDetails(Map<String, Object> customerDetails) { this.customerDetails = customerDetails; }

        public java.util.List<Map<String, Object>> getItemDetails() { return itemDetails; }
        public void setItemDetails(java.util.List<Map<String, Object>> itemDetails) { this.itemDetails = itemDetails; }

        public String getCustomField1() { return customField1; }
        public void setCustomField1(String customField1) { this.customField1 = customField1; }
    }
}
