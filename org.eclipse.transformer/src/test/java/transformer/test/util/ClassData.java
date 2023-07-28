/********************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: (EPL-2.0 OR Apache-2.0)
 ********************************************************************************/

package transformer.test.util;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClassData {
	public static final String		CLASS_NAME			= ClassData.class.getSimpleName();

	//

	private static final String[]	EMPTY_STRING_ARRAY	= new String[0];

	//

	protected final String			hashText;
	protected final String			simpleClassName;

	public String getHashText() {
		return hashText;
	}

	public String getSimpleClassName() {
		return simpleClassName;
	}

	//

	private final String					className;
	private final String					superclassName;
	private final String[]					interfaceNames;

	private final String[]				classAnnotationNames;

	private final Set<String>				fieldNames;
	private final BiDiMap<String, String>	fieldAnnotationNames;

	private final Set<String>				methodDescs;
	private final BiDiMap<String, String>	methodAnnotationNames;

	private final Set<String>				initDescs;
	private final BiDiMap<String, String>	initAnnotationNames;

	private final Set<String>				staticMethodDescs;
	private final BiDiMap<String, String>	staticMethodAnnotationNames;

	public String getClassName() {
		return className;
	}

	public String getSuperclassName() {
		return superclassName;
	}

	public String[] getInterfaceNames() {
		return interfaceNames;
	}

	public String[] getClassAnnotationNames() {
		return classAnnotationNames;
	}

	public Set<String> getFieldNames() {
		return fieldNames;
	}

	public BiDiMap<String, String> getFieldAnnotationNames() {
		return fieldAnnotationNames;
	}

	public Set<String> getMethodDescriptions() {
		return methodDescs;
	}

	public BiDiMap<String, String> getMethodAnnotationNames() {
		return methodAnnotationNames;
	}

	public Set<String> getInitDescriptions() {
		return initDescs;
	}

	public BiDiMap<String, String> getInitAnnotationNames() {
		return initAnnotationNames;
	}

	public Set<String> getStaticMethodDescriptions() {
		return staticMethodDescs;
	}

	public BiDiMap<String, String> getStaticMethodAnnotationNames() {
		return staticMethodAnnotationNames;
	}

	//

	public ClassData(Class<?> testClass) {
		this.hashText = generateHashText(testClass);
		this.simpleClassName = testClass.getSimpleName();
		this.className = testClass.getName();
		this.superclassName = (testClass.getSuperclass() != null) ? testClass.getSuperclass().getName() : null;

		this.interfaceNames = getInterfaceNames(testClass);
		this.classAnnotationNames = getAnnotationNames(testClass.getDeclaredAnnotations());

		this.fieldNames = getFieldNames(testClass);
		this.fieldAnnotationNames = getFieldAnnotationNames(testClass);

		this.staticMethodDescs = getMethodDescriptions(testClass, true);
		this.staticMethodAnnotationNames = getMethodAnnotationNames(testClass, true);
		this.methodDescs = getMethodDescriptions(testClass, false);
		this.methodAnnotationNames = getMethodAnnotationNames(testClass, false);

		this.initDescs = getConstructorDescriptions(testClass);
		this.initAnnotationNames = getConstructorAnnotationNames(testClass);
	}

	private String generateHashText(Class<?> testClass) {
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + "(" + testClass.getSimpleName() + ")";
	}

	private String[] getInterfaceNames(Class<?> testClass) {
		Class<?>[] interfaces = testClass.getInterfaces();
		if (interfaces.length == 0) {
			return EMPTY_STRING_ARRAY;
		}
		String[] interfaceNames = new String[interfaces.length];
		for (int i = 0; i < interfaces.length; i++) {
			interfaceNames[i] = interfaces[i].getName();
		}
		return interfaceNames;
	}

	private String[] getAnnotationNames(Annotation[] annotations) {
		String[] annotationNames = new String[annotations.length];
		for (int i = 0; i < annotations.length; i++) {
			annotationNames[i] = annotations[i].annotationType().getName();
		}
		return annotationNames;
	}

	private Set<String> getFieldNames(Class<?> testClass) {
		Field[] fields = testClass.getDeclaredFields();
		Set<String> fieldNames = new HashSet<>(fields.length);
		for (Field field : fields) {
			fieldNames.add(field.getName());
		}
		return fieldNames;
	}

	private BiDiMap<String, String> getFieldAnnotationNames(Class<?> testClass) {
		BiDiMap<String, String> fieldAnnotationNames = new BiDiMapImpl<>(String.class, "field", String.class, "annotation");
		for (Field field : testClass.getDeclaredFields()) {
			String fieldName = field.getName();
			for (Annotation annotation : field.getAnnotations()) {
				fieldAnnotationNames.record(fieldName, annotation.annotationType().getName());
			}
		}
		return fieldAnnotationNames;
	}

	private Set<String> getMethodDescriptions(Class<?> testClass, boolean isStatic) {
		Method[] methods = testClass.getDeclaredMethods();
		Set<String> methodDescs = new HashSet<>(methods.length);
		for (Method method : methods) {
			if (isStatic == isStatic(method)) {
				methodDescs.add(method.toString());
			}
		}
		return methodDescs;
	}

	private BiDiMap<String, String> getMethodAnnotationNames(Class<?> testClass, boolean isStatic) {
		BiDiMap<String, String> methodAnnotationNames = new BiDiMapImpl<>(String.class, "method", String.class, "annotation");
		for (Method method : testClass.getDeclaredMethods()) {
			if (isStatic == isStatic(method)) {
				String methodDesc = method.toString();
				for (Annotation annotation : method.getDeclaredAnnotations()) {
					methodAnnotationNames.record(methodDesc, annotation.annotationType().getName());
				}
			}
		}
		return methodAnnotationNames;
	}

	private Set<String> getConstructorDescriptions(Class<?> testClass) {
		Constructor<?>[] constructors = testClass.getDeclaredConstructors();
		Set<String> initDescs = new HashSet<>(constructors.length);
		for (Constructor<?> constructor : constructors) {
			initDescs.add(constructor.toString());
		}
		return initDescs;
	}

	private BiDiMap<String, String> getConstructorAnnotationNames(Class<?> testClass) {
		BiDiMap<String, String> initAnnotationNames = new BiDiMapImpl<>(String.class, "init", String.class, "annotation");
		for (Constructor<?> constructor : testClass.getDeclaredConstructors()) {
			String initDescription = constructor.toString();
			for (Annotation annotation : constructor.getDeclaredAnnotations()) {
				initAnnotationNames.record(initDescription, annotation.annotationType().getName());
			}
		}
		return initAnnotationNames;
	}

	protected static boolean isStatic(Method method) {
		return ((method.getModifiers() & Modifier.STATIC) != 0);
	}

	protected static Set<String> getNames(Annotation[] annotations) {
		Set<String> annotationNames;
		int numAnnotations = annotations.length;
		if (numAnnotations == 0) {
			annotationNames = Collections.emptySet();
		} else {
			annotationNames = new HashSet<>(numAnnotations);
			for (Annotation classAnnotation : annotations) {
				annotationNames.add(classAnnotation.annotationType()
					.getName());
			}
		}
		return annotationNames;
	}

	//

	private static final String logPrefix = CLASS_NAME + ": " + "log" + ": ";

	public void log(PrintWriter writer) {
		writer.println(logPrefix + "Class Data: BEGIN: " + getHashText());

		writer.println(logPrefix + "Class name: " + className);
		writer.println(logPrefix + "Superclass name: " + superclassName);
		writer.println(logPrefix + "Interface names: " + interfaceNames);

		if (classAnnotationNames.length == 0) {
			writer.println(logPrefix + "Class annotations: ** EMPTY **");
		} else {
			writer.println(logPrefix + "Class annotations:");
			for (String annoName : classAnnotationNames) {
				writer.println(logPrefix + "  " + annoName);
			}
		}

		if (fieldNames.isEmpty()) {
			writer.println(logPrefix + "Fields: ** EMPTY **");
		} else {
			writer.println(logPrefix + "Fields:");
			for (String fieldName : fieldNames) {
				writer.println(logPrefix + "  " + fieldName);
			}
		}

		if (fieldAnnotationNames.isEmpty()) {
			writer.println(logPrefix + "Field annotations: ** EMPTY **");
		} else {
			writer.println(logPrefix + "Field annotations:");
			for (String fieldName : fieldAnnotationNames.getHolders()) {
				writer.println(logPrefix + "  Field: " + fieldName);
				for (String annoName : fieldAnnotationNames.getHeld(fieldName)) {
					writer.println(logPrefix + "    " + annoName);
				}
			}
		}

		if (methodDescs.isEmpty()) {
			writer.println(logPrefix + "Methods: ** EMPTY **");
		} else {
			writer.println(logPrefix + "Methods:");
			for (String methodDesc : methodDescs) {
				writer.println(logPrefix + "  " + methodDesc);
			}
		}

		if (methodAnnotationNames.isEmpty()) {
			writer.println(logPrefix + "Method annotations: ** EMPTY **");
		} else {
			writer.println(logPrefix + "Method annotations:");
			for (String methodName : methodAnnotationNames.getHolders()) {
				writer.println(logPrefix + "  Method: " + methodName);
				for (String annoName : methodAnnotationNames.getHeld(methodName)) {
					writer.println(logPrefix + "    " + annoName);
				}
			}
		}

		if (initDescs.isEmpty()) {
			writer.println(logPrefix + "Inits: ** EMPTY **");
		} else {
			writer.println(logPrefix + "Inits:");
			for (String initDesc : initDescs) {
				writer.println(logPrefix + "  " + initDesc);
			}
		}

		if (initAnnotationNames.isEmpty()) {
			writer.println(logPrefix + "Init annotations: ** EMPTY **");
		} else {
			writer.println(logPrefix + "Init annotations:");
			for (String initName : initAnnotationNames.getHolders()) {
				writer.println(logPrefix + "  Init: " + initName);
				for (String annoName : initAnnotationNames.getHeld(initName)) {
					writer.println(logPrefix + "    " + annoName);
				}
			}
		}

		if (staticMethodDescs.isEmpty()) {
			writer.println(logPrefix + "Static methods: ** EMPTY **");
		} else {
			writer.println(logPrefix + "Static methods:");
			for (String staticMethodDesc : staticMethodDescs) {
				writer.println(logPrefix + "  " + staticMethodDesc);
			}
		}

		if (staticMethodAnnotationNames.isEmpty()) {
			writer.println(logPrefix + "Static method annotations: ** EMPTY **");
		} else {
			writer.println(logPrefix + "Static method annotations:");
			for (String staticMethodName : staticMethodAnnotationNames.getHolders()) {
				writer.println(logPrefix + "  Static method: " + staticMethodName);
				for (String annoName : staticMethodAnnotationNames.getHeld(staticMethodName)) {
					writer.println(logPrefix + "    " + annoName);
				}
			}
		}

		writer.println(logPrefix + "Class Data: END: " + getHashText());
	}
}
