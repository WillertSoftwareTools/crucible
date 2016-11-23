package de.willert.crucible.reportplugin.template;

import com.atlassian.plugin.util.ClassLoaderUtils;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by czoeller on 31.08.16.
 */
public class TemplateEnvironment {
    private final String tempDirectory;
    private final String tmpSubDir;
    private String destinationFilename;

    @Inject
    public TemplateEnvironment(String tmpSubDir, String destinationFilename) {
        this.tmpSubDir = tmpSubDir;
        this.tempDirectory = Paths.get(FileUtils.getTempDirectoryPath(), tmpSubDir).toString();
        this.destinationFilename = destinationFilename;
    }

    public File getDestinationFile() {
        return new File(getTemplateFile().get().getParentFile().getAbsolutePath(), destinationFilename);
    }

    public Optional<File> getTemplateFile() {
        final List<String> filesMoved = new ArrayList<>();

        List<String> resources = Lists.newArrayList("template/logo.png", "template/ReportTemplate.tex");
        resources.forEach( s -> {
            try {
                File targetFile = new File( this.tempDirectory + "/" + s.split("/")[1] );
                FileUtils.copyURLToFile( ClassLoaderUtils.getResource(s, this.getClass()), targetFile );
                filesMoved.add( targetFile.getName() );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Predicate<String> isTemplateFile = s -> s.contains(".tex");
        final File templateFile = filesMoved.stream()
                                            .filter(isTemplateFile)
                                            .map(s -> Paths.get(tempDirectory, s).toString())
                                            .map(File::new)
                                            .findFirst()
                                            .orElseThrow(IllegalStateException::new);
        return Optional.ofNullable(templateFile);
    }

    public Optional<File> getTransformFile() {
        Predicate<File> isTemplateFile = s -> s.getName().contains("Transform") && s.getName()
                                                                                    .endsWith("tex");

        final Collection<File> files = FileUtils.listFiles(new File(tempDirectory), null, false);

        File transformPath = files.stream()
                                  .filter(isTemplateFile)
                                  .findFirst()
                                  .orElseGet(() -> {
                                      // If it doesn't exist just create it
                                      return new File(tempDirectory + "/Transform.tex");
                                  });
        return Optional.ofNullable(transformPath);
    }


}
