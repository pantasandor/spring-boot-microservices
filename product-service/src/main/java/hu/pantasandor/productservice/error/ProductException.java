package hu.pantasandor.productservice.error;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

public abstract class ProductException extends ErrorResponseException {

    public ProductException(HttpStatusCode httpStatusCode, String type, String detail) {
        super(httpStatusCode, createProblemDetail(httpStatusCode, type, detail), null);
    }

    private static ProblemDetail createProblemDetail(HttpStatusCode httpStatusCode, String type, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatusCode, detail);
        problemDetail.setType(URI.create(type));
        return problemDetail;
    }

}
