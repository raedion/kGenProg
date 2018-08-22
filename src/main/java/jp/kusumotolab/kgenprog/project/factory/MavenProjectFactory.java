package jp.kusumotolab.kgenprog.project.factory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.kusumotolab.kgenprog.project.ClassPath;
import jp.kusumotolab.kgenprog.project.ProductSourcePath;
import jp.kusumotolab.kgenprog.project.TestSourcePath;

public class MavenProjectFactory extends BuildToolProjectFactory {

  private final static String CONFIG_FILE_NAME = "pom.xml";
  private final static Logger log = LoggerFactory.getLogger(MavenProjectFactory.class);

  private final Path rootPath;

  public MavenProjectFactory(final Path rootPath) {
    super(rootPath);

    this.rootPath = rootPath;
  }

  @Override
  public boolean isApplicable() {
    return getConfigPath().size() > 0;
  }

  @Override
  protected String getConfigFileName() {
    return CONFIG_FILE_NAME;
  }

  @Override
  public TargetProject create() {
    final List<ProductSourcePath> sourcePathList = resolveSourcePath(rootPath);
    final List<TestSourcePath> testSourcePathList = resolveTestPath(rootPath);
    final List<ClassPath> classPathList = resolveClassPath(rootPath);
    return new TargetProject(rootPath, sourcePathList, testSourcePathList, classPathList);
  }

  private List<ProductSourcePath> resolveSourcePath(Path rootPath) {
    final Path path = rootPath.resolve("src")
        .resolve("main")
        .resolve("java");
    final List<Path> javaFilePaths = searchJavaFilePaths(path);
    return javaFilePaths.stream()
        .map(ProductSourcePath::new)
        .collect(Collectors.toList());
  }

  private List<TestSourcePath> resolveTestPath(Path rootPath) {
    final Path path = rootPath.resolve("src")
        .resolve("test")
        .resolve("java");
    final List<Path> javaFilePaths = searchJavaFilePaths(path);
    return javaFilePaths.stream()
        .map(TestSourcePath::new)
        .collect(Collectors.toList());
  }

  private class JavaFileVisitor extends SimpleFileVisitor<Path> {

    private final List<Path> javaFilePathList = new ArrayList<>();

    @Override
    public FileVisitResult visitFile(final Path path, final BasicFileAttributes attrs) {
      final Path fileName = path.getFileName();
      final String pathString = fileName.toString();
      if (pathString.endsWith(".java")) {
        javaFilePathList.add(path);
      }
      return FileVisitResult.CONTINUE;
    }

    List<Path> getJavaFilePathList() {
      return javaFilePathList;
    }
  }

  private List<Path> searchJavaFilePaths(final Path rootPath) {
    final JavaFileVisitor visitor = new JavaFileVisitor();
    try {
      Files.walkFileTree(rootPath, visitor);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return visitor.getJavaFilePathList();
  }

  private List<ClassPath> resolveClassPath(Path rootPath) {
    final List<ClassPath> list = new ArrayList<>();
    final Path pomFilePath = rootPath.resolve(CONFIG_FILE_NAME);
    final MavenXpp3Reader reader = new MavenXpp3Reader();
    try {
      final Model model = reader.read(Files.newBufferedReader(pomFilePath));
      final String userHome = System.getProperty("user.home");
      final Path repositoryPath = Paths.get(userHome)
          .resolve(".m2")
          .resolve("repository");
      for (final Object object : model.getDependencies()) {
        if (!(object instanceof Dependency)) {
          continue;
        }
        final Dependency dependency = (Dependency) object;
        Path path = repositoryPath;
        final String groupId = dependency.getGroupId();
        for (String string : groupId.split("\\.")) {
          path = path.resolve(string);
        }
        final Path libPath = path.resolve(dependency.getArtifactId())
            .resolve(dependency.getVersion());
        final File libDirectory = libPath.toFile();
        if (!libDirectory.isDirectory()) {
          continue;
        }

        Files.find(libPath, Integer.MAX_VALUE, (p, attr) -> p.toFile()
            .getName()
            .endsWith(".java"))
            .map(ClassPath::new)
            .forEach(list::add);
      }
    } catch (final IOException | XmlPullParserException e) {
      log.debug(e.getMessage());
    }
    return list;
  }
}
