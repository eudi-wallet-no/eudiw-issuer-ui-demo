package no.idporten.eudiw.bevisgenerator.integration.verifierservice;

import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationStartResponse;

public interface VerifierService {
    VerificationStartResponse startVerification(String dcqlQuery);
}
