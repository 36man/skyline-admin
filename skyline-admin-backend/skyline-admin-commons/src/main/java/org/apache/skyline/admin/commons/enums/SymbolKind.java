package org.apache.skyline.admin.commons.enums;

import lombok.Getter;

/**
 * @author jojocodex
 * @version @Id: SymbolKind.java, v 0.1 2022年12月22日 14:29 jojocodex Exp $
 */
@Getter
public enum SymbolKind implements IEnum {
    COMMA(","), DOT("."),COLON(":"),SEMICOLON(";"),SLASH("/"),
    BACKSLASH("\\"),PLUS("+"), DASH("-"),EQUALS("="),UNDERSCORE("_"),
    LEFT_SQUARE_BRACKET("["), RIGHT_SQUARE_BRACKET("]"),LEFT_CURLY_BRACE("{"),
    RIGHT_CURLY_BRACE("}"), AMPERSAND("&amp;"),ACCENT("`"),LINE_BREAK("\r\n"),QUESTION("?");

    private String symbol;
    SymbolKind(String symbol){
        this.symbol = symbol;
    }

    @Override
    public String getCode() {
        return this.symbol;
    }

}