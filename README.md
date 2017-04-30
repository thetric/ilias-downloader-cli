[![Travis](https://img.shields.io/travis/thetric/ilias-downloader.svg?style=flat-square)](https://travis-ci.org/thetric/ilias-downloader)
[![GitHub release](https://img.shields.io/github/release/thetric/ilias-downloader.svg?style=flat-square)](https://github.com/thetric/ilias-downloader/releases)

# Ilias Downloader

The Ilias Downloader enables you to easily download the files from your Ilias account through a simple CLI.

## Usage

### CLI

```
# required
 -d,--dir <Directory>   Directory for the sync (required)

# optional
 -c,--select-courses    show dialog to select courses to sync
 -s,--max-size <arg>    size limit in MiB per file download (exclusive)
```

## Getting started

### Building from source

In order to build the application you need the latest Java Development Kit (JDK 8) installed.
It might be possible to build it with earlier versions but I do _not_ recommended it.
If you have not installed Java or an older version you can get it [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

To actually build the app execute the following command in a terminal app:

```sh
# on Linux/Mac
./gradlew build

# on Windows
gradlew build
```

This will download all necessary tools and libraries and create an executable jar at `ui/cli/build/libs/ilias-downloader-cli-[VERSION].jar`.


## Library usage

The Ilias connector may be used as a library.
You can get the artifacts from [JitPack.io](https://jitpack.io/#thetric/ilias-downloader) by selecting a release (click on the 'Get it' button) and a subproject (`ilias-downloader-connector-[impl]`).


The core classes are in the `connector/connector-api` module:

* `com.github.thetric.iliasdownloader.service.IliasServiceProvider`: factory interface for the `IliasService`, implemented by the implementations
* `com.github.thetric.iliasdownloader.service.IliasService`: provides access to the Ilias

### Implementations

#### `connector-domparser`
* service provider: `com.github.thetric.iliasdownloader.service.webparser.WebParserIliasServiceProvider`
