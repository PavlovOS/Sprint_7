package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.example.Constants.*;
import static org.example.CourierApi.*;

public class DeleteCourierTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("Удаление существующего курьера")
    @Description("Кейс проверяет, что успешный запрос возвращает ok: true")
    public void deleteCourierTrue() {
        //Создаем нового курьера
        NewCourier courier = new NewCourier(LOGIN, PASSWORD, "Abu");
        sendPostCreateCourier(courier);
        //Получаем ID созданного курьера
        Login login = new Login(LOGIN, PASSWORD);
        Response responseGetID = sendPostLoginCourier(login);
        String id = getCourierID(responseGetID);
        //Удаляем созданного курьера
        Response delete = sendDeleteRequestCourier(id);
        responseThen(delete, "ok", true, SC_OK);
    }

    @Test
    @DisplayName("Удаление курьера без параметра ID")
    @Description("Проверка, что возвращается ошибка, если нет параметра ID")
    @Issue("Ошибка: получаем \"Not Found.\" и код 404, вместо ответа \"Недостаточно данных для удаления курьера\" и код 400, который указан в спецификации")
    public void deleteCourierWithoutID() {
        Response response = given()
                .delete("/api/v1/courier/");
        responseThen(response, "message", "Недостаточно данных для удаления курьера", SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Удаление несуществующего курьера")
    @Description("Проверка, что возвращается ошибка, если передать ID несуществующего курьера")
    @Issue("Ошибка: получаем \"Курьера с таким id нет.\", вместо ответа \"Курьера с таким id нет\", который указан в спецификации")
    public void deleteNonExistentCourier() {
        String parameters = "000";
        Response response = sendDeleteRequestCourier(parameters);
        responseThen(response, "message", "Курьера с таким id нет", SC_NOT_FOUND);
    }
}
