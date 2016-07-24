package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
import ru.phi.modules.entity.Error;

import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ErrorControllerTest extends AbstractRestTest {

    @Test
    public void list() throws Exception {
        createError();
        createError();
        createError();
        final String accessToken = newToken("read:error");
        get("/rest/v1/errors", accessToken)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void count() throws Exception {
        createError();
        createError();
        createError();
        final String accessToken = newToken("read:error");
        final String content = getContent("/rest/v1/errors/count", accessToken)
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertEquals(Long.parseLong(content), 3L);
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