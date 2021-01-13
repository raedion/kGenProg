package jp.kusumotolab.kgenprog.project.test;

import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.build.BuildResults;
import jp.kusumotolab.kgenprog.project.build.EmptyBuildResults;
import jp.kusumotolab.kgenprog.project.build.ProjectBuilder;
import java.util.List;
import java.util.Collections;

/**
 * junitテストをローカルマシン上で実行する．<br>
 * コンストラクタで指定されたConfiguration情報に基づき，指定Variantのテストを実行する．<br>
 *
 * TestThreadクラスのラッパークラスである．<br>
 *
 * @author shinsuke
 */
public class LocalBuildExecutor implements BuildExecutor {

  private final ProjectBuilder projectBuilder;

  /**
   * コンストラクタ．<br>
   *
   * @param config テスト実行に必要なプロジェクト設定情報
   */
  public LocalBuildExecutor(final Configuration config) {
    projectBuilder = new ProjectBuilder(config.getTargetProject());
  }

  /**
   * 対象ソースコードのビルドとテストの実行を行う．<br>
   * ビルド失敗時はEmptyTestResultsを返す．<br>
   */
  @Override
  public BuildResults exec(final Variant variant) {
    final GeneratedSourceCode generatedSourceCode = variant.getGeneratedSourceCode();
    if (!generatedSourceCode.isGenerationSuccess()) {
      return new EmptyBuildResults();
    }
    final BuildResults buildResults = projectBuilder.build(generatedSourceCode);
    if (buildResults.isBuildFailed) {
      return new EmptyBuildResults(buildResults.diagnostics, buildResults.buildProgressText,
          buildResults.buildTime);
    }
    return buildResults;
  }
}

