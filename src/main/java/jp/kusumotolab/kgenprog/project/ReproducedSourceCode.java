package jp.kusumotolab.kgenprog.project;

import java.util.Collections;
import jp.kusumotolab.kgenprog.ga.codegeneration.ReproducedStatus;

public class ReproducedSourceCode extends GeneratedSourceCode {

  private final boolean isGenerationSuccess;
  private final String generationMessage;
  private final double genASTTime;

  public ReproducedSourceCode(final ReproducedStatus status) {
    super(Collections.emptyList(), Collections.emptyList());

    this.isGenerationSuccess = status.isGenerationSuccess;
    this.generationMessage = status.generationMessage;
    this.genASTTime = Double.NaN;
  }

  public ReproducedSourceCode(final ReproducedStatus status, final double time) {
    super(Collections.emptyList(), Collections.emptyList());

    this.isGenerationSuccess = status.isGenerationSuccess;
    this.generationMessage = status.generationMessage;
    this.genASTTime = time;
  }
  @Override
  public boolean isGenerationSuccess() {
    return isGenerationSuccess;
  }

  @Override
  public String getGenerationMessage() {
    return "(Reproduced Source Code) " + generationMessage;
  }

  @Override
  public boolean isReproducedSourceCode() {
    return true;
  }

  @Override
  public double getGenASTTime() {
    return genASTTime;
  }
}
