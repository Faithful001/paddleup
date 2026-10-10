# PaddleUp API

PaddleUp is a high-performance backend platform for online auctions and real-time bidding, built with Spring Boot, PostgreSQL, Redis, RabbitMQ, and Cloudinary. It features real-time event streaming via Server-Sent Events (SSE), concurrency-safe bidding with pessimistic locking, threaded auction discussions with rich media attachments, and automated email workflows.

---

## Table of Contents

- [Core Features](#core-features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Prerequisites](#prerequisites)
- [Environment Variables](#environment-variables)
- [Running Locally](#running-locally)
- [Real-Time Streaming (SSE)](#real-time-streaming-sse)
- [API Documentation](#api-documentation)
  - [Authentication](#authentication)
  - [Auctions](#auctions)
  - [Bidding](#bidding)
  - [Comments & Discussions](#comments--discussions)
  - [Media Uploads (Cloudinary)](#media-uploads-cloudinary)
  - [Notifications](#notifications)
  - [Social & Follower Graph](#social--follower-graph)
  - [Categories](#categories)
- [Database Schema & Migrations](#database-schema--migrations)
- [Building & Testing](#building--testing)

---

## Core Features

- **Concurrency-Safe Bidding Engine**: Utilizes PostgreSQL pessimistic write locks (`PESSIMISTIC_WRITE`) to prevent race conditions and ensure bid integrity during fast-paced auctions.
- **Real-Time Updates via Server-Sent Events (SSE)**:
  - Live bid broadcast stream (`/auctions/{id}/bids/stream`)
  - Live comment and discussion stream (`/auctions/{id}/comments/stream`)
  - Personal user notification stream (`/notifications/stream`)
- **Unified Media Attachments**: Supports both image and video attachments across auctions and comments using PostgreSQL `JSONB` structures.
- **Cloudinary Integration**: Dedicated upload service supporting single and batch image and video uploads with size and MIME validation.
- **Threaded Comment System**: Nested replies, soft deletion to preserve reply trees, and automatic real-time event broadcasting.
- **Social Graph & Discovery**: Follow/unfollow sellers, like auctions, and browse personalized auction feeds.
- **Security & Identity**: Stateless authentication with JWT access and refresh tokens, account status checks (suspension handling), email verification, and password resets via Resend.
- **Automated Database Migrations**: Schema evolution tracked with Flyway migrations.

---

## Technology Stack

- **Language & Runtime**: Java 21
- **Framework**: Spring Boot 4.1.x (Spring Web, Spring Security, Spring Data JPA, Spring Validation)
- **Database**: PostgreSQL 15 (with Flyway database migrations)
- **Cache & Session Management**: Redis 7
- **Message Broker**: RabbitMQ
- **Media Storage**: Cloudinary (Cloudinary HTTP5 Java SDK)
- **Transactional Emails**: Resend Java SDK
- **Data Mapping**: MapStruct & Lombok
- **Token Security**: JJWT (Java JWT)

---

## System Architecture

```
[ Client Application (Web / Mobile) ]
       │
       ├── HTTP REST Mutations (JWT Secured) ──> [ Spring Boot Controllers ]
       │                                                    │
       │                                          [ Domain Services ]
       │                                           (Transactional)
       │                                                    │
       │                                  ┌─────────────────┴─────────────────┐
       │                                  ▼                                   ▼
       │                        [ PostgreSQL 15 ]                      [ Cloudinary ]
       │                      (Pessimistic Locking)                  (Media Storage)
       │                                  │
       │                                  ▼
       │                    [ Spring Application Events ]
       │                      (@TransactionalEventListener)
       │                                  │
       └── SSE Streams (Unidirectional) ──┴──> [ SseEmitter Broadcast Services ]
           - /auctions/{id}/bids/stream
           - /auctions/{id}/comments/stream
           - /notifications/stream
```

---

## Prerequisites

Ensure you have the following installed on your machine:

- Java 21 JDK or higher
- Docker and Docker Compose
- Maven (or use the included `./mvnw` wrapper)
- A Cloudinary account (for image and video uploads)
- A Resend account (for transactional emails)

---

## Environment Variables

Create a `.env` file in the project root directory or supply the environment variables in your deployment environment:

```env
# Application Server
PORT=8020

# PostgreSQL Database
DB_URL=jdbc:postgresql://localhost:5443/paddleup_db
DB_USER=paddleup
DB_PASSWORD=paddleup321
DB_NAME=paddleup_db

# Redis Cache
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=paddleup321

# RabbitMQ Messaging
RABBITMQ_USER=paddleup
RABBITMQ_PASSWORD=paddleup321
RABBITMQ_PORT=5674
RABBITMQ_UI_PORT=15672

# Security & Tokens
JWT_SECRET=your_base64_or_hex_encoded_secret_key_minimum_256_bits
JWT_EXPIRATION_MS=900000
JWT_REFRESH_EXPIRATION_MS=604800000

# Resend Email Delivery
RESEND_API_KEY=re_your_api_key_here

# Cloudinary Media Storage
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
CLOUDINARY_URL=cloudinary://your_api_key:your_api_secret@your_cloud_name
```

---

## Running Locally

### 1. Start Infrastructure Services

Use Docker Compose to start PostgreSQL and Redis:

```bash
docker compose up -d
```

Verify that the containers are healthy:

```bash
docker compose ps
```

### 2. Build the Project

Run Maven clean compile using the Maven wrapper:

```bash
# On Linux / macOS
./mvnw clean compile

# On Windows PowerShell
.\mvnw.cmd clean compile
```

### 3. Run the Spring Boot Application

```bash
# On Linux / macOS
./mvnw spring-boot:run

# On Windows PowerShell
.\mvnw.cmd spring-boot:run
```

The server starts by default on port `8020` with the context path `/api/v1`. The base URL will be:

```
http://localhost:8020/api/v1
```

---

## Real-Time Streaming (SSE)

PaddleUp uses Server-Sent Events (SSE) for real-time data delivery. Clients connect over standard HTTP and receive events automatically.

### 1. Auction Bids Stream

- **Endpoint**: `GET /api/v1/auctions/{auctionId}/bids/stream`
- **Authentication**: Public (no token required)
- **Event Name**: `bid-placed`

```javascript
const bidStream = new EventSource("http://localhost:8020/api/v1/auctions/123/bids/stream");

bidStream.addEventListener("bid-placed", (event) => {
  const bid = JSON.parse(event.data);
  console.log("New highest bid:", bid.amount);
});
```

### 2. Auction Comments Stream

- **Endpoint**: `GET /api/v1/auctions/{auctionId}/comments/stream`
- **Authentication**: Public (no token required)
- **Event Name**: `comment-created`

```javascript
const commentStream = new EventSource("http://localhost:8020/api/v1/auctions/123/comments/stream");

commentStream.addEventListener("comment-created", (event) => {
  const comment = JSON.parse(event.data);
  console.log("New comment from:", comment.authorUsername);
  console.log("Media attached:", comment.media);
});
```

### 3. Personal User Notifications Stream

- **Endpoint**: `GET /api/v1/notifications/stream`
- **Authentication**: Bearer Token required
- **Event Name**: `notification`

```javascript
// Note: When connecting to an authenticated SSE endpoint, pass the token as a query param or via an EventSource polyfill:
const notificationStream = new EventSource("http://localhost:8020/api/v1/notifications/stream?token=" + userToken);

notificationStream.addEventListener("notification", (event) => {
  const notification = JSON.parse(event.data);
  console.log("Alert:", notification.title, notification.body);
});
```

---

## API Documentation

All responses follow a standard envelope format:

```json
{
  "success": true,
  "message": "Request successful",
  "data": { ... }
}
```

### Authentication

Base path: `/api/v1/auth`

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Register a new user account | No |
| `POST` | `/auth/login` | Login with email and password | No |
| `POST` | `/auth/refresh` | Obtain new access token via refresh token | No |
| `POST` | `/auth/logout` | Invalidate current session and tokens | Yes |
| `GET` | `/auth/me` | Fetch authenticated user profile | Yes |
| `POST` | `/auth/forgot-password` | Send password reset instructions via email | No |
| `POST` | `/auth/reset-password` | Reset password using email verification token | No |
| `POST` | `/auth/change-password` | Change password for authenticated user | Yes |
| `POST` | `/auth/verify-email` | Verify email address using token | No |
| `POST` | `/auth/resend-verification` | Resend verification email to address | No |

---

### Auctions

Base path: `/api/v1/auctions`

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/auctions` | Create and publish a new auction | Yes |
| `POST` | `/auctions/draft` | Save an auction as a draft | Yes |
| `GET` | `/auctions` | List public auctions (paginated) | No |
| `GET` | `/auctions/me` | List auctions created by authenticated user | Yes |
| `GET` | `/auctions/{id}` | Get detailed auction information | No |
| `PATCH` | `/auctions/{id}` | Update draft auction details | Yes |

#### Create Auction Payload Example:

```json
POST /api/v1/auctions
{
  "title": "Vintage 1968 Chronograph Watch",
  "description": "Mint condition mechanical chronograph with original leather strap.",
  "startingPrice": 500.00,
  "reservePrice": 850.00,
  "minIncrement": 25.00,
  "status": "ACTIVE",
  "endsAt": "2026-11-01T18:00:00Z",
  "media": [
    {
      "url": "https://res.cloudinary.com/paddleup/image/upload/v1/watch-front.jpg",
      "type": "IMAGE"
    },
    {
      "url": "https://res.cloudinary.com/paddleup/video/upload/v1/watch-movement.mp4",
      "type": "VIDEO"
    }
  ]
}
```

---

### Bidding

Base path: `/api/v1/auctions/{auctionId}/bids`

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/auctions/{id}/bids` | Place a bid on an active auction | Yes |
| `GET` | `/auctions/{id}/bids` | Get bid history for an auction (paginated) | No |
| `GET` | `/auctions/{id}/bids/stream` | Stream real-time bids via SSE | No |

#### Place Bid Payload:

```json
POST /api/v1/auctions/{auctionId}/bids
{
  "amount": 550.00
}
```

---

### Comments & Discussions

Base path: `/api/v1/auctions/{auctionId}/comments`

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/auctions/{id}/comments` | Post a top-level comment or reply with media | Yes |
| `GET` | `/auctions/{id}/comments` | List top-level comments (paginated) | No |
| `GET` | `/auctions/{id}/comments/{commentId}/replies` | Get replies for a specific comment | No |
| `DELETE`| `/auctions/{id}/comments/{commentId}` | Soft delete comment (author or seller only) | Yes |
| `GET` | `/auctions/{id}/comments/stream` | Stream real-time comments via SSE | No |

#### Post Comment with Media:

```json
POST /api/v1/auctions/{auctionId}/comments
{
  "content": "Can you provide a close-up picture of the watch dial?",
  "parentId": null,
  "media": [
    {
      "url": "https://res.cloudinary.com/paddleup/image/upload/v1/reference-dial.jpg",
      "type": "IMAGE"
    }
  ]
}
```

---

### Media Uploads (Cloudinary)

Base path: `/api/v1/uploads`

Upload images and videos directly to Cloudinary before attaching them to auctions or comments.

| Method | Endpoint | Format | Description | Auth Required |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/uploads` | `multipart/form-data` | Upload a single image or video | Yes |
| `POST` | `/uploads/batch` | `multipart/form-data` | Upload multiple images and videos | Yes |

#### Validation Limits:
- Images: max 10MB (JPEG, PNG, GIF, WebP)
- Videos: max 100MB (MP4, MOV, WebM, AVI, MKV)

#### Single Upload Response (`201 CREATED`):

```json
{
  "success": true,
  "message": "File uploaded successfully",
  "data": {
    "url": "https://res.cloudinary.com/paddleup/image/upload/v1728520000/paddleup/sample_abc123.jpg",
    "publicId": "paddleup/sample_abc123",
    "type": "IMAGE",
    "format": "jpg",
    "bytes": 245100
  }
}
```

---

### Notifications

Base path: `/api/v1/notifications`

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/notifications` | Get user notifications (paginated) | Yes |
| `GET` | `/notifications/unread-count`| Get unread notifications counter | Yes |
| `PATCH`| `/notifications/{id}/read` | Mark a specific notification as read | Yes |
| `PATCH`| `/notifications/read-all` | Mark all user notifications as read | Yes |
| `GET` | `/notifications/stream` | Real-time notification SSE stream | Yes |

---

### Social & Follower Graph

Base paths: `/api/v1/users`, `/api/v1/auctions/{id}/likes`

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/users/{id}` | Get public profile of a user/seller | No |
| `GET` | `/users/{id}/auctions` | Get auctions listed by a specific seller | No |
| `POST` | `/users/{id}/follow` | Follow a seller | Yes |
| `DELETE`| `/users/{id}/follow` | Unfollow a seller | Yes |
| `GET` | `/users/me/followers` | Get followers of the current user | Yes |
| `GET` | `/users/me/following` | Get accounts followed by current user | Yes |
| `POST` | `/auctions/{id}/likes` | Like an auction | Yes |
| `DELETE`| `/auctions/{id}/likes` | Unlike an auction | Yes |
| `GET` | `/auctions/{id}/likes/count`| Get total likes on an auction | No |
| `GET` | `/auctions/{id}/likes/status`| Check if current user liked an auction | Yes |

---

### Categories

Base path: `/api/v1/categories`

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/categories` | List all available auction categories | No |
| `GET` | `/categories/{id}` | Get category details | No |
| `POST` | `/categories` | Create a category (admin) | Yes |
| `PATCH`| `/categories/{id}` | Update a category (admin) | Yes |
| `DELETE`| `/categories/{id}` | Delete a category (admin) | Yes |

---

## Database Schema & Migrations

Database migrations are managed using Flyway located in `src/main/resources/db/migration`:

| Version | Migration Script | Description |
| :--- | :--- | :--- |
| `V1` | `V1__init_schema.sql` | Users, auctions, and bids core tables |
| `V2` | `V2__add_is_supended_to_users.sql` | User suspension flag |
| `V3` | `V3__add_highest_bid_winner_id_payment_deadline_to_auction.sql` | Auction closing and settlement columns |
| `V4` | `V4__add_suspended_at_to_users.sql` | Suspension timestamp tracking |
| `V5` | `V5__add_email_verification_to_users.sql` | Email verification flag |
| `V6` | `V6__create_tokens_table.sql` | Refresh and reset token persistence |
| `V7` | `V7__create_followers_table.sql` | User following and follower graph table |
| `V8` | `V8__create_comments_table.sql` | Threaded auction comments with parent_id |
| `V9` | `V9__create_auction_likes_table.sql` | Auction likes table |
| `V10`| `V10__create_notifications_table.sql`| User notifications table |
| `V11`| `V11__add_media_to_auctions_and_comments.sql`| Migrates auctions and comments to JSONB media |

---

## Building & Testing

Run the full Maven test and build cycle:

```bash
# Run unit and integration tests
./mvnw test

# Compile and package application JAR
./mvnw clean package

# Run the packaged JAR
java -jar target/paddleup-0.0.1-SNAPSHOT.jar
```

---

## License

This project is licensed under the MIT License.
