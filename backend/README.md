Make the Docker build process integrate with the Maven build process.
If you bind the default phases, when you type mvn package,
you get a Docker image.
When you type mvn deploy, your image gets pushed.


You can type mvn dockerfile:build and
later mvn dockerfile:tag
and later mvn dockerfile:push without problems.


This also eliminates the need for something
like mvn dockerfile:build -DalsoPush;
instead you can just say mvn dockerfile:build dockerfile:push.