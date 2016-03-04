package com.yuanheng100.task.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ClassUtils {

	/**
	 * 获取父类泛型类型
	 * 步骤：获取所有带泛型的父类，在获取父类的泛型，最终返回所有直接父类的泛型类型
	 * @return 
	 */
	public static Class<?>[] getGenericClass(Class<?> clazz){
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Type superclass = clazz.getGenericSuperclass();
		if(superclass instanceof ParameterizedType){
			Type[] actualTypeArguments = ((ParameterizedType) superclass).getActualTypeArguments();
			for (int i = 0; i < actualTypeArguments.length; i++) {
				Type type = actualTypeArguments[i];
				if(type instanceof Class){
					classes.add((Class<?>) type);
				}
			}
		}
		return classes.toArray(new Class[0]);
	}
	
	/**
	 * 获取类的简短类名
	 * @param clazz
	 * @return
	 */
	public static String getShortClassName(Class<?> clazz){
		return clazz.getSimpleName();
	}
	
	/**
	 * 获取所有的属性：只包含父类，不包含接口中定义的属性
	 * @param clazz
	 * @return
	 */
	public static List<Field> getField(Class<?> clazz){
		List<Field> fields = new LinkedList<Field>();
		
		Class<?> currentClazz = clazz;
		Field[] declaredFields = currentClazz.getDeclaredFields();
		List<Field> asList = Arrays.asList(declaredFields);
		fields.addAll(asList);
		
		while((currentClazz = currentClazz.getSuperclass())!=null){
			declaredFields = currentClazz.getDeclaredFields();
			asList = Arrays.asList(declaredFields);
			fields.addAll(asList);
		}
		return fields;
	}
}
