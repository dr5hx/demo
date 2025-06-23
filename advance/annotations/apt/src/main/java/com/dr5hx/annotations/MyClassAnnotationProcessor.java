package com.dr5hx.annotations;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes("com.dr5hx.annotations.MyAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MyClassAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element instanceof TypeElement) {
                    TypeElement typeElement = (TypeElement) element;

                    try {
                        String newClassName = typeElement.getSimpleName() + "Generated";
                        JavaFileObject builderFile = processingEnv.getFiler()
                                .createSourceFile(typeElement.getQualifiedName() + "Generated");

                        Writer writer = builderFile.openWriter();

                        writer.write("package com.dr5hx.generated;\n\n");
                        writer.write("public class " + newClassName + " {\n");
                        writer.write("    public void sayHello() {\n");
                        writer.write("        System.out.println(\"Hello from generated class!\");\n");
                        writer.write("    }\n");
                        writer.write("}\n");

                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return true;
    }
}