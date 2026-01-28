package no.idporten.eudiw.issuer.ui.demo.integration.byobservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CredentialDefinitionCollection {
    @JsonProperty("credential_configurations")
    public List<CredentialDefinition> credentialConfigurations;
}
