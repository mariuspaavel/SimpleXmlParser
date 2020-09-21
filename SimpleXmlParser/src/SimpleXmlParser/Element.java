package SimpleXmlParser;
public abstract class Element {
	abstract void print(StringBuilder op);
	public abstract int getstrlen();
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.ensureCapacity(getstrlen());
		print(sb);
		return sb.toString();
	}
}
