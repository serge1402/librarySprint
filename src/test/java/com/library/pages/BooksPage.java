package com.library.pages;

import com.library.utility.Driver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class BooksPage extends BasePage {

    @FindBy(xpath = "//*[@class = 'form-control input-sm input-small input-inline']")
    public WebElement searchBox;

    @FindBy(xpath = "//*[@id=\"tbl_books\"]/tbody/tr/td[2]")
    public WebElement result_isbn;

    @FindBy(xpath = "//*[@id=\"tbl_books\"]/tbody/tr/td[3]")
    public WebElement result_name;

    @FindBy(xpath = "//*[@id=\"tbl_books\"]/tbody/tr/td[4]")
    public WebElement result_author;

    @FindBy(xpath = "//*[@id=\"tbl_books\"]/tbody/tr/td[6]")
    public WebElement result_year;


}
