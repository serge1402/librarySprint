package com.library.pages;

import com.library.utility.Driver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public abstract class BasePage {

    public BasePage() {
        PageFactory.initElements(Driver.get(),this);
    }

    @FindBy(xpath = "//*[@id=\"navbarDropdown\"]/span")
    public WebElement userName;

    @FindBy(xpath = "//*[@id=\"menu_item\"]/li[3]")
    public WebElement booksPageButton;



}
