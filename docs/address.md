# Address API Spec

## Create Address

Endpoint: POST /api/contact/{idContact}/address

Request Header:

- X-API-TOKEN: Token

Request Body:

```json
{
  "street": "street",
  "city": "city",
  "province": "province",
  "country": "country",
  "postalCode": "postalCode"
}
```

Response Body (Success):

```json
{
  "data": {
    "id": "id", // random string
    "street": "street",
    "city": "city",
    "province": "province",
    "country": "country",
    "postalCode": "postalCode"
  }
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Update Address

Endpoint: PATCH /api/contact/{idContact}/address/{idAddress}

Request Header:

- X-API-TOKEN: Token

Request Body:

```json
{
  "street": "street",
  "city": "city",
  "province": "province",
  "country": "country",
  "postalCode": "postalCode"
}
```

Response Body (Success):

```json
{
  "data": {
    "id": "id", // random string
    "street": "street",
    "city": "city",
    "province": "province",
    "country": "country",
    "postalCode": "postalCode"
  }
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Get Address

Endpoint: GET /api/contact/{idContact}/address/{idAddress}

Request Header:

- X-API-TOKEN: Token

Response Body (Success):

```json
{
  "data": {
    "id": "id", // random string
    "street": "street",
    "city": "city",
    "province": "province",
    "country": "country",
    "postalCode": "postalCode"
  }
}
```

Response Body (Failed):

```json
{
  "error": "Error message"
}
```

## Delete Address

Endpoint: DELETE /api/contact/{idContact}/address/{idAddress}

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

## List Address

Endpoint: POST /api/contact/{idContact}/address

Query Param:

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
      "street": "street",
      "city": "city",
      "province": "province",
      "country": "country",
      "postalCode": "postalCode"
    },
    {
      "id": "id", // random string
      "street": "street",
      "city": "city",
      "province": "province",
      "country": "country",
      "postalCode": "postalCode"
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
