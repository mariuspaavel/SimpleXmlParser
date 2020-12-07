package SimpleXmlParser;

public class Str extends Element implements Cloneable {

	StringBuilder content;
	
	String source;
	int start, end;
	
	boolean indep;
	boolean fullrange;
	
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
		if(indep)return content.length();
		return end-start;
	}
	public StringBuilder getContentSB() {
		makeIndependent();
		return content;
	}
	public String getContent() {
		if(indep) {
			return content.toString();
		}
		else {
			return source.substring(start, end);
		}
	}
	
	public void set(String s) {
		indep = false;
		source = s;
		start = 0;
		end = s.length();
	}
	
	
	private void makeIndependent() {
		content = new StringBuilder(source.substring(start, end));
		indep = true;
	}
	
	
	Str(String source, int start, int end) {
		this.source = source;
		this.start = start;
		this.end = end;
		if(start == 0 && end == source.length())fullrange = true;
		else fullrange = false;
	}
	public Str(String content) {
		if(content == null)this.content = new StringBuilder("null");
		this.content = new StringBuilder(content);
		indep = true;
	}
	public Str(Str Str) {
		indep = Str.indep;
		if(indep) {
			content = new StringBuilder(Str.content);
			return;
		}
		source = Str.source;
		start = Str.start;
		end = Str.end;
		fullrange = Str.fullrange;
	}
	Str(Index idx) {
		this.source = idx.source;
		this.start = idx.prev;
		this.end = idx.pos;
		if(start == 0 && end == source.length())fullrange = true;
		else fullrange = false;
	}
	@Override
	public String toString() {
		if(indep)return content.toString();
		return source.substring(start, end);
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Str))return false;
		return compare((Str)o);
		
	}
	@Override
	public int hashCode() {
		int code = 0;
		int len = indep ? content.length() : end-start;
		for(int i = 0; i < 16; i++) {
			int stridx = i%len;
			int byteidx = i%4;
			int c = indep ? content.charAt(stridx) : source.charAt(start + stridx);
			code ^= c << byteidx*8;
		}
		return code;
	}
	@Override
	public Object clone() {
		return new Str(this);
	}
	private boolean compare(Str other) {
		if(indep) {
			if(other.indep) return other.content.equals(content);
			else return content.toString().equals(other.source.substring(other.start, other.end));
		}
		else {
			if(other.indep) return other.content.toString().equals(source.substring(start, end));
			else return source.substring(start, end).equals(other.source.substring(other.start, other.end));
		}
		
	}
}
