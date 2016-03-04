package com.yuanheng100.task.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * 容器调用close()方法时，触发onApplicationEvent(ContextClosedEvent event)方法
 * @author jlqian
 *
 */
public class SystemClosedListener implements ApplicationListener<ContextClosedEvent>{
	
	private Logger logger = Logger.getLogger(SystemClosedListener.class);

	/**
	 * 表示系统退出
	 */
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		logger.info("系统正在退出...");
	}

}
