package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.console.ConsoleService
import com.github.thetric.iliasdownloader.cli.console.SystemEnvironmentAwareConsoleService
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.webparser.WebParserIliasServiceProvider
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceServiceImpl
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
            new Cli(resourceBundle).parseAndHandleOpts args
        } catch (InvalidUsageException ue) {
            log.catching(TRACE, ue)
            System.exit(1)
        } catch (Throwable t) {
            log.catching(t)
            System.exit(2)
        }
    }

    def parseAndHandleOpts(String[] args) {
        def cliBuilder = createCliBuilder()
        final opts = cliBuilder.parse(args)
        if (!opts) {
            throw new InvalidUsageException()
        } else {
            handleOptsReal(cliBuilder.parse(args))
        }
    }

    def createCliBuilder() {
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
        return cliBuilder
    }

    def handleOptsReal(OptionAccessor options) {
        def cliOptions = new CliOptions(
            syncDir: Paths.get(options.d as String),
            showCourseSelection: options.c as boolean
        )
        Path settingsPath = cliOptions.syncDir.resolve('.ilias-downloader.yml')
        UserPreferenceService preferenceService = new UserPreferenceServiceImpl(settingsPath)
        final ConsoleService consoleService = new SystemEnvironmentAwareConsoleService()
        Function<String, IliasService> webIliasServiceProvider = { new WebParserIliasServiceProvider(it).newInstance() }

        new IliasCliController(cliOptions, webIliasServiceProvider, resourceBundle, preferenceService, consoleService).
            start()
    }
}
