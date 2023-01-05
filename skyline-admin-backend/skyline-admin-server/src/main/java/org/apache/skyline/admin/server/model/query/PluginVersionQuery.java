package org.apache.skyline.admin.server.model.query;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author hejianbing
 * @version @Id: PluginVersionQuery.java, v 0.1 2023年01月05日 15:30 hejianbing Exp $
 */
@Data
public class PluginVersionQuery {

    @NotNull(message = "pluginId is not null")
    private Long pluginId;


    private String tags;
}