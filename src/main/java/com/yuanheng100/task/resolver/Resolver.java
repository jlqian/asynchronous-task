package com.yuanheng100.task.resolver;

import com.yuanheng100.task.entity.Task;
import com.yuanheng100.task.service.AbstractTaskService;

/**
 * 放到队列中的Runnable实例类
 * @author jlqian
 *
 * @param <T>
 */
public class Resolver<T extends Task> implements Runnable{
	
	private T task;
	
	private AbstractTaskService<T> taskService;
	
	public Resolver(T task , AbstractTaskService<T> taskService) {
		this.task = task;
		this.taskService = taskService;
	}

	@Override
	public void run() {
		taskService.asynComplete(task);
	}

}
