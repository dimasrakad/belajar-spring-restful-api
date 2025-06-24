# User API Spec

## Register User

Endpoint: POST /api/auth/register

Request Body:

```json
{
  "username": "username",
  "password": "password",
  "name": "name",
  "email": "email"
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

## Verify Email

Endpoint: GET /api/auth/verify

Query Param:

- token: string, token from email

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
  "username/email": "username/email",
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

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Request Reset Password

Endpoint: POST /api/auth/request-reset-password

Request Body:

```json
{
  "email": "email"
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

## Reset Password

Endpoint: POST /api/auth/reset-password

Request Body:

```json
{
  "token": "token",
  "newPassword": "newPassword",
  "confirmPassword": "confirmPassword"
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
