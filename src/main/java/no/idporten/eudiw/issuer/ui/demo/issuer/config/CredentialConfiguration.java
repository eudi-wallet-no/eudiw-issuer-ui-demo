package no.idporten.eudiw.issuer.ui.demo.issuer.config;

import org.springframework.boot.context.properties.bind.DefaultValue;

public record CredentialConfiguration(String credentialConfigurationId, String scope, String personIdentifier, String description, String jsonRequest, @DefaultValue("true") boolean readonly) { }
