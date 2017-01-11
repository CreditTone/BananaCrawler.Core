package banana.core.modle;

public interface TaskContext {

	/**
	 * 返回attribute对应的value 这个方法是线程安全的
	 * @param key
	 * @return  返回attribute对应的value
	 */
	Object getContextAttribute(String attribute);

	/**
	 * 向Context域put一个属性值。并返回之前的attribute对应的value。如果之前没有attribute属性那么返回null。这个方法是线程安全的
	 * @param attribute
	 * @param value
	 * @return 返回之前的attribute对应的value。如果之前没有attribute属性那么返回null
	 */
	Object putContextAttribute(String attribute, Object value);

	/**
	 * 返回StartContext是否为空。
	 * @return
	 */
	boolean isEmpty();

}