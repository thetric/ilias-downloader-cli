package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.webparser.WebParserIliasServiceProvider
import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j2

import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Function

import static org.apache.logging.log4j.Level.DEBUG

/**
 * @author broj
 * @since 14.01.2017
 */
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
            log.catching(DEBUG, ue)
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
        return cliBuilder
    }

    def handleOptsReal(OptionAccessor options) {
        Path syncDir = Paths.get('C:\\Users\\broj\\Google Drive\\Ilias\\5. Semester')
        final ConsoleService consoleService = new SystemEnvironmentAwareConsoleService()
        Function<String, IliasService> webIliasServiceProvider = { new WebParserIliasServiceProvider(it).newInstance() }

        new IliasCliController(syncDir, consoleService, webIliasServiceProvider, resourceBundle).start()
    }
}
