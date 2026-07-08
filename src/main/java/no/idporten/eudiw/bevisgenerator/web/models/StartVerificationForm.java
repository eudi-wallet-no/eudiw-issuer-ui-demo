package no.idporten.eudiw.bevisgenerator.web.models;

public record StartVerificationForm(String dcql)  {
    public StartVerificationForm() {
        this("");
    }
}
