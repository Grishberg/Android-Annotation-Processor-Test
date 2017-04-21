package com.grishberg.annotationprocessor.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
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
        String parentClassName = "";
        final ArrayList<ArgsHolder> args = new ArrayList<>();
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(SubscribeTest.class)) {
            String methodName = annotatedElement.getSimpleName().toString();

            ExecutableType executableType = (ExecutableType)annotatedElement.asType();
            List<? extends TypeMirror> parameters = executableType.getParameterTypes();
            TypeMirror param1 = parameters.get(0);
            DeclaredType declaredType = (DeclaredType)param1;
            List<? extends AnnotationMirror> anns = ((TypeElement)declaredType.asElement()).getAnnotationMirrors( );

            args.add(new ArgsHolder(methodName, declaredType.toString()));

            if (parentClassName == null || parentClassName.isEmpty()) {
                Element patentClass = annotatedElement.getEnclosingElement();
                parentClassName = patentClass.getSimpleName().toString();
                originalPackageName = patentClass.getEnclosingElement().toString();
            }
        }
        roundEnv.getElementsAnnotatedWith(SubscribeTest.class).toArray();
        String className = "GeneratedClass" + parentClassName + "Subscriber";
        StringBuilder builder = new StringBuilder()
                .append("package ")
                .append(originalPackageName)
                .append(";\n\n")
                .append("public class ")
                .append(className)
                .append(" {\n\n")
                .append("\tprivate final ")
                .append(parentClassName)
                .append(" view;\n")
                .append("\tpublic ")
                .append(className)
                .append("(final ")
                .append(parentClassName)
                .append(" view){\n")
                .append("\t\tthis.view = view;\n")
                .append("\t}\n\n")
                .append("\tpublic void processState(MvpState state) {\n");// open method; // open class

        boolean isFirstArg = true;
        for (ArgsHolder arg : args) {
            builder.append("\t\t");
            if (!isFirstArg) {
                builder.append(" else ");
            }
            builder.append("if(state instanceof ")
                    .append(arg.argType)
                    .append(") {\n")
                    .append("\t\t\tview.")
                    .append(arg.methodName)
                    .append("((")
                    .append(arg.argType)
                    .append(") state);\n\t\t}");
            isFirstArg = false;
        }

        builder.append("\n\t}\n") // close method
                .append("}\n"); // close class

        try { // write the file
            JavaFileObject source = processingEnv.getFiler()
                    .createSourceFile(originalPackageName + "." + className);

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

    static class ArgsHolder {
        final String methodName;
        final String argType;

        public ArgsHolder(String methodName, String argType) {
            this.methodName = methodName;
            this.argType = argType;
        }
    }
}
