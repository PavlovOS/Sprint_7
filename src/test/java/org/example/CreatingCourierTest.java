package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.example.Constants.*;
import static org.hamcrest.Matchers.equalTo;

public class CreatingCourierTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = baseURL;
    }

    @Test
    @DisplayName("Создание нового курьера")
    @Description("Кейс проверяет: что курьера можно создать; что возвращается правильный код ответа; что возвращается правильное тело ответа; чтобы создать курьера, нужно передать в ручку все обязательные поля")
    public void createNewCourierWithAllFields() {
        NewCourier courier = new NewCourier(login, password, firstName);
        Response response = sendPostRequestCourier(courier);
        response.then().assertThat()
                .body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Test
    @DisplayName("Создание двух полностью одинаковых курьеров")
    @Description("Проверка, что нельзя создать двух одинаковых курьеров")
    @Issue("Ошибка: получаем \"Этот логин уже используется. Попробуйте другой.\", вместо ответа \"Этот логин уже используется\", который указан в спецификации")
    public void createDuplicationCourier() {
        NewCourier courier = new NewCourier(login, password, firstName);
        Response responseCreateFirstCourier = sendPostRequestCourier(courier);
        responseCreateFirstCourier.then().statusCode(201);
        Response responseCreateSecondCourier = sendPostRequestCourier(courier);
        responseThen(responseCreateSecondCourier, "message", "Этот логин уже используется", 409);
    }

    @Test
    @DisplayName("Создание курьера без поля login")
    @Description("Проверка, что получаем ошибку, если нет поля login при создании нового курьера")
    public void createNewCourierWithoutLogin() {
        String json = "{\"password\": \"" + password + "\", \"firstName\": \"" + firstName + "\"}";
        Response response = sendPostRequestCourier(json);
        responseThen(response, "message", "Недостаточно данных для создания учетной записи", 400);
    }

    @Test
    @DisplayName("Создание курьера без поля password")
    @Description("Проверка, что получаем ошибку, если нет поля password при создании нового курьера")
    public void createNewCourierWithoutPassword() {
        String json = "{\"login\": \"" + login + "\", \"firstName\": \"" + firstName + "\"}";
        Response response = sendPostRequestCourier(json);
        responseThen(response, "message", "Недостаточно данных для создания учетной записи", 400);
    }

    @Test
    @DisplayName("Создание курьера без поля firstName")
    @Description("Проверка, что можно создать курьера без имени. В соотвтествии с документацией ошибка должна быть только при отсутствии логина или пароля")
    public void createNewCourierWithoutFirstName() {
        String json = "{\"login\": \"" + login + "\", \"password\": \"" + password + "\"}";
        Response response = sendPostRequestCourier(json);
        response.then().assertThat()
                .body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Test
    @DisplayName("Создание курьера с повторяющимся логином")
    @Description("Проверка, что нельзя создать нового курьера с логином, который уже используется")
    @Issue("Ошибка: получаем \"Этот логин уже используется. Попробуйте другой.\", вместо ответа \"Этот логин уже используется\", который указан в спецификации")
    public void createCourierWithDuplicateLogin() {
        NewCourier courierOne = new NewCourier(login, password, firstName);
        Response responseCreateFirstCourier = sendPostRequestCourier(courierOne);
        responseCreateFirstCourier.then().statusCode(201);
        NewCourier courierTwo = new NewCourier(login, "2467", "Рамиль");
        Response responseCreateSecondCourier = sendPostRequestCourier(courierTwo);
        responseThen(responseCreateSecondCourier, "message", "Этот логин уже используется", 409);
    }

    @Step("Send POST request to /api/v1/courier")
    public Response sendPostRequestCourier(NewCourier courier) {
        return given()
                .header(headerType, headerJson)
                .body(courier)
                .post(createCourier);
    }

    @Step("Send POST request to /api/v1/courier")
    public Response sendPostRequestCourier(String json) {
        return given()
                .header(headerType, headerJson)
                .body(json)
                .post(createCourier);
    }

    @Step("Request with body and status verification")
    public void responseThen(Response response, String message, String equalTo, int statusCode) {
        response.then().assertThat()
                .body(message, equalTo(equalTo))
                .and()
                .statusCode(statusCode);
    }

    @After
    public void deleteCourier() {
        Login newLogin = new Login(login, password);
        Response responseGetID = given()
                .header(headerType, headerJson)
                .body(newLogin)
                .post(loginCourier);
        String id = responseGetID.body().as(CourierID.class).getId();
        given().delete(deleteCourier + id);
    }
}
