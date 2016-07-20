package ru.phi.modules.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.phi.modules.api.ExceptionService;
import ru.phi.modules.entity.Error;

@SuppressWarnings("unused")
@Controller
@ControllerAdvice
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
final class RestExceptionController extends ResponseEntityExceptionHandler {
    @Autowired
    private ExceptionService service;

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public
    @ResponseBody
    ResponseEntity<Object> handleUnauthorizedException(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({AccessScopeException.class})
    public
    @ResponseBody
    ResponseEntity<Object> handleForbiddenException(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler({ValidationException.class})
    public
    @ResponseBody
    ResponseEntity<Object> handleValidationException(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ObjectNotFoundException.class})
    public
    @ResponseBody
    ResponseEntity<Object> handleNotFoundException(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        final Error error = service.handleException(ex, headers, status, request);
        return new ResponseEntity<>(error, headers, status);
    }
}
