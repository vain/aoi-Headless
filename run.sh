#!/bin/bash

AOIRES=ArtOfIllusion.jar
TESTFILE=/tmp/test.aoi

javac -cp .:$AOIRES headless/*.java -Xlint:deprecation || exit 1

unset DISPLAY
java -cp .:$AOIRES headless.Controller "$TESTFILE"
