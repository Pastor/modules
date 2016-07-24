package ru.phi.modules.rest;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.Version;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.VersionRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController("versionController.v1")
class VersionController {

    @Autowired
    private VersionRepository versionRepository;

    @RequestMapping(value = "/version", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    Version current() {
        return versionRepository.last();
    }

    @RequestMapping(
            value = "/version/swagger",
            method = RequestMethod.GET,
            produces = "application/yml;charset=UTF-8",
            headers = {
            })
    @ResponseStatus(HttpStatus.OK)
    public String swagger() throws IOException {
        try (final InputStream stream = VersionController.class.getResourceAsStream("/api.v1.0.3.yaml")) {
            try (Reader reader = new InputStreamReader(stream)) {
                return CharStreams.toString(reader);
            }
        }
    }
}
