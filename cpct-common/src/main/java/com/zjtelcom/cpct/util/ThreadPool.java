package com.zjtelcom.cpct.util;

import java.util.concurrent.*;

public class ThreadPool {
    /**
     * // 构造一个任务池
     * 参数说明：
     * corePoolSize - 池中所保存的线程数，包括空闲线程。
     * maximumPoolSize - 池中允许的最大线程数。
     * keepAliveTime - 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。
     * unit - keepAliveTime 参数的时间单位。
     * workQueue - 执行前用于保持任务的队列。此队列仅保持由 execute 方法提交的 Runnable 任务。
     * threadFactory - 执行程序创建新线程时使用的工厂。
     * handler - 由于超出线程范围和队列容量而使执行被阻塞时所使用的处理程序
     */
//    private ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 200,TimeUnit.SECONDS, new LinkedBlockingQueue<>(32));
//    private static ThreadPool instance = null;

    //使用volatile关键字保其可见性
    volatile public static ThreadPoolExecutor threadPool = null;

    private ThreadPool(){}


    /**
     * 无返回值直接执行
     * @param runnable
     */
    public  static void execute(Runnable runnable){
        getThreadPool().execute(runnable);
    }

    /**
     * 返回值直接执行
     * @param callable
     */
    public  static <T> Future<T> submit(Callable<T> callable){
        return  getThreadPool().submit(callable);
    }

    /**
     * 关闭线程池
     */
//    public static void shutdown() {
//        executor.shutdown();
//    }

    /**
     * dcs获取线程池
     * @return 线程池对象
     *
     * threadPool = new ThreadPoolExecutor(corePoolSize,// 核心线程数
        maximumPoolSize, // 最大线程数
        keepAliveTime, // 闲置线程存活时间
        TimeUnit.MILLISECONDS,// 时间单位
        new LinkedBlockingDeque<Runnable>(Integer.MAX_VALUE),// 线程队列
        Executors.defaultThreadFactory(),// 线程工厂
        new ThreadPoolExecutor.AbortPolicy() {// 队列已满,而且当前线程数已经超过最大线程数时的异常处理策略
        ————————————————
        版权声明：本文为CSDN博主「包子君Afeng」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
        原文链接：https://blog.csdn.net/jiaqiangziji/article/details/85955045
     */
//    public static ThreadPoolExecutor getThreadPool() {
//        if (threadPool != null) {
//            return threadPool;
//        } else {
//            synchronized (ThreadPool.class) {
//                if (threadPool == null) {
//                    threadPool = new ThreadPoolExecutor(8, 16, 60, TimeUnit.SECONDS,
//                            new LinkedBlockingQueue<>(32), new ThreadPoolExecutor.CallerRunsPolicy());
//                }
//                return threadPool;
//            }
//        }
//    }


    public static ThreadPoolExecutor getThreadPool() {
        try {
            if (threadPool != null) {
                return threadPool;
            } else {
                synchronized (ThreadPool.class) {
                    if (threadPool == null) {//二次检查
                        //创建实例之前可能会有一些准备性的耗时工作
                        Thread.sleep(100);
                        int cpuNum = Runtime.getRuntime().availableProcessors();// 获取处理器数量
                        int threadNum = cpuNum * 2 + 1;// 根据cpu数量,计算出合理的线程并发数
                        threadPool = new ThreadPoolExecutor(
                                threadNum - 1,// 核心线程数
                                threadNum, // 最大线程数
                                Integer.MAX_VALUE, // 闲置线程存活时间
                                TimeUnit.MILLISECONDS,// 时间单位
                                new LinkedBlockingDeque<Runnable>(Integer.MAX_VALUE),// 线程队列
                                Executors.defaultThreadFactory(),// 线程工厂
                                new ThreadPoolExecutor.AbortPolicy() {// 队列已满,而且当前线程数已经超过最大线程数时的异常处理策略
                                    @Override
                                    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                                        super.rejectedExecution(r, e);
                                    }
                                }
                        );
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return threadPool;
    }


}
