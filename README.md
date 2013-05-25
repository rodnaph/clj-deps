
# Clojure/Script Dependencies WebApp

This is a web application that checks a projects dependencies [Gemnasium](https://gemnasium.com/) style. The 
live version is running on [Heroku](http://heroku.com) at:

http://clj-deps.herokuapp.com

## Usage

To run your own copy, or hack on the project first clone the repository, and then start the application from the REPL.

```
$> lein repl
clj-deps.core=> (use 'dev)
clj-deps.core=> (start)
```

This will start a web server on port 9001 by default.

