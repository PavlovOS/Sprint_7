package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.example.Constants.*;
import static org.example.CourierApi.*;

public class CreatingCourierTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("Создание нового курьера")
    @Description("Кейс проверяет: что курьера можно создать; что возвращается правильный код ответа; что возвращается правильное тело ответа; чтобы создать курьера, нужно передать в ручку все обязательные поля")
    public void createNewCourierWithAllFields() {
        NewCourier courier = new NewCourier(LOGIN, PASSWORD, FIRST_NAME);
        Response response = sendPostRequestCourier(courier);
        responseThen(response, "ok", true, SC_CREATED);
    }

    @Test
    @DisplayName("Создание двух полностью одинаковых курьеров")
    @Description("Проверка, что нельзя создать двух одинаковых курьеров")
    @Issue("Ошибка: получаем \"Этот логин уже используется. Попробуйте другой.\", вместо ответа \"Этот логин уже используется\", который указан в спецификации")
    public void createDuplicationCourier() {
        NewCourier courier = new NewCourier(LOGIN, PASSWORD, FIRST_NAME);
        Response responseCreateFirstCourier = sendPostRequestCourier(courier);
        responseCreateFirstCourier.then().statusCode(HttpStatus.SC_CREATED);
        Response responseCreateSecondCourier = sendPostRequestCourier(courier);
        responseThen(responseCreateSecondCourier, "message", "Этот логин уже используется", SC_CONFLICT);
    }

    @Test
    @DisplayName("Создание курьера без поля login")
    @Description("Проверка, что получаем ошибку, если нет поля login при создании нового курьера")
    public void createNewCourierWithoutLogin() {
        NewCourier courier = new NewCourier(null, PASSWORD, FIRST_NAME);
        Response response = sendPostRequestCourier(courier);
        responseThen(response, "message", "Недостаточно данных для создания учетной записи", SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание курьера без поля password")
    @Description("Проверка, что получаем ошибку, если нет поля password при создании нового курьера")
    public void createNewCourierWithoutPassword() {
        NewCourier courier = new NewCourier(LOGIN, null, FIRST_NAME);
        Response response = sendPostRequestCourier(courier);
        responseThen(response, "message", "Недостаточно данных для создания учетной записи", SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание курьера без поля firstName")
    @Description("Проверка, что можно создать курьера без имени. В соотвтествии с документацией ошибка должна быть только при отсутствии логина или пароля")
    public void createNewCourierWithoutFirstName() {
        NewCourier courier = new NewCourier(LOGIN, PASSWORD, null);
        Response response = sendPostRequestCourier(courier);
        responseThen(response, "ok", true, SC_CREATED);
    }

    @Test
    @DisplayName("Создание курьера с повторяющимся логином")
    @Description("Проверка, что нельзя создать нового курьера с логином, который уже используется")
    @Issue("Ошибка: получаем \"Этот логин уже используется. Попробуйте другой.\", вместо ответа \"Этот логин уже используется\", который указан в спецификации")
    public void createCourierWithDuplicateLogin() {
        NewCourier courierOne = new NewCourier(LOGIN, PASSWORD, FIRST_NAME);
        Response responseCreateFirstCourier = sendPostRequestCourier(courierOne);
        responseCreateFirstCourier.then().statusCode(HttpStatus.SC_CREATED);
        NewCourier courierTwo = new NewCourier(LOGIN, "2467", "Рамиль");
        Response responseCreateSecondCourier = sendPostRequestCourier(courierTwo);
        responseThen(responseCreateSecondCourier, "message", "Этот логин уже используется", SC_CONFLICT);
    }

    @After
    public void deleteCourier() {
        Login newLogin = new Login(LOGIN, PASSWORD);
        Response responseGetID = sendPostLoginCourier(newLogin);
        String id = getCourierID(responseGetID);
        deleteCourierByID(id);
    }
}
