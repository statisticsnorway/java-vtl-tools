# Changelog 

## develop

### Added

### Changed

## 0.1.14 - 2021-05-26

* Downgraded to Java VTL 0.1.12 and Java VTL Connectors 0.1.16 due to filter bugs in complex VTL expressions.

## 0.1.13 - 2021-04-08

* Fixed Spring Boot 2.1.8 and Java 11 dependencies

### Changed

## 0.1.12 - 2021-04-07

### Changed

* Update VTL version to 0.1.13
* Update VTL Connector version to 0.1.17
* Upgrade to Spring Boot 2.1.8.RELEASE

## 0.1.11-3 - 2019-03-05

### Changed

* Update URLs to internal SSB distribution repos

## 0.1.11-2 - 2018-12-12

* Update VTL version to 0.1.12

## 0.1.11-1 - 2018-10-23

### Changed

* Update VTL Connector version to 0.1.16

## 0.1.11 - 2018-10-23

### Changed

* Configuration flag to disable/enable caching

## 0.1.10-1 - 2018-09-04

### Changed

* Update VTL version to 0.1.10-2

## 0.1.10 - 2018-08-31

### Changed

* Update VTL version to 0.1.10

### Added

* Prometheus endpoint
* Expose the list of VTL keywords at the endpoint `/keywords`

### Changed

* Update connector versions
* Remove log file
* Update to spring-boot 2

## 0.1.9 - 2018-06-20

### Added

* This changelog

### Changed

* Fixed regex connector to support URI without parameters
* Validation now uses a new Bindings for each call
* Do syntax check before validation
* Add API to expose Bindings
