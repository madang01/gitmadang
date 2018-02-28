package javapackage.java.util;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

public class ListPerfTest {

	private static final Queue<String> queue1 = new LinkedList<String>();

	private static final Queue<String> queue2 = new ArrayDeque<String>();

	private static int Count = 1000000;

	public static void main(String ... args) { 
	
	if (args.length > 1) { 
		Count = Integer.parseInt(args[0]); 
	} 
	
	System.out.println("Test starts"); 
	long start = System.nanoTime(); 
	for (int i = 0; i < Count; i++) {
		queue1.offer(i + ""); 
	}
	
	for(int i = (Count >> 1); i > 0; i++) {
		queue1.peek();
		queue1.poll();
	}
	
	long end = System.nanoTime();System.out.println("Using linked list takes about:"+(end-start)/1000000+"ms");
	
	System.gc();
	
	start=System.nanoTime();
	for (int i = 0; i < Count; i++) {
			queue2.offer(i + ""); 
	}
	
	for(int i = (Count >> 1); i > 0; i++) {
		queue2.peek();
		queue2.poll();
	}
	
	end=System.nanoTime();
	
	System.out.println("Using ArrayDeque takes about:"+(end-start)/1000000+"ms");System.out.println("Test finished");
	
	}
	
}
