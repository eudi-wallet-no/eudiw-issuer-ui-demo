package no.idporten.eudiw.bevisgenerator.integration.verifierservice.model;

import net.minidev.json.JSONObject;

import java.util.List;

public record CredentialDefinitionDisplayData(
        String title,
        String issuer,
        String format,
        JSONObject meta,
        List<SelectableClaim> claims
) { }
