package ru.phi.modules.rest;

import org.junit.Test;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.Version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class VersionControllerTest extends AbstractRestTest {

    @Test
    public void current() throws Exception {
        final Version version = versionRepository.last();
        final Version current = environment.current();
        assertEquals(version, current);
    }

    @Test
    public void swagger() throws Exception {
        assertNotNull(environment.swagger());
    }
}