# Used for Map Matching
MAP_MATCHING_COORDINATES = 13.418946862220764,52.50055852688439;13.419011235237122,52.50113000479732;13.419756889343262,52.50171780290061;13.419885635375975,52.50237416816131;13.420631289482117,52.50294888790448

# Used for directions
DIRECTIONS_POST_COORDINATES = 2.344003915786743,48.85805170891599;2.346750497817993,48.85727523615161;2.348681688308716,48.85936462637049;2.349550724029541,48.86084691113991;2.349550724029541,48.8608892614883;2.349625825881958,48.86102337068847;2.34982967376709,48.86125629633996

build-config:
	./gradlew compileBuildConfig

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
