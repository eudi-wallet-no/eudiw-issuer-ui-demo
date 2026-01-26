package no.idporten.eudiw.issuer.ui.demo.web.models.advancedForm;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import no.idporten.eudiw.issuer.ui.demo.byob.model.CredentialDefinition;
import no.idporten.eudiw.issuer.ui.demo.byob.model.ExampleCredentialData;
import no.idporten.eudiw.issuer.ui.demo.credentials.unique.UniqueVct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record SimpleCredentialForm(
        @NotBlank(message = "VCT er påkrevd")
        @Pattern(
                regexp = "^[a-z0-9_:]{3,155}",
                message = "VCT kan kun bestå av små bokstaver, tall, kolon og understrek.\n Lengde: 3-155 tegn",
                groups = CreateForm.class
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

    public SimpleCredentialForm(CredentialDefinition cd) {
        String name = cd.getCredentialMetadata().display().getFirst().name();
        Map<String, String> exampleData =
            cd.getExampleCredentialData().stream()
                .collect(Collectors.toMap(
                    ExampleCredentialData::name,
                    ExampleCredentialData::value
                ));

        List<ClaimForm> claims = cd
            .getCredentialMetadata()
            .claims()
            .stream()
            .map(claim -> {
                String fieldName = claim.display().getFirst().name();
                String exampleValue = exampleData.get(claim.path());
                return new ClaimForm(claim.path(), fieldName, exampleValue);
            }).toList();

        this(cd.getVct(), name, claims);
    }
}
