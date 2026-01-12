package no.idporten.eudiw.issuer.ui.demo.byob;

import no.idporten.eudiw.issuer.ui.demo.byob.model.*;

import java.util.List;
import java.util.Map;

public class CredentialDefinitionFactory {

    public static CredentialDefinition empty() {
        return new CredentialDefinition(
            "eidas2.[your-certificate-name].123",
            new ExampleCredentialData(Map.of("family-name", "Normann", "given-name", "Kari")),
            new CredentialMetadata(
                List.of(
                    new Display(
                        "",
                        "no",
                        null,
                        null
                    )
                ),
                List.of(
                    new Claim(
                        "family_name",
                        true,
                        new Display(
                            "Etternavn",
                            "no",
                            null,
                            null
                        )
                    ),
                        new Claim(
                            "given_name",
                            true,
                            new Display(
                                "Fornavn",
                                "no",
                                null,
                                null
                            )
                        )
                )
            )
        );
    }
}

