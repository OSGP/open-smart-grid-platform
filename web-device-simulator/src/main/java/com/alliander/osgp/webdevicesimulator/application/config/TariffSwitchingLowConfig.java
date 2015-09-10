package com.alliander.osgp.webdevicesimulator.application.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.alliander.osgp.webdevicesimulator.application.tasks.TariffSwitchingLow;

@Configuration
@EnableScheduling
@PropertySource("file:${osp//webDeviceSimulator/config}")
public class TariffSwitchingLowConfig {

    private static final String PROPERTY_NAME_AUTONOMOUS_TASKS_TARIFFSWITCHING_LOW_CRON_EXPRESSION = "autonomous.tasks.tariffswitching.low.cron.expression";
    private static final String PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_LOW_POOL_SIZE = "autonomous.tasks.tariffswitching.low.pool.size";
    private static final String PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_LOW_THREAD_NAME_PREFIX = "autonomous.tasks.tariffswitching.low.thread.name.prefix";

    @Resource
    private Environment environment;

    @Autowired
    private TariffSwitchingLow tariffSwitchingLow;

    @Bean
    public CronTrigger tariffSwitchingLowTrigger() {
        final String cron = this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TASKS_TARIFFSWITCHING_LOW_CRON_EXPRESSION);
        return new CronTrigger(cron);
    }

    @Bean(destroyMethod = "shutdown")
    public TaskScheduler tariffSwitchingLowTaskScheduler() {
        final ThreadPoolTaskScheduler tariffSwitchingLowTaskScheduler = new ThreadPoolTaskScheduler();
        tariffSwitchingLowTaskScheduler.setPoolSize(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_LOW_POOL_SIZE)));
        tariffSwitchingLowTaskScheduler.setThreadNamePrefix(this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_LOW_THREAD_NAME_PREFIX));
        tariffSwitchingLowTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
        tariffSwitchingLowTaskScheduler.setAwaitTerminationSeconds(10);
        tariffSwitchingLowTaskScheduler.initialize();
        tariffSwitchingLowTaskScheduler.schedule(this.tariffSwitchingLow, this.tariffSwitchingLowTrigger());
        return tariffSwitchingLowTaskScheduler;
    }

}
