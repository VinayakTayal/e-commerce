# E-Commerce Backend API

A RESTful e-commerce backend built with **Spring Boot 4**, **PostgreSQL**, and **JWT authentication**. Supports role-based access control, paginated product browsing, keyword/category search, and image upload via **ImageKit**.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.1.1 |
| Language | Java 25 |
| Database | PostgreSQL |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Image Storage | ImageKit |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Utilities | Lombok |

---

## Project Structure

```
src/main/java/com/vinayak/ecommerce/
├── config/           # Security and ImageKit configuration
├── controller/       # REST controllers (Auth, Product, Image)
├── exception/        # Global exception handler
├── model/            # JPA entities (User, Product, UploadedImage)
├── repository/       # Spring Data JPA repositories
├── security/         # JWT authentication filter
└── service/          # Business logic (Auth, Product, ImageKit, JWT)
```

---

## Getting Started

### Prerequisites

- Java 25+
- Maven
- PostgreSQL
- ImageKit account

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/VinayakTayal/ecommerce-backend.git
   cd ecommerce-backend
   ```

2. **Configure properties**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```
   Fill in your credentials in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
   spring.datasource.username=YOUR_DB_USERNAME
   spring.datasource.password=YOUR_DB_PASSWORD

   imagekit.private-key=YOUR_IMAGEKIT_PRIVATE_KEY

   jwt.secret=YOUR_JWT_SECRET_KEY
   jwt.expiration=3600000
   ```

3. **Create the PostgreSQL database**
   ```sql
   CREATE DATABASE ecommerce_db;
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```
   The server starts at `http://localhost:8080`

---

## Authentication

This API uses **JWT (Bearer Token)** authentication. Include the token in the `Authorization` header for all protected routes:

```
Authorization: Bearer <your_token>
```

### Roles

| Role | Access |
|---|---|
| `ROLE_USER` | Read-only access to products |
| `ROLE_ADMIN` | Full CRUD access to products and image upload |

---

## API Endpoints

### Auth — `/api/auth`

> Public endpoints, no token required.

#### `POST /api/auth/register`
Register a new user.

**Request Body:**
```json
{
  "username": "vinayak",
  "password": "secret123",
  "role": "ROLE_USER"
}
```

**Response `201 Created`:**
```json
{
  "message": "User registered successfully",
  "id": 1,
  "username": "vinayak"
}
```

---

#### `POST /api/auth/login`
Login and receive a JWT token.

**Request Body:**
```json
{
  "username": "vinayak",
  "password": "secret123"
}
```

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### Products — `/api/products`

> `GET` routes require `ROLE_USER` or `ROLE_ADMIN`. All other methods require `ROLE_ADMIN`.

#### `POST /api/products`
Create a new product with an image. Uses `multipart/form-data`.

**Form Data:**
| Field | Type | Description |
|---|---|---|
| `product` | JSON Part | Product details |
| `image` | File | Product image |

**Product JSON:**
```json
{
  "productName": "Wireless Headphones",
  "description": "Noise cancelling over-ear headphones",
  "price": 2999.99,
  "category": "Electronics",
  "quantity": 50
}
```

**Response `200 OK`:**
```json
{
  "id": 1,
  "productName": "Wireless Headphones",
  "description": "Noise cancelling over-ear headphones",
  "price": 2999.99,
  "category": "Electronics",
  "quantity": 50,
  "imageUrl": "https://ik.imagekit.io/...",
  "imageKitFileId": "abc123"
}
```

---

#### `GET /api/products`
Get all products with pagination and sorting.

**Query Parameters:**
| Param | Default | Description |
|---|---|---|
| `page` | `0` | Page number (0-indexed) |
| `size` | `10` | Items per page (1–100) |
| `sortBy` | `id` | Field to sort by |
| `direction` | `asc` | Sort direction (`asc` / `desc`) |

**Example:** `GET /api/products?page=0&size=5&sortBy=price&direction=desc`

**Response `200 OK`:** Paginated `Page<Product>` object.

---

#### `GET /api/products/{id}`
Get a single product by ID.

**Response `200 OK`:**
```json
{
  "id": 1,
  "productName": "Wireless Headphones",
  "price": 2999.99,
  "category": "Electronics",
  "quantity": 50,
  "imageUrl": "https://ik.imagekit.io/..."
}
```

**Response `404 Not Found`:** If product does not exist.

---

#### `PUT /api/products/{id}`
Update an existing product. Image is optional.

**Form Data:**
| Field | Type | Required |
|---|---|---|
| `product` | JSON Part | Yes |
| `image` | File | No |

---

#### `DELETE /api/products/{id}`
Delete a product by ID. Also removes the image from ImageKit.

**Response `200 OK`**

---

#### `GET /api/products/search`
Search products by keyword and optional category with pagination.

**Query Parameters:**
| Param | Required | Description |
|---|---|---|
| `keyword` | Yes | Search term (matches name/description) |
| `category` | No | Filter by category |
| `page` | No | Page number (default `0`) |
| `size` | No | Page size (default `10`, max `100`) |
| `sortBy` | No | Sort field (default `id`) |
| `direction` | No | `asc` or `desc` (default `asc`) |

**Example:** `GET /api/products/search?keyword=headphones&category=Electronics`

---

### Images — `/api/images`

> Requires authentication.

#### `POST /api/images/upload`
Upload a standalone image to ImageKit.

**Form Data:**
| Field | Type |
|---|---|
| `file` | Image file |

**Response `200 OK`:**
```json
{
  "url": "https://ik.imagekit.io/...",
  "fileId": "abc123"
}
```

---

## Validation Rules

| Field | Rule |
|---|---|
| `productName` | Required, not blank |
| `price` | Required, must be > 0 |
| `category` | Required, not blank |
| `quantity` | Required, must be >= 0 |
| `page` | Must be >= 0 |
| `size` | Must be between 1 and 100 |

---

## Environment Variables Reference

See `application.properties.example` for all required configuration keys.

| Key | Description |
|---|---|
| `spring.datasource.url` | PostgreSQL connection URL |
| `spring.datasource.username` | Database username |
| `spring.datasource.password` | Database password |
| `imagekit.private-key` | ImageKit private API key |
| `jwt.secret` | Secret key for signing JWT tokens |
| `jwt.expiration` | Token expiry in milliseconds (default: 3600000 = 1hr) |
