package no.idporten.eudiw.bevisgenerator.web.models;

public record StartVerificationForm(String credentialType, String dcql)  {
    public StartVerificationForm() {
        this("", "");
    }
}
