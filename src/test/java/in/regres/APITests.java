package in.regres;

import io.qameta.allure.Step;
import org.example.data.ApiResponse;
import org.example.data.User;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.example.specification.Specification.createRequestSpecification;

public class APITests {

    @DataProvider(name = "pageProvider")
    public Object[][] providePages() {
        return new Object[][]{{2}};
    }

    @Test(dataProvider = "pageProvider")
    public void testUserApi(int page) {
        ApiResponse response = fetchUserData(page);
        printUserData(response);
        checkAvatarUniqueness(response);
    }

    @Step("Получение данных пользователя для страницы: {page}")
    private ApiResponse fetchUserData(int page) {
        return given().spec(createRequestSpecification())
                .when()
                .get("/users?page=" + page)
                .then()
                .extract()
                .as(ApiResponse.class);
    }

    @Step("Вывод данных пользователя")
    private void printUserData(ApiResponse response) {
        System.out.println("Полученные данные:");
        System.out.println("Страница: " + response.getPage());
        System.out.println("На странице: " + response.getPer_page());
        System.out.println("Всего пользователей: " + response.getTotal());
        System.out.println("Всего страниц: " + response.getTotal_pages());
        System.out.println("Пользователи:");
        response.getData().forEach(user -> {
            System.out.println("ID: " + user.getId() + ", Имя: " + user.getFirst_name() + " " + user.getLast_name() + ", Email: " + user.getEmail() + ", Avatar: " + user.getAvatar());
        });
    }

    @Step("Проверка уникальности имен файлов аватаров")
    private void checkAvatarUniqueness(ApiResponse response) {
        Set<String> uniqueAvatarFileNames = response.getData().stream()
                .map(User::getAvatar)
                .collect(Collectors.toSet());

        Assert.assertEquals(uniqueAvatarFileNames.size(), response.getData().size(), "Имена файлов аватаров пользователей не уникальны");
    }
}
