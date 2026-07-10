package no.idporten.eudiw.bevisgenerator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientException;

public class VerifierServiceException extends RuntimeException {

    public VerifierServiceException(String message) {
        super(message);
    }

     public VerifierServiceException(String message, RestClientException cause) {
        super(message, cause);
    }

    public HttpStatusCode getHttpStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
