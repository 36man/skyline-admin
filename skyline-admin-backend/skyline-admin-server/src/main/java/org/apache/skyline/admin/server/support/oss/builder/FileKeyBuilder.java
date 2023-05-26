package org.apache.skyline.admin.server.support.oss.builder;

import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.commons.enums.SymbolKind;

/**
 * @author hejianbing
 * @version @Id: FileKeyBuilder.java, v 0.1 2022年12月22日 14:08 hejianbing Exp $
 */
public class FileKeyBuilder {

    public static final String BACKSLASH = "[\\\\]+";

    public static final String SLASH     = "[/\\\\//]+";

    private String namespace;

    private String fileName;

    public FileKeyBuilder(String namespace, String fileName) {
        this.namespace = namespace;

        this.fileName = fileName;
    }

    public static FileKeyBuilder newBuilder(String namespace, String fileName) {
        return new FileKeyBuilder(namespace, fileName);
    }

    public String build() {
        if (StringUtils.isBlank(this.namespace)) {
            return fileName;
        }

        this.namespace = this.namespace.replaceAll(BACKSLASH, SymbolKind.SLASH.getSymbol());

        // /path1/path/ ---> /path1/path//
        String path = this.namespace + SymbolKind.SLASH.getSymbol();

        // /path1/path// ---> /path1/path/
        path = path.replaceAll(SLASH, SymbolKind.SLASH.getSymbol());

        // /path1/path/ ---> path1/path/
        if (path.startsWith(SLASH)) {
            path = path.substring(1);
        }

        return path + fileName;
    }

}