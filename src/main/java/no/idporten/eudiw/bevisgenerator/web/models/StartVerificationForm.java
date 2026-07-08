package no.idporten.eudiw.bevisgenerator.web.models;

import jakarta.validation.constraints.NotBlank;

public record StartVerificationForm(
        @NotBlank(message = "Credential configuration må velgast")
        String credentialConfigurationId,
        @NotBlank(message = "DCQL query må ha verdi")
        String dcql
) {
    public StartVerificationForm() {
        this("", "");
    }
}
