package no.idporten.eudiw.issuer.ui.demo.byob;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.idporten.eudiw.issuer.ui.demo.byob.model.CredentialDefinition;
import no.idporten.eudiw.issuer.ui.demo.byob.model.CredentialDefinitionCollection;
import no.idporten.eudiw.issuer.ui.demo.certificates.CertificateDto;
import no.idporten.eudiw.issuer.ui.demo.exception.ByobServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.*;

@Service
public class ByobService {
    private final ByobServiceProperties byobServiceProperties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

   private final Map<String, CredentialDefinition> customCredentialDefinitions = new HashMap<>();

   @Autowired
    public ByobService(
            ByobServiceProperties byobServiceProperties,
            ObjectMapper objectMapper,
            @Qualifier("byobServiceRestClient")
            RestClient restClient
   ) {
        this.byobServiceProperties = byobServiceProperties;
        this.objectMapper = objectMapper;
        this.restClient = restClient;
   }

    public List<CredentialDefinition> getCustomCredentialDefinitions() {
        List<CredentialDefinition> result = getCertificateDefinitions().credentialConfigurations;
        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }

    public CredentialDefinition addCustomCredentialDefinition(CredentialDefinition credentialDefinition) {
        return storeCertificate(credentialDefinition);
    }

    public CredentialDefinition getCustomCredentialDefinition(String key) {
        return customCredentialDefinitions.get(key);
    }

    public void removeCustomCredentialDefinition(String key) {
        customCredentialDefinitions.remove(key);
    }

    public List<CertificateDto> getCredentialConfigurations() {
        Collection<CredentialDefinition> credentialDefinitions = getCustomCredentialDefinitions();

        return credentialDefinitions.stream().map(cd -> {
            try {
                return new CertificateDto(cd.getVct(), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cd));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    private CredentialDefinitionCollection getCertificateDefinitions() {
       String getEndpoint = byobServiceProperties.getEndpoint();

       CredentialDefinitionCollection result;

       try {
          result = restClient
                  .get()
                  .uri(getEndpoint)
                  .accept(MediaType.APPLICATION_JSON)
                  .retrieve()
                  .body(CredentialDefinitionCollection.class);
        } catch (RestClientResponseException e) {
            throw new ByobServiceException("Configuration error against byob-service? path=" + getEndpoint, e);
        }

       return result;
    }

    private CredentialDefinition storeCertificate(CredentialDefinition cd) {
        String persistEndpoint = byobServiceProperties.createEndpoint();

        try {
            return restClient
                    .post()
                    .uri(persistEndpoint)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cd)
                    .retrieve()
                    .body(CredentialDefinition.class);
        } catch (RestClientResponseException e) {
            throw new ByobServiceException("Configuration error against byob-service? path=" + persistEndpoint, e);
        }
    }
}
