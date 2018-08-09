Web service
===========

# Play framework

The web service is built on Play Framework 2.5. Play is heavily inspired by
ASP.NET MVC, Ruby on Rails and Django and is similar to this family of
frameworks. A rich documentation can be found here:

* [Play Framework web site](https://playframework.com)
* [Play Framework project in GitHub](https://github.com/playframework/playframework)

Some details about Play 2.x:
* Stateless: Play 2 is fully RESTful, there is no Java EE session per connection
* Asynchronous I/O: Play can serve long requests asynchronously rather than
  tying up HTTP threads doing business logic
* Based on JBoss Netty and Akka Streams
* Built in hot-reloading, i.e. after code changes
* Uses `sbt` as the build tool and for dependency management

## Guidelines

The web service is the microservice entry point. There might be other
entry points if the microservice has some background agent, for instance to run
continuous tasks like log aggregation, simulations, watchdogs etc.

The web service takes care of loading the configuration, and injecting it to
underlying dependencies, like the service layer. Most of the business logic
is encapsulated in the service layer, while the web service has the
responsibility of accepting requests and providing responses in the correct
format.

You can find more information about Play Framework in
[Stack Overflow](https://stackoverflow.com/tags/playframework) and in
[Play Distribution list](https://groups.google.com/forum/#!forum/play-framework)

## Conventions

* Web service routing is defined in the `routes` file stored in the `conf`
  folder.
* The microservice configuration is defined in the `application.conf` file
  stored in the `conf` folder.
* The service is structured following the default
  [anatomy of a Play application](https://www.playframework.com/documentation/2.5.x/Anatomy)
