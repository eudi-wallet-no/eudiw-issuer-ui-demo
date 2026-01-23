package no.idporten.eudiw.issuer.ui.demo.web.models.advancedForm;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import no.idporten.eudiw.issuer.ui.demo.credentials.unique.UniqueVct;

import java.util.ArrayList;
import java.util.List;

public record SimpleCredentialForm(
        @NotBlank(message = "VCT er påkrevd")
        @Pattern(
                regexp = "^[a-z0-9_:]{3,155}",
                message = "VCT kan kun bestå av små bokstaver, tall, kolon og understrek.\n Lengde: 3-155 tegn"
        )
        @UniqueVct()
        String vct,

        String name,

        @Valid
        List<ClaimForm> claims
) {
    public SimpleCredentialForm() {
        this("", "", new ArrayList<>());
    }
}
