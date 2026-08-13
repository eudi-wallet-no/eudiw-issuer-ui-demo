package no.idporten.eudiw.bevisgenerator.integration.verifierservice;

import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationResult;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationStatus;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationTransactionData;

import java.net.URI;

public interface VerifierService {
    VerificationTransactionData startVerification(String dcqlQuery, URI redirectUri);
    VerificationResult retrieveVerificationResult(String transactionId);
    VerificationStatus retrieveVerificationStatus(String transactionId);
}
