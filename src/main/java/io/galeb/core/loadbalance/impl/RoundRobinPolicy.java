package io.galeb.core.loadbalance.impl;

import io.galeb.core.loadbalance.LoadBalancePolicy;

public class RoundRobinPolicy extends LoadBalancePolicy {

    @Override
    public int getChoice() {
        return last.incrementAndGet() % hosts.size();
    }

    @Override
    public synchronized void reset() {
        last.set(0);
    }

}
