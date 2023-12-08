package in.regres;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;
import org.example.data.ApiResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.example.data.User;

import static org.example.specification.Specification.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class UserAvatarTest {


    @DataProvider(name = "pageProvider")
    public Object[][] providePages() {
        return new Object[][]{{2}};
    }

    @Test(dataProvider = "pageProvider")
    public void testAvatarUniqueness(int page) {
        Response response = given().spec(createRequestSpecification())
                .when()
                .get("/users?page=" + page)
                .then()
                .extract()
                .response();

        List<String> avatarUrls = response.jsonPath().getList("data.avatar");
        Set<String> uniqueAvatarFileNames = new HashSet<>();

        for (String url : avatarUrls) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            uniqueAvatarFileNames.add(fileName);
        }

        Assert.assertEquals(uniqueAvatarFileNames.size(), avatarUrls.size(), "Avatar filenames are not unique");
    }

    @Test(dataProvider = "pageProvider")
    public void testUserApi(int page) {

        ApiResponse response = given().spec(createRequestSpecification())
                .when()
                .get("/users?page=" + page)
                .then()
                .extract()
                .as(ApiResponse.class);

        System.out.println("Полученные данные:");
        System.out.println("Страница: " + response.getPage());
        System.out.println("На странице: " + response.getPer_page());
        System.out.println("Всего пользователей: " + response.getTotal());
        System.out.println("Всего страниц: " + response.getTotal_pages());
        System.out.println("Пользователи:");
        response.getData().forEach(user -> {
            System.out.println("ID: " + user.getId() + ", Имя: " + user.getFirst_name() + " " + user.getLast_name() + ", Email: " + user.getEmail() + ", Avatar: " + user.getAvatar());
        });

        Set<String> uniqueAvatarFileNames = response.getData().stream()
                .map(User::getAvatar)
                .collect(Collectors.toSet());

        Assert.assertEquals(uniqueAvatarFileNames.size(), response.getData().size(), "Имена файлов аватаров пользователей не уникальны");
    }
}
