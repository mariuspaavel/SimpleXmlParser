package SimpleXmlParser;

public class Text extends Element {

	StringBuilder content;
	
	String source;
	int start, end;
	
	boolean indep;
	
	@Override
	public void print(StringBuilder op) {
		if(indep) {
			op.append(content);
			return;
		}
		op.append(source.substring(start, end));
	}

	@Override
	public int getstrlen() {
		if(indep)return source.length();
		return end-start;
	}
	public StringBuilder getContent() {
		makeIndependent();
		return content;
	}
	
	private void makeIndependent() {
		content = new StringBuilder(source.substring(start, end));
		indep = true;
	}
	
	
	public Text(String source, int start, int end) {
		this.source = source;
		this.start = start;
		this.end = end;
	}
	public Text(String content) {
		this.content = new StringBuilder(content);
		indep = true;
	}
	Text(Index idx) {
		this.source = idx.source;
		this.start = idx.prev;
		this.end = idx.pos;
	}
	@Override
	public String toString() {
		if(indep)return content.toString();
		return source.substring(start, end);
	}
}
