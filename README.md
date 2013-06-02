
# Clojure/Script Dependencies [![Build Status](https://api.travis-ci.org/rodnaph/clj-deps.png)](http://travis-ci.org/rodnaph/clj-deps) [![Dependencies Status](http://clj-deps.herokuapp.com/github/rodnaph/clj-deps/status.png)](http://clj-deps.herokuapp.com/github/rodnaph/clj-deps) [![Dependencies Status](http://clj-deps.herokuapp.com/github/rodnaph/clj-deps/status.png)](http://clj-deps.herokuapp.com/github/rodnaph/clj-deps)

This is a web application that checks a projects dependencies [Gemnasium](https://gemnasium.com/) style. The 
live version is running on [Heroku](http://heroku.com) at:

http://clj-deps.herokuapp.com

![](http://github.com/rodnaph/clj-deps/raw/master/images/splash.png)

![](http://github.com/rodnaph/clj-deps/raw/master/images/example.png)

## Usage

To run your own copy, or hack on the project first clone the repository, and then start the application from the REPL.

```
$> lein repl
clj-deps.core=> (use 'dev)
clj-deps.core=> (start)
```

This will start a web server on port 9001 by default. Or you can also start using Leiningen.

```
lein run 9001
```

