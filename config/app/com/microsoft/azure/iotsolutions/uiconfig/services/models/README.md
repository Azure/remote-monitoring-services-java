Service layer models
====================

## Guidelines

* Do not reference classes from the "webservice" package.
* Ensure datetime values are transfered using UTC timezone.
* Use `DateTime.parse(lastActivity, ISODateTimeFormat.dateTimeParser().withZoneUTC())`
  to parse datetime values returned by Azure IoT SDK.

## Conventions

* For DateTime fields use org.joda.time.DateTime.
