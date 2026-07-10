package no.idporten.eudiw.bevisgenerator.integration.verifierservice;

import no.idporten.eudiw.bevisgenerator.exception.VerifierServiceException;
import no.idporten.eudiw.bevisgenerator.exception.VerifierServiceIOException;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.config.VerificationProperties;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationResult;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationStartResponse;
import no.idporten.eudiw.bevisgenerator.integration.verifierservice.model.VerificationTransactionData;
import org.jspecify.annotations.Nullable;
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
        VerificationStartResponse response = getVerificationStartResponse(dcql);

        if (response == null) {
            throw new VerifierServiceException("Verifier service returned null response when starting verification");
        }

        URI statusUri = URI.create(verificationProperties.baseUrl() +
                verificationProperties.verificationStatusEndpoint()
                        .replace("{client_application_id}", verificationProperties.clientApplicationId())
                        .replace("{verifier_transaction_id}", response.verifierTransactionId()));
        URI responseUri = URI.create(verificationProperties.baseUrl() +
                verificationProperties.verificationResultEndpoint()
                        .replace("{client_application_id}", verificationProperties.clientApplicationId())
                        .replace("{verifier_transaction_id}", response.verifierTransactionId()));

        return new VerificationTransactionData(response, statusUri, responseUri);

    }

    @Override
    public VerificationResult retrieveVerificationResult(String transactionId) {
        try {
            return restClient
                    .get()
                    .uri(verificationProperties.verificationResultEndpoint(), verificationProperties.clientApplicationId(), transactionId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(VerificationResult.class);
        } catch (ResourceAccessException e) {
            throw new VerifierServiceIOException("IO error when calling Verifier service to retrieve verification result", e);
        } catch (RestClientException e) {
            throw new VerifierServiceException("Configuration error against Verifier-service? path=" + verificationProperties.verificationResultEndpoint(), e);
        }
    }


    private @Nullable VerificationStartResponse getVerificationStartResponse(String dcql) {
        VerificationStartResponse response;
        try {
            response = restClient
                    .post()
                    .uri(verificationProperties.verificationStartEndpoint(), verificationProperties.clientApplicationId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dcql)
                    .retrieve()
                    .body(VerificationStartResponse.class);


        } catch (ResourceAccessException e) {
            throw new VerifierServiceIOException("IO error when calling Verifier service to start verification", e);
        } catch (RestClientException e) {
            throw new VerifierServiceException("Configuration error against Verifier-service? path=" + verificationProperties.verificationStartEndpoint(), e);
        }
        return response;
    }
}
