package in.regres;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.example.data.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    @Test
    @Step("Тест успешной авторизации")
    public void testSuccessfulLogin() {
        LoginData loginData = new LoginData("eve.holt@reqres.in", "cityslicka");

        LoginResponse loginResponse = given().spec(createRequestSpecification())
                .body(loginData)
                .log().all()
                .when()
                .post("/login")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(LoginResponse.class);

        Assert.assertNotNull(loginResponse.getToken(), "Токен не был получен");
    }
    @Test
    @Step("Тест неуспешной авторизации из-за отсутствия пароля")
    public void testUnsuccessfulLoginDueToMissingPassword() {
        LoginData loginData = new LoginData("peter@klaven", null); // Отсутствие пароля

        ErrorResponse errorResponse = given().spec(createRequestSpecification())
                .body(loginData)
                .log().all()
                .when()
                .post("/login")
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        Assert.assertEquals(errorResponse.getError(), "Missing password", "Ошибка не соответствует ожидаемой: 'Missing password'");
    }
    @Test
    @Step("Проверка сортировки ресурсов по годам")
    public void testResourcesAreSortedByYear() {
        ResourceResponse response = given().spec(createRequestSpecification())
                .when()
                .get("/unknown")
                .then()
                .extract()
                .as(ResourceResponse.class);
// Выводим данные для проверки порядка
        for (ResourceData resource : response.getData()) {
            System.out.println("ID: " + resource.getId() + ", Name: " + resource.getName() + ", Year: " + resource.getYear());
        }
        // Проверка, что данные отсортированы по годам
        boolean isSorted = isSortedByYear(response.getData());
        Assert.assertTrue(isSorted, "Данные не отсортированы по годам");
    }

    private boolean isSortedByYear(List<ResourceData> data) {
        for (int i = 0; i < data.size() - 1; i++) {
            if (data.get(i).getYear() > data.get(i + 1).getYear()) {
                return false;
            }
        }
        return true;
    }
    @Test
    @Step("Проверка количества XML тегов")
    public void testXmlTagCount() {
        String responseXml = RestAssured.given()
                .header("Content-Type", "application/xml")
                .when()
                .get("https://gateway.autodns.com/")
                .then()
                .extract()
                .asString();

        int tagCount = countXmlTags(responseXml);
        Assert.assertEquals(tagCount, 14, "Количество XML тегов не соответствует ожидаемому");
    }

    private int countXmlTags(String xml) {
        Pattern pattern = Pattern.compile("<\\w+>");
        Matcher matcher = pattern.matcher(xml);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}



