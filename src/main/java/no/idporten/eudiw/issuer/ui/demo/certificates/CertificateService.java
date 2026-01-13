package no.idporten.eudiw.issuer.ui.demo.certificates;

import no.idporten.eudiw.issuer.ui.demo.byob.ByobService;
import no.idporten.eudiw.issuer.ui.demo.issuer.config.CredentialConfiguration;
import no.idporten.eudiw.issuer.ui.demo.issuer.config.IssuerServerProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<CredentialConfiguration> getAllCredentialsConfigurations() {
        List<CredentialConfiguration> result = new ArrayList<>();
        result.addAll(this.credentialConfigurations);
        //result.addAll(this.byobService.getCredentialConfigurations());
        return result;
    }

    public CredentialConfiguration findCredentialConfiguration(String credentialConfigurationId) {
        return getAllCredentialsConfigurations()
                .stream()
                .filter(c -> Objects.equals(c.credentialConfigurationId(), credentialConfigurationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown credential configuration id"));
    }
}
