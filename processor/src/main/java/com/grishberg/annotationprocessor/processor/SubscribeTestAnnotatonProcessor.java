package com.grishberg.annotationprocessor.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * Created by grishberg on 19.04.17.
 */
@SupportedAnnotationTypes("com.grishberg.annotationprocessor.processor.SubscribeTest")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SubscribeTestAnnotatonProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        String originalPackageName = "";
        for (Element element : roundEnv.getElementsAnnotatedWith(SubscribeTest.class)) {
            originalPackageName = element.getEnclosingElement().toString();
            break;
        }
        roundEnv.getElementsAnnotatedWith(SubscribeTest.class).toArray();
        StringBuilder builder = new StringBuilder()
                .append("package ")
                .append(originalPackageName)
                .append(";\n\n")
                .append("public class GeneratedClassSubscriber {\n\n") // open class
                .append("\tpublic String getMessage() {\n") // open method
                .append("\t\treturn \"");

        // for each javax.lang.model.element.Element annotated with the CustomAnnotation
        for (Element element : roundEnv.getElementsAnnotatedWith(SubscribeTest.class)) {
            String objectType = element.getSimpleName().toString();

            // this is appending to the return statement
            builder.append(objectType).append(" says hello!\\n");
        }

        builder.append("\";\n") // end return
                .append("\t}\n") // close method
                .append("}\n"); // close class

        try { // write the file
            JavaFileObject source = processingEnv.getFiler()
                    .createSourceFile(originalPackageName + ".GeneratedClassSubscriber");

            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }

        return true;
    }

}
