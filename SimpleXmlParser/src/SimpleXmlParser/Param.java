package SimpleXmlParser;

class Param extends Element {
	Identifier identif;
	Str value;
	
	Param(Param param) {
		identif = new Identifier(param.identif);
		value = new Str(param.value);
	}
	
	Param(String name, String value) {
		this(new Identifier(name), new Str(value));
	}
	Param(String name, String domain, String value) {
		this(new Identifier(name, domain), new Str(value));
	}
	
	Param(Identifier name, Str value) {
		this.identif = name;
		this.value = value;
	}
	Identifier getIdentifier() {
		return identif;
	}
	
	StringBuilder getNameSB() {
		return identif.name.getContentSB();
	}
	String getName() {
		return identif.name.getContent();
	}
	void setName(String s) {
		identif.name.set(s);
	}
	StringBuilder getValueSB() {
		return value.getContentSB();
	}
	String getValue() {
		return value.getContent().toString();
	}
	void setValue(String s) {
		value.set(s);
	}
	StringBuilder getDomainSB() {
		return identif.domain.getContentSB();
	}
	String getDomain() {
		return identif.domain.getContent().toString();
	}
	void setDomain(String domain) {
		identif.addDom(domain);
	}
	void removeDomain() {
		identif.removeDom();
	}
	
	void print(StringBuilder sb) {

		identif.print(sb);
		sb.append("=\"");
		value.print(sb);
		sb.append('\"');
	}
	public int getstrlen() {
		return identif.getstrlen() + 3 + value.getstrlen();
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Param))return false;
		Param p = (Param)o;
		if(!identif.equals(p.identif))return false;
		if(!value.equals(p.value))return false;
		return true;
	}
	@Override
	public int hashCode() {
		return identif.hashCode() ^ value.hashCode();
	}
	@Override
	public Object clone() {
		return new Param(this);
	}
}
