package mayday.Reveal.functions;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mayday.core.io.BufferedRandomAccessFile;

public class MultiMappedByteBuffer {

	private long maxValue;
	private MappedByteBuffer currentBuffer;
	private int currentBufferIndex = 0;
	
	private MappedByteBuffer[] buffers;
	
	public MultiMappedByteBuffer() {
		this.maxValue = Integer.MAX_VALUE;
	}
	
	public MultiMappedByteBuffer(int bufferSize) {
		this.maxValue = bufferSize;
	}
	
	public void mapFile(File file) throws IOException {
		BufferedRandomAccessFile aFile = new BufferedRandomAccessFile(file, "r");
		FileChannel ioChannel = aFile.getChannel();
		
		long size = ioChannel.size();
		
		int numBuffer = (int)(size / maxValue);
		long finalSize = (int)(size % maxValue);
		
		if(finalSize > 0) {
			numBuffer++;
		} else {
			finalSize = maxValue;
		}
		
		buffers = new MappedByteBuffer[numBuffer];
		for(int i = 0; i < numBuffer-1; i++) {
			long start = (long)i*maxValue;
			buffers[i] = ioChannel.map(FileChannel.MapMode.READ_ONLY, start, maxValue);
		}
		long finalStart = ((long)numBuffer-1l) * maxValue;
		buffers[numBuffer-1] = ioChannel.map(FileChannel.MapMode.READ_ONLY, finalStart, finalSize);
		
		position(0);
		
		ioChannel.close();
		aFile.close();
	}
	
	public long position() {
		return currentBuffer.position();
	}
	
	public void position(long pos) {
		int bufferIndex = (int)(pos / maxValue);
		
		if(bufferIndex > buffers.length)
			throw new IllegalArgumentException();
		
		int newPos = (int)(pos % maxValue);
		currentBuffer = buffers[bufferIndex];
		currentBuffer.position(newPos);
		currentBufferIndex = bufferIndex;
	}
	
	public byte get() {
		if(currentBuffer.position() == maxValue) {
			if(currentBufferIndex == buffers.length-1) {
				throw new IllegalArgumentException();
			} else {
				currentBuffer = buffers[++currentBufferIndex];
				currentBuffer.position(0);
			}
		}
		return currentBuffer.get();
	}
	
	public boolean hasRemaining() {
		if(currentBuffer.position() == maxValue) {
			if(currentBufferIndex == buffers.length-1) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}
	
	public long limit() {
		int numBuffer = buffers.length;
		long limit = (long)(numBuffer-1) * maxValue;
		limit += buffers[numBuffer-1].limit();
		return limit;
	}
	
	public static void main(String[] args) throws Exception {
		File file = new File(args[0]);
		
		MultiMappedByteBuffer mmbb = new MultiMappedByteBuffer();
		mmbb.mapFile(file);
		
		System.out.println(mmbb.buffers.length);
		
		Random r = new Random();
		ArrayList<Integer> positions = new ArrayList<Integer>();
		for(int i = 0; i < 10000; i++) {
			int j = r.nextInt(Integer.MAX_VALUE);
			positions.add(j);
		}
		long memBefore = Runtime.getRuntime().freeMemory();
		double time4 = mmbb.showBufferData(positions);
		long memAfter = Runtime.getRuntime().freeMemory();
		System.out.println((memBefore-memAfter)/1024. + "kb");
		System.out.println(time4 + "s");
	}
	
	private double showBufferData(List<Integer> positions) {
		long startTime = System.currentTimeMillis();
		char[] elements = new char[positions.size()];
		for(int i = 0; i < elements.length; i++) {
			elements[i] = getCharAtPosMappedVersion(positions.get(i));
		}
		long stopTime = System.currentTimeMillis();
		
		//System.out.println(Arrays.toString(elements));
		
		return (stopTime -startTime)/1000.;
	}
	
	private char getCharAtPosMappedVersion(int position) {
		long pos = position();
		if(position > limit())
			return '?';
		position(position);
		if(hasRemaining()) {
			char c = (char)get();
			position(pos);
			return c;
		}
		return '?';
	}
}
