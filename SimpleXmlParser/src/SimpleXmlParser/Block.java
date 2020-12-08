package SimpleXmlParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Predicate;

public class Block extends Element implements List<Element> {

	Identifier identif;
	Map<Identifier, Str> params;
	ArrayList<Element> children;
	
	boolean hasBody = false;
	
	public Block(Block block) {
		identif = new Identifier(block.identif);
		params = new HashMap<Identifier, Str>();
		for(Identifier i : block.params.keySet())
			params.put(new Identifier(i), block.params.get(i));
		children = new ArrayList<Element>();
		for(Element e : block.children)children.add((Element)e.clone());
		
	}
	
	public Block(String name, String domain) {
		this(new Identifier(name, domain));
	}
	public Block(String name) {
		this(new Identifier(name));
	}
	Block(){ //For Document super()
		this.params = new HashMap<Identifier, Str>();
		this.children = new ArrayList<Element>();
	}
	
	
	Block(Identifier identif) {
		this.identif = identif;
		params = new HashMap<Identifier, Str>();
		children = new ArrayList<Element>();
	}
	public StringBuilder getNameSB() {
		return identif.name.getContentSB();
	}
	public String getName() {
		return identif.name.getContent().toString();
	}
	
	
	public void setParam(String name, String value) {
		params.put(new Identifier(name), new Str(value));
	}
	public void setParam(String domain, String name, String value) {
		params.put(new Identifier(name, domain), new Str(value));
	}
	public void setParam(String name, Object value) {
		params.put(new Identifier(name), new Str(value.toString()));
	}
	public void setParam(String domain, String name, Object value) {
		params.put(new Identifier(name, domain), new Str(value.toString()));
	}
	public void setParam(String name, long value) {
		params.put(new Identifier(name), new Str(Long.toString(value)));
	}
	public void setParam(String domain, String name, long value) {
		params.put(new Identifier(name, domain), new Str(Long.toString(value)));
	}
	
	
	public void removeParam(String name) {
		params.remove(new Identifier(name));
	}
	public String getParam(String name) {
		Str param = params.get(new Identifier(name));
		if(param == null)return null;
		return param.toString();
	}
	public String getParam(String domain, String name) {
		Str param = params.get(new Identifier(name, domain));
		if(param == null)return null;
		return param.toString();
	}
	public void removeParam(String domain, String name) {
		params.remove(new Identifier(name, domain));
	}
	public Str addText(String text) {
		Str str = new Str(text);
		children.add(str);
		return str;
	}
	public Block addBlock(String name) {
		Block b = new Block(name);
		add(b);
		return b;
	}
	public Block addBlock(String domain, String name) {
		Block b = new Block(domain, name);
		add(b);
		return b;
	}
	@Override
	public boolean add(Element e) {
		children.add(e);
		return true;
	}
	public ArrayList<Block> getSubset(String name) {
		ArrayList<Block> subset = new ArrayList<Block>();
	
		for(Element e : this) {
			if(e instanceof Block && ((Block)e).getName().equals(name))subset.add((Block) e);
		}
		return subset;
	}
	
	
	@Override
	public Element get(int index) {
		return children.get(index);
	}
	
	public Block getFirstWithName(String name) {
		return findBlock(b -> b.getName().equals(name));
	}
	
	public Block getChildBlock(int index) {
		Element e = children.get(index);
		if(e instanceof Block) {
			return (Block)e;
		}else {
			throw new RuntimeException("Element at given index is not the Block type");
		}
	}
	public int findIndex(int start, Predicate<? super Element> p) {
		for(int i = start; i < children.size(); i++) {
			if(p.test(children.get(i)))return i;
		}
		return -1;
	}
	public int findIndex(Predicate<? super Element> p) {
		return findIndex(0, p);
	}
	public Element findElement(Predicate<? super Element> p) {
		int index = findIndex(p);
		if(index == -1)return null;
		return children.get(findIndex(p));
	}
	public int findBlockIndex(int start, Predicate<? super Block> p) {
		Predicate<Element> p1 = e -> e instanceof Block && p.test((Block)e);
		return findIndex(start, p1);
	}
	public int findBlockIndex(Predicate<? super Block> p) {
		return findBlockIndex(0, p);
	}
	public Block findBlock(Predicate<? super Block> p) {
		int index = findBlockIndex(p);
		if(index == -1)return null;
		return (Block)children.get(findBlockIndex(p));
	}
	
	@Override
	void print(StringBuilder op) {
		op.append('<');
		identif.print(op);
		for(Identifier name : params.keySet()) {
			op.append(' ');
			name.print(op);
			op.append('=');
			op.append('\"');
			params.get(name).print(op);
			op.append('\"');
		}
		if(children.size() == 0) {
			op.append("/>");
			return;
		}
		op.append('>');
		for(Element e : children)e.print(op);
		op.append("</");
		identif.print(op);
		op.append('>');
	}

	@Override
	public int getstrlen() {
		int len = identif.getstrlen();
		len+=2;
		for(Identifier i : params.keySet())len+=i.getstrlen()+params.get(i).getstrlen()+3;
		if(children.size() == 0) {
			len+=1;
			return len;
		}
		for(Element e : children) len += e.getstrlen();
		len+=identif.getstrlen()+3;
		return len;
	}
	
	@Override
	public boolean equals(Object o) {
		if(! (o instanceof Block))return false;
		if(o == this)return true;
		Block b = (Block) o;
		if(!identif.equals(b.identif))return false;
		if(!params.equals(b.params))return false;
		if(!children.equals(b.children))return false;
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
		return new Block(this);
	}

	
	@Override
	public Iterator<Element> iterator() {
		return new ElementIterator();
	}
	public Iterator<Block> blockIterator(){
		return new BlockIterator();
	}


	@Override
	public void add(int index, Element e) {
		children.add(index, e);
	}
	

	@Override
	public boolean addAll(Collection<? extends Element> childrenToAdd) {
		for(Element e : childrenToAdd)children.add(e);
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Element> childrenToAdd) {
		children.addAll(index, childrenToAdd);
		return false;
	}

	@Override
	public void clear() {
		children.clear();
		
	}

	@Override
	public boolean contains(Object o) {
		return children.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		return children.containsAll(collection);
	}


	@Override
	public int indexOf(Object o) {
		return children.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return children.isEmpty();
	}

	@Override
	public int lastIndexOf(Object o) {
		return children.lastIndexOf(o);
	}

	@Override
	public ListIterator<Element> listIterator() {
		return null;
	}

	@Override
	public ListIterator<Element> listIterator(int arg0) {
		return null;
	}

	@Override
	public boolean remove(Object o) {
		return children.remove(o);
	}

	@Override
	public Element remove(int index) {
		return children.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> objectsToRemove) {
		return children.removeAll(objectsToRemove);
	}

	@Override
	public boolean retainAll(Collection<?> objectsToRetain) {
		return children.retainAll(objectsToRetain);
	}

	@Override
	public Element set(int index, Element e) {
		return children.set(index, e);
	}

	@Override
	public int size() {
		return children.size();
	}

	@Override
	public List<Element> subList(int fromIndex, int toIndex) {
		return children.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return children.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return children.toArray(arg0);
	}
	
	
	public class ElementIterator implements Iterator<Element> {
		int index = -1;
		
		@Override
		public boolean hasNext() {
			return index < size()-1;
		}

		@Override
		public Element next() {
			return get(++index);
		}
		
	}
	public class BlockIterator implements Iterator<Block>{
		int index = -1;
		@Override
		public boolean hasNext() {
			for(int i = index+1; i < size(); i++) {
				if(get(i) instanceof Block)return true;
			}
			return false;
		}
		@Override
		public Block next() {
			while(++index < size()) {
				if(get(index) instanceof Block)return (Block)get(index);
			}
			return null;
		}
	}
	public class StrIterator implements Iterator<Str>{
		int index = -1;
		@Override
		public boolean hasNext() {
			for(int i = index+1; i < size(); i++) {
				if(get(i) instanceof Str)return true;
			}
			return false;
		}
		@Override
		public Str next() {
			while(++index < size()) {
				if(get(index) instanceof Str)return (Str)get(index);
			}
			return null;
		}
	}
	public class CommentIterator implements Iterator<Comment>{
		int index = -1;
		@Override
		public boolean hasNext() {
			for(int i = index+1; i < size(); i++) {
				if(get(i) instanceof Comment)return true;
			}
			return false;
		}
		@Override
		public Comment next() {
			while(++index < size()) {
				if(get(index) instanceof Comment)return (Comment)get(index);
			}
			return null;
		}
	}
	
	public class FilteredIterator implements Iterator<Element>{
		int index = -1;
		Predicate<? super Element> condition;
		
		public FilteredIterator(Predicate<? super Element> condition) {
			this.condition = condition;
		}
		
		@Override
		public boolean hasNext() {
			for(int i = index+1; i < size(); i++) {
				if(condition.test(get(i)))return true;
			}
			return false;
		}
		@Override
		public Element next() {
			while(++index < size()) {
				if(condition.test(get(index)))return (Element)get(index);
			}
			return null;
		}
	}
	public class FilteredBlockIterator implements Iterator<Block>{
		int index = -1;
		Predicate<? super Block> condition;
		
		public FilteredBlockIterator(Predicate<? super Block> condition) {
			this.condition = condition;
		}
		
		@Override
		public boolean hasNext() {
			for(int i = index+1; i < size(); i++) {
				Element e = get(i);
				if(e instanceof Block && condition.test((Block)get(i)))return true;
			}
			return false;
		}
		@Override
		public Block next() {
			while(++index < size()) {
				Element e = get(index);
				if(e instanceof Block && condition.test((Block)e))return (Block)get(index);
			}
			return null;
		}
	}
}

