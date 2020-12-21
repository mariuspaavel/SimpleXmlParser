package SimpleXmlParser;

class Parser {

	private Parser() {}
	
	private static Identifier readIdentifier(Index idx) {
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
	
	private static Param readParam(Index idx) throws IllegalParamException {
		//System.out.println("Param start: " + idx.pos);
		Identifier identif = readIdentifier(idx);
		idx.skipWS();
		if(idx.get() != '=')throw new IllegalParamException("Parameter identifier must be followed by \'=\'");
		idx.jump(1);
		idx.skipWS();
		if(idx.get() != '\"')throw new IllegalParamException("Parameter value must be wrapped in double quotes");
		idx.jump(1);
		idx.findChar('\"');
		Str value = new Str(idx);
		idx.pos+=1;
		Param p = new Param(identif, value);
		//System.out.println("param end: " + idx.pos);
		return p;
	}
	
	private static void readXmlHead(Index idx, Document d) throws XmlParsingException {
		idx.skipWS();
		if(idx.get() != '<')throw new IllegalXmlHeadException("xml head must start with '<'");
		idx.jump(1);
		idx.skipWS();
		if(idx.get() != '?')throw new IllegalXmlHeadException("xml head must have '?'");
		idx.findWS();
		idx.skipWS();
		
		while(true) {
			if(Index.keyc(idx.get()))break;
			Param p = readParam(idx);
			d.params.put(p.identif, p.value);
			idx.skipWS();
		}
		if(idx.get() != '?')throw new IllegalXmlHeadException("xml head must have '?'");
		
		idx.jump(1);
		idx.skipWS();
		if(idx.get() != '>')throw new IllegalXmlHeadException("xml head must end with ?");
		
		idx.jump(1);
	}
	
	private static void readXmlBody(Index idx, Document d) throws XmlParsingException {
		while(true){
			if(idx.pos == idx.source.length())return;
			else if(idx.get() != '<')d.children.add((readStr(idx)));
			else d.children.add((readBlock(idx)));
		}
	}
	
	static Document parse(String source, Document d) throws XmlParsingException {
		Index idx = new Index(source, 0);
		readXmlHead(idx, d);
		readXmlBody(idx, d);
		
		return d;
	}
	
	private static Block readBlockHead(Index idx) throws IllegalBlockHeadException {
		idx.skipWS();
		if(idx.get() != '<')throw new IllegalBlockHeadException("Block head must start with '<'", "");
		idx.jump(1);
		idx.skipWS();
		Identifier identif = readIdentifier(idx);
		Block b = new Block(identif);
		while(true) {
			idx.skipWS();
			if(idx.get() == '/') {
				idx.jump(1);
				idx.skipWS();
				if(idx.get() != '\"')throw new IllegalBlockHeadException("Block terminator '/' must be followed by '>'", b.getName());
				idx.jump(1);
				return b;
			}
			if(idx.get() == '>') {
				idx.jump(1);
				b.hasBody = true;
				return b;
			}
			Param p = readParam(idx);
			b.params.put(p.identif, p.value);
		}
	}
	private static boolean readBlockTail(Index idx, Block b) throws IllegalBlockTailException {
		Index idxcpy = new Index(idx.source, idx.pos);
		
		idxcpy.jump(1);
		idxcpy.skipWS();
		if(idxcpy.get() != '/')return false;
		idxcpy.jump(1);
		idxcpy.skipWS();
		
		Identifier identif = readIdentifier(idxcpy);
		
		//System.out.println(identif);
		//System.out.println(b.identif);
		
		if(!identif.equals(b.identif))throw new IllegalBlockTailException("Block tail doesn't match head", b.getName());
		
		idxcpy.skipWS();
		
		if(idxcpy.get() != '>')throw new IllegalBlockTailException("Block tail must end with '>'", b.getName());
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
	

	
	private static Block readBlock(Index idx) throws XmlParsingException {
		Block b = readBlockHead(idx);
		
		if(!b.hasBody)return b;
		
		while(true){
			if(idx.get() != '<')b.add((readStr(idx)));
			else if(idx.cmpIgnWs("<!--"))b.add(readComment(idx));
			else if(readBlockTail(idx, b))return b;
			else b.add((readBlock(idx)));
		}
	}
	
	
	private static Comment readComment(Index idx) throws IllegalCommentException {
		
		if(!idx.cmpIgnWs("<!--"))throw new IllegalCommentException("Illegal comment start");
	
		idx.findChar('!');
		idx.jump(1);
		idx.findChar('-');
		idx.jump(1);
		idx.findChar('-');
		idx.jump(1);
		
		Comment comment = null;
		int stringstart = idx.pos;
		
		do {
			idx.findChar('-');
		}while(!idx.cmpIgnWs("-->"));
		
		idx.prev = stringstart;
		
		comment = new Comment(idx);
		
		idx.findChar('>');
		idx.jump(1);
		return comment;
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
	@Override
	public Object clone() {
		Index clone = new Index(source, pos);
		clone.prev = prev;
		clone.len = len;
		return clone;
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
	char get(int index) {
		return source.charAt(index);
	}
	
	boolean cmpIgnWs(String sequence) {
		
		Index copyindex = (Index)clone();
		for(int i = 0; i < sequence.length(); i++) {
			char c = sequence.charAt(i);
			if(!ws(c)){
				copyindex.skipWS();
				if(copyindex.get() != c)return false;
			}
		}
		return true;
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
