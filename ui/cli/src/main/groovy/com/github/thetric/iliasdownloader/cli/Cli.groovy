package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.webparser.WebParserIliasServiceProvider
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
final class Cli {
    // NOTE DO NOT launch in IntelliJ with 'Delegate IDE build/run actions to gradle' as no console is then available!

    static void main(String[] args) {
        try {
            new Cli().parseAndHandleOpts args
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
        cliBuilder.d(longOpt: 'dir', argName: 'Directory', args: 1, required: true, 'Directory for the sync')
        return cliBuilder
    }

    def handleOptsReal(OptionAccessor options) {
        Path syncDir = Paths.get(options.d as String)
        final ConsoleService consoleService = new SystemEnvironmentAwareConsoleService()
        Function<String, IliasService> webIliasServiceProvider = { new WebParserIliasServiceProvider(it).newInstance() }

        new IliasCliController(syncDir, consoleService, webIliasServiceProvider).start()
    }
}
