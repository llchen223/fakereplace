package org.fakereplace.test.replacement.annotated;

@Annotation2(ivalue = 10)
public class AnnotatedClass1 {

    @Annotation1(svalue = "hello2", cvalue = Annotation1.class)
    public String field;

}
