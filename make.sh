#!/bin/bash
set -e

mkdir -p jar/assets

javac -cp ".:libs/*" -d jar/ $(find src/ -name "*.java")

cp -r assets/* jar/assets/

jar cfe game/client.jar com.mojang.rubydung.RubyDung -C jar/ .

rm -r jar
