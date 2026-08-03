#!/bin/bash
set -e

mkdir -p jar

javac -cp ".:libs/*" -d jar/ $(find src/ -name "*.java")

find src -type f ! -name "*.java" -exec cp {} jar/ \;

jar cfe game/client.jar com.mojang.rubydung.RubyDung -C jar/ .

rm -r jar
