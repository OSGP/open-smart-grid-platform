package org.opensmartgridplatform.shared.application.services;

public interface PublisherService<T> {
    public void subscribe(SubscriberService<T> s);

    public void publish(T t);
}
