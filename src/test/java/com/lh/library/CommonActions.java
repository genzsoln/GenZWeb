package com.lh.library;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.lh.xray.Log;
import com.microsoft.playwright.*;

import com.lh.reports.ExtentManager;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.Page;
import io.cucumber.datatable.DataTable;

import org.apache.log4j.Logger;
import org.assertj.core.api.SoftAssertions;
import org.junit.Assert;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.lh.utilities.Configurations;

import cucumber.api.Scenario;

public class CommonActions {
    private final static Logger LOGGER = Logger.getLogger(CommonActions.class);
    Playwright playwright;
    Browser browser;
    BrowserContext context;

    Page page;
    WebDriver driver;
    Exception e;
    public ExtentReports report;
    public ExtentTest scenario;
    Properties properties;
    public FileInputStream fis;
    public static String featurename;
    public static String scenarioname;
    public SoftAssertions softAssertions;
    public String downloadDir;

    public Scenario sc;

    public CommonActions() {

        if (properties == null) {
            try {
                properties = new Properties();
                fis = new FileInputStream(
                        System.getProperty("user.dir") + "/src/test/resources/ObjectRepository/Object.properties");
                properties.load(fis);
                softAssertions = new SoftAssertions();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        }

    }

    public Page getPage() {
        try {
            playwright = Playwright.create();
            BrowserType chromium = playwright.chromium();
            //BrowserType firefox = playwright.firefox();
            browser = chromium.launch(new BrowserType.LaunchOptions().setHeadless(false));
            context = browser.newContext(new Browser.NewContextOptions()
                    .setHttpCredentials("u192312", "Lufthansa@123").setRecordVideoDir(Paths.get(Configurations.reportPath)).setRecordVideoSize(1280, 800));
//            .setExtraHTTPHeaders(getModHeaderMap())
            // Add the custom initialization script
            context.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
            page = context.newPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }

    private static Map<String, String> getModHeaderMap() {
        Map<String, String> headers = new HashMap<>();
        headers.put("ModHeader", "x-access4tcs=moNNBHkmp588itusa3t780pmnztr8jC");
        headers.put("ModHeader", "x-paytest-automation=136c2565ca01d2c31ce91347f68a5c11");
        return headers;
    }

    public void handle_Privacy_Settings_Page() {
        try {
            //page.locator(Privacy_setting_accept).click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Agree").setExact(true)).click();
            System.out.println("Privacy setting page handled : successfully");
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public String getProductName(String tenant) {
//        String productName = page.textContent(properties.getProperty("home_page_title"));
        String productName = page.textContent("//h1[text()='" + tenant + "']");
        page.screenshot();
        System.out.println("Page title : " + productName);
        return productName;
    }

    public Locator getElementByLocator(String objectKey) throws IllegalArgumentException, Exception {
        Locator el = null;
//        ElementHandle el = null;
        String[] xpaths = null;
        try {
//            e = page.locator(properties.getProperty(objectKey));
            String xpathExpressions = properties.getProperty(objectKey);
            if (xpathExpressions.contains("|")) {
                xpaths = xpathExpressions.split("\\|");
                // Wait for each element and perform some action
                for (String xpath : xpaths) {
//                    el = page.querySelector(xpath);
                    el = page.locator(xpath);
                    if (el.isHidden()) {
                        scrollDownFromCurrentPosition();
                    } else {
                        return el;
                    }
                }
            } else {
                el = page.locator(xpathExpressions);
                el.scrollIntoViewIfNeeded();
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            System.out.println("\r\n" + "Locator key missing in object repository file: " + objectKey);
            logAssert_Fail("\r\n" + "Locator key missing in object repository file: " + objectKey);
        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
            System.out.println("Element not present on the page: " + objectKey);
            logAssert_Fail("\r\n" + "Element not present on the page: " + objectKey);
        } catch (ElementNotInteractableException ex) {
            ex.printStackTrace();
            logAssert_Fail("\r\n" + "Element not visible on the page: " + objectKey);
        } catch (InvalidSelectorException ex) {
            ex.printStackTrace();
            logAssert_Fail("Invalid xpath selector");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Object identification failed: " + objectKey);
            logAssert_Fail("\r\n" + "Object identification failed: " + objectKey);
            throw ex;
        }
        return el;
    }

    public ElementHandle getElementHandle(String objectKey) throws IllegalArgumentException {
        ElementHandle element = null;
        try {
            element = page.querySelector(properties.getProperty(objectKey));
            while (element.isHidden()) {
                scrollDownFromCurrentPosition();
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            System.out.println("\r\n" + "Locator key missing in object repository file: " + objectKey);
            logAssert_Fail("\r\n" + "Locator key missing in object repository file: " + objectKey);
        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
            System.out.println("Element not present on the page: " + objectKey);
            logAssert_Fail("\r\n" + "Element not present on the page: " + objectKey);
        } catch (ElementNotInteractableException ex) {
            ex.printStackTrace();
            logAssert_Fail("\r\n" + "Element not visible on the page: " + objectKey);
        } catch (InvalidSelectorException ex) {
            ex.printStackTrace();
            logAssert_Fail("Invalid xpath selector");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Object identification failed: " + objectKey);
            logAssert_Fail("\r\n" + "Object identification failed: " + objectKey);
        }
        return element;
    }

    public List<ElementHandle> getElementsHandle(String objectKey) throws IllegalArgumentException {
//        ElementHandle element = null;
        List<ElementHandle> elements = null;
        try {
            elements = page.querySelectorAll(properties.getProperty(objectKey));
//            element = page.querySelector(properties.getProperty(objectKey));
//            while (element.isHidden()) {
//                scrollDownFromCurrentPosition();
//            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            System.out.println("\r\n" + "Locator key missing in object repository file: " + objectKey);
            logAssert_Fail("\r\n" + "Locator key missing in object repository file: " + objectKey);
        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
            System.out.println("Element not present on the page: " + objectKey);
            logAssert_Fail("\r\n" + "Element not present on the page: " + objectKey);
        } catch (ElementNotInteractableException ex) {
            ex.printStackTrace();
            logAssert_Fail("\r\n" + "Element not visible on the page: " + objectKey);
        } catch (InvalidSelectorException ex) {
            ex.printStackTrace();
            logAssert_Fail("Invalid xpath selector");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Object identification failed: " + objectKey);
            logAssert_Fail("\r\n" + "Object identification failed: " + objectKey);
        }
        return elements;
    }

    public void selectValFromDropDown(String dataVal, String objectKey) {
        // Wait for the drop-down element to be present on the page

        page.waitForSelector(properties.getProperty(objectKey));
        page.locator(properties.getProperty(objectKey)).scrollIntoViewIfNeeded();
        // Select the desired value from the drop-down element
        if (!objectKey.equals("payment_option")) {
            ElementHandle ele = page.querySelector(properties.getProperty(objectKey));
            ele.click();
        }
        page.locator(properties.getProperty(dataVal + "_option")).click();
    }

    public String getObjectKey(String locator) {
        return (locator.replaceAll("\"", ""));
    }

    public void selectCheckBox(String objectKey) {
        String objKey = objectKey + "_checkbox";
        Locator objVal;
        try {
            objVal = getElementByLocator(objKey);
            if (!objVal.isChecked()) {
                Thread.sleep(1000);
                objVal.click();
            }
        } catch (Exception ex) {
            logAssert_Fail("Clear checkbox failed");
        }
    }

    public void enterText(String objectKey, String textToEnter) {

        try {
            getElementByLocator(objectKey).scrollIntoViewIfNeeded();
//            getElementByLocator(objectKey).click();
//            getElementByLocator(objectKey).clear();
            if (objectKey.equals("cardNumberField")) {
//                page.evaluate("window.scrollBy(0, -window.innerHeight/2)");
                page.locator("//div[@id='irc-payment-options-ITEM_PAYMENT_FLIGHTS-panel']").click();
            }
            getElementByLocator(objectKey).type(textToEnter);
//            getElementByLocator(objectKey).fill(textToEnter);
        } catch (Exception e) {
            e.getCause();
            logFailStatus(e.getMessage());
        }
    }

    public void performCardAuthentication(String objectKey, String textToEnter) throws InterruptedException {
        // Switch to the iframe by its selector
//        ElementHandle iframe = page.querySelector(objectKey+"_iframe");
//        Page.Frame frame = page.frame(String.valueOf(iframe));
        ElementHandle f = page.querySelector(properties.getProperty(objectKey + "_iframe"));
        f.scrollIntoViewIfNeeded();
        FrameLocator frame = page.frameLocator(properties.getProperty(objectKey + "_iframe"));
        // Find the element inside the iframe by its selector
        frame.locator(properties.getProperty(objectKey + "_text")).fill(textToEnter);
//        ElementHandle inputField = frame.querySelector("#my-input-field");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        frame.locator(properties.getProperty(objectKey + "_btn")).click();

//        waitForPageLoad();
//        page.waitForTimeout(40000);
//        Thread.sleep(180000);
//        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(60000));

    }

    public void enterTextSelectOption(String objectKey, String textToEnter) {
        String selector_Obj_Key = objectKey + "_selector";
        try {
            getElementByLocator(objectKey).clear();
            ElementHandle e = getElementHandle(objectKey);
//			getElementByLocator(objectKey).type(textToEnter);
            e.type(textToEnter);
            if (objectKey.equals("countryCode")) {
                page.querySelector("//span[@class='mat-option-text']//span[contains(text(), '" + textToEnter + "')]").click();
            } else {
                Keyboard keyboard = page.keyboard();
                keyboard.press("ArrowDown");
                page.waitForTimeout(2000);
                keyboard.press("Enter");
//                ElementHandle element = page.querySelector("//div[@class='selectable-result-list selectable-result-list-undefined d-none']//ul[@role='listbox']");
//                element.selectOption(textToEnter + ", all flights", new ElementHandle.SelectOptionOptions().setTimeout(2000));
//                ElementHandle dynamicList = page.querySelector(objectKey+"_selector");
//                List<ElementHandle> options = dynamicList.querySelectorAll("option");

                // Select the desired value from the dynamic list
//                String desiredValue = textToEnter + ", all flights";
//                for (ElementHandle option : options) {
//                    String optionText = option.innerText();
//                    if (optionText.equals(desiredValue)) {
////                        option.selectOption();
//                        option.click();
//                        break;
//                    }
//                }

//                page.getByRole(AriaRole.LISTBOX, new Page.GetByRoleOptions().setName(textToEnter + ", all flights").setExact(true)).click();
            }
//			page.locator(properties.getProperty(selector_Obj_Key)).nth(0).click();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(page.content());
        }
    }

    public void click(String objectKey) {
        try {
            Thread.sleep(2000);
            getElementByLocator(objectKey).click();
            logInfoStatus("Info | Clicked on : " + objectKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickOnIframeElement(String objectKey) {
        try {
            Thread.sleep(2000);
            ElementHandle f = page.querySelector(properties.getProperty(objectKey + "_iframe"));
            f.scrollIntoViewIfNeeded();
            FrameLocator frame = page.frameLocator(properties.getProperty(objectKey + "_iframe"));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            frame.locator(properties.getProperty(objectKey)).click();

            logInfoStatus("Info | Clicked on : " + objectKey);
//           Thread.sleep(15000);
            page.waitForTimeout(60000);
            page.waitForLoadState(LoadState.LOAD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectDate(String objectKey, String date) {
        String selector = "";
        String dropdown_Obj = "";
        // get current month
        String currentMonth = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        System.out.println("Current Month : " + currentMonth);
        String[] d = date.split(" ");
        System.out.println(d[0] + " " + d[1]);
        String day = d[0];
        String month = d[1];
        String year = d[2];

        dropdown_Obj = "//span[@class='dropdown-content mb-0' and text()='" + currentMonth + "']";
        page.locator(dropdown_Obj).click();
        //selector = "//div[@class='DayPickerMonthLabel' and text()='" + month + "']";
        selector = "//div[@class='selectable-result-list selectable-result-list-calendar']//ul//li//div[text()='" + month + "']";
        System.out.println("Month selector : " + selector);
        page.locator(selector).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(date)).click();
    }

    public void select_Seat(String data, String objectKey) {
        try {
            List<ElementHandle> elements = getElementsHandle(objectKey);
            for (ElementHandle el : elements) {
                el.scrollIntoViewIfNeeded();
                System.out.println(el.textContent());
                String title = el.getAttribute("title");
                System.out.println("Title Seat no : " + title);
                el.click();
//                page.querySelector(properties.getProperty("Select_seat")).click();
                getElementByLocator("Select_seat").click();
                break;
            }
        } catch (Exception e) {
            logInfoStatus(e.getMessage());
            System.out.println("Error in seat selection : " + e.getMessage());
        }
    }
    public void clickAndThenSelectValFromDropDownList(String objectKey, String data){
        try{
            click(objectKey);
            selectFromDropDownList(objectKey, data);
        }catch(Exception e){
            String errMsg = String.format("Passenger : enter Data failed");
            LOGGER.info(errMsg + " : " + e);
        }
    }

    public void selectFromDropDownList(String objectKey, String data) throws Exception {
        try {
            //dynamic xpath
            if (objectKey.equals("fare_Option")) {
                page.querySelector("//div[text()='" + data + "']//parent::div[@role='radio']//following-sibling::div[@class='content']").click();
            } else if (objectKey.equals("fare_class")) {
                page.querySelector("//div[@class='upsell-premium-row-pres has-indicator-ribbons first-row']//div[@class='refx-body-2 price-card-title-label ng-star-inserted' and text()='" + data + "']").click();
//                page.querySelector("//div[@class='price-card-container fare-family-STWWM2NC0E cabin-eco']").click();
            } else if (objectKey.equals("seat_Map")) {
                if (featurename.contains("B2")) {
//                    page.querySelector("//button[@class='seat-button selectable-button seat-characteristic-chargeable available chargeable-seat ng-star-inserted']//span[@class='cdk-visually-hidden seat-number ng-star-inserted' and contains(text(), '"+data+"')]").click();
                    page.querySelector("//div[@class='seatmap-table-content seatmap-table-content-seat']//button[@title='" + data + "']").click();
                    page.querySelector(properties.getProperty("Select_seat")).click();
                } else {
                    page.querySelector("//td[@role='gridcell' and @data-seat-nr='" + data + "']").click();
                }
            } else if (objectKey.equals("Title_dropdown")) {
                if (featurename.contains("B2")) {
                    page.querySelector("//span[@class='mat-option-text']//span[text()='" + data + "']").click();
                } else {
                    page.querySelector("//ul[@class='selectbox items']//li[@data-value='" + data + "']").click();
                }
            } else if (objectKey.equals("CCard_Type")) {
                page.querySelector("//li[@class='mdc-list-item']//span[contains(text(), '" + data + "')]").click();
            } else if (objectKey.equals("cardType_options")) {
                page.querySelector("//ul[@class='selectbox items']//li[@role='option']//div[text()='" + data + "']").click();
            } else if (objectKey.equals("Country")) {
                page.querySelector("//li[@class='mdc-list-item ']//span[@class='mdc-list-item__text' and contains(text(), '" + data + "')]").click();
            } else {
                page.querySelector("//ul[@role='listbox']//li//div[text()='" + data + "']").click();
            }
        } catch (Exception e) {
            logAssert_Fail("Select by visble text failed on: " + objectKey);
            throw e;
        }
    }

    public void scrollDownFromCurrentPosition() {
        page.evaluate("window.scrollBy(0, 500)");
    }

    public void waitForPageLoad() {
        page.waitForTimeout(15000);
        page.waitForLoadState(LoadState.LOAD);
    }

    public void waitForDOMLoadStateWithOption() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(15000));
    }

    public void verifyPresenceOfElement(String locator) throws Exception {
        page.waitForLoadState(LoadState.LOAD);

        if (getElementByLocator(locator).isVisible()) {
            System.out.println("Element is visible on page : " + locator);
            System.out.println("Captured value : " + getElementByLocator(locator).textContent());
        } else {
            System.out.println("Element is not visible on page : " + locator);
        }
    }

    public void verifyPresenceOfElementWithLoadStateWait(String locator) throws Exception {
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(60000));
//        page.waitForSelector(properties.getProperty(locator));
//        page.waitForSelector(properties.getProperty(locator), new Page.WaitForSelectorOptions().setState(WaitForSelectorState.valueOf("stable")).setTimeout(5000));

        if (getElementByLocator(locator).isVisible()) {
            System.out.println("Element is visible on page : " + locator);
        } else {
            System.out.println("Element is not visible on page : " + locator);
        }
    }

    public void getTextFromElement(String objKey) {
        String text = null;
        try {
            getElementByLocator(objKey).scrollIntoViewIfNeeded();
            if (getElementByLocator(objKey).isVisible()) {
                text = getElementByLocator(objKey).innerText();
            } else {
                Log.info("Element : " + objKey + "is not visible on the page");
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        System.out.println("Captured text for " + objKey + " : " + text);
    }

    public void clickAndWaitForPageLoad(String objectKey) {
        Locator element = null;
        try {
            Thread.sleep(2000);
            element = getElementByLocator(objectKey);
            element.scrollIntoViewIfNeeded();
            element.click();
            logInfoStatus("Info | Clicked on : " + objectKey);
//            Thread.sleep(15000);
            page.waitForTimeout(15000);
            page.waitForLoadState(LoadState.LOAD);
//            if (objectKey.equals("Continue_to_payment")) {
//                page.setExtraHTTPHeaders(getModHeaderMap());
//            }
            page.evaluate("window.scrollTo(0, 0)");
//               page.waitForLoadState(LoadState.LOAD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectFromDropDownByValue(String objectKey, String dataVal) {
        String objKeyMonth = objectKey + "_month";
        String objKeyYear = objectKey + "_year";
        String month = "";
        String year = "";
        if (dataVal.contains("/")) {
            String[] data = dataVal.split("/");
            month = data[0];
            year = data[1];
            System.out.println("Month : " + month + "Year : " + year);

        }
        try {
            page.selectOption(properties.getProperty(objKeyMonth), month);
            page.selectOption(properties.getProperty(objKeyYear), year);
        } catch (Exception e) {
            logAssert_Fail("Select by value failed " + objectKey);
        }
    }

    public void enterAddress(String data, String objKey) {
        int i = 0;
        ;
        String[] postObjKey = {"_line1", "_city", "_postalCode"};
        String[] addressValues = data.split("/");
        for (String addressVal : addressValues) {
            System.out.println("Address splited : " + addressVal);
            System.out.println("ObjectKey : " + postObjKey[i]);
            enterText(objKey + postObjKey[i], addressValues[i]);
            i++;
        }

    }

    public void captureConfirmationDetails(String data, String objKey) {
        String[] dataKeys = new String[0];
        if (data.contains("/")) {
            dataKeys = data.split("/");
        } else {
            dataKeys = new String[]{data};
        }
        for (String dataKey : dataKeys) {
            getTextFromElement(objKey + "_" + dataKey);
        }
        takeSceenShot1();
    }

    public void captureBookingConfirmationDetails(DataTable dataTable) {
        Map<String, String> details = dataTable.asMap(String.class, String.class);
        for (Map.Entry<String, String> entry : details.entrySet()) {
            Log.info(entry.getKey());
            Log.info(entry.getValue());
            String Key = entry.getKey();
            String data = entry.getValue();
            String[] objKeys = new String[0];
            if (data.contains("/")) {
                objKeys = data.split("/");
            } else {
                objKeys = new String[]{data};
            }
            for (String objKey : objKeys) {
                if (objKey.equals("itenary")) {
                    click("Booking_" + objKey);
                    takeSceenShot1();
                } else {
                    getTextFromElement("Booking_" + objKey);
                }
            }
        }


    }

    public void enterPassengerDetails(DataTable dataTable) {
        try {
            Map<String, String> details = dataTable.asMap(String.class, String.class);
            for (Map.Entry<String, String> entry : details.entrySet()) {
                Log.info(entry.getKey());
                Log.info(entry.getValue());
                String objKey = entry.getKey();
                String data = entry.getValue();
                if(!(page.locator("//div[@class='refx-caption profile-name']").isVisible())){
                    if (objKey.equals("Title_dropdown")) {
                        clickAndThenSelectValFromDropDownList(objKey, data);
                    } else if(objKey.equals("firstName") || objKey.equals("lastName") || objKey.equals("emailAddress")){
                        enterText(objKey, data);
                    }
                }
                if (objKey.equals("countryCode")) {
                    enterTextSelectOption(objKey, data);
                }else if (objKey.equals("phoneNumber")) {
                    enterText(entry.getKey(), entry.getValue());
                }
                System.out.println("For loop iteration completed ");
            }
        } catch (Exception e) {
            String errMsg = String.format("Passenger : enter Data failed");
            LOGGER.info(errMsg + " : " + e);
        }

    }


    public void enterCreditCardDetails(DataTable dataTable) {
        try {
            Map<String, String> details = dataTable.asMap(String.class, String.class);
            for (Map.Entry<String, String> entry : details.entrySet()) {
                Log.info(entry.getKey());
                Log.info(entry.getValue());
                String objKey = entry.getKey();
                String data = entry.getValue();
                //Logged in user
                if(page.locator("//span[@class='mdc-button__label profile-label']").isVisible()){
                    if (objKey.equals("CCard_Type")) {
                        click("saved_Payment_Method");
                    }else if(objKey.equals("Security_Code")) {
                        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Security Code*")).fill(data);
                    }
                }else {
                    if (objKey.equals("CCard_Type")) {
                        click(objKey);
                        selectFromDropDownList(objKey, data);
                    } else if (objKey.equals("Card_Expiry")) {
                        String[] date = data.split("/");
                        enterText(objKey + "_month", date[0]);
                        enterText(objKey + "_year", date[1]);
                    } else if (objKey.equals("Country")) {
                        click(entry.getKey());
                        selectFromDropDownList(entry.getKey(), entry.getValue());
                        System.out.println("Country selected : " + entry.getValue());
                    } else if (objKey.equals("Card_number")) {
                        Locator.TypeOptions options = new Locator.TypeOptions().setDelay(100).setTimeout(5000);
                        getElementByLocator(entry.getKey()).scrollIntoViewIfNeeded();
                        getElementByLocator(entry.getKey()).type(entry.getValue(), options);
                        System.out.println("Card Number selected : " + entry.getValue());
                    } else if (objKey.equals("Security_Code")) {
                        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Security Code*")).fill(data);
                    } else {
                        enterText(entry.getKey(), entry.getValue());
                    }
                }
                System.out.println("For loop iteration completed ");
            }
        } catch (Exception e) {
            String errMsg = String.format("CC : enter Data failed");
            LOGGER.info(errMsg + " : " + e);
        }

    }

    public void enterDetailsInForm(String formType, DataTable dataTable) {
        try {
            if (formType.contains("Registration")) {
                enterRegistrationDetails(dataTable);
            } else if (formType.contains("Personal")) {
                enterPersonalDetails(dataTable);
            }
        } catch (Exception e) {
            String errMsg = String.format("CC : enter Data failed");
            LOGGER.info(errMsg + " : " + e);
        }

    }

    public void enterRegistrationDetails(DataTable dataTable) {
        try {
            Map<String, String> details = dataTable.asMap(String.class, String.class);
            for (Map.Entry<String, String> entry : details.entrySet()) {
                Log.info(entry.getKey());
                Log.info(entry.getValue());
                String objKey = entry.getKey();
                String data = entry.getValue();

                enterText(entry.getKey(), entry.getValue());
                System.out.println("For loop iteration completed ");
            }
        } catch (Exception e) {
            String errMsg = String.format("Registration : enter Data failed");
            LOGGER.info(errMsg + " : " + e);
        }

    }

    public void enterPersonalDetails(DataTable dataTable) {
        try {
            Map<String, String> details = dataTable.asMap(String.class, String.class);
            for (Map.Entry<String, String> entry : details.entrySet()) {
                Log.info(entry.getKey());
                Log.info(entry.getValue());
                String objKey = entry.getKey();
                String data = entry.getValue();
                if (objKey.equals("Salutation")) {
                    if (data.contains("Mr")) {
                        objKey = objKey + "_Male";
                    } else {
                        objKey = objKey + "_Female";
                    }
                    click(objKey);
                } else if (objKey.contains("Date")) {
                    if (data.contains("/")) {
                        String[] date = {"date", "month", "year"};
                        String[] d = data.split("/");
                        System.out.println(d[0] + " " + d[1]);
                        String day = d[0];
                        String month = d[1];
                        String year = d[2];
                        for (int i = 0; i < 3; i++) {
                            objKey.replace("[^_]*", date[i]);
                            System.out.println("replaced String : "+ objKey);
                            enterText(objKey, d[i]);
                        }
                        //
                    }
                }else{
                    enterText(entry.getKey(), entry.getValue());
                }
            }
            System.out.println("For loop iteration completed ");

        } catch (Exception e) {
            String errMsg = String.format("Registration : enter Data failed");
            LOGGER.info(errMsg + " : " + e);
        }

    }

    public void enterLoginCredentials(DataTable dataTable) {
        try {
            Map<String, String> details = dataTable.asMap(String.class, String.class);
            for (Map.Entry<String, String> entry : details.entrySet()) {
                Log.info(entry.getKey());
                Log.info(entry.getValue());
                String objKey = entry.getKey();
                String data = entry.getValue();
                if(objKey.equals("Email_address")){
                    enterText(entry.getKey(), entry.getValue());
                    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).click();
                } else if (objKey.equals("password")) {
                    enterText(entry.getKey(), entry.getValue());
                }
                System.out.println("For loop iteration completed ");
            }
        } catch (Exception e) {
            String errMsg = String.format("Login : enter Data failed");
            LOGGER.info(errMsg + " : " + e);
        }

    }

    /**
     * @param errMsg Description Common function to fail the report and stop execution
     */
    public void logAssert_Fail(String errMsg) {
        // fail in extent reports

        scenario.log(Status.FAIL, errMsg);
        if ((Configurations.takeScreenshots).equalsIgnoreCase("Y")) {
            takeSceenShot1();
        }
        // take screenshot and put in repots
        // fail in cucumber as well
        Assert.fail();
        // try {
        // throw new NoSuchFieldException();
        // } catch (Exception e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        //
    }

    /**
     * Description Reporting function to message the information step
     */
    public void logInfoStatus(String msg) {
        scenario.log(Status.INFO, msg);
    }

    /**
     * @param msg Description Reporting function to pass the step
     */
    public void logPassStatus(String msg) {
        scenario.log(Status.PASS, msg);

        softAssertions.assertThat(true);

        // assertEquals(true, true);
    }


    /**
     * @param msg Description Reporting function to fail the step and continue
     *            execution
     */
    public void logFailStatus(String msg) {
        scenario.log(Status.FAIL, msg);
        softAssertions.assertThat(false);
        takeSceenShot1();
        try {
            throw new NoSuchFieldError();
        } catch (Exception e) {

        }
    }

    /**
     * Description Common function to take the screenshots of failure steps
     */

    public void takeSceenShot1() {
        if ((Configurations.takeScreenshots).equals("Y")) {
            Date d = new Date();
            try {

                String screenshotFile = d.toString().replace(":", "_").replace(" ", "_") + ".png";
                // take screenshot
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                Files.write(Paths.get(ExtentManager.screenshotFolderPath + screenshotFile), screenshot);

                // get the dynamic folder name
//                FileUtils.copyFile(srcFile, new File(ExtentManager.screenshotFolderPath + screenshotFile));
                String PathofScreenShot = System.getProperty("user.dir") + "/" + ExtentManager.screenshotFolderPath
                        + screenshotFile;
                // put screenshot file in reports
                scenario.info("Screenshot", MediaEntityBuilder.createScreenCaptureFromPath(PathofScreenShot).build());
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail();
            }
        }
    }


    /**
     *
     * @param scenarioName
     *            Description Common function to initialize the reports
     */
    public void initReports(String scenarioName) {
        try {
            report = ExtentManager.getInstance(Configurations.reportPath);
            scenario = report.createTest(scenarioName);
            scenario.log(Status.INFO, "Starting " + scenarioName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw e;
        }
    }

    public void setfeaturefilenameandsceanrio(String id, String name) {
        featurename = id;
        String[] d = featurename.split("/features/");
        // System.out.println(d[0] + " " + d[1]);
        String[] d2 = d[1].split(".feature");
        // System.out.println(d2[0]);
        featurename = d2[0];
        scenarioname = name;
    }

    public void setScenario(Scenario s) {
        sc = s;
    }


    /**
     * Description Common function for quitting the browser and reports.
     */
    public void quit() {
        takeSceenShot1();
        if (report != null)
            report.flush();
        context.close();
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////
}
