package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.*;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.NewsRepository;
import ru.phi.modules.repository.ProfileRepository;
import ru.phi.modules.repository.QualityRepository;
import ru.phi.modules.repository.SettingsRepository;
import ru.phi.modules.security.AuthorizedScope;
import ru.phi.modules.security.AuthorizedToken;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@RestController
@Transactional
class MeController {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private QualityRepository qualityRepository;

    @AuthorizedScope(scopes = {"profile"})
    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Profile me(@AuthorizedToken Token token)
            throws AuthenticationException {
        final User user = token.getUser();
        return user.getProfile();
    }

    @AuthorizedScope(scopes = {"profile"})
    @RequestMapping(value = "/me", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    void putMe(@AuthorizedToken Token token, @RequestBody Profile profile) throws AuthenticationException {
        final User user = token.getUser();
        final Profile meProfile = user.getProfile();
        if (meProfile == null) {
            profile.clear();
            profile.setUser(user);
            profileRepository.save(profile);
        } else {
            meProfile.setMiddleName(profile.getMiddleName());
            meProfile.setFirstName(profile.getLastName());
            meProfile.setLastName(profile.getLastName());
            meProfile.setEmail(profile.getEmail());
            if (profile.getAccessibility() != null) {
                meProfile.setAccessibility(profile.getAccessibility());
            }
            meProfile.setCity(profile.getCity());
            meProfile.setAddress(profile.getAddress());
            if (profile.getQuality() != null) {
                final Quality quality = qualityRepository.findOne(profile.getQuality().getId());
                meProfile.setQuality(quality);
            }
            profileRepository.save(meProfile);
        }
    }

    @AuthorizedScope(scopes = {"profile", "settings"})
    @RequestMapping(value = "/me/settings", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Settings getSettings(@AuthorizedToken Token token)
            throws AuthenticationException {
        final User user = token.getUser();
        if (user != null && user.getProfile() != null) {
            final Profile profile = user.getProfile();
            Settings settings = profile.getSettings();
            if (settings == null) {
                settings = new Settings();
                settings.setProfile(update(profile));
                settingsRepository.save(settings);
            }
            return settings;
        }
        return null;
    }

    @AuthorizedScope(scopes = {"profile", "news"})
    @RequestMapping(value = "/me/news", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    List<News> getNews(@AuthorizedToken Token token,
                       @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                       @RequestParam(name = "size", defaultValue = "10", required = false) Integer size)
            throws AuthenticationException {
        final User user = token.getUser();
        if (user != null && user.getProfile() != null) {
            final Sort sort = new Sort(Sort.Direction.ASC, "createdAt");
            final PageRequest pageable = new PageRequest(page, size, sort);
            return newsRepository.findByProfile(user.getProfile(), pageable).getContent();
        }
        return Collections.emptyList();
    }

    protected final Profile update(Profile profile) {
        return profileRepository.findOne(profile.getId());
    }

    @AuthorizedScope(scopes = {"profile", "settings"})
    @RequestMapping(value = "/me/settings", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void putSettings(@AuthorizedToken Token token, @RequestBody Settings settings)
            throws AuthenticationException {
        final User user = token.getUser();
        if (user != null && user.getProfile() != null) {
            final Profile profile = update(user.getProfile());
            if (profile.getSettings() != null) {
                /**FIXME: Копирование параметровю Следует переписать */
                final Settings meSettings = profile.getSettings();
                meSettings.setFilter(settings.getFilter());
                meSettings.setStartLatitude(settings.getStartLatitude());
                meSettings.setStartLongitude(settings.getStartLongitude());
                meSettings.setEndLatitude(settings.getEndLatitude());
                meSettings.setEndLongitude(settings.getEndLongitude());
                if (settings.getQuality() != null) {
                    final Quality quality = qualityRepository.findOne(settings.getQuality().getId());
                    meSettings.setQuality(quality);
                }
                meSettings.setRouteType(settings.getRouteType());
                settingsRepository.save(meSettings);
            } else {
                settings.clear();
                settings.setProfile(profile);
                settingsRepository.save(settings);
            }
        }
    }
}
