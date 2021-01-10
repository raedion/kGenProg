package jp.kusumotolab.kgenprog.project;

public class MeasureBuildAndTestTime {
  private double allBuildTime;
  private double allTestTime;
  private long buildCount;
  public MeasureBuildAndTestTime() {
    this.allBuildTime = 0d;
    this.allTestTime = 0d;
  }
  public void addBuildTime(double buildTime) {
    allBuildTime = allBuildTime + buildTime;
  }
  public void addTestTime(double testTime) {
    allTestTime = allTestTime + testTime;
  }
  public void setBuildCount(final long buildCount) {
    this.buildCount = buildCount;
  }
  public String getMessage() {
    return String.format(
        "\nBuild Count: %s\nAll Build time [ms]: %s\nAll Test time[ms]: %s",
        buildCount,
        allBuildTime,
        allTestTime
    );
  }
}
