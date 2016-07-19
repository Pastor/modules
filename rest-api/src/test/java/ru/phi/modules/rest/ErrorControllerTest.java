package ru.phi.modules.rest;

import org.junit.Test;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.Error;
import ru.phi.modules.entity.Token;

import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;

public final class ErrorControllerTest extends AbstractRestTest {

    @Test
    public void list() throws Exception {
        createError();
        createError();
        createError();
        final Token token = newToken("error");
        final List<Error> errors = environment.errors(token.getKey());
        assertEquals(errors.size(), 3L);
    }

    @Test
    public void count() throws Exception {
        createError();
        createError();
        createError();
        final Token token = newToken("error");
        final Long count = environment.errorsCount(token.getKey());
        assertEquals(count.longValue(), 3L);
    }

    private void createError() {
        final Error error = new Error();
        error.setUser(successUser);
        error.setCode("CODE");
        error.setStatus("STATUS");
        error.setDescription(UUID.randomUUID().toString());
        errorRepository.save(error);
    }
}