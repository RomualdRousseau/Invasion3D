package com.rworld.core.graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public class MeshLoader {

    public MeshLoader(InputStream stream) throws IOException {
        _loadFromStream(new BufferedReader(new InputStreamReader(stream)));
        stream.close();
    }

    public void dispose() {
        _elements = null;
        _indexes = null;
    }

    public void _loadFromStream(BufferedReader stream) throws NumberFormatException, IOException {
        ArrayList<Float> vertice = new ArrayList<Float>();
        ArrayList<Float> texCoords = new ArrayList<Float>();
        String line;
        String[] parts;
        String[] tokens;

        while ((line = stream.readLine()) != null) {
            tokens = line.split(" ");

            if (tokens[0].equals("v")) {
                vertice.add(Float.parseFloat(tokens[1]));
                vertice.add(Float.parseFloat(tokens[2]));
                vertice.add(Float.parseFloat(tokens[3]));
            } else if (tokens[0].equals("vt")) {
                texCoords.add(Float.parseFloat(tokens[1]));
                texCoords.add(Float.parseFloat(tokens[2]));
            } else if (tokens[0].equals("f")) {
                for (int i = 1; i <= 3; i++) {
                    parts = tokens[i].split("/");

                    Element tmp = new Element();
                    int vi = Integer.parseInt(parts[0]) - 1;
                    tmp.x = vertice.get(vi * 3 + 0);
                    tmp.y = vertice.get(vi * 3 + 1);
                    tmp.z = vertice.get(vi * 3 + 2);
                    if (parts.length > 1) {
                        int vti = Integer.parseInt(parts[1]) - 1;
                        tmp.u = texCoords.get(vti * 2 + 0);
                        tmp.v = texCoords.get(vti * 2 + 1);
                    }

                    int k = _elements.indexOf(tmp);
                    if (k == -1) {
                        _elements.add(tmp);
                        k = _elements.size() - 1;
                    }
                    _indexes.add((short) k);
                }
            }
        }
    }

    public int GetElementCount() {
        return _elements.size();
    }

    public int GetIndexCount() {
        return _indexes.size();
    }

    public void loadBuffers(FloatBuffer vertexBuffer, FloatBuffer texCoordBuffer, ShortBuffer indexBuffer, float scaleX, float scaleY, float scaleZ) {
        for (Element e : _elements) {
            vertexBuffer.put(e.x * scaleX);
            vertexBuffer.put(e.y * scaleY);
            vertexBuffer.put(e.z * scaleZ);
            texCoordBuffer.put(e.u);
            texCoordBuffer.put(e.v);
        }
        for (short s : _indexes) {
            indexBuffer.put(s);
        }
    }

    class Element {
        public float x, y, z, u, v;

        @Override
        public boolean equals(Object o) {
            if (o instanceof Element) {
                return equals((Element) o);
            } else {
                return false;
            }
        }

        public boolean equals(Element o) {
            return (x == o.x) && (y == o.y) && (z == o.z) && (u == o.u) && (v == o.v);
        }
    }

    ;

    private ArrayList<Element> _elements = new ArrayList<Element>();
    private ArrayList<Short> _indexes = new ArrayList<Short>();
}
