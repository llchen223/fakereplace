package org.fakereplace.test.replacement.interfacemethod;

import org.fakereplace.test.util.ClassReplacer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InterfaceMethodTest {
    @BeforeClass(groups = "interface")
    public void setup() {
        ClassReplacer rep = new ClassReplacer();
        rep.queueClassForReplacement(InterfaceCallingClass.class, InterfaceCallingClass1.class);
        rep.queueClassForReplacement(SomeInterface.class, SomeInterface1.class);
        rep.queueClassForReplacement(ImplementingClass.class, ImplementingClass1.class);
        rep.replaceQueuedClasses();
    }

    @Test(groups = "interface")
    public void testAddingInterfaceMethod() {
        SomeInterface iface = new ImplementingClass();
        InterfaceCallingClass caller = new InterfaceCallingClass();
        assert caller.call(iface).equals("added");
    }

    @Test(groups = "interface")
    public void testAddingInterfaceMethodByReflection() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method method = SomeInterface.class.getDeclaredMethod("added");
        ImplementingClass cls = new ImplementingClass();
        assert method.invoke(cls).equals("added");
    }
}
