package no.idporten.eudiw.bevisgenerator.integration.verifierservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VerificationResult(
        @JsonProperty("verifier_transaction_id") String verifierTransactionId,
        @JsonProperty("credentials") JsonNode credentials
) {
}
