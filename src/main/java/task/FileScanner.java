package task;

import app.FileMeta;
import callback.FileScannerCallBack;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: xinyan
 * @data: 2022/07/18/20:53
 **/
@Data
public class FileScanner {

    // 当前扫描的文件个数
    private AtomicInteger fileNum = new AtomicInteger();

    // 当前扫描的文件夹个数
    // 最开始扫描的根路径没有统计，因此初始化文件夹的个数为1，表示从根目录下开始进行扫描任务
    private AtomicInteger dirNum = new AtomicInteger();

    // 所有扫描文件的子线程个数，只有当子线程个数为0时，主线程再继续执行
    private AtomicInteger threadCount = new AtomicInteger();

    // 当最后一个子线程执行完任务之后，再调用countDown方法唤醒主线程
    private CountDownLatch latch = new CountDownLatch(1);

    // 获取当前电脑的可用CPU个数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    // 线程池创建对象
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT*2, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy());

    private FileScannerCallBack callBack;
    public FileScanner(FileScannerCallBack callBack) {
        this.callBack = callBack;
    }

    public void scan(File filePath) {
        System.out.println("开始文件扫描任务，根目录为 : " + filePath);
        long start = System.nanoTime();

        // 将具体的扫描任务交给子线程处理
        scanInternal(filePath);
        threadCount.incrementAndGet();
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println("扫描任务中断，根目录为 : " + filePath);
        }finally {
            System.out.println("关闭线程池.....");
            // 当所有子线程已经执行结束，就是正常关闭
            // 中断任务，需要立即停止所有还在扫描的子线程
            pool.shutdownNow();
        }

        long end = System.nanoTime();
        System.out.println("文件扫描任务结束，共耗时 : " + (end - start) * 1.0 / 1000000 + "ms");
        System.out.println("文件扫描任务结束，根目录为 : " + filePath);
        System.out.println("共扫描到 : " + fileNum.get() + "个文件");
        System.out.println("共扫描到 : " + dirNum.get() + "个文件夹");
    }

    private void scanInternal(File filePath) {
        if (filePath == null) {
            return;
        }
        pool.submit(() -> {

            // 使用回调函数，将当前目录下的所有内容保存到指定终端
            this.callBack.callback(filePath);
            // 先将当前这一级目录下的file对象获取出来
            File[] files = filePath.listFiles();
            // 遍历这些file对象，根据是否是文件夹进行区别处理
            for (File file : files) {
                if (file.isDirectory()) {
                    // 等同于 ++i
                    dirNum.incrementAndGet();
                    // 将子文件夹的任务交给新线程处理
                    // 碰到文件夹递归创建新线程
                    threadCount.incrementAndGet();
                    scanInternal(file);
                }else {
                    // 等同于 i++
                    fileNum.getAndIncrement();
                }
            }
            // 当前线程将这一级目录下的文件夹(创建新线程递归处理)和文件的保存扫描任务执行结束
            System.out.println(Thread.currentThread().getName() + "扫描 : " + filePath + "任务结束");
            // 子线程数 --
            threadCount.decrementAndGet();
            if (threadCount.get() == 0) {
                // 所有线程已经结束任务
                System.out.println("所有扫描任务结束");
                // 唤醒主线程
                latch.countDown();
            }
        });
    }
}
