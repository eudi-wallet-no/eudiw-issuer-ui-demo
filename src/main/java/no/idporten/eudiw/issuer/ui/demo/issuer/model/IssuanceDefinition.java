package no.idporten.eudiw.issuer.ui.demo.issuer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record IssuanceDefinition(
        @JsonProperty("credential_configuration_id")
        String credentialConfigurationId,
        IssuanceSubject subject,
        List<IssuanceClaim> claims
) {
    public IssuanceDefinition(String credentialConfigurationId, String id, List<IssuanceClaim> claims) {
        this(credentialConfigurationId, new IssuanceSubject(id),  claims);
    }

    public IssuanceDefinition(String credentialConfigurationId, List<IssuanceClaim> claims) {
        this(credentialConfigurationId, new IssuanceSubject("50917500484"),  claims);
    }
}
//{
//        "credential_configuration_id": "no.skatteetaten.nnid_mso_mdoc",
//        "subject": {
//        "identifier": "50917500484"
//        },
//        "claims": [
//        {
//        "name": "norwegian_national_id_number",
//        "value": "50917500484"
//        },
//        {
//        "name": "norwegian_national_id_number_type",
//        "value": "D-nummer"
//        }
//        ]
//        }