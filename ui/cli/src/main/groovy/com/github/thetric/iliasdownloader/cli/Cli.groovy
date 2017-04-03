package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.console.ConsoleService
import com.github.thetric.iliasdownloader.cli.console.SystemEnvironmentAwareConsoleService
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.webparser.WebParserIliasServiceProvider
import com.github.thetric.iliasdownloader.ui.common.prefs.JsonUserPreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceService
import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j2

import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Function

import static org.apache.logging.log4j.Level.TRACE

@Log4j2
@TupleConstructor
final class Cli {
    // NOTE DO NOT launch in IntelliJ with 'Delegate IDE build/run actions to gradle' as no console is then available!
    // instead you should define system environment variables (see IliasCliController for the var names)

    ResourceBundle resourceBundle

    static void main(String[] args) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle('ilias-cli')
            new Cli(resourceBundle).parseAndHandleOpts(args)
        } catch (InvalidUsageException ue) {
            log.catching(TRACE, ue)
        }
    }

    private void parseAndHandleOpts(String[] args) {
        CliBuilder cliBuilder = createCliBuilder()
        final opts = cliBuilder.parse(args)
        if (opts) {
            handleOptsReal(cliBuilder.parse(args))
        } else {
            throw new InvalidUsageException()
        }
    }

    private CliBuilder createCliBuilder() {
        final cliBuilder = new CliBuilder()
        cliBuilder.header = 'Ilias Downloader (CLI)'
        cliBuilder.d(
            longOpt: 'dir',
            argName: resourceBundle.getString('args.directory'),
            args: 1,
            required: true,
            resourceBundle.getString('args.directory.description'))
        cliBuilder.c(
            longOpt: 'select-courses',
            required: false,
            args: 0,
            resourceBundle.getString('args.course.selection'))
        cliBuilder.s(
            longOpt: 'max-size',
            required: false,
            args: 1,
            resourceBundle.getString('args.sync.max-size')
        )
        return cliBuilder
    }

    private void handleOptsReal(OptionAccessor options) {
        long size = options.s == false ? 0 : options.s as Long
        CliOptions cliOptions = new CliOptions(
            syncDir: Paths.get(options.d as String),
            showCourseSelection: options.c as boolean,
            fileSizeLimitInMiB: size,
        )
        Path settingsPath = cliOptions.syncDir.resolve('.ilias-downloader.json')
        UserPreferenceService preferenceService = new JsonUserPreferenceService(settingsPath)
        final ConsoleService consoleService = new SystemEnvironmentAwareConsoleService()
        Function<String, IliasService> webIliasServiceProvider = { new WebParserIliasServiceProvider(it).newInstance() }

        new IliasCliController(cliOptions, webIliasServiceProvider, resourceBundle, preferenceService, consoleService).
            start()
    }
}
