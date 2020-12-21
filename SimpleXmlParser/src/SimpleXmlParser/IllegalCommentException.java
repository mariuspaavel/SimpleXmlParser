package SimpleXmlParser;

public class IllegalCommentException extends XmlParsingException {
	IllegalCommentException(){}
	IllegalCommentException(String message){super(message);}
}
