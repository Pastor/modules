package ru.phi.modules.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;
import ru.phi.modules.entity.Error;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ExceptionService {
    Error handleException(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request);

    Error handleException(Exception ex, HttpHeaders headers, HttpStatus status);

    Error handleException(Exception ex, HttpStatus status);

    void write(HttpServletResponse response, Exception ex, HttpStatus status) throws IOException;
}
