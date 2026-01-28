package no.idporten.eudiw.issuer.ui.demo.integration.byobservice.model;

import java.util.List;

public record CredentialMetadata(
        List<Display> display,
        List<Claim> claims
) {}
