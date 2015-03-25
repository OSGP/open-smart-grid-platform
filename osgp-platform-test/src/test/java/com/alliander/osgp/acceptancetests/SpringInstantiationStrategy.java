package com.alliander.osgp.acceptancetests;

import java.lang.reflect.InvocationTargetException;

import org.givwenzen.annotations.InstantiationState;
import org.givwenzen.annotations.InstantiationStateCreator;
import org.givwenzen.annotations.InstantiationStrategy;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public class SpringInstantiationStrategy implements InstantiationStrategy {
    private ApplicationContext applicationContext;

    public SpringInstantiationStrategy(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public InstantiationState instantiate(final Class<?> markedClass, final Object parameter) throws InvocationTargetException, InstantiationException,
            IllegalAccessException {

        final InstantiationStateCreator creator = new InstantiationStateCreator();

        if (markedClass.isAnnotationPresent(Configurable.class)) {
            final AutowireCapableBeanFactory factory = this.applicationContext.getAutowireCapableBeanFactory();
            final Object object = factory.createBean(markedClass, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
            return creator.didInstantiate(object);
        }

        return creator.didNotInstantiate();
    }

}
