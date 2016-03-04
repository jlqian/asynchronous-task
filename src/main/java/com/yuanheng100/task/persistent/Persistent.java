package com.yuanheng100.task.persistent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.yuanheng100.task.entity.Task;
import com.yuanheng100.task.util.DataSourceUtils;
import com.yuanheng100.task.util.SqlUtil;

/**
 * 持久化类
 * @author jlqian
 *
 */
public class Persistent {
	
	/**
	 * 保存一个任务，并返回主键
	 * @param connection
	 * @param task
	 */
	public static void save(Connection connection,Task task){
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			
			String sql = SqlUtil.save(task.getClass(), task);
			
			prepareStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			prepareStatement.executeUpdate();
			ResultSet generatedKeys = prepareStatement.getGeneratedKeys();
			while(generatedKeys.next()){
				int id = generatedKeys.getInt(1);
				task.setId(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DataSourceUtils.closeConnection(connection, prepareStatement, resultSet);
		}
		
	}
	
	/**
	 * 创建数据库表
	 * @param connection
	 * @param createTables
	 */
	public static void createTable(Connection connection,String createTables){
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(createTables);
			prepareStatement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DataSourceUtils.closeConnection(connection, prepareStatement, null);
		}
	}
	
	/**
	 * 查询结果为NULL的任务，即存储到数据库并没有发送的任务
	 * @param connection
	 * @param resume
	 * @param clazz
	 * @return
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> List<T> resume(Connection connection,String resume,Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException{
		List<T> list = new ArrayList<T>();
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			prepareStatement = connection.prepareStatement(resume);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				T newInstance = clazz.newInstance();
				Method[] methods = clazz.getMethods();
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				for (int i = 1 ; i <= columnCount ; i ++) {
					String columnName = metaData.getColumnName(i);
					Object object = resultSet.getObject(columnName);
					if(object==null){
						continue;
					}
					for (Method method : methods) {
						Class<?>[] parameterTypes = method.getParameterTypes();
						if(method.getName().equals("set"+Character.toUpperCase(columnName.charAt(0))+columnName.substring(1))){
							try {
								Constructor<?> constructor = parameterTypes[0].getConstructor(String.class);
								method.invoke(newInstance, constructor.newInstance(object.toString()));
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							break;
						}
					}
				}
				list.add(newInstance);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}finally{
			DataSourceUtils.closeConnection(connection, prepareStatement, null);
		}
		
		return list;
	}
	
	/**
	 * 保存任务结果
	 * @param connection
	 * @param task
	 */
	public static void update(Connection connection,Task task){
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			
			String sql = SqlUtil.update(task.getClass(), task);
			
			prepareStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			prepareStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DataSourceUtils.closeConnection(connection, prepareStatement, resultSet);
		}
	}
}
