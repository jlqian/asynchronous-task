package com.yuanheng100.task.listener;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;

import com.yuanheng100.task.entity.Task;
import com.yuanheng100.task.persistent.Persistent;
import com.yuanheng100.task.service.AbstractTaskService;
import com.yuanheng100.task.util.ClassUtils;
import com.yuanheng100.task.util.DataSourceUtils;
import com.yuanheng100.task.util.SqlUtil;

/**
 * 容器调用start()方法时，将会执行onApplicationEvent(ContextStartedEvent event)方法
 * 在创建对象后，需要注册AbstractTaskService<Task>集合
 * @author jlqian
 *
 */
public class SystemStartedListener implements ApplicationListener<ContextStartedEvent>{
	
	private Logger logger = Logger.getLogger(SystemStartedListener.class);

	private AbstractTaskService<Task>[] taskServicees;
	
	public SystemStartedListener() {
	}

	/**
	 * 系统初始化与系统恢复服务
	 * 系统初始化：创建数据库表
	 * 系统恢复服务：根据注册的AbstractTaskService，查询结果为NULL的任务，并将其重新放到任务队列中
	 */
	@Override
	public void onApplicationEvent(ContextStartedEvent event) {
		logger.info("系统初始化开始...");
		for (AbstractTaskService<?> taskService : taskServicees) {
			Connection connection = DataSourceUtils.getConnection();
			Class<?>[] genericClass = ClassUtils.getGenericClass(taskService.getClass());
			if(genericClass.length==1){
				String createTables = SqlUtil.createTables(genericClass[0]);
				Persistent.createTable(connection, createTables);
			}
		}
		logger.info("系统初始化完成...");
		
		logger.info("系统恢复服务开始...");
		for (AbstractTaskService<Task> taskService : taskServicees) {
			try {
				Connection connection = DataSourceUtils.getConnection();
				Class<?>[] genericClass = ClassUtils.getGenericClass(taskService.getClass());
				if(genericClass.length==1){
					String resume = SqlUtil.resume(genericClass[0]);
					try {
						List<? extends Task> resumeList = (List<? extends Task>) Persistent.resume(connection, resume, genericClass[0]);
						for (Task task : resumeList) {
							taskService.adapt(task);
						}
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		logger.info("系统恢复服务完成...");
	}

	public AbstractTaskService<Task>[] getTaskServicees() {
		return taskServicees;
	}

	public void setTaskServicees(AbstractTaskService<Task>[] taskServicees) {
		this.taskServicees = taskServicees;
	}
}
