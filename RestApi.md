# Banking REST API

This is bankapi rest api documentation.

## Install

    ./mvnw clean install

## Run the app

    ./mvnw spring-boot:run

## Run the tests

    ./mvnw clean test

# REST API

The REST API to the example app is described below.

## Get list of accounts

### Request

`GET /accounts`

    curl -i -H 'Accept: application/json' http://localhost:8080/accounts

### Response

    HTTP/1.1 200 OK
    Date: Mon, 11 Oct 2021 17:05:38 GMT
    Status: 200 OK
    Connection: close
    Content-Type: application/json

    [
        {
            "accountNumber": 4,
            "customerNumber": 155,
            "status": "ACTIVE",
            "type": "DEBIT",
            "balance": 10.00
        }
    ]

## Create account for customer with deposit

### Request

`POST /accounts/`

    curl --location --request POST 'http://localhost:8080/accounts' \
    --header 'Content-Type: application/json' \
    --data-raw \
    '{
        "customerNumber": 1,
        "status": "ACTIVE",
        "type": "DEBIT",
        "balance": 100.00
    }'

### Response

    HTTP/1.1 201 Created
    Date: Thu, 24 Feb 2011 12:36:30 GMT
    Content-Type: application/json

    {
        "accountNumber": 1,
        "customerNumber": 1,
        "status": "ACTIVE",
        "type": "DEBIT",
        "currency": "GBP",
        "balance": 100.00
    }

## Get an account

### Request

`GET /accounts/<accountNubmer>`

    curl -i -H 'Accept: application/json' http://localhost:8080/accounts/1

### Response

    HTTP/1.1 200 OK
    Content-Type: application/json

    {
        "accountNumber": 1,
        "customerNumber": 1,
        "status": "ACTIVE",
        "type": "DEBIT",
        "currency": "GBP",
        "balance": 100.00
    }

## Get a non-existent account

### Request

`GET /accounts/<accountNumber>`

    curl -i -H 'Accept: application/json' http://localhost:8080/accounts/9999

### Response

    HTTP/1.1 404 Not Found
    Content-Type: application/json

    {"timestamp":"2021-10-11T23:06:24.346+00:00","status":404,"error":"Not Found","path":"/accounts/9999"}

## Transfer

### Request
`POST /accounts/transfer`

    curl --location --request POST 'http://localhost:8080/accounts/transfer' \
    --header 'Content-Type: application/json' \
    --data-raw \
    '{
        "fromAccountNumber": 1,
        "toAccountNumber": 2,
        "amount": 2,
        "currency": "GBP"
    }'

### Response
    HTTP/1.1 204 No Content
    Connection: close