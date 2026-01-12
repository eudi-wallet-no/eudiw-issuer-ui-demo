package no.idporten.eudiw.issuer.ui.demo.certificates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import no.idporten.eudiw.issuer.ui.demo.byob.ByobService;
import no.idporten.eudiw.issuer.ui.demo.byob.model.CredentialDefinition;
import no.idporten.eudiw.issuer.ui.demo.issuer.config.CredentialConfiguration;
import no.idporten.eudiw.issuer.ui.demo.issuer.config.IssuerServerProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CertificateService {
    private final ByobService byobService;
    private final IssuerServerProperties issuerServerProperties;

    private final List<CredentialConfiguration> credentialConfigurations = new ArrayList<>();

    public CertificateService(ByobService byobService, IssuerServerProperties issuerServerProperties) {
        this.byobService = byobService;
        this.issuerServerProperties = issuerServerProperties;
        this.credentialConfigurations.addAll(issuerServerProperties.credentialConfigurations());
    }

    public ByobService getByobService() {
        return byobService;
    }

    public IssuerServerProperties getIssuerServerProperties() {
        return issuerServerProperties;
    }

    public List<CredentialConfiguration> getAllCredentialsConfigurations() {
        List<CredentialConfiguration> result = new ArrayList<>();
        result.addAll(this.credentialConfigurations);
        result.addAll(getAllCredentialsConfigurations());
        return result;
    }

    public List<CredentialConfiguration> getCredentialConfigurations() {
        Collection<CredentialDefinition> credentialDefinitions = this.byobService.getCustomCredentialDefinitions().values();

        ObjectMapper objectMapper = new ObjectMapper();

        return credentialDefinitions.stream().map(cd -> {
            try {
                return new CredentialConfiguration(cd.getVct(), "", "16903349844", cd.getVct(), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cd), false);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
}
