# Contact Category API Spec

## Create Contact Category

Endpoint: POST /api/contact-category

Request Header:

- X-API-TOKEN: Token

Request Body:

```json
{
  "name": "name",
  "color": "COLOR" // COLOR is enum, from get colors api
}
```

Response Body (Success):

```json
{
  "data": {
    "id": "id", // random string
    "name": "name",
    "color": "COLOR"
  }
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Get Contact Category

Endpoint: GET /api/contact-category/{idContactCategory}

Request Header:

- X-API-TOKEN: Token

Response Body (Success):

```json
{
  "data": {
    "id": "id", // random string
    "name": "name",
    "color": "COLOR"
  }
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Get Colors

Endpoint: GET /api/contact-category/colors

Request Header:

- X-API-TOKEN: Token

Response Body (Success):

```json
{
  "data": ["COLOR", "COLOR", "COLOR"]
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## List Contact Category

Endpoint: GET /api/contact-category

Request Header:

- X-API-TOKEN: Token

Response Body (Success):

```json
{
  "data": [
    {
      "id": "id", // random string
      "name": "name",
      "color": "COLOR"
    },
    {
      "id": "id", // random string
      "name": "name",
      "color": "COLOR"
    },
    {
      "id": "id", // random string
      "name": "name",
      "color": "COLOR"
    }
  ]
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Update Contact Category

Endpoint: PATCH /api/contact-category/{idContactCategory}

Request Header:

- X-API-TOKEN: Token

Request Body:

```json
{
  "name": "name",
  "color": "COLOR" // COLOR is enum, from get colors api
}
```

Response Body (Success):

```json
{
  "data": {
    "id": "id", // random string
    "name": "name",
    "color": "COLOR"
  }
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Delete Contact Category

Endpoint: DELETE /api/contact-category/{idContactCategory}

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
