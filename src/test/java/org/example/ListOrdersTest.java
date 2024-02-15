package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.example.Constants.*;
import static org.example.CourierApi.*;
import static org.example.OrderApi.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListOrdersTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("Получение списка заказов без параметров")
    @Description("Проверка, что получаем список заказов")
    public void listOrdersWithoutParameters() {
        Response response = sendGetRequestCourier();
        response.then().assertThat().body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение списка заказов с фильтрацией по станциям метро")
    @Description("Проверка, что получаем список заказов только для указанных станций метро")
    public void listOrdersWithNearestStation() {
        String stationOne = "10";
        String stationTwo = "121";
        String parameters = "?nearestStation=[\"" + stationOne + "\", \"" + stationTwo + "\"]";
        ListOrders listOrders = sendGetRequestCourier(parameters);
        String resultOne = listOrders.getAvailableStations().get(0).getNumber();
        String resultTwo = listOrders.getAvailableStations().get(1).getNumber();
        assertEquals(stationOne, resultOne);
        assertEquals(stationTwo, resultTwo);
    }

    @Test
    @DisplayName("Получение списка заказов с лимитом на страницу")
    @Description("Проверка, что получаем заданное количество заказов на странице")
    public void listOrdersWithLimit() {
        int limit = 5;
        ListOrders listOrders = given()
                .get(ORDERS + "?limit=" + limit)
                .body().as(ListOrders.class);
        int resultResponseOrders = listOrders.getOrders().size();
        int resultLimit = listOrders.getPageInfo().getLimit();
        assertEquals(limit, resultResponseOrders);
        assertEquals(limit, resultLimit);
    }

    @Test
    @DisplayName("Получение списка заказов с указанием страницы")
    @Description("Проверка, что получаем список заказов указанной страницы")
    public void listOrdersWithPage() {
        int page = 2;
        ListOrders listOrders = given()
                .get(ORDERS + "?page=" + page)
                .body().as(ListOrders.class);
        int resultPage = listOrders.getPageInfo().getPage();
        assertEquals(page, resultPage);
    }

    @Test
    @DisplayName("Получение списка заказов с фильтрацией идентификатору курьера")
    @Description("Проверка, что получаем список заказов указанного курьера")
    public void listOrdersWithCourierId() {
        //Создаем нового курьера
        NewCourier courier = new NewCourier(LOGIN, PASSWORD, "Abu");
        sendPostCreateCourier(courier);
        //Получаем ID созданного курьера
        Login newLogin = new Login(LOGIN, PASSWORD);
        Response response = sendPostLoginCourier(newLogin);
        String id = getCourierID(response);
        //Получаем список заказов для созданного курьера (проверяем, что заказов нет)
        ListOrders listOrders = given()
                .get(ORDERS + "?courierId=" + id)
                .body().as(ListOrders.class);
        assertTrue(listOrders.getOrders().isEmpty());
        //Удаляем созданного курьера
        deleteCourierByID(id);
    }
}
