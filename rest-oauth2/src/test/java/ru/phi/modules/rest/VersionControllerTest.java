package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
import ru.phi.modules.entity.Version;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class VersionControllerTest extends AbstractRestTest {
    @Test
    public void current() throws Exception {
        // @formatter:off
        final Version v = new Version();
        v.setMajor(2L);
        v.setMinor(0L);
        v.setBuild(3L);
        v.setRc(Boolean.TRUE);
        v.setSupport("support@me.com");
        versionRepository.save(v);
        get("/rest/v1/version")
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.major", is(2)));
        versionRepository.delete(v);
        // @formatter:on
    }

    @Test
    public void swagger() throws Exception {
        getContent("/rest/v1/version/swagger")
                .andExpect(status().isOk());
    }

}

