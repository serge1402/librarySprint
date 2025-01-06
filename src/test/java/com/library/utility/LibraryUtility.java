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

    String name = "SN_test " + faker.book().title();
    String isbn = faker.code().isbn13();
    Integer year = faker.number().numberBetween(2000, 2024);
    String author = "SN_test " + faker.book().author();
    Integer bookId = faker.number().numberBetween(1, 20);
    String description = faker.lorem().sentence(10);

    book.put("name", name);
    book.put("isbn", isbn);
    book.put("year", year);
    book.put("author", author);
    book.put("book_category_id", bookId);
    book.put("description", description);

    return book;
}

public static Map<String,Object> createRandomUser(){
        Faker faker = new Faker();
        Map<String,Object> randomUser = new HashMap<String,Object>();

        String full_name = "SN_test " + faker.name().fullName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        Integer user_group_id = faker.number().numberBetween(1, 3);
        String status = "ACTIVE";
        String start_date = "2000-01-01";
        String end_date = "2025-04-02";
        String address = "SN_test " + faker.address().fullAddress();

        randomUser.put("full_name", full_name);
        randomUser.put("email", email);
        randomUser.put("password", password);
        randomUser.put("user_group_id", user_group_id);
        randomUser.put("status", status);
        randomUser.put("start_date", start_date);
        randomUser.put("end_date", end_date);
        randomUser.put("address", address);

        return randomUser;
}



}
