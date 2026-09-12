package com.acme.commerce.payment.gateway;

public record PaymentDecision(
        boolean approved,
        String providerReference,
        String failureReason
) {
    public static PaymentDecision approved(String providerReference) {
        return new PaymentDecision(true, providerReference, null);
    }

    public static PaymentDecision declined(String reason) {
        return new PaymentDecision(false, null, reason);
    }
}
