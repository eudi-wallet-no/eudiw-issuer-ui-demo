package no.idporten.eudiw.issuer.ui.demo.byob.model;


public class CredentialDefinition {
    private String vct;
    private final String format = "sd-jwt";
    private ExampleCredentialData exampleCredentialData;
    private CredentialMetadata credentialMetadata;

    public CredentialDefinition() {}

    public CredentialDefinition(String vct, ExampleCredentialData exampleCredentialData, CredentialMetadata credentialMetadata) {
        this.vct = vct;
        this.exampleCredentialData = exampleCredentialData;
        this.credentialMetadata = credentialMetadata;
    }

    public String getVct() {
        return vct;
    }

    public void setVct(String vct) {
        this.vct = vct;
    }

    public String getFormat() {
        return format;
    }

    public ExampleCredentialData getExampleCredentialData() {
        return exampleCredentialData;
    }

    public void setExampleCredentialData(ExampleCredentialData exampleCredentialData) {
        this.exampleCredentialData = exampleCredentialData;
    }

    public CredentialMetadata getCredentialMetadata() {
        return credentialMetadata;
    }

    public void setCredentialMetadata(CredentialMetadata credentialMetadata) {
        this.credentialMetadata = credentialMetadata;
    }
}