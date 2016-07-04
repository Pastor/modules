package ru.phi.modules.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.phi.modules.entity.Error;
import ru.phi.modules.repository.ErrorRepository;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings("unused")
@Controller
@ControllerAdvice
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
final class RestExceptionController extends ResponseEntityExceptionHandler {
    @Autowired
    private ErrorRepository repository;

    @PostConstruct
    private void construct() {
        repository.findAll();
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public @ResponseBody ResponseEntity<Object> handleUnauthorizedException(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        final Error error = new Error();
        error.setCode("SERVER_EXCEPTION");
        error.setDescription(ex.getMessage());
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        error.setTrace(writer.toString());
        repository.save(error);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new ResponseEntity<>(error, headers, status);
    }
}
