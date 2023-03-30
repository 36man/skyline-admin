package org.apache.skyline.admin.commons.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ConfigPluginInfo {

    @NotBlank
    private String stage;

    @NotBlank
    private String stageName;

    @NotNull
    private Integer stateSn;

    @NotBlank
    private String jarUrl;

    @NotNull
    private Integer sn;

    private String config;

    @NotBlank
    private String classDefine;

    @NotBlank
    private String ver;
}