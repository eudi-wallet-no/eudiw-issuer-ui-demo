package no.idporten.eudiw.bevisgenerator.web.models;

public record RevokeBySubjectForm(
        String credentialConfigurationId,
        String subjectIdentifier
) {
    public RevokeBySubjectForm() {
        this("", "");
    }
}
