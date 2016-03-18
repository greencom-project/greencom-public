package eu.greencom.xgateway.scheduler.api.service;

import java.util.Map;

import org.quartz.SchedulerException;

public interface Scheduler {

	public void addJob(String className, String jobName,
			String jobGroup, Map<String, String> jobParams);

	public void removeJob(String jobName, String jobGroup);

	public void addTrigger(String className, String cronExpression,
			String triggerName, String triggerGroup, String jobName,
			String jobGroup, Map<String, String> jobParams)
			throws SchedulerException;

	public void removeTrigger(String triggerName, String triggerGroup);

	public void start();

	public void shutdown();

	public void pauseAllTriggers();

	public void pauseTriggersByGroup(String group);

	public void pauseTrigger(String name, String group);

	public void resumeAllTriggers();

	public void resumeTriggersByGroup(String group);

	public void resumeTrigger(String name, String group);

	public void clearScheduler();
}
