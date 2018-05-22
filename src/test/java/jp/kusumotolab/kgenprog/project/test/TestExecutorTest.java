package jp.kusumotolab.kgenprog.project.test;

import static jp.kusumotolab.kgenprog.project.test.Coverage.Status.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URL;
import java.util.Arrays;

import org.junit.Test;

import jp.kusumotolab.kgenprog.project.ProjectBuilder;
import jp.kusumotolab.kgenprog.project.TargetProject;

public class TestExecutorTest {

	final static FullyQualifiedName buggyCalculator = new FullyQualifiedName("jp.kusumotolab.BuggyCalculator");
	final static FullyQualifiedName buggyCalculatorTest = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest");
	final static FullyQualifiedName util = new FullyQualifiedName("jp.kusumotolab.Util");
	final static FullyQualifiedName utilTest = new FullyQualifiedName("jp.kusumotolab.UtilTest");

	final static FullyQualifiedName test01 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test01");
	final static FullyQualifiedName test02 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test02");
	final static FullyQualifiedName test03 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test03");
	final static FullyQualifiedName test04 = new FullyQualifiedName("jp.kusumotolab.BuggyCalculatorTest.test04");

	final static FullyQualifiedName plusTest01 = new FullyQualifiedName("jp.kusumotolab.UtilTest.plusTest01");
	final static FullyQualifiedName plusTest02 = new FullyQualifiedName("jp.kusumotolab.UtilTest.plusTest02");
	final static FullyQualifiedName minusTest01 = new FullyQualifiedName("jp.kusumotolab.UtilTest.minusTest01");
	final static FullyQualifiedName minusTest02 = new FullyQualifiedName("jp.kusumotolab.UtilTest.minusTest02");
	final static FullyQualifiedName dummyTest01 = new FullyQualifiedName("jp.kusumotolab.UtilTest.dummyTest01");

	private TestResults generateTestResultsForExample01() throws Exception {
		final String rootDir = "example/example01";
		final String outDir = rootDir + "/_bin/";
		final TargetProject targetProject = TargetProject.generate(rootDir);
		new ProjectBuilder(targetProject).build(outDir);
		final TestExecutor executor = new TestExecutor(new URL[] { new URL("file:./" + outDir) });
		return executor.exec(Arrays.asList(buggyCalculator), Arrays.asList(buggyCalculatorTest));
	}

	private TestResults generateTestResultsForExample02() throws Exception {
		final String rootDir = "example/example02";
		final String outDir = rootDir + "/_bin/";
		final TargetProject targetProject = TargetProject.generate(rootDir);
		new ProjectBuilder(targetProject).build(outDir);
		final TestExecutor executor = new TestExecutor(new URL[] { new URL("file:./" + outDir) });
		return executor.exec(Arrays.asList(buggyCalculator, util), Arrays.asList(buggyCalculatorTest, utilTest));
	}

	@Test
	public void testTestResultsForExample01() throws Exception {
		final TestResults r = generateTestResultsForExample01();

		// example01で実行されたテストは4つのはず
		assertThat(r.getExecutedTestFQNs(), is(containsInAnyOrder(test01, test02, test03, test04)));

		// テストの成否はこうなるはず
		assertThat(r.getTestResult(test01).failed, is(false));
		assertThat(r.getTestResult(test02).failed, is(false));
		assertThat(r.getTestResult(test03).failed, is(true));
		assertThat(r.getTestResult(test04).failed, is(false));

		// test01()ではBuggyCalculatorのみが実行されたはず
		assertThat(r.getTestResult(test01).getExecutedTargetFQNs(), is(containsInAnyOrder(buggyCalculator)));

		// test01()で実行されたBuggyCalculatorのカバレッジはこうなるはず
		assertThat(r.getTestResult(test01).getCoverages(buggyCalculator).statuses, //
				is(contains(EMPTY, EMPTY, COVERED, EMPTY, COVERED, COVERED, EMPTY, NOT_COVERED, EMPTY, COVERED)));

	}

	@Test
	public void testTestResultsForExample02() throws Exception {
		final TestResults r = generateTestResultsForExample02();

		// example02で実行されたテストは10個のはず
		assertThat(r.getExecutedTestFQNs(), is(containsInAnyOrder( //
				test01, test02, test03, test04, //
				plusTest01, plusTest02, minusTest01, minusTest02, dummyTest01)));

		// テストの成否はこうなるはず
		assertThat(r.getTestResult(test01).failed, is(false));
		assertThat(r.getTestResult(test02).failed, is(false));
		assertThat(r.getTestResult(test03).failed, is(true));
		assertThat(r.getTestResult(test04).failed, is(false));

		// test01()ではBuggyCalculatorとUtilが実行されたはず
		final TestResult test01_result = r.getTestResult(test01);
		assertThat(test01_result.getExecutedTargetFQNs(), is(containsInAnyOrder(buggyCalculator, util)));

		// test01()で実行されたBuggyCalculatorのカバレッジはこうなるはず
		assertThat(test01_result.getCoverages(buggyCalculator).statuses, //
				is(contains(EMPTY, EMPTY, COVERED, EMPTY, COVERED, COVERED, EMPTY, NOT_COVERED, EMPTY, COVERED)));

		// plusTest01()ではBuggyCalculatorとUtilが実行されたはず
		final TestResult plusTest01_result = r.getTestResult(plusTest01);
		assertThat(plusTest01_result.getExecutedTargetFQNs(), is(containsInAnyOrder(buggyCalculator, util)));

		// plusTest01()で実行されたUtilのカバレッジはこうなるはず
		assertThat(plusTest01_result.getCoverages(util).statuses, //
				is(contains(EMPTY, EMPTY, NOT_COVERED, EMPTY, COVERED, EMPTY, EMPTY, EMPTY, NOT_COVERED, EMPTY, EMPTY,
						EMPTY, EMPTY, NOT_COVERED, NOT_COVERED)));
		// TODO 最後のNOT_COVERDだけ理解できない．謎．
	}

}