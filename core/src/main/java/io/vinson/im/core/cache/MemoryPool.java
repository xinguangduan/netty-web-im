package io.vinson.im.core.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryPool<T extends MemoryObject> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<T> cache = Collections.synchronizedList(new ArrayList<T>());

    private int maxSize = 512;

    public MemoryPool() {
    }

    public MemoryPool(int max) {
        this.maxSize = max;
    }

    /**
     * 放回对象池并释放资源属性
     */
    public void put(T value) {
        synchronized (cache) {
            if (!cache.contains(value) && cache.size() < this.maxSize) {
                value.release();
                cache.add(value);
            }
        }
    }

    /**
     * 批量放回
     *
     * @author JiangZhiYong
     * @QQ 359135103 2017年11月8日 下午2:53:22
     * @param values
     */
    @SuppressWarnings("unchecked")
    public void putAll(T... values) {
        synchronized (cache) {
            for (T value : values) {
                if (value == null) {
                    continue;
                }
                if (!cache.contains(value) && cache.size() < this.maxSize) {
                    cache.add(value);
                }
                value.release();
            }
        }
    }

    /**
     * 获取缓存对象，如果没有，新建
     */
    public T get(Class<? extends T> c) {
        synchronized (cache) {
            if (!cache.isEmpty()) {
                return cache.remove(0);
            }
            try {
                return c.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public int size() {
        return cache.size();
    }

    public void clear() {
        synchronized (cache) {
            cache.clear();
        }
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
