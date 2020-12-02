package SimpleXmlParser;

public interface XmlConverter<T> {
	Block toXml();
	T fromXml(Block input);
}
