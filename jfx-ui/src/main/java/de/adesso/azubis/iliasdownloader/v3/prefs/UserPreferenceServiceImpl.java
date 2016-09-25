package de.adesso.azubis.iliasdownloader.v3.prefs;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

/**
 * Created by Dominik Broj on 31.01.2016.
 *
 * @author Dominik Broj
 * @since 31.01.2016
 */
public final class UserPreferenceServiceImpl implements UserPreferenceService {
    private final XmlMapper xmlMapper;
    private final String settingsFilename;

    public UserPreferenceServiceImpl(@NonNull String userSettingsFilename) {
        this.settingsFilename = userSettingsFilename;

        xmlMapper = new XmlMapper();
        xmlMapper.enable(INDENT_OUTPUT);
        xmlMapper.disable(FAIL_ON_EMPTY_BEANS);
        xmlMapper.disable(FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public UserPreferences loadUserPreferences() throws IOException {
        final Path iliasDownloaderSettingsPath = Paths.get(settingsFilename);
        try (final InputStream inputStream = Files.newInputStream(iliasDownloaderSettingsPath)) {
            return xmlMapper.readValue(inputStream, UserPreferences.class);
        }
    }

    @Override
    public void saveUserPreferences(@NonNull UserPreferences userPreferences) throws IOException {
        final Path iliasDownloaderSettingsPath = Paths.get(settingsFilename);
        try (OutputStream outputStream = Files.newOutputStream(iliasDownloaderSettingsPath)) {
            xmlMapper.writeValue(outputStream, userPreferences);
        }
    }
}
