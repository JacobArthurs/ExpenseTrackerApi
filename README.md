# Expense Tracker API

This project is an Expense Tracker RESTful API made using Java Spring Boot, PostgreSQL, and Docker. It is designed to work in conjunction with my [Expense Tracker Frontend](https://github.com/JacobArthurs/expense-tracker-frontend), ensuring a full-stack application experience.

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Docker installed and running on your machine.
- Java Development Kit (JDK) installed.
- Maven installed.
- Git installed.

## Getting Started

1. **Clone the repository:**

   ```bash
   git clone https://github.com/JacobArthurs/ExpenseTrackerApi.git
   
   cd ./ExpenseTrackerApi/
   ```

2. **Build and start the docker containers:**

    ```bash
    ./build_and_compose.bat
    ```

## Usage

- To explore endpoints and view documentation navigate to: `http://localhost:8080/api/swagger-ui`.

- The default user for authentication is:
  - Username: default
  - Password: password
  - This user has all dummy data populated.

- Admin user for authentication is:
  - Username: admin
  - Password: password
  - This user has no data but has access to admin-level endpoints.

- To clear docker postgres persistent storage run command:

    ```bash
    docker volume rm expensetrackerapi_postgres-data
    ```

- To generate JavaDocs, run the following command. The output files will be located in ./target/site.

    ```bash
    mvn javadoc:javadoc
    ```

## Collaboration and Feedback

If you spot any areas for improvement or have suggestions, please don't hesitate to reach out. Whether it's through contacting me directly, opening an issue, or submitting a pull request, I welcome your input. Constructive criticism is invaluable for growth and improvement, and I appreciate your contributions to making this project better.
