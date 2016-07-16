package ru.phi.modules.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.phi.modules.entity.Version;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.VersionRepository;

import javax.annotation.PostConstruct;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController
@Transactional
@Slf4j
class VersionController {

    @Autowired
    private VersionRepository versionRepository;

    @RequestMapping(value = "/version", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Version current()
            throws AuthenticationException {
        return versionRepository.last();
    }

    @PostConstruct
    private void construct() {
        final Version v = new Version();
        v.setMajor(1L);
        v.setMinor(0L);
        v.setBuild(3L);
        v.setRc(Boolean.TRUE);
        v.setSupport("support@me.com");
        final Version current = versionRepository.last();
        if (current != null) {
            if (v.equals(current))
                return;
        }
        try {
            versionRepository.save(v);
        } catch (Exception ex) {
            log.error("Версия уже зарегистрирована?");
        }
    }
}
