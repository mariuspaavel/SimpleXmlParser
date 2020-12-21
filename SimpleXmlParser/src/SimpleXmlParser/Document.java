package SimpleXmlParser;

public class Document extends Block {
	
	public Document(Document doc) {
		super(doc);
		
	}
	public Document() {
		this("1.0", "UTF-8");
	}
	public Document(String version, String encoding) {
		super();
		setParam("version", version);
		setParam("encoding", encoding);
	}
	public Document(String source) throws XmlParsingException {
		try {
			Parser.parse(source, this);
			hasBody = true;
		}catch(IndexOutOfBoundsException e) {
			throw new XmlParsingException("Illegal document");
		}
	}
	
	
	@Override
	void print(StringBuilder op) {
		op.append("<?xml");
		for(Identifier name : params.keySet()) {
			op.append(' ');
			name.print(op);
			op.append("=");
			op.append('\"');
			params.get(name).print(op);
			op.append('\"');
		}
		op.append("?>");
		for(Element e: children)e.print(op);
	}

	@Override
	public int getstrlen() {
		int len = 4;
		for(Identifier i : params.keySet())len+=i.getstrlen()+params.get(i).getstrlen()+3;
		for(Element e : children)len+=e.getstrlen();
		return len;
	}
	
	@Override
	public boolean equals(Object o) {
		if(! (o instanceof Document))return false;
		if(o == this)return true;
		Document d = (Document) o;
		if(!params.equals(d.params))return false;
		if(!children.equals(d.children))return false;
		return true;
	}
	@Override
	public int hashCode() {
		int code = identif.hashCode();
		for(Identifier i : params.keySet()) {
			code ^= i.hashCode();
			code ^= params.get(i).hashCode();
		}
		code ^= 0x01;
		for(Element e : children)code ^= e.hashCode();
		return code;
	}
	@Override
	public Object clone() {
		return new Document(this);
	}

}
