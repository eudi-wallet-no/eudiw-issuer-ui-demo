package no.idporten.eudiw.bevisgenerator.integration.verifierservice.model;

public record VerificationStartResponse(String authorizationRequest, String authorizationRequestQrCode, String verifierTransactionId) {
}
