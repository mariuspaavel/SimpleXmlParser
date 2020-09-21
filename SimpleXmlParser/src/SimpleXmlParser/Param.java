package SimpleXmlParser;

public class Param extends Element {
	Text name, value;
	
	public Param(Text name, Text value) {
		this.name = name;
		this.value = value;
	}
	public StringBuilder getName() {
		return name.getContent();
	}
	public StringBuilder getValue() {
		return value.getContent();
	}
	
	public void print(StringBuilder sb) {
		name.print(sb);
		sb.append("=\"");
		value.print(sb);
		sb.append('\"');
	}
	public int getstrlen() {
		return name.getstrlen() + 3 + value.getstrlen();
	}
}
