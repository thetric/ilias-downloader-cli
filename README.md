[![Travis](https://img.shields.io/travis/thetric/ilias-downloader.svg?style=flat-square&maxAge=2592000)](https://travis-ci.org/thetric/ilias-downloader)

# Ilias Downloader

The Ilias Downloader enables you to easily download the files from your Ilias account.
The first release will provide a simple CLI, the second a JavaFX UI.

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
