package ru.phi.modules.exceptions;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;
import ru.phi.modules.Utilities;
import ru.phi.modules.entity.Error;
import ru.phi.modules.repository.ErrorRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings("unused")
@Slf4j
@Service("exceptionService.v1")
final class ExceptionServiceImpl implements ExceptionService {

    private static final int maxLength;

    static {
        maxLength = Utilities.entityColumnLength(Error.class, "trace");
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ErrorRepository repository;

    @Override
    public Error handleException(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final Error error = new Error();
        error.setCode("SERVER_EXCEPTION");
        error.setStatus(status.name());
        error.setDescription(ex.getMessage());
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        error.setTrace(writer.toString().substring(0, maxLength));
        repository.save(error);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        log.error("", ex);
        return error;
    }

    @Override
    public Error handleException(Exception ex, HttpHeaders headers, HttpStatus status) {
        return handleException(ex, headers, status, null);
    }

    @Override
    public Error handleException(Exception ex, HttpStatus status) {
        return handleException(ex, new HttpHeaders(), status);
    }

    @Override
    public void write(HttpServletResponse response, Exception ex, HttpStatus status) throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        final Error error = handleException(ex, headers, status);
        response.setStatus(status.value());
        response.setContentType(headers.getContentType().getType());
        objectMapper.writer().writeValue(response.getWriter(), error);
    }
}
