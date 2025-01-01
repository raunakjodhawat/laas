## LAAS (Login As a Service)

A small microservice that handles:
1. User registration (Sign ups)
2. User login (Login)
3. Forgot password (Password reset)

### Features
- A user can sign up/login using username, email.
- 
### Tech Stack
1. Scala 3.x with ZIO (Http, JSON)
2. Postgres (Database) with Slick

### How to run
#### Local

#### Production
- Make sure to change the default username and password of the Postgres database in the `application.conf` file.