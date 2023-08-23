package com.lh.steps;

import com.lh.library.CommonActions;
import com.lh.utilities.Configurations;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import org.junit.Assert;
import org.openqa.selenium.ElementNotInteractableException;

public class stepDefination {
    CommonActions commonactions;
    Page page;

    public stepDefination(CommonActions commonactions) {
        this.commonactions = commonactions;
    }

    @Before
    public void before(Scenario s) throws Exception {

        try {
            page = commonactions.getPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ((Configurations.RunOnBrowserStack).equals("Y")) {
            commonactions.initReports(s.getName() + "_" + System.getProperty("browser"));
        } else {
            commonactions.initReports(s.getName() + "_" + "chrome");
        }
        commonactions.setfeaturefilenameandsceanrio(s.getId(), s.getName());
        commonactions.setScenario(s);
    }

    @After
    public void after(Scenario s)
    {
        commonactions.quit();
    }


    @Given("User is on the Genz homepage")
    public void user_is_on_the_Genz_homepage() {
        try {
            page.navigate(Configurations.url);
            page.waitForLoadState();
            System.out.println("GenZ url loaded : successfully");
            //commonactions.handle_Privacy_Settings_Page();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Then("Homepage should be accessible")
    public void homepage_should_be_accessible() {
        String expectedTitle = "Gen Z Solutions Pune | Consulting | Solutions | Outsourcing Delhi";
        String pageTitle = page.title();
        //page.close();
        Assert.assertTrue(pageTitle.contains(expectedTitle));
        System.out.println("Homepage accessible : successfully");
    }

    @When("User clicks on the About Us link")
    public void user_clicks_on_the_about_us_link()
    {
        page.locator("(//a[text()='Contact Us'])[2]").click();
        System.out.println("Clicked on Contact Us tab");
    }

    @Then("About Us page should be accessible")
    public void about_Us_page_should_be_accessible()
    {
        String expectedTitle = "Mail us for Information Pune | Call +91 20 71330356";
        String pageTitle = page.title();
        //page.close();
        Assert.assertTrue(pageTitle.contains(expectedTitle));
        System.out.println("Contact Us page accessible : successfully");
    }



}


