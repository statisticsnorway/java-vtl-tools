# Changelog 

## develop

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
