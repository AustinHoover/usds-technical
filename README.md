# eCFR Visualizer

## Overview
This is a visualizer for CFR data. It periodically scrapes the api and exposes a ui to visualize the results.

## Building
### Prod
To get a locally-served prod build, you should only need a docker 3 install.
It should be as simple as `docker compose --env-file .env up -d --build`

### Dev
It's a little more involved to build the project in a dev configuration. You'll need:
 - JDK 17+
 - Maven
 - Node
 - Local postgres server

From the frontend directory, run `npm i` then `npm start` to serve the ui.

From the backend folder, run `mvn package` then `java -jar ./target/demo-0.0.1-SNAPSHOT.jar` to serve the backend separately.

## Tech Stack
 - Java
 - Spring Boot
 - Postgres
 - React
 - Typescript

## Further Work
 - Track size of individual versions of all titles by getting size value of root node
 - Graph change in title over time to better visualize trend of title (gaining or losing length)
