package SimpleXmlParser;

public class Parser {

	private Parser() {}
	
	private static Param readParam(Index idx) {
		assert(!Index.keyc(idx.get()) && !Index.ws(idx.get()));
		idx.findidentifend();
		Text identif = new Text(idx);
		idx.skipWS();
		if(idx.get() != '=')throw new RuntimeException("invalid param");
		idx.jump(1);
		idx.skipWS();
		if(idx.get() != '\"')throw new RuntimeException("invalid param");
		idx.jump(1);
		idx.findChar('\"');
		Text value = new Text(idx);
		idx.pos+=1;
		Param p = new Param(identif, value);
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
			d.params.add(readParam(idx));
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
			else if(idx.get() != '<')d.children.add((readText(idx)));
			else d.children.add((readBlock(idx)));
		}
	}
	
	
	public static Document parse(String source) {
		Index idx = new Index(source, 0);
		Document d = new Document();
		readXmlHead(idx, d);
		readXmlBody(idx, d);
		
		return d;
	}
	
	private static Block readBlockHead(Index idx) {
		checkch(idx, '<', "invalid block start position");
		idx.jump(1);
		idx.skipWS();
		idx.findidentifend();
		Block b = new Block(new Text(idx), false);
		while(true) {
			idx.skipWS();
			if(idx.get() == '/') {
				idx.skipWS();
				checkch(idx, '>', "invalid block head no '>' after '/'");
				idx.jump(1);
				return b;
			}
			if(idx.get() == '>') {
				idx.jump(1);
				b.addBody();
				return b;
			}
			b.params.add(readParam(idx));
		}
	}
	private static boolean readBlockTail(Index idx, Block b) {
		Index idxcpy = new Index(idx.source, idx.pos);
		checkch(idxcpy, '<', "invalid block tail start");
		
		idxcpy.jump(1);
		idxcpy.skipWS();
		if(idxcpy.get() != '/')return false;
		idx.jump(1);
		idxcpy.skipWS();
		idxcpy.findidentifend();
		if(idx.source.substring(idxcpy.prev, idxcpy.pos).equals(b.name.toString()))return false;
		idxcpy.skipWS();
		checkch(idxcpy, '>', "invalid block tail");
		idxcpy.jump(1);
		idx.pos = idxcpy.pos;
		idx.prev = idxcpy.prev;
		return true;
		
	}
	
	private static Text readText(Index idx) {
		idx.findChar('<');
		if(idx.pos >= idx.source.length())idx.pos = idx.source.length();
		Text txt = new Text(idx);
		return txt;
	}
	
	private static Block readBlock(Index idx) {
		Block b = readBlockHead(idx);
		
		while(true){
			if(idx.get() != '<')b.children.add((readText(idx)));
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
	Index(String source, int pos){
		this.source = source;
		this.pos = this.prev = pos;
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
		while(get()!=c)pos++;
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
		default: return false;
		}
	}
}
