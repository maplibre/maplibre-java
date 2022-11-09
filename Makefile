checkstyle:
	./gradlew checkstyleMain

test:
	./gradlew test -i

build-release:
	./gradlew assemble

build-cli:
	./gradlew shadowJar

javadoc:
	mkdir documentation
	mkdir documentation/core/
	mkdir documentation/geojson/
	mkdir documentation/turf/
	mkdir documentation/services/
	./gradlew :services-core:javadoc; mv services-core/build/docs/javadoc/ ./documentation/core/javadoc/ ; \
	./gradlew :services-geojson:javadoc; mv services-geojson/build/docs/javadoc/ ./documentation/geojson/javadoc/ ; \
	./gradlew :services-turf:javadoc; mv services-turf/build/docs/javadoc/ ./documentation/turf/javadoc/ ; \
	./gradlew :services:javadoc; mv services/build/docs/javadoc/ ./documentation/services/javadoc/ ; \

publish:
	./gradlew publishReleasePublicationToSonatypeRepository closeAndReleaseSonatypeStagingRepository

clean:
	./gradlew clean
