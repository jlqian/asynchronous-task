package com.yuanheng100.task.adaptor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import com.yuanheng100.task.entity.Task;
import com.yuanheng100.task.resolver.Resolver;
import com.yuanheng100.task.service.AbstractTaskService;

/**
 * 适配器抽象类
 * 作用：为适配器子类提供线程数可自定义的ThreadPoolExecutor，通过ThreadPoolExecutor可以完成
 *      向队列中放置任务，最终由线程池中的线程调用由子类实现的asynComplete(T, AbstractTaskService<T>)方法
 * 使用：由具体的适配器继承，并实现
 *      com.yuanheng100.task.adaptor.Adapter.asynComplete(T, AbstractTaskService<T>)
 *      方法，这个方法是完成任务逻辑的核心方法
 * @author jlqian
 *
 * @param <T>
 */
public abstract class Adapter<T extends Task> {
	
	Logger logger = Logger.getLogger(Adapter.class);
	
	//线程池数量，可以通过setPoolSize(Integer poolSize)方法对其进行设置
	protected Integer poolSize = 10;
	//线程池
	protected ThreadPoolExecutor executorService;
	
	/**
	 * 初始化线程池
	 */
	public void init(){
		logger.info("线程池初始化开始");
		start();
	}
	
	private void start(){
		executorService = (ThreadPoolExecutor)Executors.newFixedThreadPool(poolSize);
	}
	
	public void setPoolSize(Integer poolSize) {
		this.poolSize = poolSize;
	}

	/**
	 * 将异步任务添加到任务队列中
	 * @param task
	 * @param taskService
	 * @return
	 */
	public boolean complete(T task , AbstractTaskService<T> taskService){
		Resolver<T> resolver = new Resolver<T>(task, taskService);
		executorService.execute(resolver);
		return true;
	}
	
	/**
	 * 子类必须实现的异步任务完成方式
	 * @param task
	 * @param taskService
	 * @return
	 */
	public abstract T asynComplete(T task , AbstractTaskService<T> taskService);
}
