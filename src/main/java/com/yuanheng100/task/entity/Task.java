package com.yuanheng100.task.entity;

/**
 * 所有任务的最高父类，定义了id和result
 * @author jlqian
 *
 */
public class Task {
	
	protected Integer id;
	
	protected Integer result;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}
}
