# Bestseller Starbux Coffee Api Assesment  - Esra Dursun 
- --
This project implemented regarding Bestseller assignment. It provides to create/update/delete/finish order, get orders by an order id, get all orders, create/update/delete products, get product by a product id and get all products with `starbux-coffee-api` service.

# Technologies
- --
- Maven
- Spring boot
- Spring Data
- Spring Security (Basic Authentication)
- H2 in-memory DB
- Restfull api
- Swagger Api documentation
- Hateoas

# Architecture
- --
- Facade Design Pattern
- Strategy Design Pattern

# Prerequisites 
- ---
 - Maven

# Run & Build
- --
To build and run `starbux-coffee-api` backend service
```sh
$ cd starbux-coffee-api
$ mvn clean install
$ mvn spring-boot:run
```

`starbux-coffee:order-api` will be run on `http://{HOST}:8080/v1/order`,
`starbux-coffee:product-api` will be run on `http://{HOST}:8080/v1/product` requires `ADMIN` role,
`starbux-coffee:report-api` will be run on `http://{HOST}:8080/v1/report` requires `ADMIN` role,
you can reach the `swagger-ui` via `http://{HOST}:8080/swagger-ui.html` 

* For admin role username: `admin` and password: `password` which is for Basic Authentication


