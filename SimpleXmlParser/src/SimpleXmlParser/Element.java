package SimpleXmlParser;
public abstract class Element implements Cloneable {
	abstract void print(StringBuilder op);
	public abstract int getstrlen();
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.ensureCapacity(getstrlen());
		print(sb);
		return sb.toString();
	}
	@Override
	public abstract boolean equals(Object o);
	@Override
	public abstract int hashCode();
	@Override
	public abstract Object clone();
}
