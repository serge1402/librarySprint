package com.library.steps;

import com.library.pages.BasePage;
import com.library.pages.BooksPage;
import com.library.pages.LoginPage;
import com.library.utility.BrowserUtils;
import com.library.utility.DB_Util;
import com.library.utility.LibraryUtility;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.openqa.selenium.Keys;

import java.util.*;

import static com.library.utility.LibraryUtility.createRandomBook;
import static com.library.utility.LibraryUtility.createRandomUser;

public class StepDefinitions {

    RequestSpecification requestSpecificationGiven = RestAssured.given().log().all();
    Response response;
    JsonPath jp;
    ValidatableResponse validatableResponseThen;

    LoginPage loginPage = new LoginPage();
    BasePage base = new BooksPage();
    BooksPage book = new BooksPage();




    //-----------US_01-----------
    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String user) {
        requestSpecificationGiven.header("x-library-token", LibraryUtility.generateTokenByRole(user));
    }
    @Given("Accept header is {string}")
    public void accept_header_is(String header) {
       requestSpecificationGiven.accept(header); // or requestSpecificationGiven.header("Accept",header);
    }
    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String endPoint) {
        response = requestSpecificationGiven.when().get(endPoint);
        jp = response.jsonPath();
        validatableResponseThen = response.then();

    }
    @Then("status code should be {int}")
    public void status_code_should_be(int code) {
        validatableResponseThen.statusCode(code);
    }
    @Then("Response Content type is {string}")
    public void response_content_type_is(String expectedContentType) {
        validatableResponseThen.contentType(expectedContentType);

    }

    @Then("{string} field should not be null")
    public void field_should_not_be_null(String field) {
        validatableResponseThen.body(field,Matchers.notNullValue());
    }



    //-----------US_02-----------
    String id;
    @Given("Path param is {string}")
    public void path_param_is(String pathParam) {
        requestSpecificationGiven.pathParam("id", pathParam);
        id = pathParam;
    }

    @Then("{string} field should be same with path param")
    public void field_should_be_same_with_path_param(String string) {
        validatableResponseThen.body(string,Matchers.is(id));
    }




    //-----------US_03_01-----------
    @And("Request Content Type header is {string}")
    public void requestContentTypeHeaderIs(String contentType) {
        requestSpecificationGiven.contentType(contentType);
    }

    Map<String, Object> randomMap = new HashMap<>();
    @And("I create a random {string} as request body")
    public void iCreateARandomAsRequestBody(String input) {
        switch (input){
            case "book"->{
                randomMap = createRandomBook();
            }
            case "user"->{
                randomMap = createRandomUser();
            }
            case "null"->{
                throw new InputMismatchException();
            }
        }
        for(Map.Entry<String, Object> entry : randomMap.entrySet()){
            requestSpecificationGiven.formParam(entry.getKey(), entry.getValue());
        }
    }

    @When("I send POST request to {string} endpoint")
    public void iSendPOSTRequestToEndpoint(String endPoint) {
        response = requestSpecificationGiven.when().post(endPoint);
        jp = response.jsonPath();
        validatableResponseThen = response.then();
        response.prettyPrint();
    }

    @And("the field value for {string} path should be equal to {string}")
    public void theFieldValueForPathShouldBeEqualTo(String path, String value) {
        validatableResponseThen.body(path,Matchers.is(value));
    }



    //-----------US_03_02-----------
    @Given("I logged in Library UI as {string}")
    public void i_logged_in_library_ui_as(String userType) {
        loginPage.login(userType);
    }
    @Given("I navigate to {string} page")
    public void i_navigate_to_page(String string) {
       base.booksPageButton.click();
    }
    @Then("UI, Database and API created book information must match")
    public void ui_database_and_api_created_book_information_must_match() {

        book.searchBox.sendKeys(randomMap.get("name").toString() + Keys.ENTER);
        BrowserUtils.waitFor(1);


        DB_Util.runQuery("select name,author,year,isbn from books where isbn=" + jp.getString("isbn"));
        Map<String,String> dataMap = DB_Util.getRowMap(1);


        DB_Util.assertMapDB(dataMap,randomMap);
        Assert.assertEquals(randomMap.get("name").toString(),book.result_name.getText());
        Assert.assertEquals(randomMap.get("author").toString(),book.result_author.getText());
        Assert.assertEquals(randomMap.get("year").toString(),book.result_year.getText());
        Assert.assertEquals(randomMap.get("isbn").toString(),book.result_isbn.getText());
    }


    //-----------US_04_01-----------
    @Then("created user information should match with Database")
    public void created_user_information_should_match_with_database() {
        DB_Util.runQuery("select * from users where id=" + jp.getString("id"));
        Map<String,String> dataMap = DB_Util.getRowMap(1);
        DB_Util.assertMapDB(dataMap,randomMap);
    }
    @Then("created user should be able to login Library UI")
    public void created_user_should_be_able_to_login_library_ui() {
        loginPage.login(randomMap.get("email").toString(), randomMap.get("password").toString());
        BrowserUtils.waitForVisibility(base.userName,20);
    }
    @Then("created user name should appear in Dashboard Page")
    public void created_user_name_should_appear_in_dashboard_page() {
        Assert.assertEquals(base.userName.getText(),randomMap.get("full_name"));
    }


    //-----------US_04-----------
    String token;
    @Given("I logged Library api with credentials {string} and {string}")
    public void i_logged_library_api_with_credentials_and(String email, String password) {
        token = LibraryUtility.getToken(email,password);
       requestSpecificationGiven.header("x-library-token", LibraryUtility.getToken(email,password));
    }
    @Given("I send token information as request body")
    public void i_send_token_information_as_request_body() {
        requestSpecificationGiven.formParam("token", token);
    }






}
