# Change Log

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [1.0.0-RC1] - 2020-07-04
### Added
- Scala 2.13 Support
- Scala.js 1.x Support
- Add GH Pages microsite
- Add API unidocs
- GitHub Actions for CI & package publishing
- Use scalafmt
- Use [scala-typesafeequals](https://github.com/er1c/scala-typesafeequals) for `===` and `=!=` macros.

### Changed
- Refactored Scala-version specific source code into 2.11/2.12/2.13-specific directories
- Upgrade to ScalaTest 3.2.0 and update 

### Removed
- TypeSafeEquals (moved to [scala-typesafeequals](https://github.com/er1c/scala-typesafeequals)
- AnyRefNullChecks (moved to [scala-typesafeequals](https://github.com/er1c/scala-typesafeequals)
- Travis build

### Fixed
- A "correct" implementation of mapValuesStrict using the CanBuildFrom for 2.12/2.11 and BuildFrom in 2.13

## [Unreleased]
- Initial fork from [fm-common](https://github.com/frugalmechanic/fm-common) 0.47.0
