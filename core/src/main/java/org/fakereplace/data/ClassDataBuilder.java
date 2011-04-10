package org.fakereplace.data;

import javassist.bytecode.FieldInfo;

import java.util.HashSet;
import java.util.Set;

public class ClassDataBuilder {

    final private BaseClassData baseData;

    final private Set<FieldData> fakeFields = new HashSet<FieldData>();

    final private Set<MethodData> fakeMethods = new HashSet<MethodData>();

    final private Set<MethodData> removedMethods = new HashSet<MethodData>();

    final private Set<FieldData> removedFields = new HashSet<FieldData>();

    public ClassDataBuilder(BaseClassData b) {
        if (b == null) {
            throw new RuntimeException("Attempted to created ClassDataBuilder with null BaseClassData");
        }
        baseData = b;
    }

    public ClassData buildClassData() {
        return new ClassData(baseData, fakeMethods, removedMethods, fakeFields, removedFields);
    }

    public BaseClassData getBaseData() {
        return baseData;
    }

    public FieldData addFakeField(FieldInfo newField, String proxyName, int modifiers) {
        FieldData data = new FieldData(newField, MemberType.FAKE, proxyName, modifiers);
        fakeFields.add(data);
        return data;
    }

    public MethodData addFakeMethod(String name, String descriptor, String proxyName, int accessFlags) {
        MethodData data = new MethodData(name, descriptor, proxyName, MemberType.FAKE, accessFlags, false);
        fakeMethods.add(data);
        return data;
    }

    public MethodData addFakeConstructor(String name, String descriptor, String proxyName, int accessFlags, int methodCount) {
        MethodData data = new MethodData(name, descriptor, proxyName, MemberType.FAKE_CONSTRUCTOR, accessFlags, methodCount);
        fakeMethods.add(data);
        return data;
    }

    public void removeRethod(MethodData md) {
        MethodData nmd = new MethodData(md.getMethodName(), md.getDescriptor(), md.getClassName(), MemberType.REMOVED, md.getAccessFlags(), false);
        removedMethods.add(nmd);
    }

    public void removeField(FieldData md) {
        FieldData nd = new FieldData(md, MemberType.REMOVED);
        removedFields.add(nd);
    }

}
