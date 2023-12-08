package in.regres;


import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.nullValue;


public class APITests {

    @Test
    public void testAvatarUniqueness() {
        RestAssured.baseURI = "https://reqres.in/api";

        Response response = given()
                .when()
                .get("/users?page=2")
                .then()
                .extract()
                .response();

        // Выводим весь JSON ответ
        System.out.println("Полученный JSON ответ:");
        response.prettyPrint();

        List<String> avatarUrls = response.jsonPath().getList("data.avatar");
        Set<String> uniqueAvatarFileNames = new HashSet<>();

        for (String url : avatarUrls) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            uniqueAvatarFileNames.add(fileName);
        }

        Assert.assertEquals(uniqueAvatarFileNames.size(), avatarUrls.size(), "Avatar filenames are not unique");
    }
}