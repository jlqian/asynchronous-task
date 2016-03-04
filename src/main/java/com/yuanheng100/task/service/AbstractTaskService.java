package com.yuanheng100.task.service;

import org.apache.log4j.Logger;

import com.yuanheng100.task.adaptor.Adapter;
import com.yuanheng100.task.entity.Task;

/**
 * 用户自定义的Service需要继承的类，在用户实例化自定义Service时，需要由Spring注入Adapter对象
 * @author jlqian
 *
 * @param <T>
 */
public abstract class AbstractTaskService<T extends Task> implements ITaskService{
	
	Logger logger = Logger.getLogger(AbstractTaskService.class);
	
	private Adapter<T> adapter;
	
	public AbstractTaskService() {
		
	}

	public void setAdapter(Adapter<T> adapter) {
		this.adapter = adapter;
	}

	/**
	 * 自定义的Service，调用次方法将任务放到队列中
	 * @param task
	 * @return
	 */
	public boolean adapt(T task){
		return adapter.complete(task,this);
	}
	
	public T asynComplete(T task){
		return adapter.asynComplete(task, this);
	}
	
}
