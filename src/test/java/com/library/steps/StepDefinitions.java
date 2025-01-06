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
        System.out.println("The message" + jp.getString(path));
        Assert.assertTrue(jp.getString(path).contains(value));
    }



    //-----------US_03_02-----------
    @Given("I logged in Library UI as {string}")
    public void i_logged_in_library_ui_as(String userType) {
        LoginPage loginPage = new LoginPage();
        loginPage.login(userType);
    }

    @Given("I navigate to {string} page")
    public void i_navigate_to_page(String string) {
       BasePage base = new BooksPage();
       base.booksPageButton.click();
    }
    @Then("UI, Database and API created book information must match")
    public void ui_database_and_api_created_book_information_must_match() {

        String nameAPI = randomMap.get("name").toString();
        String authorAPI = randomMap.get("author").toString();
        String yearAPI = randomMap.get("year").toString();
        String isbnAPI = randomMap.get("isbn").toString();


        BooksPage book = new BooksPage();
        book.searchBox.sendKeys(randomMap.get("name").toString() + Keys.ENTER);

        BrowserUtils.waitFor(1);

        String nameUI = book.result_name.getText();
        String authorUI = book.result_author.getText();
        String yearUI = book.result_year.getText();
        String isbnUI = book.result_isbn.getText();

        DB_Util.runQuery("select name,author,year,isbn from books where isbn='"+isbnAPI+"'");
        Map<String,String> dataMap = DB_Util.getRowMap(1);

        Assert.assertEquals(nameAPI,dataMap.get("name"));
        Assert.assertEquals(authorAPI,dataMap.get("author"));
        Assert.assertEquals(yearAPI,dataMap.get("year"));
        Assert.assertEquals(isbnAPI,dataMap.get("isbn"));

        Assert.assertEquals(nameAPI,nameUI);
        Assert.assertEquals(authorAPI,authorUI);
        Assert.assertEquals(yearAPI,yearUI);
        Assert.assertEquals(isbnAPI,isbnUI);
    }


    //-----------US_04_01-----------
    @Then("created user information should match with Database")
    public void created_user_information_should_match_with_database() {
        DB_Util.runQuery("select full_name,email,user_group_id,status,start_date,end_date,address from users where full_name=" + randomMap.get("user_id") + "and email=" +randomMap.get("email"));
        Map<String,String> dataMap = DB_Util.getRowMap(1);
        DB_Util.assertMapDB(dataMap,randomMap);
    }
    @Then("created user should be able to login Library UI")
    public void created_user_should_be_able_to_login_library_ui() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(randomMap.get("email").toString(), randomMap.get("password").toString());
    }
    @Then("created user name should appear in Dashboard Page")
    public void created_user_name_should_appear_in_dashboard_page() {
        BasePage base = new BooksPage();
        Assert.assertEquals(base.userName.getText(),randomMap.get("name").toString());
    }







}
