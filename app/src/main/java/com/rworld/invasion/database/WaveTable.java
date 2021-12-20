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

public class WaveTable {

    public final WaveRow[] waveRows;

    public WaveTable(MainActivity activity) {
        try {
            waveRows = _parse(activity, activity.getAssets().open("waves.xml"));
        } catch (Exception e) {
            Log.e("Invasion3D", "couldn't load waves database", e);
            throw new RuntimeException("couldn't load waves database");
        }
    }

    public WaveRow createRandom(int level) {
        int newWveIndex = 0;
        if (waveRows.length > 1) {
            do {
                newWveIndex = (int) (Math.random() * waveRows.length);
            }
            while ((newWveIndex == _lastSelectedWave) || (level < waveRows[newWveIndex].level));
        }
        _lastSelectedWave = newWveIndex;
        return waveRows[newWveIndex];
    }

    private WaveRow[] _parse(GameActivity activity, InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        ArrayList<WaveRow> waveRows = new ArrayList<WaveRow>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse(inputStream);
        Element root = dom.getDocumentElement();

        NodeList waves = root.getElementsByTagName("wave");
        for (int i = 0; i < waves.getLength(); i++) {
            Node wave = waves.item(i);
            waveRows.add(_parseWave(wave));
        }

        return waveRows.toArray(new WaveRow[waveRows.size()]);
    }

    private WaveRow _parseWave(Node node) {
        ArrayList<WaveKey[]> channels = new ArrayList<WaveKey[]>();

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            String childNodeName = childNode.getNodeName();
            if (childNodeName.equalsIgnoreCase("channel")) {
                channels.add(_parseChannel(childNode));
            }
        }

        WaveRow waveRow = new WaveRow();
        waveRow.name = node.getAttributes().getNamedItem("name").getTextContent();
        waveRow.level = Integer.parseInt(node.getAttributes().getNamedItem("level").getTextContent());
        waveRow.channels = channels.toArray(new WaveKey[channels.size()][]);

        return waveRow;
    }

    private WaveKey[] _parseChannel(Node node) {
        ArrayList<WaveKey> waveKeys = new ArrayList<WaveKey>();

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            String childNodeName = childNode.getNodeName();
            if (childNodeName.equalsIgnoreCase("key")) {
                waveKeys.add(_parseKey(childNode));
            }
        }

        return waveKeys.toArray(new WaveKey[waveKeys.size()]);
    }

    private WaveKey _parseKey(Node node) {
        WaveKey waveKey = new WaveKey();
        waveKey.time = Float.parseFloat(node.getAttributes().getNamedItem("time").getTextContent());

        NodeList properties = node.getChildNodes();
        for (int j = 0; j < properties.getLength(); j++) {
            Node property = properties.item(j);
            String propertyNodeName = property.getNodeName();
            if (propertyNodeName.equalsIgnoreCase("position")) {
                String[] position = property.getTextContent().split(",\\s*");
                waveKey.positionX = Float.parseFloat(position[0]);
                waveKey.positionY = Float.parseFloat(position[1]);
                waveKey.positionZ = Float.parseFloat(position[2]);
            } else if (propertyNodeName.equalsIgnoreCase("rotation")) {
                String[] rotation = property.getTextContent().split(",\\s*");
                waveKey.rotationX = Float.parseFloat(rotation[0]);
                waveKey.rotationY = Float.parseFloat(rotation[1]);
                waveKey.rotationZ = Float.parseFloat(rotation[2]);
            }
        }

        return waveKey;
    }

    private int _lastSelectedWave = -1;
}
