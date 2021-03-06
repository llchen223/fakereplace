/*
 * Copyright 2016, Stuart Douglas, and individual contributors as indicated
 * by the @authors tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.fakereplace.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import org.fakereplace.core.Constants;
import org.fakereplace.util.DescriptorUtils;

/**
 * This class holds everything there is to know about a class that has been seen
 * by the transformer. This stores the information about the original class, not
 * about any modifications
 *
 * @author stuart
 */
public class BaseClassData {

    private final String className;
    private final String internalName;
    private final Set<MethodData> methods;
    private final List<FieldData> fields;
    private final ClassLoader loader;
    private final String superClassName;
    private final boolean replaceable;

    public BaseClassData(ClassFile file, ClassLoader loader, boolean replaceable) {
        className = file.getName();
        this.replaceable = replaceable;
        internalName = Descriptor.toJvmName(file.getName());
        this.loader = loader;
        superClassName = file.getSuperclass();
        boolean finalMethod = false;
        Set<MethodData> meths = new HashSet<MethodData>();
        for (Object o : file.getMethods()) {
            String methodClassName = className;
            MethodInfo m = (MethodInfo) o;
            MemberType type = MemberType.NORMAL;
            if ((m.getDescriptor().equals(Constants.ADDED_METHOD_DESCRIPTOR) && m.getName().equals(Constants.ADDED_METHOD_NAME))
                    || (m.getDescriptor().equals(Constants.ADDED_STATIC_METHOD_DESCRIPTOR) && m.getName().equals(Constants.ADDED_STATIC_METHOD_NAME))
                    || (m.getDescriptor().equals(Constants.ADDED_CONSTRUCTOR_DESCRIPTOR))) {
                type = MemberType.ADDED_SYSTEM;
            } else if (m.getAttribute(Constants.FINAL_METHOD_ATTRIBUTE) != null) {
                finalMethod = true;
            }

            MethodData md = new MethodData(m.getName(), m.getDescriptor(), methodClassName, type, m.getAccessFlags(), finalMethod);
            meths.add(md);
        }
        this.methods = Collections.unmodifiableSet(meths);
        List<FieldData> fieldData = new ArrayList<>();
        for (Object o : file.getFields()) {
            FieldInfo m = (FieldInfo) o;
            MemberType mt = MemberType.NORMAL;
            fieldData.add(new FieldData(m, mt, className, m.getAccessFlags()));
        }
        this.fields = Collections.unmodifiableList(fieldData);
    }

    public BaseClassData(Class<?> cls) {
        className = cls.getName();
        internalName = Descriptor.toJvmName(cls.getName());
        this.loader = cls.getClassLoader();
        replaceable = false;
        if (cls.getSuperclass() != null) {
            superClassName = cls.getSuperclass().getName();
        } else {
            superClassName = null;
        }
        Set<MethodData> meths = new HashSet<MethodData>();
        for (Method m : cls.getDeclaredMethods()) {
            MemberType type = MemberType.NORMAL;
            final String descriptor = DescriptorUtils.getDescriptor(m);
            if ((descriptor.equals(Constants.ADDED_METHOD_DESCRIPTOR) && m.getName().equals(Constants.ADDED_METHOD_NAME))
                    || (descriptor.equals(Constants.ADDED_STATIC_METHOD_DESCRIPTOR) && m.getName().equals(Constants.ADDED_STATIC_METHOD_NAME))) {
                type = MemberType.ADDED_SYSTEM;
            }
            MethodData md = new MethodData(m.getName(), descriptor, cls.getName(), type, m.getModifiers(), false);
            meths.add(md);
        }
        for (Constructor<?> c : cls.getDeclaredConstructors()) {
            MemberType type = MemberType.NORMAL;
            final String descriptor = DescriptorUtils.getDescriptor(c);
            if (descriptor.equals(Constants.ADDED_CONSTRUCTOR_DESCRIPTOR)) {
                type = MemberType.ADDED_SYSTEM;
            }
            MethodData md = new MethodData("<init>", descriptor, cls.getName(), type, c.getModifiers(), false);
            meths.add(md);
        }

        this.methods = Collections.unmodifiableSet(meths);
        List<FieldData> fieldData = new ArrayList<FieldData>();
        for (Field m : cls.getDeclaredFields()) {
            fieldData.add(new FieldData(m));
        }
        this.fields = Collections.unmodifiableList(fieldData);
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public String getClassName() {
        return className;
    }

    public String getInternalName() {
        return internalName;
    }

    public Collection<MethodData> getMethods() {
        return methods;
    }

    public Collection<FieldData> getFields() {
        return fields;
    }

    public boolean isReplaceable() {
        return replaceable;
    }

    public FieldData getField(String fieldName) {
        for(FieldData field : fields) {
            if(field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    public MethodData getMethodOrConstructor(String methodName, String methodDesc) {
        for(MethodData method : methods) {
            if(method.getMethodName().equals(methodName) && method.getDescriptor().equals(methodDesc)) {
                return method;
            }
        }
        return null;
    }
}
