package no.idporten.eudiw.issuer.ui.demo.credentials;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.idporten.eudiw.issuer.ui.demo.byob.ByobService;
import no.idporten.eudiw.issuer.ui.demo.byob.model.CredentialDefinition;
import no.idporten.eudiw.issuer.ui.demo.byob.model.Display;
import no.idporten.eudiw.issuer.ui.demo.byob.model.ExampleCredentialData;
import no.idporten.eudiw.issuer.ui.demo.issuer.config.CredentialConfiguration;
import no.idporten.eudiw.issuer.ui.demo.issuer.model.IssuanceClaim;
import no.idporten.eudiw.issuer.ui.demo.issuer.model.IssuanceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CredentialIsserService {
    private final ByobService byobService;
    private final ObjectMapper objectMapper;
    private final Logger log  = LoggerFactory.getLogger(CredentialIsserService.class);

    @Autowired
    public CredentialIsserService(ByobService byobService, ObjectMapper objectMapper) {
        this.byobService = byobService;
        this.objectMapper = objectMapper;
    }

    public List<CredentialConfiguration> getCredentialConfigurations() throws JsonProcessingException {
       return getCustomCredentials();
    }

    public CredentialConfiguration getCredentialConfigurationById(String id) throws JsonProcessingException {
        CredentialDefinition cd = byobService.getByCredentialConfigurationId(id);
        return convertCredentialDefinitionCredentials(cd);
    }

    private List<CredentialConfiguration> getCustomCredentials() {
        return byobService.getCredentialDefinitions()
                .stream()
                .map(cd -> {
                    try {
                        return convertCredentialDefinitionCredentials(cd);
                    } catch (JsonProcessingException e) {
                        log.warn(e.getMessage());
                        return null;
                    }
                }).toList();
    }

    private CredentialConfiguration convertCredentialDefinitionCredentials(CredentialDefinition cd) throws JsonProcessingException {
        IssuanceDefinition data = convertFromCredentialDefinitionToIssuanceDefinition(cd);
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);

        String description = getDescription(cd);

        return new CredentialConfiguration(
                cd.getCredentialConfigurationId(),
                cd.getVct(),
                "50917500484",
                description,
                json
        );
    }

    private static String getDescription(CredentialDefinition cd) {
        return cd
            .getCredentialMetadata()
            .display()
            .stream()
            .findFirst()
            .map(Display::name)
            .orElse("Navn ikke funnet");
    }

    private IssuanceDefinition convertFromCredentialDefinitionToIssuanceDefinition(CredentialDefinition cd) {
        return new IssuanceDefinition(cd.getCredentialConfigurationId(), convertFromExampleDataToIssuanceClaim(cd));
    }

    private List<IssuanceClaim> convertFromExampleDataToIssuanceClaim(CredentialDefinition cd) {
        ArrayList<IssuanceClaim> issuanceClaims = new ArrayList<>();

        for (ExampleCredentialData exampleData : cd.getExampleCredentialData()) {
            issuanceClaims.add(new IssuanceClaim(exampleData.name(), exampleData.value()));
        }

        return issuanceClaims;
    }
}
