package jp.kusumotolab.kgenprog.project.test;

import io.reactivex.Single;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.build.BuildResults;

/**
 * テスト実行インタフェース．<br>
 *
 * @author shinsuke
 */
public interface TestExecutor {

  /**
   * テスト実行を行う．<br>
   *
   * @param buildResults 実行対象のソースコードを保持するVariant
   * @return テスト結果
   */
  TestResults exec(final BuildResults buildResults);

  default Single<TestResults> execAsync(final Single<BuildResults> buildSingle) {
    return buildSingle.map(this::exec);
  }

  default void initialize() {
  }

  default void finish() {
  }
}
