# Book Management App

This application provides a CRUD interface for managing books. It utilizes gRPC for service methods, Gradle for build management, Citrus for integration tests, and Docker along with Docker Compose for deployment.

## Usage

### Running the Application

#### Using Docker Compose

1. Clone the repository:
   ```bash
   git clone https://github.com/jurok3x/unillence_testtask.git

2. Navigate to the project directory:
    ```bash
   cd book-management-app
3. Run Docker Compose:
    ```bash
   docker-compose up
   
#### Running Locally

1. Clone the repository and navigate to the project directory as mentioned above.

2. Set up your local database with appropriate credentials.
3. Add the system env properties *DB_USER*, *DB_PASSWORD*, *DB_HOST* to point to your local database.
4. Build and run the application using Gradle.

### Running tests
Integration tests are written using Citrus. Make sure Docker is running to maintain test containers.

## gRPC Service Methods
The application provides the following gRPC service methods for CRUD operations on books:

1. **read**: Read a single book by ID.
2. **readAll**: Read all books.
3. **create**: Create a new book.
4. **update**: Update an existing book.
5. **delete**: Delete a book.

## Entity: Book

- **`id`** (UUID)
- **`title`** (String)
- **`author`** (String)
- **`isbn`** (String)
- **`quantity`** (Integer)