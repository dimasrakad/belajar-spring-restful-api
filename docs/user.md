# User API Spec

## Register User

Endpoint: POST /api/auth/register

Request Body:

```json
{
  "username": "username",
  "password": "password",
  "name": "name"
}
```

Response Body (Success):

```json
{
  "data": null
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Login User

Endpoint: POST /api/auth/login

Request Body:

```json
{
  "username": "username",
  "password": "password"
}
```

Response Body (Success):

```json
{
  "data": {
    "token": "token",
    "expiredAt": 1234567890 // milliseconds
  }
}
```

Response Body (Failed, 401):

```json
{
  "error": "Username or password is wrong"
}
```

## Get Current User

Endpoint: GET /api/user/current

Request Header:

- X-API-TOKEN: Token

Response Body (Success):

```json
{
  "data": {
    "username": "username",
    "name": "name"
  }
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Update User

- Endpoint: PATCH /api/user/current

Request Header:

- X-API-TOKEN: Token

Request Body:

```json
{
  "name": "name", // optional
  "password": "password" // optional
}
```

Response Body (Success):

```json
{
  "data": {
    "username": "username",
    "name": "name"
  }
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Logout User

Endpoint: GET /api/auth/logout

Request Header:

- X-API-TOKEN: Token

Response Body (Success):

```json
{
  "data": null
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```
