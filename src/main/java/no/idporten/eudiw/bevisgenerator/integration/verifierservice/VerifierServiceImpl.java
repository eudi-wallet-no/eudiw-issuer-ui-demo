package no.idporten.eudiw.bevisgenerator.integration.verifierservice;

import no.idporten.eudiw.bevisgenerator.exception.VerifierServiceException;
import no.idporten.eudiw.bevisgenerator.exception.VerifierServiceIOException;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.config.VerificationProperties;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationStartResponse;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationTransactionData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;

@Service
public class VerifierServiceImpl implements VerifierService {
    private final RestClient restClient;
    private final VerificationProperties verificationProperties;

    public VerifierServiceImpl(@Qualifier("verificationServiceRestClient") RestClient restClient, VerificationProperties verificationProperties) {
        this.restClient = restClient;
        this.verificationProperties = verificationProperties;
    }

    @Override
    public VerificationTransactionData startVerification(String dcql) {
        try {
            VerificationStartResponse response = restClient
                    .post()
                    .uri(verificationProperties.verificationStartEndpoint(), verificationProperties.clientApplicationId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dcql)
                    .retrieve()
                    .body(VerificationStartResponse.class);


            URI statusUri = URI.create(verificationProperties.baseUrl() +
                    verificationProperties.verificationStatusEndpoint()
                            .replace("{client_application_id}", verificationProperties.clientApplicationId())
                            .replace("{verifier_transaction_id}", response.verifierTransactionId()));
            URI responseUri = URI.create(verificationProperties.baseUrl() +
                    verificationProperties.verificationResultEndpoint()
                            .replace("{client_application_id}", verificationProperties.clientApplicationId())
                            .replace("{verifier_transaction_id}", response.verifierTransactionId()));

            return new VerificationTransactionData(response, statusUri, responseUri);

        } catch (ResourceAccessException e) {
            throw new VerifierServiceIOException("IO error when calling Verifier service to start verification", e);
        } catch (RestClientException e) {
            throw new VerifierServiceException("Configuration error against Verifier-service? path=" + verificationProperties.verificationStartEndpoint(), e);
        }
   }
}
