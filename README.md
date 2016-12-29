[![Travis](https://img.shields.io/travis/thetric/ilias-downloader.svg?style=flat-square&maxAge=2592000)](https://travis-ci.org/thetric/ilias-downloader)

# Ilias Downloader

The Ilias Downloader enables you to easily download the files from your Ilias account.
The primary target is to provide a simple yet beautiful JavaFX UI.


## Getting started

### Building from source
As the application has not hit an `1.0` release I won't publish executable JARs.
So if you are curious about the app you have to built it yourself.


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

This will download all necessary tools and libraries and create an executable jar at `jfx-ui/build/libs`.
