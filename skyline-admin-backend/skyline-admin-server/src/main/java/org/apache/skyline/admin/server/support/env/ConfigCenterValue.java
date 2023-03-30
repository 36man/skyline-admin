package org.apache.skyline.admin.server.support.env;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ConfigCenterValue {

    private String user;

    private String secret;

    private String url;

    private Map<String, Object> configs = new HashMap<>();

}