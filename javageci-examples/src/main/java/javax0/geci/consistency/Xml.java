package javax0.geci.consistency;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Xml {
    final Document doc;
    final XPath xPath;

    public static Xml from(File file) throws IOException, SAXException, ParserConfigurationException {
        return new Xml(file);
    }

    private Xml(File file) throws ParserConfigurationException, IOException, SAXException {
        var dbFactory = DocumentBuilderFactory.newInstance();
        final var dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(file);
        xPath = XPathFactory.newInstance().newXPath();
    }

    public Document document() {
        return doc;
    }

    public List<String> gets(String path) throws XPathExpressionException {
        var nodes = (NodeList) xPath.evaluate(path, doc, XPathConstants.NODESET);
        var strings = new ArrayList<String>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); ++i) {
            var e = (Element) nodes.item(i);
            strings.add(e.getTextContent());
        }
        return strings;
    }

    public String get(String path) throws XPathExpressionException {
        return (String) xPath.evaluate(path + "/text()", doc, XPathConstants.STRING);
    }
}
