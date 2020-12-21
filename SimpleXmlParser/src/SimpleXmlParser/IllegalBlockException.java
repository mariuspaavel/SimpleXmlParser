package SimpleXmlParser;

public class IllegalBlockException extends XmlParsingException {
	IllegalBlockException(){}
	IllegalBlockException(String message, String blockname){super(String.format("%s Blockname=\"%s\"", message, blockname));}
}
