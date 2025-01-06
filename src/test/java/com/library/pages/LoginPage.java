package com.library.pages;

import com.library.utility.BrowserUtils;
import com.library.utility.ConfigurationReader;
import com.library.utility.Driver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {

    public LoginPage() {
        PageFactory.initElements(Driver.get(), this);
    }

    @FindBy (id = "inputEmail")
    public WebElement userEmail;

    @FindBy (id = "inputPassword")
    public WebElement userPassword;

    @FindBy (xpath = "//*[@id=\"login-form\"]/button")
    public WebElement signInButton;


    public void login(String role){
        switch(role){
            case "librarian"-> login(ConfigurationReader.getProperty("librarian_username"), ConfigurationReader.getProperty("librarian_password"));
            case "student" -> login(ConfigurationReader.getProperty("student_username"), ConfigurationReader.getProperty("student_password"));
            default -> throw new IllegalStateException("Unexpected value: " + role);
        }
    }

    public void login(String email, String password) {
        userEmail.sendKeys(email);
        userPassword.sendKeys(password);
        BrowserUtils.waitFor(1);
        signInButton.click();
    }







}
