package SimpleXmlParser;

class Parser {

	private Parser() {}
	
	private static Identifier readIdentifier(Index idx) {
		int start = idx.pos;
		idx.findidentifend();
		Str name = new Str(idx);
		idx.skipWS();
		if(idx.get() == ':') {
			Str domain = name;
			idx.jump(1);
			idx.skipWS();
			idx.findidentifend();
			name = new Str(idx);
			return new Identifier(name, domain);
		}
		else return new Identifier(name);
	}
	
	private static Param readParam(Index idx) {
		//System.out.println("Param start: " + idx.pos);
		Identifier identif = readIdentifier(idx);
		idx.skipWS();
		if(idx.get() != '=')throw new RuntimeException("invalid param");
		idx.jump(1);
		idx.skipWS();
		if(idx.get() != '\"')throw new RuntimeException("invalid param");
		idx.jump(1);
		idx.findChar('\"');
		Str value = new Str(idx);
		idx.pos+=1;
		Param p = new Param(identif, value);
		//System.out.println("param end: " + idx.pos);
		return p;
	}
	
	private static void readXmlHead(Index idx, Document d) {
		idx.skipWS();
		idx.jump(1);
		idx.skipWS();
		idx.findWS();
		idx.skipWS();
		
		while(true) {
			if(Index.keyc(idx.get()))break;
			Param p = readParam(idx);
			d.params.put(p.identif, p.value);
			idx.skipWS();
		}
		checkch(idx, '?', "invalid xml head");
		idx.jump(1);
		idx.skipWS();
		checkch(idx, '>', "invalid xml head");
		idx.jump(1);
	}
	
	private static void readXmlBody(Index idx, Document d) {
		while(true){
			if(idx.pos == idx.source.length())return;
			else if(idx.get() != '<')d.children.add((readStr(idx)));
			else d.children.add((readBlock(idx)));
		}
	}
	
	static Document parse(String source, Document d) {
		Index idx = new Index(source, 0);
		readXmlHead(idx, d);
		readXmlBody(idx, d);
		
		return d;
	}
	
	private static Block readBlockHead(Index idx) {
		checkch(idx, '<', "invalid block start position");
		//System.out.println("Block head start: " + idx.pos);
		idx.jump(1);
		idx.skipWS();
		Identifier identif = readIdentifier(idx);
		Block b = new Block(identif);
		while(true) {
			idx.skipWS();
			if(idx.get() == '/') {
				idx.jump(1);
				idx.skipWS();
				checkch(idx, '>', "invalid block head no '>' after '/'");
				idx.jump(1);
				//System.out.println("Block head end without body" + idx.pos);
				return b;
			}
			if(idx.get() == '>') {
				idx.jump(1);
				b.hasBody = true;
				//System.out.println("Block head end with body: " + idx.pos);
				return b;
			}
			Param p = readParam(idx);
			b.params.put(p.identif, p.value);
		}
	}
	private static boolean readBlockTail(Index idx, Block b) {
		Index idxcpy = new Index(idx.source, idx.pos);
		checkch(idxcpy, '<', "invalid block tail start");
		
		idxcpy.jump(1);
		idxcpy.skipWS();
		if(idxcpy.get() != '/')return false;
		idxcpy.jump(1);
		idxcpy.skipWS();
		
		Identifier identif = readIdentifier(idxcpy);
		
		//System.out.println(identif);
		//System.out.println(b.identif);
		
		if(!identif.equals(b.identif))throw new RuntimeException("Unmatching block tail");
		
		idxcpy.skipWS();
		checkch(idxcpy, '>', "invalid block tail");
		idxcpy.jump(1);
		idx.pos = idxcpy.pos;
		idx.prev = idxcpy.prev;
		//System.out.println("Block tail end: "+ idx.pos);
		return true;
		
	}
	
	private static Str readStr(Index idx) {
		idx.findChar('<');
		if(idx.pos >= idx.source.length())idx.pos = idx.source.length();
		Str txt = new Str(idx);
		return txt;
	}
	
	private static Block readBlock(Index idx) {
		Block b = readBlockHead(idx);
		
		if(!b.hasBody)return b;
		
		while(true){
			if(idx.get() != '<')b.children.add((readStr(idx)));
			else if(readBlockTail(idx, b))return b;
			else b.children.add((readBlock(idx)));
		}
	}
	
	private static void checkch(Index idx, char c, String errmsg) {
		if(idx.get() != c)throw new RuntimeException(errmsg);
	}

}
class Index{
	int prev;
	int pos;
	String source;
	int len;
	Index(String source, int pos){
		this.source = source;
		this.pos = this.prev = pos;
		len = source.length();
	}

	void jump(int amount) {
		prev = pos;
		pos+=amount;
	}
	void jumpBack() {
		pos = prev;
	}
	void skipWS() {
		while(ws(get()))pos++;
	}
	void findWS() {
		prev = pos;
		while(!ws(get()))pos++;
	}
	void findChar(char c) {
		prev = pos;
		while(get()!=c && pos < len) pos++;
	}
	void findCharBackwards(char c, int limit) {
		prev = pos;
		while(get()!=c && pos > limit)pos--;
	}
	void findidentifend() {
		prev = pos;
		while(!ws(get()) && !keyc(get()))pos++;
	}
	char get() {
		return source.charAt(pos); 
	}
	
	
	public static boolean ws(char c){
		switch(c){
			case ' ': return true;
			case '\t': return true;
			case '\n': return true;
			default: return false;
		}
	}
	public static boolean keyc(char c) {
		switch(c) {
		case '=':return true;
		case '<':return true;
		case '>':return true;
		case '\"':return true;
		case '?':return true;
		case ':':return true;
		case '/': return true;
		default: return false;
		}
	}
}
