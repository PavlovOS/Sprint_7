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
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = baseURL;
    }

    @Test
    @DisplayName("Авторизация существующего курьера")
    @Description("Кейс проверяет: что курьер может авторизоваться; что для авторизации нужно передать все обязательные поля; что успешный запрос возвращает id")
    public void courierAuthorizationTrue() {
        NewCourier courier = new NewCourier(login, password, firstName);
        sendPostCreateCourier(courier);
        Login newLogin = new Login(login, password);
        Response response = sendPostLoginCourier(newLogin);
        response.then().assertThat()
                .body("id", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Авторизация без обязательного поля login")
    @Description("Проверка, что возвращается ошибка, если нет поля login")
    public void courierAuthorizationWithoutLogin() {
        NewCourier courier = new NewCourier(login, password, firstName);
        sendPostCreateCourier(courier);
        String json = "{\"password\": \"" + password + "\"}";
        Response response = sendPostLoginCourier(json);
        responseThen(response, "message", "Недостаточно данных для входа", 400);
    }

    @Test
    @DisplayName("Авторизация без обязательного поля password")
    @Description("Проверка, что возвращается ошибка, если нет поля password")
    @Issue("Ошибка: получаем \"Service unavailable\" и 504, вместо ответа \"Недостаточно данных для входа\", который указан в спецификации")
    public void courierAuthorizationWithoutPassword() {
        NewCourier courier = new NewCourier(login, password, firstName);
        sendPostCreateCourier(courier);
        String json = "{\"login\": \"" + login + "\"}";
        Response response = sendPostLoginCourier(json);
        responseThen(response, "message", "Недостаточно данных для входа", 400);
    }

    @Test
    @DisplayName("Авторизация с неправильным login")
    @Description("Проверка, что система вернёт ошибку, если неправильно указать логин")
    public void courierAuthorizationWithInvalidLogin() {
        NewCourier courier = new NewCourier(login, password, firstName);
        sendPostCreateCourier(courier);
        Login newLogin = new Login("courier", password);
        Response response = sendPostLoginCourier(newLogin);
        responseThen(response, "message", "Учетная запись не найдена", 404);
    }

    @Test
    @DisplayName("Авторизация с неправильным password")
    @Description("Проверка, что система вернёт ошибку, если неправильно указать пароль")
    public void courierAuthorizationWithInvalidPassword() {
        NewCourier courier = new NewCourier(login, password, firstName);
        sendPostCreateCourier(courier);
        Login newLogin = new Login(login, "12356");
        Response response = sendPostLoginCourier(newLogin);
        responseThen(response, "message", "Учетная запись не найдена", 404);
    }

    @Test
    @DisplayName("Авторизация под несуществующим пользователем")
    @Description("Проверка, что если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void courierAuthorizationWithMissingUser() {
        Login newLogin = new Login(login, password);
        Response response = sendPostLoginCourier(newLogin);
        responseThen(response, "message", "Учетная запись не найдена", 404);
    }

    @Step("Send POST request to /api/v1/courier")
    public void sendPostCreateCourier(NewCourier courier) {
        given().header(headerType, headerJson).body(courier).post(createCourier);
    }

    @Step("Send POST request to /api/v1/courier/login")
    public Response sendPostLoginCourier(String json) {
        return given()
                .header(headerType, headerJson)
                .body(json)
                .post(loginCourier);
    }

    @Step("Send POST request to /api/v1/courier/login")
    public Response sendPostLoginCourier(Login login) {
        return given()
                .header(headerType, headerJson)
                .body(login)
                .post(loginCourier);
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
