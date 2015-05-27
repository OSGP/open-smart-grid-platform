package com.alliander.osgp.webdevicesimulator.application.config;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.alliander.osgp.webdevicesimulator.application.tasks.TariffSwitchingOn;

@Configuration
@EnableScheduling
@PropertySource("file:${osp//webDeviceSimulator/config}")
public class TariffSwitchingOnConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(TariffSwitchingOnConfig.class);

    private static final String PROPERTY_NAME_AUTONOMOUS_TASKS_TARIFFSWITCHING_ON_CRON_EXPRESSION = "autonomous.tasks.tariffswitching.on.cron.expression";
    private static final String PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_ON_POOL_SIZE = "autonomous.tasks.tariffswitching.on.pool.size";
    private static final String PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_ON_THREAD_NAME_PREFIX = "autonomous.tasks.tariffswitching.on.thread.name.prefix";

    @Resource
    private Environment environment;

    @Autowired
    private TariffSwitchingOn tariffSwitchingOn;

    @Bean
    public CronTrigger tariffSwitchingOnTrigger() {
        final String cron = this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TASKS_TARIFFSWITCHING_ON_CRON_EXPRESSION);
        return new CronTrigger(cron);
    }

    @Bean(destroyMethod = "shutdown")
    public TaskScheduler tariffSwitchingOnTaskScheduler() {
        final ThreadPoolTaskScheduler tariffSwitchingOnTaskScheduler = new ThreadPoolTaskScheduler();
        tariffSwitchingOnTaskScheduler.setPoolSize(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_ON_POOL_SIZE)));
        tariffSwitchingOnTaskScheduler.setThreadNamePrefix(this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_ON_THREAD_NAME_PREFIX));
        tariffSwitchingOnTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
        tariffSwitchingOnTaskScheduler.setAwaitTerminationSeconds(10);
        tariffSwitchingOnTaskScheduler.initialize();
        tariffSwitchingOnTaskScheduler.schedule(this.tariffSwitchingOn, this.tariffSwitchingOnTrigger());
        return tariffSwitchingOnTaskScheduler;
    }

}
