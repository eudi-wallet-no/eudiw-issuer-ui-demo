package no.idporten.eudiw.issuer.ui.demo.certificates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.idporten.eudiw.issuer.ui.demo.byob.ByobService;
import no.idporten.eudiw.issuer.ui.demo.byob.model.CredentialDefinition;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class CertificateService {
    private final ByobService byobService;

    public CertificateService(ByobService byobService) {
        this.byobService = byobService;
    }

    public List<CertificateDto> getCertificates() {
        Collection<CredentialDefinition> credentialDefinitions = byobService.getCustomCredentialDefinitions().values();

        ObjectMapper objectMapper = new ObjectMapper();

        return credentialDefinitions.stream().map(cd -> {
            try {
                return new CertificateDto(cd.getVct(), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cd));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public CertificateDto findCertificate(String cvt) {
        return getCertificates()
                .stream()
                .filter(c -> Objects.equals(c.cvt(), cvt))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown credential configuration id"));
    }

    public void storeCertificate(CertificateDto certificateDto) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CredentialDefinition cd = mapper.readValue(certificateDto.json(), CredentialDefinition.class);

        cd.setVct(certificateDto.cvt());
        byobService.addCustomCredentialDefinition(cd);
    }

    public void deleteCertificate(String cvt) {
        byobService.removeCustomCredentialDefinition(cvt);
    }
}
