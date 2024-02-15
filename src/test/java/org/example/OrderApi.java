package org.example;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.example.Constants.*;

public class OrderApi {
    @Step("Send GET request to /api/v1/orders")
    public static Response sendGetRequestCourier() {
        return given().get(ORDERS);
    }
    @Step("Send GET request to /api/v1/orders")
    public static ListOrders sendGetRequestCourier(String parameters) {
        return given()
                .get(ORDERS + parameters)
                .body().as(ListOrders.class);
    }
    @Step("Send POST request to /api/v1/orders")
    public static Response sendPostRequestOrder(CreateOrder createOrder) {
        return given()
                .header(HEADER_TYPE, HEADER_JSON)
                .body(createOrder)
                .post(ORDERS);
    }
}
