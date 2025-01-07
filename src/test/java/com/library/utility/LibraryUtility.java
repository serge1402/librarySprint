package com.library.utility;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class LibraryUtility {

    public static String getToken(String email, String password) {
        JsonPath jsonPath = RestAssured.given().log().uri()
                .accept(ContentType.JSON)
                .contentType(ContentType.URLENC)
                .formParam("email", email)
                .formParam("password", password)
                .when().post("/login")
                .then().statusCode(200)
                .extract().jsonPath();

        return jsonPath.getString("token");
    }

    public static String generateTokenByRole(String role) {

        String email = "";
        String password = "";

        switch (role) {
            case "librarian" -> {
                email = ConfigurationReader.getProperty("librarian_username");
                password = ConfigurationReader.getProperty("librarian_password");
            }
            case "student" -> {
                email = ConfigurationReader.getProperty("student_username");
                password = ConfigurationReader.getProperty("student_password");
            }
            default -> throw new RuntimeException("Invalid Role Entry :\n>> " + role + " <<");
        };

        System.out.println("login: "+email);
        System.out.println("password: "+password);
        System.out.println("token: "+getToken(email, password));
        return getToken(email, password);

    }

public static Map<String,Object> createRandomBook(){
    Faker faker = new Faker();
    Map<String,Object> book = new HashMap<String,Object>();

    book.put("name", "SN_test " + faker.book().title());
    book.put("isbn", faker.code().isbn13());
    book.put("year", faker.number().numberBetween(2000, 2024));
    book.put("author", "SN_test " + faker.book().author());
    book.put("book_category_id", faker.number().numberBetween(1, 20));
    book.put("description", faker.lorem().sentence(10));

    return book;
}

public static Map<String,Object> createRandomUser(){
        Faker faker = new Faker();
        Map<String,Object> randomUser = new HashMap<String,Object>();

        randomUser.put("full_name", "SN_test " + faker.name().fullName());
        randomUser.put("email", faker.internet().emailAddress());
        randomUser.put("password", faker.internet().password());
        randomUser.put("user_group_id", faker.number().numberBetween(1, 3));
        randomUser.put("status", "ACTIVE");
        randomUser.put("start_date", "2000-01-01");
        randomUser.put("end_date", "2025-04-02");
        randomUser.put("address", "SN_test " + faker.address().fullAddress());

        return randomUser;
}



}
