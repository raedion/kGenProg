package jp.kusumotolab.kgenprog.project.test;

import org.apache.commons.lang3.time.StopWatch;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.build.BuildResults;
import jp.kusumotolab.kgenprog.project.build.ProjectBuilder;

/**
 * junitテストをローカルマシン上で実行する．<br>
 * コンストラクタで指定されたConfiguration情報に基づき，指定Variantのテストを実行する．<br>
 *
 * TestThreadクラスのラッパークラスである．<br>
 *
 * @author shinsuke
 */
public class LocalTestExecutor implements TestExecutor {

  private final Configuration config;
  private final ProjectBuilder projectBuilder;
  private double buildTime;
  private long buildCount;
  private long trybuildCount;

  /**
   * コンストラクタ．<br>
   *
   * @param config テスト実行に必要なプロジェクト設定情報
   */
  public LocalTestExecutor(final Configuration config) {
    this.config = config;
    projectBuilder = new ProjectBuilder(config.getTargetProject());
    this.buildTime = 0;
    buildCount = 0;trybuildCount=0;
  }

  /**
   * 対象ソースコードのビルドとテストの実行を行う．<br>
   * ビルド失敗時はEmptyTestResultsを返す．<br>
   */
  @Override
  public TestResults exec(final Variant variant) {
    trybuildCount++;
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    if (!generatedSourceCode.isGenerationSuccess()) {
      return new EmptyTestResults("build failed.");
    }

    final StopWatch stopWatch = StopWatch.createStarted();
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);
    stopWatch.stop();
    buildTime += stopWatch.getTime();
    buildCount++;
    final TestThread testThread = new TestThread(buildResults, config.getTargetProject(),
        config.getExecutedTests(), config.getTestTimeLimitSeconds());
    testThread.run();

    return testThread.getTestResults();
  }

  public double getBuildTime() {
    return buildTime;
  }
  public long getBuildCount() {
    System.out.println("Try Build Count: " + trybuildCount);
    return buildCount;
  }
}
