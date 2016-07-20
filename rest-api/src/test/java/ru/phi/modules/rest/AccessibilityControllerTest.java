package ru.phi.modules.rest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.phi.modules.AbstractRestTest;
import ru.phi.modules.entity.AccessibilityProcess;

import java.util.List;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class AccessibilityControllerTest extends AbstractRestTest {

    @Test
    public void list() throws Exception {
        final List<AccessibilityProcess> processes = environment.accessibilityProcesses();
        assertEquals(processes.size(), accessibilityProcessRepository.count());
    }
}