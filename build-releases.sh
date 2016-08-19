#!/bin/sh

mvn clean
mvn test

mvn package -Pstandalone -Djavacpp.platform=windows-x86_64 -DskipTests
mvn package -Pstandalone -Djavacpp.platform=windows-x86 -DskipTests
mvn package -Pstandalone -Djavacpp.platform=linux-x86_64 -DskipTests
mvn package -Pstandalone -Djavacpp.platform=linux-x86 -DskipTests
mvn package -Pstandalone -Djavacpp.platform=macosx-x86_64 -DskipTests
