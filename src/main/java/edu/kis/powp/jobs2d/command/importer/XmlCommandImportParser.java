package edu.kis.powp.jobs2d.command.importer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

public class XmlCommandImportParser implements CommandImportParser {
    @Override
    public CommandImportResult parse(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new CommandImportException("Input is empty");
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(text)));
            Element root = document.getDocumentElement();
            if (root == null) {
                throw new CommandImportException("Missing root element");
            }

            String name = null;
            if (root.hasAttribute("name")) {
                name = root.getAttribute("name");
            }

            List<DriverCommand> commands = new ArrayList<>();
            if ("commands".equals(root.getTagName())) {
                parseChildren(root, commands);
            } else if ("command".equals(root.getTagName())) {
                commands.add(parseCommandElement(root));
            } else {
                throw new CommandImportException("Unsupported root element: " + root.getTagName());
            }

            if (commands.isEmpty()) {
                throw new CommandImportException("No commands found");
            }

            return new CommandImportResult(commands, name);
        } catch (CommandImportException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CommandImportException("XML import failed", ex);
        }
    }

    private void parseChildren(Element parent, List<DriverCommand> commands) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            commands.add(parseCommandElement((Element) node));
        }
    }

    private DriverCommand parseCommandElement(Element element) {
        String tagName = element.getTagName();
        String type = tagName;
        if ("command".equals(tagName)) {
            type = element.getAttribute("type");
        }

        int x = parseRequiredInt(element, "x");
        int y = parseRequiredInt(element, "y");

        if ("setPosition".equalsIgnoreCase(type)) {
            return new SetPositionCommand(x, y);
        }
        if ("operateTo".equalsIgnoreCase(type)) {
            return new OperateToCommand(x, y);
        }
        throw new CommandImportException("Unknown command type: " + type);
    }

    private int parseRequiredInt(Element element, String attribute) {
        String value = element.getAttribute(attribute);
        if (value == null || value.trim().isEmpty()) {
            throw new CommandImportException("Missing '" + attribute + "' attribute");
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new CommandImportException("Attribute '" + attribute + "' must be an integer");
        }
    }
}
