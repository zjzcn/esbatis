package com.github.esbatis.utils;

import org.w3c.dom.CharacterData;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author jinzhong.zhang
 */
public class XmlNodeUtils {


    public static Node getParent(Node node) {
        Node parent = node.getParentNode();
        if (parent == null || !(parent instanceof Element)) {
            return null;
        }
        return parent;
    }

    public static String getPath(Node node) {
        StringBuilder builder = new StringBuilder();
        Node current = node;
        while (current != null && current instanceof Element) {
            if (current != node) {
                builder.insert(0, "/");
            }
            builder.insert(0, current.getNodeName());
            current = current.getParentNode();
        }
        return builder.toString();
    }

    public static String getName(Node node) {
        return node.getNodeName();
    }

    public static String getStringBody(Node node) {
        return getStringBody(node, null);
    }

    public static String getStringBody(Node node, String def) {
        String body = parseBody(node);
        if (body == null) {
            return def;
        } else {
            return body;
        }
    }

    public static Boolean getBooleanBody(Node node) {
        return getBooleanBody(node, null);
    }

    public static Boolean getBooleanBody(Node node, Boolean def) {
        String body = parseBody(node);
        if (body == null) {
            return def;
        } else {
            return Boolean.valueOf(body);
        }
    }

    public static Integer getIntBody(Node node) {
        return getIntBody(node, null);
    }

    public static Integer getIntBody(Node node, Integer def) {
        String body = parseBody(node);
        if (body == null) {
            return def;
        } else {
            return Integer.parseInt(body);
        }
    }

    public static Long getLongBody(Node node) {
        return getLongBody(node, null);
    }

    public static Long getLongBody(Node node, Long def) {
        String body = parseBody(node);
        if (body == null) {
            return def;
        } else {
            return Long.parseLong(body);
        }
    }

    public static Double getDoubleBody(Node node) {
        return getDoubleBody(node, null);
    }

    public static Double getDoubleBody(Node node, Double def) {
        String body = parseBody(node);
        if (body == null) {
            return def;
        } else {
            return Double.parseDouble(body);
        }
    }

    public static Float getFloatBody(Node node) {
        return getFloatBody(node, null);
    }

    public static Float getFloatBody(Node node, Float def) {
        String body = parseBody(node);
        if (body == null) {
            return def;
        } else {
            return Float.parseFloat(body);
        }
    }

    public static <T extends Enum<T>> T getEnumAttribute(Node node, Class<T> enumType, String name) {
        return getEnumAttribute(node, enumType, name, null);
    }

    public static <T extends Enum<T>> T getEnumAttribute(Node node, Class<T> enumType, String name, T def) {
        String value = getStringAttribute(node, name);
        if (value == null) {
            return def;
        } else {
            return Enum.valueOf(enumType, value);
        }
    }

    public static String getStringAttribute(Node node, String name) {
        return getStringAttribute(node, name, null);
    }

    public static String getStringAttribute(Node node, String name, String def) {
        Properties attributes = parseAttributes(node);
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return value;
        }
    }

    public static Boolean getBooleanAttribute(Node node, String name) {
        return getBooleanAttribute(node, name, null);
    }

    public static Boolean getBooleanAttribute(Node node, String name, Boolean def) {
        Properties attributes = parseAttributes(node);
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Boolean.valueOf(value);
        }
    }

    public static Integer getIntAttribute(Node node, String name) {
        return getIntAttribute(node, name, null);
    }

    public static Integer getIntAttribute(Node node, String name, Integer def) {
        Properties attributes = parseAttributes(node);
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static Long getLongAttribute(Node node, String name) {
        return getLongAttribute(node, name, null);
    }

    public static Long getLongAttribute(Node node, String name, Long def) {
        Properties attributes = parseAttributes(node);
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Long.parseLong(value);
        }
    }

    public static Double getDoubleAttribute(Node node, String name) {
        return getDoubleAttribute(node, name, null);
    }

    public static Double getDoubleAttribute(Node node, String name, Double def) {
        Properties attributes = parseAttributes(node);
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Double.parseDouble(value);
        }
    }

    public static Float getFloatAttribute(Node node, String name) {
        return getFloatAttribute(node, name, null);
    }

    public static Float getFloatAttribute(Node node, String name, Float def) {
        Properties attributes = parseAttributes(node);
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Float.parseFloat(value);
        }
    }

    public static List<Node> getChildren(Node node) {
        List<Node> children = new ArrayList<Node>();
        NodeList nodeList = node.getChildNodes();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    children.add(item);
                }
            }
        }
        return children;
    }

    public static Properties getChildrenAsProperties(Node node) {
        Properties properties = new Properties();
        for (Node child : getChildren(node)) {
            String name = getStringAttribute(child, "name");
            String value = getStringAttribute(child, "value");
            if (name != null && value != null) {
                properties.setProperty(name, value);
            }
        }
        return properties;
    }

    private static Properties parseAttributes(Node n) {
        Properties attributes = new Properties();
        NamedNodeMap attributeNodes = n.getAttributes();
        if (attributeNodes != null) {
            for (int i = 0; i < attributeNodes.getLength(); i++) {
                Node attribute = attributeNodes.item(i);
                attributes.put(attribute.getNodeName(), attribute.getNodeValue());
            }
        }
        return attributes;
    }

    private static String parseBody(Node node) {
        String data = getBodyData(node);
        if (data == null) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                data = getBodyData(child);
                if (data != null) {
                    break;
                }
            }
        }
        return data;
    }

    private static String getBodyData(Node child) {
        if (child.getNodeType() == Node.CDATA_SECTION_NODE
                || child.getNodeType() == Node.TEXT_NODE) {
            String data = ((CharacterData) child).getData();
            return data;
        }
        return null;
    }

}