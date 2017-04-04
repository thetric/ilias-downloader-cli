package com.github.thetric.iliasdownloader.cli

import java.nio.file.Paths

class CliService {
    private final CliBuilder cliBuilder
    private final ResourceBundle resourceBundle

    CliService(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle
        this.cliBuilder = createCliBuilder()
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

    private OptionAccessor parseArgs(String[] args) {
        CliBuilder cliBuilder = createCliBuilder()
        final OptionAccessor opts = cliBuilder.parse(args)
        if (!opts) {
            throw new InvalidUsageException()
        }
        return opts
    }

    CliOptions parseOpts(final String[] args) {
        final options = parseArgs(args)
        Long size = options.s == false ? null : options.s as Long
        return new CliOptions(
            syncDir: Paths.get(options.d as String),
            showCourseSelection: options.c as boolean,
            fileSizeLimitInMiB: size,
        )
    }
}
