package com.yuanheng100.task.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DataSourceUtils {

	private  static DataSource dataSource = null;
	//解决事务管理
	private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();
	/**
	 * 获取数据库连接
	 * @return
	 */
	public static Connection getConnection(){
		Connection connection = null;
		try {
			connection = threadLocal.get();
			if(connection==null){
				connection = dataSource.getConnection();
				threadLocal.set(connection);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	/**
	 * 关闭数据库连接，同时在本地线程移除数据库连接的变量副本
	 * @param connection
	 * @param prepareStatement
	 * @param resultSet
	 */
	public static void closeConnection(Connection connection,PreparedStatement prepareStatement,ResultSet resultSet){
		if(resultSet!=null){
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(prepareStatement!=null){
			try {
				prepareStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(connection!=null){
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		threadLocal.remove();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		DataSourceUtils.dataSource = dataSource;
	}
	
}
