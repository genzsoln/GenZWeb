package com.lh.steps;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.genz.xray.XRAY_CONFIG;
import com.genz.xray.Xray;
import com.lh.library.CommonActions;
import com.lh.runner.JunitRunner;
import com.lh.utilities.Configurations;
import com.lh.xray.Log;
import com.lh.xray.XrayHelper;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class GenericSteps {

	CommonActions commonActions;
	// String testStart = "";
	String testFinish = "";
	String XrayIssueKey = "";

	public GenericSteps(CommonActions con) {
		this.commonActions = con;
	}

	/**
	 * 
	 * @param s
	 * @throws Exception
	 *             Description Initialization before starting of each scenario
	 *             execution
	 */
	@Before
	public void before(Scenario s) throws Exception {

		if ((Configurations.RunOnBrowserStack).equals("Y")) {
			commonActions.initReports(s.getName() + "_" + System.getProperty("browser"));
		} else {
			commonActions.initReports(s.getName() + "_" + "chrome");
		}
		commonActions.setfeaturefilenameandsceanrio(s.getId(), s.getName());
		commonActions.setScenario(s);

		checkNewTest(s);
	}

	/**
	 * Description Closing the resources after execution of each scenario
	 * 
	 * @throws IOException
	 */
	@After
	public void after(Scenario s) {

		//commonActions.quit();

		//saveTestResultsToXray(s);

	}

	private void saveTestResultsToXray(Scenario s) {

		ZonedDateTime finishDateTime = ZonedDateTime.now();
		testFinish = finishDateTime.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		Log.info("Test Finish Time: " + testFinish);

		if (s.isFailed()) {
			Log.error("Test Failed!");
			JunitRunner.featureTestPassed = false;
			Xray.writeResultsForSingleTest(JunitRunner.ExecutionID, XrayIssueKey, XRAY_CONFIG.TEST_STATUS_FAIL,
					JunitRunner.testStart, testFinish);
		} else {
			if (JunitRunner.featureTestPassed == true) {
				Log.info("Test Passed!");
				Xray.writeResultsForSingleTest(JunitRunner.ExecutionID, XrayIssueKey, XRAY_CONFIG.TEST_STATUS_PASS,
						JunitRunner.testStart, testFinish);
			}
		}

	}

	private void checkNewTest(Scenario s) {
		XrayIssueKey = XrayHelper.getTestIdFromFileName(s.getId());

		if (!JunitRunner.currentXrayIssueKey.contains(XrayIssueKey)) {
			System.out.println("This is a new Feature!");
			JunitRunner.currentXrayIssueKey = XrayIssueKey;
			JunitRunner.featureTestPassed = true;
		}

	}

}
