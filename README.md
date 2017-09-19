[![Travis](https://img.shields.io/travis/thetric/ilias-downloader-cli/master.svg?style=flat-square)](https://travis-ci.org/thetric/ilias-downloader-cli)
[![GitHub release](https://img.shields.io/github/release/thetric/ilias-downloader-cli.svg?style=flat-square)](https://github.com/thetric/ilias-downloader-cli/releases)

# Ilias Downloader (CLI)

The Ilias Downloader enables you to easily download the files from your Ilias account through a simple CLI.

## Usage

**IMPORTANT:** In order to use the application you should have the latest JRE 8 installed.
Older versions are *not* guaranteed to work.

```
# required
 -d,--dir <Directory>   Directory for the sync (required)

# optional
 -c,--select-courses    show dialog to select courses to sync
 -s,--max-size <arg>    size limit in MiB per file download (exclusive)
```

## Debugging

For more debug output add `-Dlog4j.configurationFile=log4j2-debug.xml` to the command line.
