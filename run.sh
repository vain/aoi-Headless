#!/bin/bash

AOIRES=ArtOfIllusion.jar
TESTFILE=/tmp/test.aoi
OUTFILE=/tmp/test.png

javac -cp .:$AOIRES headless/*.java -Xlint:deprecation || exit 1

unset DISPLAY
java -cp .:$AOIRES headless.Controller "$TESTFILE" "$OUTFILE"
