package ru.phi.modules.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.phi.modules.entity.*;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.oauth2.UserGetter;
import ru.phi.modules.repository.NewsRepository;
import ru.phi.modules.repository.ProfileRepository;
import ru.phi.modules.repository.QualityRepository;
import ru.phi.modules.repository.SettingsRepository;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@RequestMapping({"/rest/v1/", "/rest/"})
@Transactional
@RestController("meController.v1")
class MeController extends AbstractController {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private QualityRepository qualityRepository;

    @PreAuthorize("#oauth2.hasScope('read:profile')")
    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Profile me(@AuthenticationPrincipal UserGetter getter)
            throws AuthenticationException {
        final User user = getter.user();
        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
            profile.setEmail(user.getEmail());
            profile = profileRepository.save(profile);
        }
        return profile;
    }

    @PreAuthorize("#oauth2.hasScope('write:profile')")
    @RequestMapping(value = "/me", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public
    @ResponseBody
    void updateMe(@AuthenticationPrincipal UserGetter getter, @RequestBody Profile profile) throws AuthenticationException {
        final User user = getter.user();
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
            meProfile.setAccessibility(profile.getAccessibility());
            meProfile.setCity(profile.getCity());
            meProfile.setAddress(profile.getAddress());
            if (profile.getQuality() != null) {
                final Quality quality = qualityRepository.findOne(profile.getQuality().getId());
                meProfile.setQuality(quality);
            }
            profileRepository.save(meProfile);
        }
    }

    @PreAuthorize("#oauth2.hasScope('read:profile') and #oauth2.hasScope('read:settings')")
    @RequestMapping(value = "/me/settings", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Settings getSettings(@AuthenticationPrincipal UserGetter getter)
            throws AuthenticationException {
        final User user = getter.user();
        if (user.getProfile() != null) {
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

    @PreAuthorize("#oauth2.hasScope('read:profile')")
    @RequestMapping(value = "/me/news", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    List<News> getNews(@AuthenticationPrincipal UserGetter getter,
                       @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                       @RequestParam(name = "size", defaultValue = "10", required = false) Integer size)
            throws AuthenticationException {
        final User user = getter.user();
        if (user.getProfile() != null) {
            final Sort sort = new Sort(Sort.Direction.DESC, "createdAt");
            final PageRequest pageable = new PageRequest(page, size, sort);
            return newsRepository.findByProfile(user.getProfile(), pageable).getContent();
        }
        return Collections.emptyList();
    }

    @PreAuthorize("#oauth2.hasScope('read:profile')")
    @RequestMapping(value = "/me/news/count", method = RequestMethod.GET)
    public long meCount(@AuthenticationPrincipal UserGetter getter)
            throws AuthenticationException {
        final User user = getter.user();
        if (user.getProfile() != null) {
            return newsRepository.profileCount(user.getProfile());
        }
        return 0;
    }

    private Profile update(Profile profile) {
        return profileRepository.findOne(profile.getId());
    }

    @PreAuthorize("#oauth2.hasScope('write:profile') and #oauth2.hasScope('write:settings')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/me/settings", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void updateSettings(@AuthenticationPrincipal UserGetter getter, @RequestBody Settings settings)
            throws AuthenticationException {
        final User user = getter.user();
        if (user.getProfile() != null) {
            final Profile profile = update(user.getProfile());
            if (profile.getSettings() != null) {
                /**FIXME: Копирование параметровю Следует переписать */
                final Settings meSettings = profile.getSettings();
                meSettings.setFilter(settings.getFilter());
                meSettings.setStart(point(user, settings.getStart()));
                meSettings.setStop(point(user, settings.getStop()));
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
