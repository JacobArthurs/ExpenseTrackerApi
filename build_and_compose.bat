@echo off

rem Step 1: Build the Maven project
call mvn package

rem Step 2: Build the Docker image
call docker build --no-cache -t expensetracker .

rem Step 3: Start the Docker containers
call docker-compose up -d
