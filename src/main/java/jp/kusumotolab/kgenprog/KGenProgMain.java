package jp.kusumotolab.kgenprog;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.ga.Crossover;
import jp.kusumotolab.kgenprog.ga.Mutation;
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.Variant;
import jp.kusumotolab.kgenprog.ga.VariantSelection;
import jp.kusumotolab.kgenprog.ga.VariantStore;
import jp.kusumotolab.kgenprog.project.PatchGenerator;
import jp.kusumotolab.kgenprog.project.PatchesStore;
import jp.kusumotolab.kgenprog.project.jdt.JDTASTConstruction;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;

public class KGenProgMain {

  private static Logger log = LoggerFactory.getLogger(KGenProgMain.class);

  private final Configuration config;
  private final FaultLocalization faultLocalization;
  private final Mutation mutation;
  private final Crossover crossover;
  private final SourceCodeGeneration sourceCodeGeneration;
  private final SourceCodeValidation sourceCodeValidation;
  private final VariantSelection variantSelection;
  private final TestExecutor testExecutor;
  private final PatchGenerator patchGenerator;
  private final JDTASTConstruction astConstruction;

  public KGenProgMain(final Configuration config, final FaultLocalization faultLocalization,
      final Mutation mutation, final Crossover crossover,
      final SourceCodeGeneration sourceCodeGeneration,
      final SourceCodeValidation sourceCodeValidation, final VariantSelection variantSelection,
      final PatchGenerator patchGenerator) {

    this.config = config;
    this.faultLocalization = faultLocalization;
    this.mutation = mutation;
    this.crossover = crossover;
    this.sourceCodeGeneration = sourceCodeGeneration;
    this.sourceCodeValidation = sourceCodeValidation;
    this.variantSelection = variantSelection;
    this.testExecutor = new TestExecutor(config);
    this.astConstruction = new JDTASTConstruction();
    this.patchGenerator = patchGenerator;
  }

  public List<Variant> run() {

    logConfig();

    final Strategies strategies = new Strategies(faultLocalization, astConstruction,
        sourceCodeGeneration, sourceCodeValidation, testExecutor, variantSelection);
    final VariantStore variantStore = new VariantStore(config.getTargetProject(), strategies);
    final Variant initialVariant = variantStore.getInitialVariant();

    sourceCodeGeneration.initialize(initialVariant);
    mutation.setCandidates(initialVariant.getGeneratedSourceCode()
        .getProductAsts());

    final StopWatch stopwatch = new StopWatch(config.getTimeLimitSeconds());
    stopwatch.start();

    while (true) {

      logGeneration(variantStore.getGenerationNumber());

      // 変異プログラムを生成
      variantStore.addGeneratedVariants(mutation.exec(variantStore));
      variantStore.addGeneratedVariants(crossover.exec(variantStore));

      // しきい値以上の completedVariants が生成された場合は，GA を抜ける
      if (areEnoughCompletedVariants(variantStore.getFoundSolutions())) {
        log.info("found enough solutions.");
        break;
      }

      // 制限時間に達した場合には GA を抜ける
      if (stopwatch.isTimeout()) {
        log.info("reached the time limit.");
        break;
      }

      // 最大世代数に到達した場合には GA を抜ける
      if (reachedMaxGeneration(variantStore.getGenerationNumber())) {
        log.info("reached the maximum generation.");
        break;
      }

      // 次世代に向けての準備
      variantStore.changeGeneration();
    }

    // 生成されたバリアントのパッチ出力
    logPatch(variantStore);

    return variantStore.getFoundSolutions(config.getRequiredSolutionsCount());
  }

  private boolean reachedMaxGeneration(final OrdinalNumber generation) {
    return config.getMaxGeneration() <= generation.get();
  }

  private boolean areEnoughCompletedVariants(final List<Variant> completedVariants) {
    return config.getRequiredSolutionsCount() <= completedVariants.size();
  }

  private void logPatch(final VariantStore variantStore) {
    final PatchesStore patchesStore = new PatchesStore();
    final List<Variant> completedVariants =
        variantStore.getFoundSolutions(config.getRequiredSolutionsCount());

    for (final Variant completedVariant : completedVariants) {
      patchesStore.add(patchGenerator.exec(completedVariant));
    }

    patchesStore.writeToLogger();

    if (!config.needNotOutput()) {
      patchesStore.writeToFile(config.getOutDir());
    }
  }

  private void logConfig() {
    final StringBuilder sb = new StringBuilder();
    sb//
        .append(System.lineSeparator())
        .append("==================== kGenProg Configuration ====================")
        .append(System.lineSeparator())
        .append(config.toString())
        .append("================================================================");
    log.info(sb.toString());
  }

  private void logGeneration(final OrdinalNumber generation) {
    final StringBuilder sb = new StringBuilder();
    sb//
        .append("entered the era of ")
        .append(generation.toString())
        .append(" generation.");
    log.info(sb.toString());
  }
}
