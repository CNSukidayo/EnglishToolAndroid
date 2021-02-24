package com.cnsukidayo.englishtoolandroid.core.cache;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 枚举类,天生单例.
 * 有待优化
 */
public enum CacheQueue {

    SINGLE {
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        private final ConcurrentHashMap<String, BlockingQueue<?>> cache = new ConcurrentHashMap<>();

        @RequiresApi(api = Build.VERSION_CODES.N)
        public <T> void doWork(String taskName, Supplier<T> supplier) {
            BlockingQueue<T> blockingQueue = new LinkedBlockingDeque<>(1);
            executorService.execute(() -> blockingQueue.add(supplier.get()));
            cache.put(taskName, blockingQueue);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public <T> T get(String taskName) {
            try {
                return (T) cache.get(taskName).take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public <T> void addWork(String taskName, Consumer<T> consumer) {
            BlockingQueue<Consumer<T>> blockingQueue = new LinkedBlockingDeque<>(1);
            executorService.execute(() -> blockingQueue.add(consumer));
            cache.put(taskName, blockingQueue);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public <T> void accept(String taskName, T args) {
            try {
                Object take = cache.get(taskName).take();
                if (take instanceof Consumer) {
                    ((Consumer) take).accept(args);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    };

    /**
     * 直接运行一个任务,添加的任务会即刻使用非主线程来执行.任务执行完毕立即丢失,只保留任务返回值.<br>
     * 如果任务执行时间过长,调用{@link #get(String)}方法时会阻塞.
     *
     * @param taskName 任务名称
     * @param supplier 任务的供应者,返回值延迟获取,由不同的线程调用{@link #get(String)}方法获得返回值.
     * @param <T>      泛型类型,泛型类型需要自我控制.
     */
    public abstract <T> void doWork(String taskName, Supplier<T> supplier);

    /**
     * 阻塞方法,异步获得doWork任务执行的结果.
     *
     * @param taskName 任务名称
     * @param <T>      泛型类型
     * @return 返回值的泛型类型应当和doWork中指定的泛型一致
     */
    public abstract <T> T get(String taskName);

    /**
     * 添加(注册)一个任务,但是不着急执行.注意该任务可被反复执行.
     *
     * @param taskName 任务名称
     * @param supplier 消费者接口
     * @param <T>
     */
    public abstract <T> void addWork(String taskName, Consumer<T> supplier);

    /**
     * 执行待执行的任务,这个任务只能是Consumer接口
     *
     * @param taskName 任务名称
     * @param args     传给待执行任务的参数
     */
    public abstract <T> void accept(String taskName, T args);


}
