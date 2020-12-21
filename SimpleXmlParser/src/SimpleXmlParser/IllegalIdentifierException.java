package SimpleXmlParser;

public class IllegalIdentifierException extends IllegalBlockException {
	IllegalIdentifierException(){
		super("Illegal characters in identifier string", "");
	}
}
