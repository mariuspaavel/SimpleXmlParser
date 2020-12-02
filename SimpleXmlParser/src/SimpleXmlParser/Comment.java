package SimpleXmlParser;

public class Comment extends Str {
	
	public Comment(String content) {
		super(content);
	}
	public Comment(Str str) {
		super(str);
	}
	public Comment(Comment comment) {
		this((Str)comment);
	}
	Comment(Index idx) {
		super(idx);
	}
	
	@Override
	public void print(StringBuilder op) {
		op.append("<!--");
		super.print(op);
		op.append("-->");	
	}
	@Override
	public int getstrlen() {
		return super.getstrlen()+7;
	}
	@Override
	public String toString() {
		if(indep) {
			StringBuilder sb = new StringBuilder();
			sb.ensureCapacity(getstrlen());
			print(sb);
			return sb.toString();
		}
		return source.substring(start, end);
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Comment))return false;
		return compare((Comment)o);
		
	}
	@Override
	public int hashCode() {
		return super.hashCode() ^ 0x3FA045E1;
	}
	@Override
	public Object clone() {
		return new Str(this);
	}
	private boolean compare(Comment other) {
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
