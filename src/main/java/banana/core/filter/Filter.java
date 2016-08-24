package banana.core.filter;

public interface Filter {

	void add(String value);

	boolean contains(String value);

	byte[] toBytes();
	
	void load(byte[] data);
}