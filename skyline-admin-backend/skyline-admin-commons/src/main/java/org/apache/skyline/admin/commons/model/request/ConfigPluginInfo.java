package org.apache.skyline.admin.commons.model.request;

import lombok.Data;

@Data
public class ConfigPluginInfo {

    private String stage;

    private String stageName;

    private Integer stateSn;

    private String jarUrl;

    private String storeType;

    private Integer sn;

    private String config;

    private String classDefine;

    private String ver;
}