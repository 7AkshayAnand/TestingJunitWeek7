package com.codingshuttle.TestingApp;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
//@TestConfiguration allows you to define Spring beans that are only available during test execution.
public class TestContainerConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer(){
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }

}
//PostgreSQLContainer:

//This is a Testcontainer class provided by the Testcontainers library. It is designed to manage PostgreSQL database containers for integration tests.

/*Step 1: Testcontainers Initializes
Your application starts running tests, and Spring Boot processes the @TestConfiguration class.
The postgreSQLContainer() method is executed to create a PostgreSQLContainer bean.
________________________________________
Step 2: Testcontainers Requests a PostgreSQL Container
The PostgreSQLContainer object specifies the Docker image to use:

DockerImageName.parse("postgres:latest")
This indicates that Testcontainers needs the postgres:latest image from Docker.
________________________________________
Step 3: Docker Engine Handles the Request
Image Check:
Docker checks if the postgres:latest image is available locally.
If the image is not found, Docker automatically pulls it from Docker Hub.
The image is downloaded and cached locally so subsequent runs are faster.
Container Creation:
Testcontainers uses the image to create and start a PostgreSQL container.
The container runs as a lightweight virtualized environment with PostgreSQL installed and ready to use.
        ________________________________________
Step 4: Spring Boot Configures the Database
The @ServiceConnection annotation simplifies integration:
Spring Boot detects the running container.
It automatically configures a DataSource bean using the container's connection details:
Hostname
        Port
Username (test)
Password (test)
Database name (test)
You don’t need to manually configure database properties like spring.datasource.url, as Spring Boot retrieves these from the container.
________________________________________
Step 5: Tests Execute
Your application connects to the PostgreSQL container as if it were a regular database.
Queries are executed, and test data is stored in the container's in-memory database.
________________________________________
Step 6: Testcontainers Cleans Up
After the tests finish:
The PostgreSQL container stops.
Docker cleans up the container, removing any temporary data unless you configure Testcontainers to persist it.
        ________________________________________
Visualization of the Workflow
Application (Tests)
Starts the test and initializes the TestContainerConfiguration.
        Testcontainers
Checks for postgres:latest locally → pulls from Docker Hub if necessary → starts the container.
        Docker Hub
Supplies the PostgreSQL image if not already cached locally.
PostgreSQL Container
Runs and serves as the database for your tests.
Tests
Connect to the database in the container, execute, and clean up.
        ________________________________________
What Makes This Approach Useful?
Isolation: Each test gets a clean database environment.
No Manual Setup: No need to install PostgreSQL locally.
Reproducibility: Tests behave the same on any system with Docker installed.
Easy Cleanup: The container is automatically removed after tests.
*/
