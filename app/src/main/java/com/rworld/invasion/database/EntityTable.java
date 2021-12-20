package com.rworld.invasion.database;

import android.util.Log;

import com.rworld.core.GameActivity;
import com.rworld.invasion.MainActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class EntityTable {

    public final EntityRow[] rows;

    public EntityTable(MainActivity activity) {
        try {
            rows = _parse(activity, activity.getAssets().open("entities.xml"));
        } catch (Exception e) {
            Log.e("Invasion3D", "couldn't load entities database", e);
            throw new RuntimeException("couldn't load entities database");
        }
    }

    private EntityRow[] _parse(GameActivity activity, InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        ArrayList<EntityRow> entityRows = new ArrayList<EntityRow>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse(inputStream);
        Element root = dom.getDocumentElement();

        NodeList childNodes = root.getElementsByTagName("entity");
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            entityRows.add(_parseEntity(activity, childNode));
        }

        return entityRows.toArray(new EntityRow[entityRows.size()]);
    }

    private EntityRow _parseEntity(GameActivity activity, Node node) {

        EntityRow entityRow = new EntityRow();
        entityRow.name = _resolveResources(activity, node.getAttributes().getNamedItem("name").getTextContent());
        entityRow.shipClass = _resolveResources(activity, node.getAttributes().getNamedItem("shipClass").getTextContent());
        entityRow.race = _resolveResources(activity, node.getAttributes().getNamedItem("race").getTextContent());
        entityRow.level = Integer.parseInt(node.getAttributes().getNamedItem("level").getTextContent());

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            String nodeName = childNode.getNodeName();
            if (nodeName.equalsIgnoreCase("attributes")) {
                _parseAttributes(childNode, entityRow);
            } else if (nodeName.equalsIgnoreCase("assets")) {
                _parseAssets(childNode, entityRow);
            }
        }

        return entityRow;
    }

    private void _parseAttributes(Node node, EntityRow entityRow) {
        NodeList attributes = node.getChildNodes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            if (attribute.getNodeName().equalsIgnoreCase("life")) {
                entityRow.life = Float.parseFloat(attribute.getTextContent());
            } else if (attribute.getNodeName().equalsIgnoreCase("size")) {
                entityRow.size = Float.parseFloat(attribute.getTextContent());
            }
        }
    }

    private void _parseAssets(Node node, EntityRow entityRow) {
        NodeList attributes = node.getChildNodes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            if (attribute.getNodeName().equalsIgnoreCase("meshFilePath")) {
                entityRow.meshFilePath = attribute.getTextContent();
            } else if (attribute.getNodeName().equalsIgnoreCase("skinFilePath")) {
                entityRow.skinFilePath = attribute.getTextContent();
            } else if (attribute.getNodeName().equalsIgnoreCase("bulletFilePath")) {
                entityRow.bulletFilePath = attribute.getTextContent();
            }
        }
    }

    private static String _resolveResources(GameActivity activity, String value) {
        int resId = 0;
        String[] parts = value.split("[:|/]");
        if (parts.length == 2) {
            String name = parts[1];
            String type = parts[0].substring(1);
            resId = activity.getResources().getIdentifier(name, type, activity.getPackageName());
        } else if (parts.length == 3) {
            String name = parts[2];
            String type = parts[1].substring(1);
            String packageName = parts[0];
            resId = activity.getResources().getIdentifier(name, type, packageName);
        }
        if (resId > 0) {
            return activity.getResources().getString(resId);
        } else {
            return value;
        }
    }
}
