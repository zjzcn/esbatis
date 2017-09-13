package com.github.esbatis.parser.nodes;

import com.github.esbatis.parser.DynamicContext;

/**
 * @author jinzhong.zhang
 */
public interface XmlNode {

    boolean apply(DynamicContext context);
}
