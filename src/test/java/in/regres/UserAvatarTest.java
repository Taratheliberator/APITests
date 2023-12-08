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


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class UserAvatarTest {

    private static RequestSpecification spec;

    @BeforeClass
    public static void createRequestSpecification() {
        spec = new RequestSpecBuilder()
                .setBaseUri("https://reqres.in/api")
                .build();
    }

    @DataProvider(name = "pageProvider")
    public Object[][] providePages() {
        return new Object[][]{{2}};
    }

    @Test(dataProvider = "pageProvider")
    public void testAvatarUniqueness(int page) {
        Response response = given().spec(spec)
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

    @Test
    public void testUserApi() {

        ApiResponse response = given().spec(spec)
                .when()
                .get("/users?page=2")
                .then()
                .extract()
                .as(ApiResponse.class);

        // Теперь вы можете использовать объект response для проверок
        // Например, проверка уникальности имен файлов аватаров:
        Set<String> uniqueAvatarFileNames = response.getData().stream()
                .map(User::getAvatar)
                .collect(Collectors.toSet());

        Assert.assertEquals(uniqueAvatarFileNames.size(), response.getData().size(), "Avatar filenames are not unique");
    }
}
