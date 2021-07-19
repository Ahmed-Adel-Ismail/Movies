# Movies
An Application that displays movies details

# Architecture
This Application follows the Ports & Adapters architecture, where the application logic is written in the core module in a plain kotlin/java module, with no external dependencies (only Rx for reactive functional support), and any platform specific code is in another modules, integrated through implementing the declared ports in the core

# Testing
unit test coverage is around 70%, regardless of the numbers, any logic is unit tested across the application, weather it is presentation logic, business logic, application logic, or even data sources implementation

# APK
for the latest apk of the app, you can find it in this repository in `releases` directory, or download it through this link: https://drive.google.com/file/d/1lPsqrvJNbk5qxbbFHMWYcRmA7pOMhSD0/view?usp=sharing
