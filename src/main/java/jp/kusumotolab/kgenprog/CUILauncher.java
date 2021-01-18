package jp.kusumotolab.kgenprog;

import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.errorprone.annotations.Var;
import ch.qos.logback.classic.Level;
import jp.kusumotolab.kgenprog.fl.FaultLocalization;
import jp.kusumotolab.kgenprog.ga.codegeneration.DefaultSourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.codegeneration.SourceCodeGeneration;
import jp.kusumotolab.kgenprog.ga.crossover.Crossover;
import jp.kusumotolab.kgenprog.ga.crossover.FirstVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.crossover.SecondVariantSelectionStrategy;
import jp.kusumotolab.kgenprog.ga.mutation.Mutation;
import jp.kusumotolab.kgenprog.ga.mutation.SimpleMutation;
import jp.kusumotolab.kgenprog.ga.mutation.selection.CandidateSelection;
import jp.kusumotolab.kgenprog.ga.mutation.selection.RouletteStatementAndConditionSelection;
import jp.kusumotolab.kgenprog.ga.selection.DefaultVariantSelection;
import jp.kusumotolab.kgenprog.ga.selection.VariantSelection;
import jp.kusumotolab.kgenprog.ga.validation.DefaultCodeValidation;
import jp.kusumotolab.kgenprog.ga.validation.SourceCodeValidation;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.output.Exporters;
import jp.kusumotolab.kgenprog.project.MeasureEachProcessTime;
import jp.kusumotolab.kgenprog.project.build.BuildExecutor;
import jp.kusumotolab.kgenprog.project.build.LocalBuildExecutor;
import jp.kusumotolab.kgenprog.project.test.LocalTestExecutor;
import jp.kusumotolab.kgenprog.project.test.TestExecutor;

public class CUILauncher {

  public static void main(final String[] args) {
    try {
      final Configuration config = Configuration.Builder.buildFromCmdLineArgs(args);
      final CUILauncher launcher = new CUILauncher();
      launcher.launch(config);
    } catch (final RuntimeException e) {
      Logger log = LoggerFactory.getLogger(CUILauncher.class);
      log.error(e.getMessage(), e);
      System.exit(1);
    }
  }

  public List<Variant> launch(final Configuration config) {
    setLogLevel(config.getLogLevel());
    final StopWatch stopWatch = StopWatch.createStarted();

    final FaultLocalization faultLocalization = config.getFaultLocalization()
        .initialize();
    final Random random = new Random(config.getRandomSeed());
    final CandidateSelection candidateSelection =
        new RouletteStatementAndConditionSelection(random);
    final Mutation mutation = new SimpleMutation(config.getMutationGeneratingCount(), random,
        candidateSelection, config.getScope());
    final FirstVariantSelectionStrategy firstVariantSelectionStrategy =
        config.getFirstVariantSelectionStrategy()
            .initialize(random);
    final SecondVariantSelectionStrategy secondVariantSelectionStrategy =
        config.getSecondVariantSelectionStrategy()
            .initialize(random);
    final Crossover crossover = config.getCrossoverType()
        .initialize(random, firstVariantSelectionStrategy,
            secondVariantSelectionStrategy, config.getCrossoverGeneratingCount());
    final SourceCodeGeneration sourceCodeGeneration = new DefaultSourceCodeGeneration();
    final SourceCodeValidation sourceCodeValidation = new DefaultCodeValidation();
    final VariantSelection variantSelection = new DefaultVariantSelection(config.getHeadcount(),
        random);
    final TestExecutor testExecutor = new LocalTestExecutor(config);
    final BuildExecutor buildExecutor = new LocalBuildExecutor(config);
    final Exporters exporters = new Exporters(config);
    final MeasureEachProcessTime measure = new MeasureEachProcessTime(config);
    final KGenProgMain kGenProgMain =
        new KGenProgMain(config, faultLocalization, mutation, crossover, sourceCodeGeneration,
            sourceCodeValidation, variantSelection, testExecutor, buildExecutor, exporters, measure);

    final List<Variant> run = kGenProgMain.run();

    stopWatch.stop();
    measure.setWholeTime(stopWatch.getTime());
    Logger log = LoggerFactory.getLogger(CUILauncher.class);
    log.info(measure.getMessage(true));       // 2021/01/18 各時間の出力に全体時間の情報を追加するために移動
    return run;
  }

  private void setLogLevel(final Level logLevel) {
    final ch.qos.logback.classic.Logger rootLogger =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(logLevel);
  }
}
