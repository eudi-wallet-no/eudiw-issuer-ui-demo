package no.idporten.eudiw.issuer.ui.demo.certificates;


public record CertificateDto(String vct, String name, String json) {
    public CertificateDto {
        name = name == null ? cleanVct(vct) : name;
    }

    public CertificateDto(String vct, String json) {
        this(vct, null, json);
    }

    private static String cleanVct(String vct) {
        return vct.replaceFirst("^[^:]+:", "");
    }
}
