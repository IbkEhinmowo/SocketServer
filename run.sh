#!/bin/bash

# Compile all the java files
echo "Compiling source files..."
javac src/Main.java src/ServerProcess.java src/Client.java

# Run the server in the background
echo "Starting server..."
java -cp src Main &
#SERVER_PID=$!

# Give the server a moment to start up
sleep 5

# Run the client
echo "Starting client..."
java -cp src Client

## Stop the server
#echo "Stopping server..."
#kill $SERVER_PID

