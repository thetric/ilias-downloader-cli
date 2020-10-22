# Ilias Downloader (CLI)
![Java CI with Gradle](https://github.com/thetric/ilias-downloader-cli/workflows/Java%20CI%20with%20Gradle/badge.svg)
[![GitHub release](https://img.shields.io/github/release/thetric/ilias-downloader-cli.svg?style=flat-square)](https://github.com/thetric/ilias-downloader-cli/releases)
[![codebeat badge](https://codebeat.co/badges/af88b559-243e-4223-a01b-e86f53701da1)](https://codebeat.co/projects/github-com-thetric-ilias-downloader-cli-master)

The Ilias Downloader enables you to easily download the files from your Ilias account through a simple CLI.

## Usage

**IMPORTANT:** In order to use the application you must have JDK version 11 or higher installed.

```sh
java -jar ilias-downloader-cli-$VERSION.jar <CLI options>
```

### CLI options
```
# required
 -d,--dir <Directory>   Directory for the sync (required)

# optional
 -c,--select-courses    show dialog to select courses to sync
 -s,--max-size <arg>    size limit in MiB per file download (exclusive)
```

## Debugging

For more debug output add `-Dlogback.configurationFile=logback-debug.xml` to the command line.
