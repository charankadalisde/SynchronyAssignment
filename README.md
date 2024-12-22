# SynchronyAssignment
Prerequisites
Before you can run this project, make sure you have the following installed:

JDK 11 or higher: Java Development Kit.
MySQL: Local MySQL installation or Dockerized MySQL instance.
Redis: Local Redis installation or Dockerized Redis instance.
Maven: For building and running the Spring Boot application

How to Run
Start MySQL and Redis locally or use Docker containers as mentioned in the setup instructions.
Run the Spring Boot Application: Run the application using Maven mvn spring-boot:run
Test CRUD Operations: Use tools like Postman or curl to test the CRUD API endpoints (e.g., GET, POST, PUT, DELETE).
Testing the Concurrent Operations:
The application will handle multiple requests concurrently for cache updates and database queries, utilizing the ExecutorService.
Testing
Unit tests are included in the project to validate the behavior of the service layer, Redis caching, and database operations. Use the following command to run tests:
