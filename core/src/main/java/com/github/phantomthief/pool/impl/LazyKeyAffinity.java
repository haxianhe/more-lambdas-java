package com.github.phantomthief.pool.impl;

import static com.github.phantomthief.util.MoreSuppliers.lazy;

import java.util.Iterator;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.github.phantomthief.pool.KeyAffinity;
import com.github.phantomthief.util.MoreSuppliers.CloseableSupplier;

/**
 * @author w.vela
 * Created on 2018-02-08.
 */
class LazyKeyAffinity<K, V> implements KeyAffinity<K, V> {

    private final CloseableSupplier<KeyAffinityImpl<K, V>> factory;

    LazyKeyAffinity(Supplier<KeyAffinityImpl<K, V>> factory) {
        this.factory = lazy(factory, false);
    }

    // 核心方法, 从一个key选出线程池
    @Nonnull
    public V select(K key) {
        return factory.get().select(key);
    }

    // 当每一个key执行完之后回收处理这个key的线程池.
    public void finishCall(K key) {
        factory.get().finishCall(key);
    }

    @Override
    public boolean inited() {
        return factory.isInitialized();
    }

    @Override
    public void close() throws Exception {
        factory.tryClose(KeyAffinity::close);
    }

    @Override
    public Iterator<V> iterator() {
        return factory.get().iterator();
    }
}
