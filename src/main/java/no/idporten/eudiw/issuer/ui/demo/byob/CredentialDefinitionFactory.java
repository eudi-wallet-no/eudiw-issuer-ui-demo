package no.idporten.eudiw.issuer.ui.demo.byob;

import no.idporten.eudiw.issuer.ui.demo.byob.model.*;

import java.util.List;
import java.util.Map;

public class CredentialDefinitionFactory {

    public static CredentialDefinition empty() {
        return new CredentialDefinition(
                "",
                new ExampleCredentialData(Map.of()),
                new CredentialMetadata(
                        List.of(
                                new Display(
                                        "",
                                        "",
                                        null,
                                        null
                                )
                        ),
                        List.of(
                                new Claim(
                                        "",
                                        true,
                                        new Display(
                                                "",
                                                "",
                                                null,
                                                null
                                        )
                                )
                        )
                )
        );
    }
}

