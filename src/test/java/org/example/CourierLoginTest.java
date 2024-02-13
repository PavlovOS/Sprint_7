package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static org.apache.http.HttpStatus.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.example.Constants.*;
import static org.example.CourierApi.*;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("Авторизация существующего курьера")
    @Description("Кейс проверяет: что курьер может авторизоваться; что для авторизации нужно передать все обязательные поля; что успешный запрос возвращает id")
    public void courierAuthorizationTrue() {
        NewCourier courier = new NewCourier(LOGIN, PASSWORD, FIRST_NAME);
        sendPostCreateCourier(courier);
        Login newLogin = new Login(LOGIN, PASSWORD);
        Response response = sendPostLoginCourier(newLogin);
        response.then().assertThat()
                .body("id", notNullValue())
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Авторизация без обязательного поля login")
    @Description("Проверка, что возвращается ошибка, если нет поля login")
    public void courierAuthorizationWithoutLogin() {
        NewCourier courier = new NewCourier(LOGIN, PASSWORD, FIRST_NAME);
        sendPostCreateCourier(courier);
        Login newLogin = new Login(null, PASSWORD);
        Response response = sendPostLoginCourier(newLogin);
        responseThen(response, "message", "Недостаточно данных для входа", SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Авторизация без обязательного поля password")
    @Description("Проверка, что возвращается ошибка, если нет поля password")
    @Issue("Ошибка: получаем \"Service unavailable\" и 504, вместо ответа \"Недостаточно данных для входа\", который указан в спецификации")
    public void courierAuthorizationWithoutPassword() {
        NewCourier courier = new NewCourier(LOGIN, PASSWORD, FIRST_NAME);
        sendPostCreateCourier(courier);
        Login newLogin = new Login(LOGIN, null);
        Response response = sendPostLoginCourier(newLogin);
        responseThen(response, "message", "Недостаточно данных для входа", SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Авторизация с неправильным login")
    @Description("Проверка, что система вернёт ошибку, если неправильно указать логин")
    public void courierAuthorizationWithInvalidLogin() {
        NewCourier courier = new NewCourier(LOGIN, PASSWORD, FIRST_NAME);
        sendPostCreateCourier(courier);
        Login newLogin = new Login("courier", PASSWORD);
        Response response = sendPostLoginCourier(newLogin);
        responseThen(response, "message", "Учетная запись не найдена", SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Авторизация с неправильным password")
    @Description("Проверка, что система вернёт ошибку, если неправильно указать пароль")
    public void courierAuthorizationWithInvalidPassword() {
        NewCourier courier = new NewCourier(LOGIN, PASSWORD, FIRST_NAME);
        sendPostCreateCourier(courier);
        Login newLogin = new Login(LOGIN, "12356");
        Response response = sendPostLoginCourier(newLogin);
        responseThen(response, "message", "Учетная запись не найдена", SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Авторизация под несуществующим пользователем")
    @Description("Проверка, что если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void courierAuthorizationWithMissingUser() {
        Login newLogin = new Login(LOGIN, PASSWORD);
        Response response = sendPostLoginCourier(newLogin);
        responseThen(response, "message", "Учетная запись не найдена", SC_NOT_FOUND);
    }

    @After
    public void deleteCourier() {
        Login newLogin = new Login(LOGIN, PASSWORD);
        Response responseGetID = sendPostLoginCourier(newLogin);
        String id = getCourierID(responseGetID);
        deleteCourierByID(id);
    }
}
