package jp.kusumotolab.kgenprog.project.test;

import io.reactivex.Single;
import jp.kusumotolab.kgenprog.ga.variant.Variant;
import jp.kusumotolab.kgenprog.project.build.BuildResults;

public interface BuildExecutor {
  BuildResults exec(final Variant variant);
  default Single<BuildResults> execAsync(final Single<Variant> variantSingle) {
    return variantSingle.map(this::exec);
  }
}
