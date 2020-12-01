package SimpleXmlParser;

class Identifier extends Element{
	Str domain;
	Str name;
	boolean hasDomain;
	
	
	Identifier(Identifier identif){
		name = new Str(identif.name);
		if(hasDomain = identif.hasDomain) domain = new Str(identif.domain);
	}
	
	Identifier(Str name) {
		this.name = name;
		hasDomain = false;
	}
	Identifier(Str name, Str domain){
		this.name = name;
		this.domain = domain;
		hasDomain = true;
	}
	Identifier(String name){
		this(new Str(name));
	}
	Identifier(String name, String domain){
		this(new Str(name), new Str(domain));
	}
	void addDom(String domain) {
		this.domain = new Str(domain);
		hasDomain = true;
	}
	void removeDom() {
		this.domain = null;
		hasDomain = false;
	}
	
	@Override
	void print(StringBuilder op) {
		if(hasDomain) {
			domain.print(op);
			op.append(':');
		}
		name.print(op);		
	}
	@Override
	public int getstrlen() {
		int len = name.getstrlen();
		if(hasDomain)len+=domain.getstrlen();
		return len;
	}
	@Override
	public boolean equals(Object o) {
		if(o.getClass() != this.getClass()) return false;
		Identifier other = (Identifier) o;
		//System.out.println(other.hasDomain + " " + hasDomain);
		if(hasDomain != other.hasDomain) return false;
		if(hasDomain) {
			if(!domain.equals(other.domain))return false;
		}
		if(!name.equals(other.name))return false;
		return true;
	}
	@Override
	public int hashCode() {
		int code = name.hashCode();
		if(hasDomain) code ^= domain.hashCode();
		return code;
	}
	@Override
	public Object clone() {
		return new Identifier(this);
	}
}
