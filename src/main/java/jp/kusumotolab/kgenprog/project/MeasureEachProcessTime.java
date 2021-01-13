package jp.kusumotolab.kgenprog.project;

public class MeasureEachProcessTime {

  // フィールド変数
  private double allBuildTime;
  private long buildCount;
  private double allTestTime;
  private long testCount;
  private double allGenASTTime;
  private long genASTCount;
  private double firstGenASTTime;

  // コンストラクタ部

  /**
   * 各フィールド変数の初期化
   */
  public MeasureEachProcessTime() {
    this.allBuildTime = 0d;
    this.allTestTime = 0d;
    this.allGenASTTime = 0d;
    this.firstGenASTTime = 0d;
  }

  public void addBuildTime(final double buildTime) {
    if (Double.compare(buildTime, Double.NaN) == 0) {
      return;
    }
    buildCount++;
    allBuildTime = allBuildTime + buildTime;
  }

  public void addTestTime(final double testTime) {
    if (Double.compare(testTime, Double.NaN) == 0) {
      return;
    }
    testCount++;
    allTestTime = allTestTime + testTime;
  }

  public void addASTGenTime(final double genASTTime) {
    if (Double.compare(genASTTime, Double.NaN) == 0) {
      return;
    }
    genASTCount++;
    allGenASTTime = allGenASTTime + genASTTime;
  }

  public void setFirstGenASTTime(final double firstGenASTTime) {
    this.firstGenASTTime = firstGenASTTime;
  }

  public void setBuildCount(final long buildCount) {
    this.buildCount = buildCount;
  }

  public String getMessage(final boolean selectOutputCount) {
    return String.format(
        getCountMessage(selectOutputCount) +
            "\nAll Build time [ms]: %s" +
            "\nAll Test time[ms]: %s" +
            "\nFirst Gen AST time[ms]: %s" +
            "\nAll AST generate time[ms]: %s",
        allBuildTime,
        allTestTime,
        firstGenASTTime,
        allGenASTTime
    );
  }

  private String getCountMessage(final boolean selectOutputCount) {
    return selectOutputCount ? String.
        format(
            "\nBuild Count: %s" +
                "\nTest Count: %s" +
                "\nGen AST Count: %s",
            buildCount,
            testCount,
            genASTCount
        ) : "";
  }
}
