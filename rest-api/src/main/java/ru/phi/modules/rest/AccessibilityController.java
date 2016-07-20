package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.Accessibility;
import ru.phi.modules.entity.AccessibilityProcess;
import ru.phi.modules.entity.AccessibilityType;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.AccessibilityProcessRepository;

import javax.annotation.PostConstruct;
import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController("accessibilityController.v1")
class AccessibilityController {

    @Autowired
    private AccessibilityProcessRepository acp;

    @RequestMapping(value = "/accessibility/processes", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    List<AccessibilityProcess> list(@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                    @RequestParam(name = "size", defaultValue = "10", required = false) Integer size)
            throws AuthenticationException {
        final Sort sort = new Sort(Sort.Direction.ASC, "createdAt");
        final PageRequest pageable = new PageRequest(page, size, sort);
        return acp.findAll(pageable).getContent();
    }

    @PostConstruct
    private void construct() {
        for (Accessibility accessibility : Accessibility.values()) {
            for (AccessibilityType type : AccessibilityType.values()) {
                final AccessibilityProcess entity = new AccessibilityProcess();
                entity.setAccessibility(accessibility);
                entity.setType(type);
                acp.save(entity);
            }
        }
    }
}
