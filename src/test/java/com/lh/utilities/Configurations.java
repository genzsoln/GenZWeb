package com.lh.utilities;

public class Configurations {

	// DriverPaths and Run on browser parameter
	public static final String ExecutionEnvnmt = "PROD"; // Valid values: QA, Stage, PROD
	public static final String BrowserName = "Chrome";


	// Application URL

	public final static String AppurlEnv1 = "";
	public final static String url = "https://genzsoln.com/";
	public final static String AppurlEnv2 = "";
//	public final static String Appurl_QA_LX_Fr_B2 = "https://qa-www.swiss.com/fr/en/homepage";
	// Test Data source path
	public final static String testDataResourcePath = "../src/test/java/com/TestData/";

	// Browser Stack configuration
	public static final String RunOnBrowserStack = "N";
	public static final String USERNAME = "";

	public static final String AUTOMATE_KEY = "5tW8jrFVdPxbpgUSvssc";

	public static final String URL_BS = "https://" + USERNAME + ":" + AUTOMATE_KEY
			+ "@hub-cloud.browserstack.com/wd/hub";
	public static boolean cloud = false;

	// Output Reports path
	public static final String reportPath = "./Reports/";

	// download file path
	public static String downloadPath = System.getProperty("user.dir") + "\\Downloads\\";

	// Take screenshots on run parameter settings.
	public static final String takeScreenshots = "Y";

	public final static String XrayConfigPath = "./src/test/java/com/lh/xray/xray_config.properties";

}
