#!/bin/bash
cd game

if [ "$1" = "new" ]; then
	rm level.dat 2>/dev/null
fi

java -Djava.library.path=../natives -cp "client.jar:../libs/*" com.mojang.rubydung.RubyDung
