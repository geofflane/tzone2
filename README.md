# tzone

APIs and interface to do time zone conversions.

A learning project using Clojure, Pedestal, Datomic, and Angular.js (with Clojurescript)

## Getting Started

1. Start datomic transactor `datomic-transactor <path to properties>`
2. Start the application: `lein run-dev` \*
3. Go to [localhost:8080](http://localhost:8080/) to see: `Hello World!`
4. Read your app's source code at src/tzone/service.clj. Explore the docs of functions
   that define routes and responses.
5. Run your app's tests with `lein test`. Read the tests at test/tzone/service_test.clj.
6. Learn more! See the [Links section below](#links).

\* `lein run-dev` automatically detects code changes. Alternatively, you can run in production mode
with `lein run`.

## Configuration

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).

## Links
* [Other examples](https://github.com/pedestal/samples)
