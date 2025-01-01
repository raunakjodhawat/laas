## LAAS (Login As a Service)

A small microservice that handles:
1. User registration (Sign ups)
2. User login (Login)
3. Forgot password (Password reset)

### Features
- A user can sign up/login using username, email.

### Endpoints
1. POST `/api/v1/user` - User registration with payload as [CreateUserRequest](src/main/scala/com/laas/model/CreateUserRequest.scala). Response is [CreateUserResponse](src/main/scala/com/laas/model/CreateUserResponse.scala)
    - Will create the user if email id and username is unique across database.
2. GET `/api/v1/authenticate` - Basic authentication with username and password. Response is either 200 or 401
### Tech Stack
1. Scala 3.x with ZIO (Http, JSON)
2. Postgres (Database) with Slick

### How to run
#### Local

#### Production
- Make sure to change the default username and password of the Postgres database in the `application.conf` file.