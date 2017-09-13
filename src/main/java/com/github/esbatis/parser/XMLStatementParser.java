package com.github.esbatis.parser;

import com.github.esbatis.mapper.MappedStatement;
import com.github.esbatis.mapper.MapperFactory;
import com.github.esbatis.parser.nodes.XmlNode;
import com.github.esbatis.utils.XMLNodeUtils;
import org.w3c.dom.Node;

/**
 * @author jinzhong.zhang
 */
public class XMLStatementParser {

    private MapperFactory mapperFactory;
    private final Node statementNode;
    private final String namespace;

    public XMLStatementParser(MapperFactory mapperFactory, String namespace, Node statementNode) {
        this.mapperFactory = mapperFactory;
        this.statementNode = statementNode;
        this.namespace = namespace;
    }

    public void parseStatementNode() {
        String commandType = XMLNodeUtils.getName(statementNode);
        String id = XMLNodeUtils.getStringAttribute(statementNode, "id");
        String url = XMLNodeUtils.getStringAttribute(statementNode, "url");
        String method = XMLNodeUtils.getStringAttribute(statementNode, "method");

        XMLNodeParser builder = new XMLNodeParser(statementNode);
        XmlNode bodyNode = builder.parseBodyNode();

        MappedStatement ms = new MappedStatement(commandType, globalId(id), url, method, bodyNode);
        mapperFactory.addMappedStatement(ms);
    }

    private String globalId(String id) {
        // is it qualified with this namespace yet?
        if (id.startsWith(namespace + ".")) {
            return id;
        }
        if (id.contains(".")) {
            throw new ParserException("Dots are not allowed in element names, please remove it from " + id);
        }
        return namespace + "." + id;
    }
}
