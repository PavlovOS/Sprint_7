package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.example.Constants.*;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreatingOrderTest {
    private final String firstName = "Naruto";
    private final String lastName = "Uchiha";
    private final String address = "Konoha, 142 apt.";
    private final String metroStation = "4";
    private final String phone = "+7 800 355 35 35";
    private final int rentTime = 5;
    private final String deliveryDate = "2022-06-06";
    private final String comment = "Saske, come back to Konoha";
    private final String[] color;

    public CreatingOrderTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][][] orderParameters() {
        return new String[][][]{
                {{"BLACK"}},
                {{"GREY"}},
                {{"BLACK", "GREY"}},
                {{}},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = baseURL;
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Кейс проверяет: что можно создать заказ с разным параметром color и тело ответа содержит track")
    public void createOrder() {
        CreateOrder createOrder;
        if (color.length == 0) {
            createOrder = new CreateOrder(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment);
        } else {
            createOrder = new CreateOrder(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        }
        Response response = given()
                .header(headerType, headerJson)
                .body(createOrder)
                .post(orders);
        response.then().assertThat()
                .body("track", notNullValue())
                .and()
                .statusCode(201);
    }
}
