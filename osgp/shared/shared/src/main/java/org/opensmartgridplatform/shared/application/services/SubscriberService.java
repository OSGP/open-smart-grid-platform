package org.opensmartgridplatform.shared.application.services;

public interface SubscriberService<T> {
    public void onNext(T t);
}
