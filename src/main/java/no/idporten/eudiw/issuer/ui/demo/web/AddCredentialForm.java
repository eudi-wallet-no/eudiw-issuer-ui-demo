package no.idporten.eudiw.issuer.ui.demo.web;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record AddCredentialForm(

        @NotBlank(message = "VCT er påkrevd")
        @Pattern(
                regexp = "^[a-z0-9_:]{3,155}",
                message = "VCT kan kun bestå av små bokstaver, tall og kolon - 3-155 tegn"
        )
        String vct,
        String json
) {
    public AddCredentialForm() {
        this("", "");
    }

    public AddCredentialForm(String json) {
        this("", json);
    }
}
