package com.dr5hx.annotations;

/**
 * MySourceAnnotationProcessor
 * Desc:
 * Date:2025/6/20 14:58
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
// processor/MySourceAnnotationProcessor.java

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

@SupportedAnnotationTypes("com.dr5hx.annotations.MySourceAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MySourceAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element instanceof TypeElement) {
                    TypeElement typeElement = (TypeElement) element;

                    // 获取注解值
                    MySourceAnnotation annotationInstance = typeElement.getAnnotation(MySourceAnnotation.class);
                    String value = annotationInstance.value();

                    // 生成 Java 类
                    try {
                        String newClassName = typeElement.getSimpleName() + "Generated";
                        JavaFileObject builderFile = processingEnv.getFiler()
                                .createSourceFile(typeElement.getQualifiedName() + "Generated");

                        Writer writer = builderFile.openWriter();

                        writer.write("package com.example.generated;\n\n");
                        writer.write("public class " + newClassName + " {\n");
                        writer.write("    public void sayHello() {\n");
                        writer.write("        System.out.println(\"" + value + "\");\n");
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