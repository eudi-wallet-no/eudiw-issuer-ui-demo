package no.idporten.eudiw.bevisgenerator.integration.verifierservice;

import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationTransactionData;

public interface VerifierService {
    VerificationTransactionData startVerification(String dcqlQuery);
}
