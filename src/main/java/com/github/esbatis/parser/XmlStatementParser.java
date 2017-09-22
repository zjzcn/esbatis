package com.github.esbatis.parser;

import com.github.esbatis.mapper.MappedStatement;
import com.github.esbatis.mapper.MapperFactory;
import com.github.esbatis.parser.nodes.XmlNode;
import com.github.esbatis.utils.XmlNodeUtils;
import org.w3c.dom.Node;

/**
 * @author jinzhong.zhang
 */
public class XmlStatementParser {

    private MapperFactory mapperFactory;
    private final Node statementNode;
    private final String namespace;

    public XmlStatementParser(MapperFactory mapperFactory, String namespace, Node statementNode) {
        this.mapperFactory = mapperFactory;
        this.statementNode = statementNode;
        this.namespace = namespace;
    }

    public void parseStatementNode() {
        String commandType = XmlNodeUtils.getName(statementNode);
        String id = XmlNodeUtils.getStringAttribute(statementNode, "id");
        String url = XmlNodeUtils.getStringAttribute(statementNode, "url");
        String method = XmlNodeUtils.getStringAttribute(statementNode, "method");

        XmlNodeParser builder = new XmlNodeParser(statementNode);
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
