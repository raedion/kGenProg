package jp.kusumotolab.kgenprog.project.test;

import org.apache.commons.lang3.time.StopWatch;
import jp.kusumotolab.kgenprog.Configuration;
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

  /**
   * コンストラクタ．<br>
   *
   * @param config テスト実行に必要なプロジェクト設定情報
   */
  public LocalTestExecutor(final Configuration config) {
    this.config = config;
  }

  /**
   * 対象ソースコードのビルドとテストの実行を行う．<br>
   * ビルド失敗時はEmptyTestResultsを返す．<br>
   */
  @Override
  public TestResults exec(final BuildResults buildResults) {
    final TestThread testThread = new TestThread(buildResults, config.getTargetProject(),
        config.getExecutedTests(), config.getTestTimeLimitSeconds());
    testThread.run();

    return testThread.getTestResults();
  }
}
