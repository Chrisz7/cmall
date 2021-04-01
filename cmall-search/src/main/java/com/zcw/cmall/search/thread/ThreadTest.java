package com.zcw.cmall.search.thread;

import java.util.concurrent.*;

/**
 * @author Chrisz
 * @date 2020/12/2 - 8:47
 */
public class ThreadTest {
    public static ExecutorService pool = Executors.newFixedThreadPool(10);



    public  void Thread(String[] args) throws ExecutionException, InterruptedException {

        System.out.println("main...start...");
        //1.继承Thread
        //1.1启动
        //1.2在后台运行
        /**
         * Thread01 thread01 = new Thread01();
         * thread01.start();
         */
        //2.实现Runnable接口
        //2.1启动
        /**
         * Runnable01 runnable01 = new Runnable01();
         * new Thread(runnable01).start();
         */

        //3.实现Callable接口
        /**
         * FutureTask<Integer> integerFutureTask = new FutureTask<>(new Callable01());
         *  new Thread(integerFutureTask).start();
         *  //阻塞等待整个线程执行完成，获取返回结果
         *  Integer integer = integerFutureTask.get();
         */

        //以上三种都不用
        //4.线程池  给线程池直接提交任务
        /**
         * pool.execute(new Runnable01());
         */

        /**
         * 七大参数
         * int corePoolSize,   核心线程数量 只要线程池不销毁一直存在，除非设置了【allowCoreThreadTimeOut】  线程池创建好之后就准备就绪的线程数量，等待异步任务执行
         * int maximumPoolSize,最大线程数量，控制资源并发
         * long keepAliveTime, 存活时间，如果当前的线程数量大于核心数量，释放空闲的线程（最大线程数量-核心线程数量 maximumPoolSize-corePoolSize
         * TimeUnit unit,      时间单位
         * BlockingQueue<Runnable> workQueue,阻塞队列，如果任务有很多，将多的任务放进队列中等待，有线程空闲的话，去队列取出新的任务  new LinkedBlockingDeque<>(), 默认是Integer的最大值，可能导致内存不够
         * ThreadFactory threadFactory, 线程的创建工厂，
         * RejectedExecutionHandler handler 如果队列满了，按照指定的拒接策略拒绝执行任务
         *
         * 工作顺序：
         * 1.线程池创建，就准备好core数量的核心线程，准备接受任务，一直存在，除非设置了【allowCoreThreadTimeOut】
         * 2.core满了，将别的任务放进阻塞队列中等待，如果有core线程空闲就去队列中取新的任务执行
         * 3.阻塞队列满了，直接开新线程执行，最大能开max指定的数量
         * 4.max满了，使用拒绝策略拒绝执行
         * 5.max都执行完成，有很多空闲，在指定keepAliveTime，释放max-core数量的线程
         */
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5,
                                                                200,
                                                                10,
                                                                TimeUnit.SECONDS,
                                                                new LinkedBlockingDeque<>(100000),
                                                                Executors.defaultThreadFactory(),
                                                                new ThreadPoolExecutor.AbortPolicy());

        System.out.println("mian...end...");
    }

    public static class Thread01 extends Thread{

        @Override
        public void run() {
            System.out.println("当前线程:"+Thread.currentThread().getId());
            int i = 10 / 2 ;
            System.out.println("运行结果:"+i);
        }
    }
    public static class Runnable01 implements Runnable{

        @Override
        public void run() {
            System.out.println("当前线程:"+Thread.currentThread().getId());
            int i = 10 / 2 ;
            System.out.println("运行结果:"+i);
        }
    }
    public static class Callable01 implements Callable<Integer>{
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程:"+Thread.currentThread().getId());
            int i = 10 / 2 ;
            System.out.println("运行结果:"+i);
            return i;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        System.out.println("main...start...");
        //1.创建异步对象
//        CompletableFuture.runAsync(()->{
//            System.out.println("当前线程:"+Thread.currentThread().getId());
//            int i = 10 / 2 ;
//            System.out.println("运行结果:"+i);
//        },pool);
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果:" + i);
//            return i;
//        }, pool).whenComplete((result,excption)->{
//            System.out.println("异步任务完成了...结果:"+result+"异常:"+excption);
//        }).exceptionally(throwable -> {
//            return 10;
//        });
        //future.get()阻塞等结果
        //Integer integer = future.get();

        //方法完成后的处理
//        CompletableFuture<Integer> handle = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果:" + i);
//            return i;
//        }, pool).handle((result, excption) -> {
//            if (result != null) {
//                return result * 2;
//            }
//            if (excption != null) {
//                return 0;
//            }
//            return 0;
//        });
//        Integer integer = handle.get();

//         CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果:" + i);
//            return i;
//        }, pool).thenRunAsync(()->{
//            System.out.println("任务2启动了");
//        },pool);
        CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getId());
            int i = 10 / 4;
            System.out.println("运行结果:" + i);
            return i;
        }, pool).thenAcceptAsync(res ->{
            System.out.println("任务3启动了"+res);
        },pool);
        System.out.println("mian...end...");
    }

}
