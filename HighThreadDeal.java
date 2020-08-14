package com.zhange.yang_ge;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HighThreadDeal {

	public static void main(String[] args) throws InterruptedException {
		
		ShareResource resource = new ShareResource(new ArrayBlockingQueue<String>(10));
		new Thread(() -> {
			System.out.println("�����߳������ɹ�����");
			try {
				resource.product();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		},"Product").start();
		new Thread(() -> {
			System.out.println("�����߳������ɹ�����");
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
		System.out.println("������ʱ�䵽�ˣ����ϰ�main�߳̽�ͣ�������");
		
		resource.stop();
	}
}

class ShareResource{
	
	private volatile boolean flag = true;
	private AtomicInteger atomicInteger = new AtomicInteger();
	
	private BlockingQueue<String> blockingQueue;
	
	public ShareResource(BlockingQueue<String> blockingQueue){
		this.blockingQueue = blockingQueue;
		System.out.println("��ǰ��BlockingQueue�����ʵ�����ǣ�" + blockingQueue.getClass().getName());
	}
	
	public void product() throws InterruptedException{
		
		String num;
		boolean retValue;
		while(flag){
			num = atomicInteger.incrementAndGet() + "";
			retValue = blockingQueue.offer(num, 2L, TimeUnit.SECONDS);
			if(retValue){
				System.out.println(Thread.currentThread().getName() + "\t �������" + num + "�ɹ�");
			}else{
				System.out.println(Thread.currentThread().getName() + "\t �������" + num + "ʧ��");
			}
			
			TimeUnit.SECONDS.sleep(1);
		}
		System.out.println(Thread.currentThread().getName() + "\t ���ϰ��ͣ�ˣ���ʾflag=false����������������");
	}
	
	public void consumer() throws InterruptedException{
		
		String num = null;
		while(flag){
			num = blockingQueue.poll(2L, TimeUnit.SECONDS);
			if(null == num || num.equals("")){
				flag = false;
				System.out.println(Thread.currentThread().getName() + "\t ��������û��ȡ�����ݣ��˳�");
				return;
			}
			System.out.println(Thread.currentThread().getName() + "\t ���Ѷ���" + num + "�ɹ�");
		}
		
	}
	
	public void stop(){
		this.flag = false;
	}
}