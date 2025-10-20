# eudiw-issuer-ui-demo
EUDI wallet: Issuer-ui demo for pre-authorized flow.

## Requirements
- Java 25
- Maven
- Docker

## Configuration

Profiles in the [resources](/src/main/resources) folder:

| Profile | Description                                |
|---------|--------------------------------------------|
| dev     | Local development                          |
| docker  | Docker locally, run by docker-compose file |
| systest | Systest environment                        |
| test    | Test environment                           |


## Running the application locally

The `dev` and `docker` profiles runs the application with similar configuration.

The local hosts file should include:
```
127.0.0.1 issuer-ui-demo
```

The application can be started with Maven:
```
mvn spring-boot:run -Dspring-boot.run.profiles=<profile>
```

The application can be started with Docker compose:
```
docker-compose up --build
```

The application will run on http://issuer-ui-demo:9290.
