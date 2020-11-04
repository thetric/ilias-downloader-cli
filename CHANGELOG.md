# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

### [4.0.5](https://github.com/thetric/ilias-downloader-cli/compare/v4.0.4...v4.0.5) (2020-11-04)


### Bug Fixes

* **connector:** improve debug log for login ([0500ee2](https://github.com/thetric/ilias-downloader-cli/commit/0500ee2b41a95ea049173bc6d86a573db7a18e59))
* **logging:** include jansi to fix color output under windows ([e5518d8](https://github.com/thetric/ilias-downloader-cli/commit/e5518d8b5fa63d5ed6592f79743a4353e7a20d7a))

### [4.0.4](https://github.com/thetric/ilias-downloader-cli/compare/v4.0.3...v4.0.4) (2020-10-24)


### Bug Fixes

* **connector:** fix file download ([89e6d28](https://github.com/thetric/ilias-downloader-cli/commit/89e6d286ecf08d00c64b10b96c8b68d71bfdeb8a)), closes [#19](https://github.com/thetric/ilias-downloader-cli/issues/19)

### [4.0.3](https://github.com/thetric/ilias-downloader-cli/compare/v4.0.2...v4.0.3) (2020-10-22)


### Bug Fixes

* **connector/domparser:** fix login issue ([ca204d4](https://github.com/thetric/ilias-downloader-cli/commit/ca204d41929ee816854b0c92459121c21eea7c3a))

### [4.0.2](https://github.com/thetric/ilias-downloader-cli/compare/v4.0.1...v4.0.2) (2020-10-16)


### Bug Fixes

* adjust course overview URL ([85c436a](https://github.com/thetric/ilias-downloader-cli/commit/85c436a27439272f1c0552243760110d9bd7433d)), closes [#19](https://github.com/thetric/ilias-downloader-cli/issues/19)

### [4.0.1](https://github.com/thetric/ilias-downloader-cli/compare/v4.0.0...v4.0.1) (2019-10-21)


### Bug Fixes

* **connector/domparser:** shutdown connections after sync ([fe1c0e1](https://github.com/thetric/ilias-downloader-cli/commit/fe1c0e1bfcf1ae3f4bdb4b27a35d072489849140)), closes [#18](https://github.com/thetric/ilias-downloader-cli/issues/18)

## 4.0.0 (2019-10-16)


### Bug Fixes

* **connector/domparser:** add missing form data attribute to fix login ([17846cc](https://github.com/thetric/ilias-downloader-cli/commit/17846ccf0c58be8c6a891b7401dcf9bf3396e7e1)), closes [#17](https://github.com/thetric/ilias-downloader-cli/issues/17)
* **connector/domparser:** correct login page url ([04ccc72](https://github.com/thetric/ilias-downloader-cli/commit/04ccc72e970fa859e247677b99b624b0c7b018df)), closes [#17](https://github.com/thetric/ilias-downloader-cli/issues/17)

### 3.2.2 (2018-10-19)


### Bug Fixes

* download courses to folders ([1eab384](https://github.com/thetric/ilias-downloader-cli/commit/1eab384fc4237f4b9b2eda43ae46dec07126e5f2)), closes [#16](https://github.com/thetric/ilias-downloader-cli/issues/16)

### 3.2.1 (2018-05-27)

## 3.2.0 (2018-04-22)


### Features

* display human readable file size ([8e180ed](https://github.com/thetric/ilias-downloader-cli/commit/8e180edf301090a1e0896f28b28a956eee212907))


### Bug Fixes

* update ilias-connector-domparser to fix a sync regression ([2d80c95](https://github.com/thetric/ilias-downloader-cli/commit/2d80c959429dc010b9e15a446f9b00caad6d3645))

## 3.1.0 (2018-04-18)


### Features

* update ilias-connector-domparser to make the CLI compatible with different ilias server than https://ilias.fh-dortmund.de ([3a46491](https://github.com/thetric/ilias-downloader-cli/commit/3a464910c3818d68137f85f04128aa2f7d04d742))

### 3.0.2 (2018-04-18)


### Bug Fixes

* **sync:** correct translation key for file download errors ([09c20a3](https://github.com/thetric/ilias-downloader-cli/commit/09c20a329b43dd8ce7304d403f654ac112c9ceb7))

### 3.0.1 (2018-04-17)


### Bug Fixes

* **sync:** continue on unsuccessful downloads and print an error ([8c5f28d](https://github.com/thetric/ilias-downloader-cli/commit/8c5f28d287dd43ab8eaa63754fba324075a9c2c9))
* correct placeholder for password prompt in the default locale ([242215c](https://github.com/thetric/ilias-downloader-cli/commit/242215c20957fffa21b2933300f3ba19be49cf29))

## 3.0.0 (2018-02-21)


### ⚠ BREAKING CHANGES

* the minimum java version now is java 9

### Features

* add translated logs for download sync ([30904fd](https://github.com/thetric/ilias-downloader-cli/commit/30904fd07ae18bd68eb60256a0028b7548a327ab))
* format size in download info ([5bd39d1](https://github.com/thetric/ilias-downloader-cli/commit/5bd39d1b4e05d4fc042994c53d78a17d88280214))


* simplify item visitor ([d11be03](https://github.com/thetric/ilias-downloader-cli/commit/d11be032408a6a2de647f87e562eba7c3c01c665))

### 2.0.9 (2017-11-08)

### 2.0.8 (2017-10-09)


### Bug Fixes

* save selected courses ([71ddad4](https://github.com/thetric/ilias-downloader-cli/commit/71ddad47a776cbb5ac75d703e8043cd37a59fa7f)), closes [#10](https://github.com/thetric/ilias-downloader-cli/issues/10)

### 2.0.7 (2017-09-28)


### Bug Fixes

* **console:** do not close System.in after the first read of console input ([dc61678](https://github.com/thetric/ilias-downloader-cli/commit/dc61678dcac7992da7d03aa3ce55fcb1f448e15e))

### 2.0.6 (2017-09-19)

### 2.0.5 (2017-09-19)

### 2.0.4 (2017-06-29)


### Bug Fixes

* remove redundant parenthesis in download log ([cb973a8](https://github.com/thetric/ilias-downloader-cli/commit/cb973a8e8dd0c4302bbc40d5e461fe4d27a1e101))

### 2.0.3 (2017-06-25)

### 2.0.2 (2017-06-17)


### Bug Fixes

* **ui/cli:** don't print a groovy runtime exception due to wrong API ([59f5630](https://github.com/thetric/ilias-downloader-cli/commit/59f563053a2e95008950b2172091951888bf812d))

### 2.0.1 (2017-06-13)


### Bug Fixes

* **ui/cli:** close scanner after usage ([5082a86](https://github.com/thetric/ilias-downloader-cli/commit/5082a865518992f1f34575c1326c70ea07bd05b5))

## 2.0.0 (2017-06-10)


### ⚠ BREAKING CHANGES

* **connector/api:** The **signature of IliasService#visit has changed** from `Course, Closure<VisitResult>` to `Course, IliasItemVisitor`.
For Groovy devs nothing changes (due to java 8's default interface impls), Java devs must accommodate to the new API.

### Features

* **ui/cli:** print URL for downloaded files ([e8777fe](https://github.com/thetric/ilias-downloader-cli/commit/e8777fe4fa649779cce6772ebfccabdbc942023c))


### Bug Fixes

* **cli:** limit cache size of paths ([c80e00d](https://github.com/thetric/ilias-downloader-cli/commit/c80e00da7312982257c72bd6b05c1ed7da2351e5))


* **connector/api:** use interface instead of closure as Visitor ([19bb82d](https://github.com/thetric/ilias-downloader-cli/commit/19bb82d25fa4fe5cca8240de71de983535fd0fc6))

## 1.1.0 (2017-04-09)


### Features

* **cli:** default to all courses if the input is blank when prompting the user for the courses to sync ([fe3b061](https://github.com/thetric/ilias-downloader-cli/commit/fe3b061d5ee3de674b85f206516e3ce6fd798454)), closes [#7](https://github.com/thetric/ilias-downloader-cli/issues/7)
* **cli:** include username in password prompt ([28bb0a2](https://github.com/thetric/ilias-downloader-cli/commit/28bb0a29bea2cf7a886d7df62adfd943af475026))
* **connector-domparser:** add okHttp web client impl ([3e27d9c](https://github.com/thetric/ilias-downloader-cli/commit/3e27d9cbcc9de864d972c71f75cab75e1bcdbe74))
* **ui-common:** add UserPrefSrvc.getSettingsFile ([4e2ce9e](https://github.com/thetric/ilias-downloader-cli/commit/4e2ce9e360a40a0928163d2089d8880689088ff5))


### Bug Fixes

* **cli:** handle login err and do not crash ([6b2a4f2](https://github.com/thetric/ilias-downloader-cli/commit/6b2a4f21b0b5a53848a3d232bb17edf4b5ca1831))
* **cli:** print "login successful" msg only if login succeeded ([c4e50cc](https://github.com/thetric/ilias-downloader-cli/commit/c4e50cc0f7139192b39d5d1969e8311faaa6860e))
* **cli:** show course selection if no courses are active ([aa6135e](https://github.com/thetric/ilias-downloader-cli/commit/aa6135e8a6004cb971edb47992a48436204d171f))
* **cli:** update activeCourse ids only one + make them unique ([7a23df2](https://github.com/thetric/ilias-downloader-cli/commit/7a23df2054d320948bacf0af80d65e8616ed5c8d))
* **common-ui:** create parent dirs for settings file if parent don't exist ([9f51f0f](https://github.com/thetric/ilias-downloader-cli/commit/9f51f0f48acd5110ca4e6fdf6cdb36e9a1cc8ab1))
* **connector-domparser:** check if login has succeeded ([831b19b](https://github.com/thetric/ilias-downloader-cli/commit/831b19b71865d99af5f40e6e56c1544ea3fe4cd7))
* **connector-domparser:** correctly prepend 'https://' ([29e1a9b](https://github.com/thetric/ilias-downloader-cli/commit/29e1a9b9dd13868b1fc9fca387d69941cdf9e0a2))
* **connector-domparser:** extract base url from trimmed url string ([b68372e](https://github.com/thetric/ilias-downloader-cli/commit/b68372e23647e5dd775f5768e9a7506ca71d6a04))
* **connector-domparser:** try to preserve content's original encoding otherwise use iso 8859-1 ([8b3cfb5](https://github.com/thetric/ilias-downloader-cli/commit/8b3cfb55767919b1582717c8783a3a63eb357054))

### 1.0.2 (2017-04-03)


### Bug Fixes

* do not reset maxFileSize to 0 ([8d60bac](https://github.com/thetric/ilias-downloader-cli/commit/8d60bac7ecf182c0504f13a1957624374fc57ccd)), closes [#6](https://github.com/thetric/ilias-downloader-cli/issues/6)

### 1.0.1 (2017-04-03)


### Bug Fixes

* save selected courses ([a47f9e3](https://github.com/thetric/ilias-downloader-cli/commit/a47f9e3f371a41c0a1cb7f2fa0c3d43e936d858e)), closes [/github.com/thetric/ilias-downloader/issues/3#issuecomment-291092174](https://github.com/thetric//github.com/thetric/ilias-downloader/issues/3/issues/issuecomment-291092174)
* use 0 instead of null for default file size ([e0bed4c](https://github.com/thetric/ilias-downloader-cli/commit/e0bed4c8087ad5a2a7ddb19a194128c628b646c1)), closes [#5](https://github.com/thetric/ilias-downloader-cli/issues/5)

## 1.0.0 (2017-03-16)


### Features

* **cli:** accept download size limit ([0890231](https://github.com/thetric/ilias-downloader-cli/commit/0890231245d3f10aed6f58fe630688eb05443b7d))
* **cli:** cache paths of parent ilias items while syncing ([15b25b1](https://github.com/thetric/ilias-downloader-cli/commit/15b25b1a4f574eb995c0ac604562eb294c905b56))
* **cli:** check file size limit in sync ([92abe32](https://github.com/thetric/ilias-downloader-cli/commit/92abe32a18d1f66d25f01707231d6dfa98645c97))
* **cli:** enable course selection ([7f5558a](https://github.com/thetric/ilias-downloader-cli/commit/7f5558a29f36207912887311c8258c98f1ec7704)), closes [#3](https://github.com/thetric/ilias-downloader-cli/issues/3)
* **cli:** use json user pref srvc instead of yml ([231681d](https://github.com/thetric/ilias-downloader-cli/commit/231681dcbd07d8b39771cc9c5329f8ffb60b244d))
* **common:** add json user pref service ([0004194](https://github.com/thetric/ilias-downloader-cli/commit/00041944064150546ad22d2e07ea2e4ad4aad96c))
* **common:** create parent dirs + settings file if not exist ([1d25081](https://github.com/thetric/ilias-downloader-cli/commit/1d250815e7d6227cd643b9ec8162d162e8d9bcaf))
* **connector:** replace IliasService.getCourseItems(Course) with traversal method ([dab41ab](https://github.com/thetric/ilias-downloader-cli/commit/dab41abaec59c54aeac78e1a153f01841ed69469))
* **connector-domparser:** extract file size ([5ac7592](https://github.com/thetric/ilias-downloader-cli/commit/5ac7592f930e5a54f2f6b62ccb0357d393c0e4f6))
* **i18n:** add translation for 'download limit' cli option ([66edf6b](https://github.com/thetric/ilias-downloader-cli/commit/66edf6ba17542a4b162cb6090ec0eb494bb47168))


### Bug Fixes

* **cli:** save prefs on 1st start ([cbdf153](https://github.com/thetric/ilias-downloader-cli/commit/cbdf1534722f1a44a001c828631afcf1b084890b))
* **common:** create parent dirs of settings path only if not exist ([49557e2](https://github.com/thetric/ilias-downloader-cli/commit/49557e2ac1ce1def7f70891b959c6618fd3a318d))
* relative DateTime is now parsed with seconds+nanos set at 0 ([61d1c69](https://github.com/thetric/ilias-downloader-cli/commit/61d1c69e1f40ded098c27e7fb7654f800a2ae5b5))
* remove @CompileStatic to make visitor pattern work ([313963c](https://github.com/thetric/ilias-downloader-cli/commit/313963c85bb953096d7384357b5c1fc5e1359589))
* replace existing files in sync ([b309f75](https://github.com/thetric/ilias-downloader-cli/commit/b309f753de6ed9e2499a4fe29e4b2c9e10a9a518))
* set correct mod time of files ([78e68b0](https://github.com/thetric/ilias-downloader-cli/commit/78e68b013075064cbf5366b037aaca11b8c874de))
* use sync dir from cli args instead of hardcoded one ([7377768](https://github.com/thetric/ilias-downloader-cli/commit/7377768a2b590ee4d063942fdbc372331d1b0b46))

<a name="3.2.2"></a>
## [3.2.2](https://github.com/thetric/ilias-downloader-cli/compare/3.2.1...3.2.2) (2018-10-19)


### Bug Fixes

* download courses to folders ([1eab384](https://github.com/thetric/ilias-downloader-cli/commit/1eab384)), closes [#16](https://github.com/thetric/ilias-downloader-cli/issues/16)



<a name="3.2.1"></a>
## [3.2.1](https://github.com/thetric/ilias-downloader-cli/compare/3.2.0...3.2.1) (2018-05-27)


### Performance Improvements

* **sync:** simplify ilias item path lookup ([6840a3b](https://github.com/thetric/ilias-downloader-cli/commit/6840a3b))



<a name="3.2.0"></a>
# [3.2.0](https://github.com/thetric/ilias-downloader-cli/compare/3.1.0...3.2.0) (2018-04-22)


### Bug Fixes

* update ilias-connector-domparser to fix a sync regression ([2d80c95](https://github.com/thetric/ilias-downloader-cli/commit/2d80c95))


### Features

* display human readable file size ([8e180ed](https://github.com/thetric/ilias-downloader-cli/commit/8e180ed))



<a name="3.1.0"></a>
# [3.1.0](https://github.com/thetric/ilias-downloader-cli/compare/3.0.2...3.1.0) (2018-04-18)


### Features

* update ilias-connector-domparser to make the CLI compatible with different ilias server than https://ilias.fh-dortmund.de ([3a46491](https://github.com/thetric/ilias-downloader-cli/commit/3a46491))



<a name="3.0.2"></a>
## [3.0.2](https://github.com/thetric/ilias-downloader-cli/compare/3.0.1...3.0.2) (2018-04-18)


### Bug Fixes

* **sync:** correct translation key for file download errors ([09c20a3](https://github.com/thetric/ilias-downloader-cli/commit/09c20a3))



<a name="3.0.1"></a>
## [3.0.1](https://github.com/thetric/ilias-downloader-cli/compare/3.0.0...3.0.1) (2018-04-17)


### Bug Fixes

* correct placeholder for password prompt in the default locale ([242215c](https://github.com/thetric/ilias-downloader-cli/commit/242215c))
* **sync:** continue on unsuccessful downloads and print an error ([8c5f28d](https://github.com/thetric/ilias-downloader-cli/commit/8c5f28d))



<a name="3.0.0"></a>
# [3.0.0](https://github.com/thetric/ilias-downloader-cli/compare/2.0.9...3.0.0) (2018-02-21)


### Code Refactoring

* simplify item visitor ([d11be03](https://github.com/thetric/ilias-downloader-cli/commit/d11be03))


### Features

* add translated logs for download sync ([30904fd](https://github.com/thetric/ilias-downloader-cli/commit/30904fd))
* format size in download info ([5bd39d1](https://github.com/thetric/ilias-downloader-cli/commit/5bd39d1))


### BREAKING CHANGES

* the minimum java version now is java 9



<a name="2.0.9"></a>
## [2.0.9](https://github.com/thetric/ilias-downloader-cli/compare/2.0.8...2.0.9) (2017-11-08)

Maintenance release

<a name="2.0.8"></a>
## [2.0.8](https://github.com/thetric/ilias-downloader-cli/compare/2.0.7...2.0.8) (2017-10-09)


### Bug Fixes

* save selected courses ([bcda977](https://github.com/thetric/ilias-downloader-cli/commit/bcda977))



<a name="2.0.7"></a>
## [2.0.7](https://github.com/thetric/ilias-downloader-cli/compare/2.0.6...2.0.7) (2017-09-28)


### Bug Fixes

* **console:** do not close System.in after the first read of console input ([95898df](https://github.com/thetric/ilias-downloader-cli/commit/95898df))



<a name="2.0.6"></a>
## [2.0.6](https://github.com/thetric/ilias-downloader-cli/compare/2.0.5...2.0.6) (2017-09-19)

Improved and lesser logging.

<a name="2.0.5"></a>
## [2.0.5](https://github.com/thetric/ilias-downloader-cli/compare/2.0.4...2.0.5) (2017-09-19)

Maintenance release (refactoring, dependency updates, docs).

<a name="2.0.4"></a>
## [2.0.4](https://github.com/thetric/ilias-downloader-cli/compare/2.0.2...v2.0.4) (2017-06-29)


### Bug Fixes

* remove redundant parenthesis in download log ([a589d5c](https://github.com/thetric/ilias-downloader-cli/commit/a589d5c))



<a name="2.0.3"></a>
## [2.0.3](https://github.com/thetric/ilias-downloader-cli/compare/2.0.2...v2.0.3) (2017-06-25)

This is only a maintenance release containing internal improvements and updates.

<a name="2.0.2"></a>
## [2.0.2](https://github.com/thetric/ilias-downloader/compare/2.0.1...v2.0.2) (2017-06-17)


### Bug Fixes

* **ui/cli:** don't print a groovy runtime exception due to wrong API ([59f5630](https://github.com/thetric/ilias-downloader/commit/59f5630))



<a name="2.0.1"></a>
## [2.0.1](https://github.com/thetric/ilias-downloader/compare/2.0.0...v2.0.1) (2017-06-13)

Fix `Error: Could not find or load main class com.github.thetric.iliasdownloader.cli.Cli`

### Bug Fixes

* **ui/cli:** close scanner after usage ([5082a86](https://github.com/thetric/ilias-downloader/commit/5082a86))



<a name="2.0.0"></a>
# [2.0.0](https://github.com/thetric/ilias-downloader/compare/1.1.0...v2.0.0) (2017-06-10)

This release contains a breaking change for developers using the `connector` modules.

One notable feature for the CLI users is that the sync log shows the downloaded files (with `file://` URL).

 Another change is the distribution of the CLI as ZIP instead of a JAR.
 The CLI can be launched by unzipping the archive and executing the corresponding shell script (`bin/cli` for Linux/Mac, `bin/cli.bat` for Windows).

### Bug Fixes

* **cli:** limit cache size of paths ([c80e00d](https://github.com/thetric/ilias-downloader/commit/c80e00d))


### Code Refactoring

* **connector/api:** use interface instead of closure as Visitor ([19bb82d](https://github.com/thetric/ilias-downloader/commit/19bb82d))


### Features

* **ui/cli:** print URL for downloaded files ([e8777fe](https://github.com/thetric/ilias-downloader/commit/e8777fe))


### BREAKING CHANGES

* **connector/api:** The **signature of IliasService#visit has changed** from `Course, Closure<VisitResult>` to `Course, IliasItemVisitor`.
For Groovy devs nothing changes (due to java 8's default interface impls), Java devs must accommodate to the new API.



<a name="1.1.0"></a>
# [1.1.0](https://github.com/thetric/ilias-downloader/compare/1.0.2...1.1.0) (2017-04-09)


### Bug Fixes

* **cli:** handle login err and do not crash ([6b2a4f2](https://github.com/thetric/ilias-downloader/commit/6b2a4f2))
* **cli:** print "login successful" msg only if login succeeded ([c4e50cc](https://github.com/thetric/ilias-downloader/commit/c4e50cc))
* **cli:** show course selection if no courses are active ([aa6135e](https://github.com/thetric/ilias-downloader/commit/aa6135e))
* **cli:** update activeCourse ids only one + make them unique ([7a23df2](https://github.com/thetric/ilias-downloader/commit/7a23df2))
* **common-ui:** create parent dirs for settings file if parent don't exist ([9f51f0f](https://github.com/thetric/ilias-downloader/commit/9f51f0f))
* **connector-domparser:** check if login has succeeded ([831b19b](https://github.com/thetric/ilias-downloader/commit/831b19b))
* **connector-domparser:** correctly prepend 'https://' ([29e1a9b](https://github.com/thetric/ilias-downloader/commit/29e1a9b))
* **connector-domparser:** extract base url from trimmed url string ([b68372e](https://github.com/thetric/ilias-downloader/commit/b68372e))
* **connector-domparser:** try to preserve content's original encoding otherwise use iso 8859-1 ([8b3cfb5](https://github.com/thetric/ilias-downloader/commit/8b3cfb5))


### Features

* **cli:** default to all courses if the input is blank when prompting the user for the courses to sync ([fe3b061](https://github.com/thetric/ilias-downloader/commit/fe3b061)), closes [#7](https://github.com/thetric/ilias-downloader/issues/7)
* **cli:** include username in password prompt ([28bb0a2](https://github.com/thetric/ilias-downloader/commit/28bb0a2))
* **connector-domparser:** add okHttp web client impl ([3e27d9c](https://github.com/thetric/ilias-downloader/commit/3e27d9c))
* **ui-common:** add UserPrefSrvc.getSettingsFile ([4e2ce9e](https://github.com/thetric/ilias-downloader/commit/4e2ce9e))



<a name="1.0.2"></a>
## [1.0.2](https://github.com/thetric/ilias-downloader/compare/1.0.1...1.0.2) (2017-04-03)


### Bug Fixes

* do not reset maxFileSize to 0 ([8d60bac](https://github.com/thetric/ilias-downloader/commit/8d60bac)), closes [#6](https://github.com/thetric/ilias-downloader/issues/6)



<a name="1.0.1"></a>
## [1.0.1](https://github.com/thetric/ilias-downloader/compare/1.0.0...1.0.1) (2017-04-03)


### Bug Fixes

* save selected courses ([a47f9e3](https://github.com/thetric/ilias-downloader/commit/a47f9e3))
* use 0 instead of null for default file size ([e0bed4c](https://github.com/thetric/ilias-downloader/commit/e0bed4c)), closes [#5](https://github.com/thetric/ilias-downloader/issues/5)



<a name="1.0.0"></a>
# [1.0.0](https://github.com/thetric/ilias-downloader/compare/313963c...1.0.0) (2017-03-16)


### Bug Fixes

* relative DateTime is now parsed with seconds+nanos set at 0 ([61d1c69](https://github.com/thetric/ilias-downloader/commit/61d1c69))
* remove [@CompileStatic](https://github.com/CompileStatic) to make visitor pattern work ([313963c](https://github.com/thetric/ilias-downloader/commit/313963c))
* replace existing files in sync ([b309f75](https://github.com/thetric/ilias-downloader/commit/b309f75))
* set correct mod time of files ([78e68b0](https://github.com/thetric/ilias-downloader/commit/78e68b0))
* use sync dir from cli args instead of hardcoded one ([7377768](https://github.com/thetric/ilias-downloader/commit/7377768))
* **cli:** save prefs on 1st start ([cbdf153](https://github.com/thetric/ilias-downloader/commit/cbdf153))
* **common:** create parent dirs of settings path only if not exist ([49557e2](https://github.com/thetric/ilias-downloader/commit/49557e2))


### Features

* **cli:** accept download size limit ([0890231](https://github.com/thetric/ilias-downloader/commit/0890231))
* **cli:** cache paths of parent ilias items while syncing ([15b25b1](https://github.com/thetric/ilias-downloader/commit/15b25b1))
* **cli:** check file size limit in sync ([92abe32](https://github.com/thetric/ilias-downloader/commit/92abe32))
* **cli:** enable course selection ([7f5558a](https://github.com/thetric/ilias-downloader/commit/7f5558a)), closes [#3](https://github.com/thetric/ilias-downloader/issues/3)
* **cli:** use json user pref srvc instead of yml ([231681d](https://github.com/thetric/ilias-downloader/commit/231681d))
* **common:** add json user pref service ([0004194](https://github.com/thetric/ilias-downloader/commit/0004194))
* **common:** create parent dirs + settings file if not exist ([1d25081](https://github.com/thetric/ilias-downloader/commit/1d25081))
* **connector:** replace IliasService.getCourseItems(Course) with traversal method ([dab41ab](https://github.com/thetric/ilias-downloader/commit/dab41ab))
* **connector-domparser:** extract file size ([5ac7592](https://github.com/thetric/ilias-downloader/commit/5ac7592))
* **i18n:** add translation for 'download limit' cli option ([66edf6b](https://github.com/thetric/ilias-downloader/commit/66edf6b))
