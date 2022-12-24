package org.apache.skyline.admin.server.oss.builder;

import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.commons.enums.SymbolKind;

/**
 * @author hejianbing
 * @version @Id: FileKeyBuilder.java, v 0.1 2022年12月22日 14:08 hejianbing Exp $
 */
public class FileKeyBuilder {

    public static final String BACKSLASH = "[\\\\]+";

    public static final String SLASH     = "[/\\\\//]+";

    private String group;

    private String fileName;

    public FileKeyBuilder(String group, String fileName) {
        this.group = group;

        this.fileName = fileName;
    }

    public static FileKeyBuilder newBuilder(String group, String fileName) {
        return new FileKeyBuilder(group, fileName);
    }

    public String build() {
        if (StringUtils.isBlank(this.group)) {
            return fileName;
        }

        this.group = this.group.replaceAll(BACKSLASH, SymbolKind.SLASH.getSymbol());

        // /path1/path/ ---> /path1/path//
        String path = this.group + SymbolKind.SLASH.getSymbol();

        // /path1/path// ---> /path1/path/
        path = path.replaceAll(SLASH, SymbolKind.SLASH.getSymbol());

        // /path1/path/ ---> path1/path/
        if (path.startsWith(SLASH)) {
            path = path.substring(1);
        }

        return path + fileName;
    }
}