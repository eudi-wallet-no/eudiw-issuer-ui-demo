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
    public VerificationTransactionData startVerification(String dcqlQuery) {
        VerificationStartResponse response;

        try {
            response = restClient
                    .post()
                    .uri(verificationProperties.verificationStartEndpoint(), verificationProperties.clientApplicationId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dcqlQuery)
                    .retrieve()
                    .body(VerificationStartResponse.class);
        } catch (ResourceAccessException e) {
            throw new VerifierServiceIOException("IO error when calling Verifier service to start verification", e);
        } catch (RestClientException e) {
            throw new VerifierServiceException("Configuration error against Verifier-service? path=" + verificationProperties.verificationStartEndpoint(), e);
        }

        URI statusUri = URI.create(verificationProperties.baseUrl() + verificationProperties.verificationStatusEndpoint());
        URI responseUri = URI.create(verificationProperties.baseUrl() + verificationProperties.verificationResultEndpoint());

        return new VerificationTransactionData(response, statusUri, responseUri);
   }
}
