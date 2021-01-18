package jp.kusumotolab.kgenprog.project;

import static java.lang.System.lineSeparator;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import org.apache.commons.lang3.time.StopWatch;
import jp.kusumotolab.kgenprog.Configuration;

public class MeasureEachProcessTime {

  // フィールド変数
  private double allBuildTime;
  private long buildCount;
  private double allTestTime;
  private long testCount;
  private double allGenASTTime;
  private long genASTCount;
  private double firstGenASTTime;
  private double wholeTime;
  private final String targetProjectName;
  private final String outputFileName;
  private final String nowHourAndMinute;

  // コンストラクタ部

  /**
   * 各フィールド変数の初期化
   */
  public MeasureEachProcessTime(Configuration config) {
    targetProjectName = config.getTargetProject()
        .toString();
    LocalDateTime nowTime = LocalDateTime.now();
    final String outputFolder = "kgp_log/";
    outputFileName =
        outputFolder + nowTime.getYear() + "-" +
            nowTime.getMonthValue() + "-" +
            nowTime.getDayOfMonth() + ".txt";
    nowHourAndMinute = nowTime.getHour() + ":" + nowTime.getMinute();
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
    if (Double.isNaN(testTime)) {
      return;
    }
    testCount++;
    allTestTime = allTestTime + testTime;
  }

  public void addASTGenTime(final double genASTTime) {
    if (Double.isNaN(genASTTime)) {
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

  public void setWholeTime(final double wholeTime) {
    this.wholeTime = wholeTime;
  }

  public String getMessage(final boolean selectOutputCount) {
    StopWatch stopWatch = StopWatch.createStarted();
    final String message = String.format(
        getCountMessage(selectOutputCount) + lineSeparator() +
            "All Build time [ms]: %s" + lineSeparator() +
            "All Test time[ms]: %s" + lineSeparator() +
            "First Gen AST time[ms]: %s" + lineSeparator() +
            "All AST generate time[ms]: %s" + lineSeparator() +
            "Whole time[ms]: %s",
        allBuildTime,
        allTestTime,
        firstGenASTTime,
        allGenASTTime,
        wholeTime
    );
    outputIOData(true, message);
    stopWatch.stop();
    System.out.println("IO output time[ms]: " + stopWatch.getTime());
    return message;
  }

  private String getCountMessage(final boolean selectOutputCount) {
    return selectOutputCount ? String.
        format(
            lineSeparator() + "Build Count: %s" +
                lineSeparator() + "Test Count: %s" +
                lineSeparator() + "Gen AST Count: %s",
            buildCount,
            testCount,
            genASTCount
        ) : "";
  }

  private void outputIOData(final boolean selectOutputIO, final String message) {
    if (!selectOutputIO) {
      return;
    }
    try {
      FileWriter fw = new FileWriter(outputFileName, true);
      fw.write(
          "---------------------" + lineSeparator() +
              "ExecTime: " + nowHourAndMinute + lineSeparator() +
              "ProjectFile: " + targetProjectName + lineSeparator() +
              message + lineSeparator()
      );
      fw.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
