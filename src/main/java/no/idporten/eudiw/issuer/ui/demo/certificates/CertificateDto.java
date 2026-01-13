package no.idporten.eudiw.issuer.ui.demo.certificates;

public record CertificateDto(String cvt, String name, String json) {
     public CertificateDto(String cvt, String json) {
         this(cvt, cvt, json);
     }
}
