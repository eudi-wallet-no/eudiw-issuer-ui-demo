package no.idporten.eudiw.issuer.ui.demo.issuer.domain;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Grants(@JsonProperty("urn:ietf:params:oauth:grant-type:pre-authorized_code") PreAuthorizedCodeGrant preAuthorizedCodeGrant) {

}

