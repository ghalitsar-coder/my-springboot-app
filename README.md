# My Spring Boot Application

This is a simple Spring Boot application that demonstrates the basic structure and functionality of a Spring Boot project.

## Project Structure

```
my-springboot-app
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── demo
│   │   │               ├── DemoApplication.java
│   │   │               └── controller
│   │   │                   └── HelloController.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── demo
│                       └── DemoApplicationTests.java
├── build.gradle
└── settings.gradle
```

## Prerequisites

- Java 11 or higher
- Gradle

## Setup Instructions

1. Clone the repository:
   ```
   git clone <repository-url>
   cd my-springboot-app
   ```

2. Build the project:
   ```
   ./gradlew build
   ```

3. Run the application:
   ```
   ./gradlew bootRun
   ```

## Usage

Once the application is running, you can access the greeting message by navigating to:
```
http://localhost:8080/hello
```

## Testing

To run the tests, use the following command:
```
./gradlew test
```

## License

This project is licensed under the MIT License.