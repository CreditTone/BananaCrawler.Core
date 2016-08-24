package banana.core.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;

public class SimpleBloomFilter implements Filter {
	private static final int DEFAULT_SIZE = 2 << 24;
	private static final int[] seeds = new int[] { 5, 7, 11, 13, 31, 37, 61 };
	private BitSet bits = new BitSet(DEFAULT_SIZE);
	private SimpleHash[] func = new SimpleHash[seeds.length];

	public SimpleBloomFilter() {
		for (int i = 0; i < seeds.length; i++) {
			func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
		}
	}

	/* (non-Javadoc)
	 * @see com.banana.filter.Filter#add(java.lang.String)
	 */
	@Override
	public void add(String value) {
		for (SimpleHash f : func) {
			bits.set(f.hash(value), true);
		}
	}

	/* (non-Javadoc)
	 * @see com.banana.filter.Filter#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String value) {
		if (value == null) {
			return false;
		}
		boolean ret = true;
		for (SimpleHash f : func) {
			ret = ret && bits.get(f.hash(value));
		}
		return ret;
	}

	// 内部类，simpleHash
	public static class SimpleHash {
		private int cap;
		private int seed;

		public SimpleHash(int cap, int seed) {
			this.cap = cap;
			this.seed = seed;
		}

		public int hash(String value) {
			int result = 0;
			int len = value.length();
			for (int i = 0; i < len; i++) {
				result = seed * result + value.charAt(i);
			}
			return (cap - 1) & result;
		}
	}

	@Override
	public byte[] toBytes() {
		ByteArrayOutputStream os = null;
		ObjectOutputStream oos = null;
		try{
			os = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(os);
			oos.writeObject(bits);
			oos.flush();
			return os.toByteArray();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (oos != null){
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public void load(byte[] data) {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try{
			bais = new ByteArrayInputStream(data);
			ois = new ObjectInputStream(bais);
			bits = (BitSet) ois.readObject();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (ois != null){
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bais != null){
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}