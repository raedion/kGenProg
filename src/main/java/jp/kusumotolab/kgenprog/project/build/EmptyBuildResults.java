package jp.kusumotolab.kgenprog.project.build;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 * ビルド失敗時を表すBuildResultsオブジェクト．<br>
 * いわゆるNullオブジェクト．
 *
 * @author shinsuke
 */
public class EmptyBuildResults extends BuildResults {

  public EmptyBuildResults() {
    super(null, null, null, true, 0d);
  }

  public EmptyBuildResults(final DiagnosticCollector<JavaFileObject> diagnostics,
      final String buildProgressText, final double buildTime) {
    super(null, diagnostics, buildProgressText, true, buildTime);
  }
}
