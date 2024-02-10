package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.example.Constants.*;
import static org.hamcrest.Matchers.equalTo;

public class DeleteCourierTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = baseURL;
    }

    @Test
    @DisplayName("Удаление существующего курьера")
    @Description("Кейс проверяет, что успешный запрос возвращает ok: true")
    public void deleteCourierTrue() {
        //Создаем нового курьера
        NewCourier courier = new NewCourier(login, password, "Abu");
        given().header(headerType, headerJson).body(courier).post(createCourier);
        //Получаем ID созданного курьера
        String jsonGetID = "{\"login\": \"" + login + "\", \"password\": \"" + password + "\"}";
        Response responseGetID = given()
                .header(headerType, headerJson)
                .body(jsonGetID)
                .post(loginCourier);
        String id = responseGetID.body().as(CourierID.class).getId();
        //Удаляем созданного курьера
        Response delete = sendDeleteRequestCourier(id);
        delete.then().assertThat()
                .body("ok", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Удаление курьера без параметра ID")
    @Description("Проверка, что возвращается ошибка, если нет параметра ID")
    @Issue("Ошибка: получаем \"Not Found.\" и код 404, вместо ответа \"Недостаточно данных для удаления курьера\" и код 400, который указан в спецификации")
    public void deleteCourierWithoutID() {
        Response response = given()
                .delete("/api/v1/courier/");
        responseThen(response, "message", "Недостаточно данных для удаления курьера", 400);
    }

    @Test
    @DisplayName("Удаление несуществующего курьера")
    @Description("Проверка, что возвращается ошибка, если передать ID несуществующего курьера")
    @Issue("Ошибка: получаем \"Курьера с таким id нет.\", вместо ответа \"Курьера с таким id нет\", который указан в спецификации")
    public void deleteNonExistentCourier() {
        String parameters = "000";
        Response response = sendDeleteRequestCourier(parameters);
        responseThen(response, "message", "Курьера с таким id нет", 404);
    }

    @Step("Send DELETE request to /api/v1/courier/")
    public Response sendDeleteRequestCourier(String parameters) {
        return given()
                .delete(deleteCourier + parameters);
    }

    @Step("Request with body and status verification")
    public void responseThen(Response response, String message, String equalTo, int statusCode) {
        response.then().assertThat()
                .body(message, equalTo(equalTo))
                .and()
                .statusCode(statusCode);
    }
}
