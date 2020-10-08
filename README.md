#Build:
- cd to project root
- ./gradlew build

#Run:
java -jar build/libs/article-analyser-1.0-SNAPSHOT.jar [URL-TO-FETCH]

URL-TO-FETCH parameter is optional and defaults to https://www.reddit.com/r/webdev.json

# Run tests:
./gradlew test
