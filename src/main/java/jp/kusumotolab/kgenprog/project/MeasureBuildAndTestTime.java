package jp.kusumotolab.kgenprog.project;

public class MeasureBuildAndTestTime {
  private double allBuildTime;
  private double allTestTime;
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
  public String getMessage() {
    return String.format(
        "\nAll Build time [ms]: %s\nAll Test time[ms]: %s",
        allBuildTime,
        allTestTime
    );
  }
}
