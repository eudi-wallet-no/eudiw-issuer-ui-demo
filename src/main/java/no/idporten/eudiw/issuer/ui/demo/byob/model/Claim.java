package no.idporten.eudiw.issuer.ui.demo.byob.model;


public record Claim(
        String path,
        Boolean mandatory,
        Display display
) {
    public Claim {
        if (mandatory == null) {
            mandatory = true;
        }
    }
}