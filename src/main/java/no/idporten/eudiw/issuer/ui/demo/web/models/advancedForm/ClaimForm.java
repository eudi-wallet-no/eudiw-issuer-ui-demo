package no.idporten.eudiw.issuer.ui.demo.web.models.advancedForm;

import jakarta.validation.constraints.NotBlank;

public record ClaimForm(
        @NotBlank(message = "path kan ikke være tom")
        String path,
        @NotBlank(message = "name kan ikke vær tom")
        String name,
        @NotBlank(message = "type kan ikke være tom")
        String type,
        @NotBlank(message = "Du må oppgi en eksempelverdi")
        String exampleValue) {
    public ClaimForm() {
        this("", "", "String", "");
    }
}
