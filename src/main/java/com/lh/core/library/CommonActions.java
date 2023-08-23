package com.lh.core.library;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import org.openqa.selenium.JavascriptExecutor;

import java.io.FileInputStream;
import java.util.Properties;


public class CommonActions {
    private Page page;
    Properties properties;
    public FileInputStream fis;

    public CommonActions() {

        if (properties == null) {
            try {
                properties = new Properties();
                fis = new FileInputStream(
                        System.getProperty("user.dir") + "/src/test/resources/ObjectRepository/Object.properties");
                properties.load(fis);
                //softAssertions = new SoftAssertions();
            } catch (Exception e) {
                e.printStackTrace();
                //Assert.fail();
            }
        }

    }
    public CommonActions(Page page) {

        this.page = page;
    }
    public void moveScrollDown() {

        //JavascriptExecutor js = (JavascriptExecutor) driver;
        //js.executeScript("window.scrollBy(0,250)");
        //page.evaluate("window.scrollTo(0, document.body.scrollHeight)");

    }

    public void moveScrollUp() {
        //JavascriptExecutor js = (JavascriptExecutor) driver;

        //js.executeScript("window.scrollBy(0,-500)");
    }

    public void scrollDownToElementView(String locator) {
        //page.waitForSelector(locator);
//        ElementHandle element = page.querySelector(locator);
//        if (element == null) {
//            throw new RuntimeException("Element not found");
//        }
//        element.scrollIntoViewIfNeeded();
        page.evaluate("window.scrollTo(0, 10000)");

        // Find element below viewport
        ElementHandle element = page.querySelector(locator);
        //ElementHandle element = page.waitForSelector("#my-element", new Page.WaitForSelectorOptions().setVisible(true));
        if (element != null) {
            element.scrollIntoViewIfNeeded();
            // Do something with the element
        }else{
            throw new RuntimeException("Element not found");
        }

    }

}
