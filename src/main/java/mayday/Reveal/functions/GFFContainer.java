package mayday.Reveal.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mayday.core.io.BufferedRandomAccessFile;
import cern.colt.Arrays;


public class GFFContainer {
	
	public MappedByteBuffer mapFile(File file) throws IOException {
		RandomAccessFile aFile = new RandomAccessFile(file, "r");
		
		FileChannel ioChannel = aFile.getChannel();
		
		MappedByteBuffer buffer = ioChannel.map(FileChannel.MapMode.READ_ONLY, 0l, ioChannel.size());
		ioChannel.close();
		aFile.close();
		return buffer;
	}
	
	public double readBufferedReader(File file) throws Exception {
		long startTime = System.currentTimeMillis();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		long count = 0;
		while((line = br.readLine()) != null) {
			count++;
		}
		br.close();
		long stopTime = System.currentTimeMillis();
		
		System.out.println(count);
		return (stopTime -startTime)/1000.;
	}
	
	public double readBufferedRandomAccessFile(File file) throws Exception {
		long startTime = System.currentTimeMillis();
		BufferedRandomAccessFile braf = new BufferedRandomAccessFile(file, "r");
		String line = null;
		long count = 0;
		
		while((line = braf.readLine()) != null) {
			count++;
		}
		
		braf.close();
		
		long stopTime = System.currentTimeMillis();
		System.out.println(count);
		return (stopTime -startTime)/1000.;
	}
	
	private double readBRAF(File file, List<Integer> positions) throws Exception {
		long startTime = System.currentTimeMillis();
		BufferedRandomAccessFile aFile = new BufferedRandomAccessFile(file, "r");
		char[] elements = new char[positions.size()];
		
		for(int i = 0; i < elements.length; i++) {
			int pos = positions.get(i);
			aFile.seek(pos);
			elements[i] = (char)aFile.read();
		}
		
		aFile.close();
		long stopTime = System.currentTimeMillis();
		return (stopTime -startTime)/1000.;
	}
	
	private double showBufferData(ByteBuffer buf, String name){
		long startTime = System.currentTimeMillis();
		int pos = buf.position();
	    //Set position to zero
	    buf.position(0);
	    long count = 0;
	    
	    while(buf.hasRemaining()){
	    	char c = (char)buf.get();
	    	count++;
	    }
	    
	    buf.position(pos);
	    long stopTime = System.currentTimeMillis();
	    System.out.println(count);
		return (stopTime -startTime)/1000.;
	}
	
	private double showBufferData(ByteBuffer buffer, String name, List<Integer> positions) {
		long startTime = System.currentTimeMillis();
		char[] elements = new char[positions.size()];
		for(int i = 0; i < elements.length; i++) {
			elements[i] = getCharAtPosMappedVersion(buffer, positions.get(i));
		}
		long stopTime = System.currentTimeMillis();
		
		System.out.println(Arrays.toString(elements));
		
		return (stopTime -startTime)/1000.;
	}
	
	private char getCharAtPosMappedVersion(ByteBuffer buffer, int position) {
		int pos = buffer.position();
		if(position >= buffer.limit())
			return '?';
		buffer.position(position);
		if(buffer.hasRemaining()) {
			char c = (char)buffer.get();
			buffer.position(pos);
			return c;
		}
		return '?';
	}
	
	public static void main(String[] args) throws Exception {
		File f = new File(args[0]);
		GFFContainer gffC = new GFFContainer();
		
		System.out.println("Speed-Test");
		System.gc();
		System.out.println("MemoryMapping");
		
//		ByteBuffer bb = gffC.mapFile(f); 
//		double time1 = gffC.showBufferData(bb, f.getName());
//		
//		System.out.println(time1 + "s");
//		
//		
//		System.gc();
//		System.out.println("BufferedReader");
//		double time2 = gffC.readBufferedReader(f);
//		System.out.println(time2 + "s");
//		System.gc();
//		System.out.println("BufferedRandomAccessFile");
//		
//		double timeSum = 0;
//		
//		for(int i = 0; i < 1; i++) {
//			double time3 = gffC.readBufferedRandomAccessFile(f);
//			timeSum += time3;
//		}
//		
//		System.out.println(timeSum + "s");
		
//		System.gc();
		
		Random r = new Random();
		ArrayList<Integer> positions = new ArrayList<Integer>();
		for(int i = 0; i < 100000; i++) {
			int j = r.nextInt(200000);
			positions.add(j);
		}
		long memBefore = Runtime.getRuntime().freeMemory();
//		double time4 = gffC.showBufferData(bb, f.getName(), positions);
		double time4 = gffC.readBRAF(f, positions);
		long memAfter = Runtime.getRuntime().freeMemory();
		System.out.println((memBefore-memAfter)/1024. + "kb");
		System.out.println(time4 + "s");
	}
}
