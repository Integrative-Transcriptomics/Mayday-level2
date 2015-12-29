package mayday.wapiti.transformations.impl.intra_norm.internal;


public class ArrayFromC {
	
	double[] arr;
	int offset;
	
	public ArrayFromC(double[] arr, int offset) {
		this.arr=arr;
		this.offset=offset;
	}
	
	public ArrayFromC(double[] arr) {
		this.arr=arr;
		this.offset=0;
	}
	
	public void set(double v) {
		arr[offset] = v;
	}
	
	public double get() {
		return arr[offset];
	}
	
	public void set(int index, double v) {
		arr[offset+index] = v;
	}
	
	public double get(int index) {
		return arr[offset+index];
	}
	
	public ArrayFromC newReference(int delta) {
		if (delta==0)
			return this;
		return new ArrayFromC(arr, offset+delta);
	}
	
	public void mult(int index, double v) {
		arr[index+offset]*=v;
	}
	public void div(int index, double v) {
		arr[index+offset]/=v;
	}
	public void add(int index, double v) {
		arr[index+offset]+=v;
	}
	
}