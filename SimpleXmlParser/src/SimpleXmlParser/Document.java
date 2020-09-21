package SimpleXmlParser;
import java.util.ArrayList;

public class Document extends Element {
	
	ArrayList<Param> params;
	ArrayList<Element> children;
	
	public Document() {
		params = new ArrayList<Param>();
		children = new ArrayList<Element>();
	}
	
	public ArrayList<Param> getParams(){
		return params;
	}
	public ArrayList<Element> getChildren(){
		return children;
	}
	
	@Override
	void print(StringBuilder op) {
		op.append("<?xml");
		for(Param p : params) {
			op.append(' ');
			p.print(op);
		}
		op.append("?>");
		for(Element e: children)e.print(op);
	}

	@Override
	public int getstrlen() {
		int len = 4;
		for(Param p : params)len+=p.getstrlen()+1;
		for(Element e : children)len+=e.getstrlen();
		return len;
	}

}
