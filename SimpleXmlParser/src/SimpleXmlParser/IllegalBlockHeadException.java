package SimpleXmlParser;

public class IllegalBlockHeadException extends IllegalBlockException {
	IllegalBlockHeadException(){}
	IllegalBlockHeadException(String message, String blockname){super(message, blockname);}
}
