package SimpleXmlParser;
import java.util.ArrayList;

public class Block extends Element {

	Text name;
	ArrayList<Param> params;
	ArrayList<Element> children;
	boolean hasbody;
	
	public Block(Text name, boolean hasbody) {
		this.name = name;
		params = new ArrayList<Param>();
		if(this.hasbody = hasbody)children = new ArrayList<Element>();
	}
	public StringBuilder getName() {
		return name.getContent();
	}
	public ArrayList<Param> getParams(){
		return params;
	}
	public ArrayList<Element> getChildren(){
		return children;
	}
	public boolean hasBody() {
		return hasbody;
	}
	public void addBody() {
		hasbody = true;
		children = new ArrayList<Element>();
	}
	public void destroyBody() {
		hasbody = false;
		children = null;
	}
	
	@Override
	void print(StringBuilder op) {
		op.append('<');
		name.print(op);
		for(Param p : params) {
			op.append(' ');
			p.print(op);
		}
		if(!hasbody) {
			op.append("/>");
			return;
		}
		op.append('>');
		for(Element e : children)e.print(op);
		op.append("</");
		name.print(op);
		op.append('>');
	}

	@Override
	public int getstrlen() {
		int len = name.getstrlen();
		len+=2;
		for(Param p : params)len+=p.getstrlen()+1;
		if(!hasbody) {
			len+=1;
			return len;
		}
		for(Element e : children) len += e.getstrlen();
		len+=name.getstrlen()+3;
		return len;
	}
	

}
