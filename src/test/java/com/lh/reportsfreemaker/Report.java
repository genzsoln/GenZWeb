package com.lh.reportsfreemaker;

import com.genz.xray.ObjTestCoverage;
import com.genz.xray.ObjTestExecution;

public interface Report {
	/**
	 * Generates the report
	 *
	 * @return report file name generated
	 * @throws Exception In case the report couldn't be generated
	 */
	String generateReport() throws Exception;

}
