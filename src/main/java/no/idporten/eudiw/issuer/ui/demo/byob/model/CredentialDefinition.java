package no.idporten.eudiw.issuer.ui.demo.byob.model;


public record CredentialDefinition(
        String vct,
        String format,
        ExampleCredentialData example_credential_data,
        CredentialMetadata credential_metadata
) {
    public CredentialDefinition(String vct,
                                ExampleCredentialData example_credential_data,
                                CredentialMetadata credential_metadata) {
        this(vct, "sd-jwt", example_credential_data, credential_metadata);
    }
}
