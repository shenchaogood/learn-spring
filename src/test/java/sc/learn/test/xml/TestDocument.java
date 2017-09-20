package sc.learn.test.xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TestDocument {

	@Test
	public void testParseXml() throws SAXException, IOException, ParserConfigurationException{
		Document document=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				getClass().getResourceAsStream("/sc/learn/test/xml/test.xml"));
		Map<String, String> params=new HashMap<>();
		parseNodeTextContent(params,document);
		System.out.println(params);
	}
	
	private void parseNodeTextContent(Map<String, String> params, Node node) {
        NodeList childNodes = node.getChildNodes();
        if (childNodes.getLength() == 1 && childNodes.item(0).getNodeType() == Node.TEXT_NODE) {
            params.put(node.getNodeName(), node.getTextContent());
        } else {
            for (int i = 0; i < childNodes.getLength(); i++) {
                parseNodeTextContent(params, childNodes.item(i));
            }
        }
    }
}
