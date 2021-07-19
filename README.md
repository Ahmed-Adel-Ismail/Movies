# Movies
An Application that displays movies details

# Architecture
This Application follows the Ports & Adapters architecture, where the application logic is written in the core module in a plain kotlin/java module, with no external dependencies (only Rx for reactive functional support), and any platform specific code is in another modules, integrated through implementing the declared ports in the core

![ports and adapters](https://github.com/Ahmed-Adel-Ismail/Movies/blob/master/read-me-resources/hexagonal-architecture.png)

## pros
 - Testing: a pure java / kotlin core makes it very easy in testing
 - Scalability: it is easy to scale as the business rules are defined into ports, any technology can be used to serve more powerful usages while respecting the same business rules
 - Maintainability: maintainability is very easy as most of the changes in the future will be outside the core, like changing UI or upgrading to a database rather than cache, when this happens, the core module is un-touched
 - Security: as our core is not depending on any 3rd parties, it is very safe to keep the code that matters into the core, and the code that does not matter can be replaced if found any vulnerabilities
 - Performance: Specially in project build time, as the architecture requires multiple modules which makes build time faster
 
 ## cons
 - Dependencies: dependencies needs to be managed across multiple modules, and each dependency must have a defined scope
 - Different Mindset: it requires some time for developers who are used to layered architectures, to rethink how they view the system, as Hexagonal architecture is very flexible that it cannot be described in layers, it is more of designing a puzzle of pluggable components rather than putting layers above each other

# Testing
unit test coverage is around 70%, regardless of the numbers, any logic is unit tested across the application, weather it is presentation logic, business logic, application logic, or even data sources implementation

![unit tests count](https://github.com/Ahmed-Adel-Ismail/Movies/blob/master/read-me-resources/unit-tests-count.png)
![core test coverage](https://github.com/Ahmed-Adel-Ismail/Movies/blob/master/read-me-resources/core-test-coverage.png)
![data-sources-test-coverage](https://github.com/Ahmed-Adel-Ismail/Movies/blob/master/read-me-resources/data-sources-test-coverage.png)


# APK
for the latest apk of the app, you can find it in this repository in `releases` directory: https://github.com/Ahmed-Adel-Ismail/Movies/tree/master/releases

or download it through this link: https://drive.google.com/file/d/1lPsqrvJNbk5qxbbFHMWYcRmA7pOMhSD0/view?usp=sharing

# What's next
- I wish I could have implemented the UI with compose, but found problems in setting up stuff, so maybe in the coming iteration will convert the UI parts to compose
- there were couple of bugs related to triggering API's while rotating the app, I disabled rotation for now till I fix those bugs then enable it again (it was fine till I started implementing the Movie Details screen)
- with more data to deal with, the project can use Room database instead of in-memory cache, and implement the Migration Port, then everything should be working as expected
- Did not have chance to use coroutines that much in this project, just in couple of classes, wish to add more features and they could show some amazing stuff wth coroutines

