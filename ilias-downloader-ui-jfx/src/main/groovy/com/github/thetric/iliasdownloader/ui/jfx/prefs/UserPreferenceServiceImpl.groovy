package com.github.thetric.iliasdownloader.ui.jfx.prefs

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import groovy.transform.CompileStatic
import lombok.NonNull
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.nio.file.NoSuchFileException
import java.nio.file.Paths

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
/**
 * Created by Dominik Broj on 31.01.2016.
 *
 * @author Dominik Broj
 * @since 31.01.2016
 */
@CompileStatic
final class UserPreferenceServiceImpl implements UserPreferenceService {
    private static final Logger log = LogManager.logger
    private final XmlMapper xmlMapper
    private final String settingsFilename

    UserPreferenceServiceImpl(@NonNull String userSettingsFilename) {
        this.settingsFilename = userSettingsFilename

        xmlMapper = new XmlMapper()
        xmlMapper.enable INDENT_OUTPUT
        xmlMapper.disable FAIL_ON_EMPTY_BEANS
        xmlMapper.disable FAIL_ON_UNKNOWN_PROPERTIES
    }

    @Override
    Optional<UserPreferences> loadUserPreferences() throws IOException {
        def settingsPath = Paths.get settingsFilename
        try {
            return (settingsPath.withInputStream() {
                return Optional.of(xmlMapper.readValue(it, UserPreferences.class))
            }) as Optional<UserPreferences>
        } catch (NoSuchFileException ex) {
            log.warn("Konnte Datei unter ${settingsPath.toAbsolutePath()} nicht finden", ex)
            return Optional.empty()
        }
    }

    @Override
    void saveUserPreferences(@NonNull UserPreferences userPreferences) throws IOException {
        Paths.get settingsFilename withOutputStream {
            xmlMapper.writeValue(it, userPreferences)
        }
    }
}
