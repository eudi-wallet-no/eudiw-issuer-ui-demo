package no.idporten.eudiw.issuer.ui.demo.web.models.advancedForm;

import jakarta.validation.constraints.NotBlank;

public record ClaimForm(
        @NotBlank(message = "Path kan ikke være tom")
        String path,
        @NotBlank(message = "Name kan ikke vær tom")
        String name,

        String type,
        @NotBlank(message = "Eksempelverdi kan ikke være tom")
        String exampleValue) {
    public ClaimForm() {
        this("", "", "String", "");
    }
}
