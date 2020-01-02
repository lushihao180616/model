package com.lushihao.model.quartz.config;

import com.lushihao.model.property.util.LSHPropertyUtils;
import com.lushihao.model.quartz.vo.Job0;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Date;

@Configuration
public class QuartzConfig {
    public static final String JOB0 = "job0";
    public static final String JOB1 = "job1";
    public static final String JOB2 = "job2";
    public static final String JOB3 = "job3";
    public static final String JOB4 = "job4";
    public static final String JOB5 = "job5";
    public static final String JOB6 = "job6";
    public static final String JOB7 = "job7";
    public static final String JOB8 = "job8";
    public static final String JOB9 = "job9";

    public static final String GROUP0 = "group0";
    public static final String GROUP1 = "group1";
    public static final String GROUP2 = "group2";
    public static final String GROUP3 = "group3";
    public static final String GROUP4 = "group4";

    @Autowired
    private LSHPropertyUtils lshPropertyUtils;
    /**
     * 任务调度
     */
    @Resource
    private Scheduler scheduler;

    /**
     * 开始执行定时任务
     *
     * @throws SchedulerException
     */
    public void startJob() throws SchedulerException {
        startJobTask(scheduler);
        scheduler.start();
    }

    /**
     * 启动定时任务
     *
     * @param scheduler
     * @throws SchedulerException
     */
    private void startJobTask(Scheduler scheduler) throws SchedulerException {
        String clareSaveSbbInfoCron = lshPropertyUtils.getPropertiesValue("job1Cron");

        JobDetail jobDetail = JobBuilder.newJob(Job0.class).withIdentity(JOB0, GROUP0).build();
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(clareSaveSbbInfoCron);
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(JOB0, GROUP0)
                .withSchedule(cronScheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

    /**
     * 获取Job信息
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    public String getjobInfo(String name, String group) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(name, group);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        return String.format("time:%s,state:%s", cronTrigger.getCronExpression(),
                scheduler.getTriggerState(triggerKey).name());
    }

    /**
     * 修改任务的执行时间
     *
     * @param name
     * @param group
     * @param cron  cron表达式
     * @return
     * @throws SchedulerException
     */
    public boolean modifyJob(String name, String group, String cron) throws SchedulerException {
        Date date = null;
        TriggerKey triggerKey = new TriggerKey(name, group);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        String oldTime = cronTrigger.getCronExpression();
        if (!oldTime.equalsIgnoreCase(cron)) {
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                    .withSchedule(cronScheduleBuilder).build();
            date = scheduler.rescheduleJob(triggerKey, trigger);
        }
        return date != null;
    }

    /**
     * 暂停所有任务
     *
     * @throws SchedulerException
     */
    public void pauseAllJob() throws SchedulerException {
        scheduler.pauseAll();
    }

    /**
     * 暂停某个任务
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    public void pauseJob(String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler.pauseJob(jobKey);
    }

    /**
     * 恢复所有任务
     *
     * @throws SchedulerException
     */
    public void resumeAllJob() throws SchedulerException {
        scheduler.resumeAll();
    }

    /**
     * 恢复某个任务
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    public void resumeJob(String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler.resumeJob(jobKey);
    }

    /**
     * 删除某个任务
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    public void deleteJob(String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler.deleteJob(jobKey);
    }

}
