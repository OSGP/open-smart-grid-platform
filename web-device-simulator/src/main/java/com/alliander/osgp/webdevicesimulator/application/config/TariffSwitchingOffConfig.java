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

import com.alliander.osgp.webdevicesimulator.application.tasks.TariffSwitchingOff;

@Configuration
@EnableScheduling
@PropertySource("file:${osp//webDeviceSimulator/config}")
public class TariffSwitchingOffConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(TariffSwitchingOffConfig.class);

    private static final String PROPERTY_NAME_AUTONOMOUS_TASKS_TARIFFSWITCHING_OFF_CRON_EXPRESSION = "autonomous.tasks.tariffswitching.off.cron.expression";
    private static final String PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_OFF_POOL_SIZE = "autonomous.tasks.tariffswitching.off.pool.size";
    private static final String PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_OFF_THREAD_NAME_PREFIX = "autonomous.tasks.tariffswitching.off.thread.name.prefix";

    @Resource
    private Environment environment;

    @Autowired
    private TariffSwitchingOff tariffSwitchingOff;

    @Bean
    public CronTrigger tariffSwitchingOffTrigger() {
        final String cron = this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TASKS_TARIFFSWITCHING_OFF_CRON_EXPRESSION);
        return new CronTrigger(cron);
    }

    @Bean(destroyMethod = "shutdown")
    public TaskScheduler tariffSwitchingOffTaskScheduler() {
        final ThreadPoolTaskScheduler tariffSwitchingOffTaskScheduler = new ThreadPoolTaskScheduler();
        tariffSwitchingOffTaskScheduler.setPoolSize(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_OFF_POOL_SIZE)));
        tariffSwitchingOffTaskScheduler.setThreadNamePrefix(this.environment
                .getRequiredProperty(PROPERTY_NAME_AUTONOMOUS_TARIFFSWITCHING_OFF_THREAD_NAME_PREFIX));
        tariffSwitchingOffTaskScheduler.setWaitForTasksToCompleteOnShutdown(false);
        tariffSwitchingOffTaskScheduler.setAwaitTerminationSeconds(10);
        tariffSwitchingOffTaskScheduler.initialize();
        tariffSwitchingOffTaskScheduler.schedule(this.tariffSwitchingOff, this.tariffSwitchingOffTrigger());
        return tariffSwitchingOffTaskScheduler;
    }

}
