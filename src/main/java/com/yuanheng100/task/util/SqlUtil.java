package com.yuanheng100.task.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuanheng100.task.entity.Task;

public class SqlUtil {
	
	//用来讲JAVA类型与MYSQL数据库的类型对应，并可以定义长度
	public static Map<String,String[]> javaTypeToSqlType = null;
	
	static {
		javaTypeToSqlType = new HashMap<String, String[]>();
		
		javaTypeToSqlType.put("Long", new String[]{"BIGINT",""});
		javaTypeToSqlType.put("Integer", new String[]{"INTEGER",""});
		javaTypeToSqlType.put("String", new String[]{"VARCHAR","(255)"});
		javaTypeToSqlType.put("Date", new String[]{"DATE",""});
		javaTypeToSqlType.put("BigDecimal", new String[]{"DECIMIAL",""});
		javaTypeToSqlType.put("Double", new String[]{"DOUBLE PRECISION",""});
		javaTypeToSqlType.put("Float", new String[]{"REAL",""});
		javaTypeToSqlType.put("Boolean", new String[]{"BIT",""});
	}

	/**
	 * 根据Class信息获取简单类名及属性，并将id属性对应的列设置为主键、自增
	 * @param clazz
	 * @return 创建表的DDL语句
	 */
	public static String createTables(Class<?> clazz){
		StringBuffer stringBuffer = new StringBuffer("CREATE TABLE IF NOT EXISTS ").append(clazz.getSimpleName())
				.append(" (");
		
		List<Field> fields = ClassUtils.getField(clazz);
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			String name = field.getName();
			String simpleName = field.getType().getSimpleName();
			
			String[] transformToSqlType = transformToSqlType(simpleName);
			
			stringBuffer.append("`").append(name).append("`").append(" ").append(transformToSqlType[0]).append(transformToSqlType[1]);
			
			if(name.equals("id")){
				stringBuffer.append(" UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY ");
			}
			
			if(i!=fields.size()-1){
				stringBuffer.append(",");
			}
		}
		
		stringBuffer.append(" )");
		return stringBuffer.toString();
	}
	
	/**
	 * 根据Class信息创建查询result为空的所有数据
	 * @param clazz
	 * @return 查询的DML语句
	 */
	public static String resume(Class<?> clazz){
		return "SELECT * FROM "+clazz.getSimpleName()+" WHERE result IS NULL";
	}
	
	/**
	 * 根据Class信息及当前Class的对象，插入数据库表中
	 * @param clazz
	 * @param task
	 * @return INSERT的DML语句
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static String save(Class<?> clazz,Task task) throws IllegalArgumentException, IllegalAccessException{
		StringBuffer stringBuffer = new StringBuffer("INSERT INTO ").append(clazz.getSimpleName())
				.append(" (");
		StringBuffer values = new StringBuffer(" VALUES(");
		
		 List<Field> fields = ClassUtils.getField(clazz);
		
		for (int i = 0; i < fields.size(); i++) {
			Field currentField = fields.get(i);
			if(!currentField.isAccessible()){
				currentField.setAccessible(true);
			}
			Object object = currentField.get(task);
			
			stringBuffer.append("`").append(currentField.getName()).append("`");
			
			if(currentField.getType().equals(String.class)){
				values.append("'").append(object).append("'");
			}else{
				values.append(object);
			}
			
			if(i!=fields.size()-1){
				stringBuffer.append(",");
				values.append(",");
			}
		}
		stringBuffer.append(" )").append(values).append(" )");
		return stringBuffer.toString();
	}
	
	/**
	 * 根据Class信息及当前Class的对象，将result保存到数据库中
	 * @param clazz
	 * @param task
	 * @return UPDATE的DML语句
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static String update(Class<?> clazz,Task task) throws IllegalArgumentException, IllegalAccessException{
		StringBuffer stringBuffer = new StringBuffer("UPDATE ").append(clazz.getSimpleName())
				.append(" SET result = ").append(task.getResult());
		return stringBuffer.toString();
	}
	/**
	 * 根据Java的简单类名，获取MySQL数据库对应的类型
	 * @param className
	 * @return
	 */
	private static String[] transformToSqlType(String className){
		return javaTypeToSqlType.get(className);
	}
}
