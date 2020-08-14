package com.zhange.yang_ge;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HighThreadDeal {

	public static void main(String[] args) throws InterruptedException {
		
		ShareResource resource = new ShareResource(new ArrayBlockingQueue<String>(10));
		new Thread(() -> {
			System.out.println("生产线程启动成功……");
			try {
				resource.product();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		},"Product").start();
		new Thread(() -> {
			System.out.println("消费线程启动成功……");
			System.out.println();
			System.out.println();
			try {
				resource.consumer();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		},"Consumer").start();
		
		TimeUnit.SECONDS.sleep(5);
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("五秒钟时间到了，大老板main线程叫停，活动结束");
		
		resource.stop();
	}
}

class ShareResource{
	
	private volatile boolean flag = true;
	private AtomicInteger atomicInteger = new AtomicInteger();
	
	private BlockingQueue<String> blockingQueue;
	
	public ShareResource(BlockingQueue<String> blockingQueue){
		this.blockingQueue = blockingQueue;
		System.out.println("当前的BlockingQueue具体的实现类是：" + blockingQueue.getClass().getName());
	}
	
	public void product() throws InterruptedException{
		
		String num;
		boolean retValue;
		while(flag){
			num = atomicInteger.incrementAndGet() + "";
			retValue = blockingQueue.offer(num, 2L, TimeUnit.SECONDS);
			if(retValue){
				System.out.println(Thread.currentThread().getName() + "\t 插入队列" + num + "成功");
			}else{
				System.out.println(Thread.currentThread().getName() + "\t 插入队列" + num + "失败");
			}
			
			TimeUnit.SECONDS.sleep(1);
		}
		System.out.println(Thread.currentThread().getName() + "\t 大老板叫停了，表示flag=false，生产动作结束。");
	}
	
	public void consumer() throws InterruptedException{
		
		String num = null;
		while(flag){
			num = blockingQueue.poll(2L, TimeUnit.SECONDS);
			if(null == num || num.equals("")){
				flag = false;
				System.out.println(Thread.currentThread().getName() + "\t 超过两秒没有取到数据，退出");
				return;
			}
			System.out.println(Thread.currentThread().getName() + "\t 消费队列" + num + "成功");
		}
		
	}
	
	public void stop(){
		this.flag = false;
	}
}