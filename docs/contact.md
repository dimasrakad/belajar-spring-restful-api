# Contact API Spec

## Create Contact

Endpoint: POST /api/contact

Request Header:

- X-API-TOKEN: Token

Request Body:

```json
{
  "firstName": "firstName",
  "lastName": "lastName",
  "email": "email",
  "phone": "phone",
  "categoryId": "categoryId" // optional
}
```

Response Body (Success):

```json
{
  "data": {
    "id": "id", // random string
    "firstName": "firstName",
    "lastName": "lastName",
    "email": "email",
    "phone": "phone",
    "categoryId": "categoryId",
    "categoryName": "categoryName"
  }
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Update Contact

Endpoint: PATCH /api/contact/{idContact}

Request Header:

- X-API-TOKEN: Token

Request Body:

```json
{
  "firstName": "firstName",
  "lastName": "lastName",
  "email": "email",
  "phone": "phone",
  "categoryId": "categoryId" // optional
}
```

Response Body (Success):

```json
{
  "data": {
    "id": "id", // random string
    "firstName": "firstName",
    "lastName": "lastName",
    "email": "email",
    "phone": "phone",
    "categoryId": "categoryId",
    "categoryName": "categoryName"
  }
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Get Contact

Endpoint: GET /api/contact/{idContact}

Request Header:

- X-API-TOKEN: Token

Response Body (Success):

```json
{
  "data": {
    "id": "id", // random string
    "firstName": "firstName",
    "lastName": "lastName",
    "email": "email",
    "phone": "phone",
    "categoryId": "categoryId",
    "categoryName": "categoryName"
  }
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Search Contact

Endpoint: GET /api/contact

Query Param:

- name: string, contact first name or last name, optional
- phone: string, contact phone, optional
- email: string, contact email, optional
- page: int, start from 0, optional, default 0
- size: int, optional, default 10
- sortBy: string, sort by chosen field, optional
- sortDirection: string, sort direction, optional, depends to sortBy, default ascending

Request Header:

- X-API-TOKEN: Token

Response Body (Success):

```json
{
  "data": [
    {
      "id": "id", // random string
      "firstName": "firstName",
      "lastName": "lastName",
      "email": "email",
      "phone": "phone",
      "categoryId": "categoryId",
      "categoryName": "categoryName"
    },
    {
      "id": "id", // random string
      "firstName": "firstName",
      "lastName": "lastName",
      "email": "email",
      "phone": "phone",
      "categoryId": "categoryId",
      "categoryName": "categoryName"
    }
  ],
  "pagination": {
    "currentPage": 0,
    "totalPage": 1,
    "size": 1
  },
  "sort": {
    "sortBy": "sortBy",
    "sortDirection": "asc"
  }
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Delete Contact

Endpoint: DELETE /api/contact/{idContact}

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
