package org.example;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.example.Constants.*;
import static org.hamcrest.Matchers.equalTo;

public class CourierApi {
    @Step("Send POST create to /api/v1/courier")
    public static void sendPostCreateCourier(NewCourier courier) {
        given().header(HEADER_TYPE, HEADER_JSON).body(courier).post(CREATE_COURIER);
    }

    @Step("Send POST request to /api/v1/courier")
    public static Response sendPostRequestCourier(NewCourier courier) {
        return given()
                .header(HEADER_TYPE, HEADER_JSON)
                .body(courier)
                .post(CREATE_COURIER);
    }

    @Step("Send POST request to /api/v1/courier/login")
    public static Response sendPostLoginCourier(Login login) {
        return given()
                .header(HEADER_TYPE, HEADER_JSON)
                .body(login)
                .post(LOGIN_COURIER);
    }

    @Step("Assigning the courier's ID")
    public static String getCourierID(Response response) {
        return response.body().as(CourierID.class).getId();
    }

    @Step("Deleting a courier by ID")
    public static void deleteCourierByID(String id) {
        given().delete(DELETE_COURIER + id);
    }

    @Step("Send DELETE request to /api/v1/courier/")
    public static Response sendDeleteRequestCourier(String parameters) {
        return given()
                .delete(DELETE_COURIER + parameters);
    }

    @Step("Request with body and status verification")
    public static void responseThen(Response response, String message, boolean equalTo, int statusCode) {
        response.then().assertThat()
                .body(message, equalTo(equalTo))
                .and()
                .statusCode(statusCode);
    }

    @Step("Request with body and status verification")
    public static void responseThen(Response response, String message, String equalTo, int statusCode) {
        response.then().assertThat()
                .body(message, equalTo(equalTo))
                .and()
                .statusCode(statusCode);
    }
}
