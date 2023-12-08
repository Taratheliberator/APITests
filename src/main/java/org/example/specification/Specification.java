package org.example.specification;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class Specification {

    public static RequestSpecification createRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri("https://reqres.in/api")
                .build();
    }
}
