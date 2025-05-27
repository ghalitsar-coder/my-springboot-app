# Spring Boot Ordering System

This is a modern Spring Boot application for a coffee shop ordering system with a beautiful TailwindCSS frontend.

## Features

- **Modern Frontend**: TailwindCSS-based responsive design with coffee shop theme
- **Backend API**: Spring Boot 3.0.0 with Java 21
- **Database**: PostgreSQL integration with JPA/Hibernate
- **Complete Ordering System**: Users, Products, Categories, Orders, Payments, Reviews
- **RESTful APIs**: Full CRUD operations for all entities
- **Modern UI Components**: Product cards, user management, search & filter functionality

## Tech Stack

- **Backend**: Spring Boot 3.0.0, Java 21, Spring Data JPA, Hibernate
- **Database**: PostgreSQL 17
- **Frontend**: TailwindCSS 3, HTML5, JavaScript ES6
- **Build Tool**: Gradle 8.7
- **Development**: VS Code, Git

## Project Structure

```
my-springboot-app
├── src
│   ├── main
│   │   ├── java/com/example/demo
│   │   │   ├── DemoApplication.java           # Main Spring Boot application
│   │   │   ├── controller/                    # REST API controllers
│   │   │   │   ├── HelloController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   └── UserController.java
│   │   │   ├── entity/                        # JPA entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── Category.java
│   │   │   │   └── ... (other entities)
│   │   │   ├── repository/                    # Data repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   └── ... (other repositories)
│   │   │   └── service/                       # Business logic services
│   │   │       ├── UserService.java
│   │   │       └── ProductService.java
│   │   └── resources
│   │       ├── application.properties         # Database configuration
│   │       ├── schema.sql                    # Database schema
│   │       ├── data.sql                      # Sample data
│   │       └── static/                       # Frontend files
│   │           ├── index-tailwind.html       # TailwindCSS homepage
│   │           ├── products-tailwind.html    # Products page
│   │           ├── users-tailwind.html       # User management
│   │           ├── coffe-template/           # Coffee shop templates
│   │           ├── js/                       # JavaScript files
│   │           └── css/                      # Custom styles
│   └── test                                  # Unit tests
├── build.gradle                             # Gradle build configuration
├── .gitignore                              # Git ignore rules
└── README.md                               # This file
```

## Prerequisites

- **Java 21** or higher
- **PostgreSQL 17** (with a database named `ordering_system`)
- **Gradle** (included via wrapper)

## Setup Instructions

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd my-springboot-app
   ```

2. **Setup PostgreSQL database:**
   - Install PostgreSQL 17
   - Create database named `ordering_system`
   - Update password in `src/main/resources/application.properties` if needed

3. **Build the project:**
   ```bash
   ./gradlew build
   ```

4. **Run the application:**
   ```bash
   ./gradlew bootRun
   ```

## Usage

Once the application is running, you can access:

### Frontend Pages
- **Homepage**: http://localhost:8080 (TailwindCSS landing page)
- **Products Page**: http://localhost:8080/products-tailwind.html
- **Users Page**: http://localhost:8080/users-tailwind.html
- **Coffee Menu**: http://localhost:8080/coffe-template/cofe-list-menu.html

### API Endpoints
- **Users API**: http://localhost:8080/api/users
- **Products API**: http://localhost:8080/api/products
- **Hello API**: http://localhost:8080/hello

## Database

The application uses PostgreSQL with automatic table creation via Hibernate. The database schema includes:

- **Users**: Customer information and authentication
- **Categories**: Product categorization
- **Products**: Coffee and bakery items
- **Orders**: Customer orders with status tracking
- **Order Details**: Individual items in orders
- **Payments**: Payment processing and status
- **Reviews**: Customer feedback and ratings
- **Customizations**: Product customization options

## Development

### Running Tests
```bash
./gradlew test
```

### Building for Production
```bash
./gradlew build
```

### Database Reset
If you need to reset the database, change `spring.jpa.hibernate.ddl-auto` to `create-drop` in `application.properties`.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.