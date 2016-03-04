package com.yuanheng100.task.aop;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.yuanheng100.task.entity.Task;
import com.yuanheng100.task.persistent.Persistent;
import com.yuanheng100.task.util.DataSourceUtils;

/**
 * 储存任务和任务结果的切面
 * @author jlqian
 *
 */
@Aspect
public class CompleteInterceptor {
	
	private Logger logger = Logger.getLogger(CompleteInterceptor.class);

	/**
	 * 保存任务
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.yuanheng100.task.adaptor.Adapter.complete(..))")
	public Object completeAround(ProceedingJoinPoint pjp) throws Throwable{
		
		
		Object proceed = null;
		
		Object[] args = pjp.getArgs();
		
		Connection connection = DataSourceUtils.getConnection();
		
		Task task = (Task)args[0];
		
		if(task.getId()==null){
			logger.info("存储Task开始");
			Persistent.save(connection, (Task)args[0]);
			logger.info("存储Task结束");
		}

		
		proceed = pjp.proceed();
			
		return proceed;
	}
	
	/**
	 * 保存结果
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.yuanheng100.task.adaptor.Adapter.asynComplete(..))")
	public Object asynCompleteAround(ProceedingJoinPoint pjp) throws Throwable{
		
		Object proceed = null;
		
		Connection connection = DataSourceUtils.getConnection();
		
		proceed = pjp.proceed();
		
		logger.info("存储Task结果开始");

		Persistent.update(connection, (Task)proceed);

		logger.info("存储Task结果结束");
		
		return proceed;
	}
	
}
