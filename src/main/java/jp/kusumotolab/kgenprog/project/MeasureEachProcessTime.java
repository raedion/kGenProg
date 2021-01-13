package jp.kusumotolab.kgenprog.project;

public class MeasureEachProcessTime {
  private double allBuildTime;
  private double allTestTime;
  private double allASTGenTime;
  private long buildCount;
  public MeasureEachProcessTime() {
    this.allBuildTime = 0d;
    this.allTestTime = 0d;
    this.allASTGenTime = 0d;
  }
  public void addBuildTime(double buildTime) {
    allBuildTime = allBuildTime + buildTime;
  }
  public void addTestTime(double testTime) {
    allTestTime = allTestTime + testTime;
  }
  public void addASTGenTime(final double ASTGenTime) {
    allASTGenTime = allASTGenTime + ASTGenTime;
  }

  public void setBuildCount(final long buildCount) {
    this.buildCount = buildCount;
  }
  public String getMessage() {
    return String.format(
        "\nBuild Count: %s\nAll Build time [ms]: %s\nAll Test time[ms]: %s\nAll AST generate time[ms]: %s",
        buildCount,
        allBuildTime,
        allTestTime,
        allASTGenTime
    );
  }
}
