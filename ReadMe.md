# Coffee Order Api
- --
This project provides to create/update/delete/finish order, get orders by an order id, get all orders, create/update/delete products, get product by a product id and get all products.

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
To build and run backend service
```sh
$ cd coffee-order-api
$ mvn clean install
$ mvn spring-boot:run
```

`:order-api` will be run on `http://{HOST}:8080/v1/order`,
`:product-api` will be run on `http://{HOST}:8080/v1/product` requires `ADMIN` role,
`:report-api` will be run on `http://{HOST}:8080/v1/report` requires `ADMIN` role,
you can reach the `swagger-ui` via `http://{HOST}:8080/swagger-ui.html` 

* For admin role username: `admin` and password: `password` which is for Basic Authentication


