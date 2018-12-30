package com.chizi.compiler;

import com.chizi.annotation.DIActivity;
import com.chizi.annotation.DIView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class DIProcessor extends AbstractProcessor {

    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(DIActivity.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("DIProcessor");
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(DIActivity.class);
        for (Element element : elements) {
            TypeElement typeElement = ((TypeElement) element);
            List<? extends Element> members = mElementUtils.getAllMembers(typeElement);
            MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("bindView")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(ClassName.get(typeElement.asType()), "activity");
            for (Element member : members) {
                DIView diView = member.getAnnotation(DIView.class);
                if (diView == null) {
                    continue;
                }
                methodSpecBuilder.addStatement(String.format("activity.%s = (%s) activity.findViewById(%s)",
                        member.getSimpleName(),
                        ClassName.get(member.asType()).toString(),
                        diView.value()));
                TypeSpec typeSpec = TypeSpec.classBuilder("DI" + element.getSimpleName())
                        .superclass(TypeName.get(typeElement.asType()))
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(methodSpecBuilder.build())
                        .build();

                JavaFile javaFile = JavaFile.builder(getPackageName(typeElement), typeSpec).build();
                try {
                    javaFile.writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private String getPackageName(TypeElement type) {
        return mElementUtils.getPackageOf(type).getQualifiedName().toString();
    }

}
