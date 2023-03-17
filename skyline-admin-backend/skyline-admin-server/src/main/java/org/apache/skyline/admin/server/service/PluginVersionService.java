package org.apache.skyline.admin.server.service;

import org.apache.skyline.admin.commons.model.query.PluginVersionQuery;
import org.apache.skyline.admin.commons.model.vo.PluginVersionVO;

import java.util.List;

/**
 * @author hejianbing
 * @version @Id: PluginVersionService.java, v 0.1 2023年01月05日 14:51 hejianbing Exp $
 */
public interface PluginVersionService {

    List<PluginVersionVO> pageList(PluginVersionQuery pluginVersionQuery);

    boolean deleted(Long id);

    boolean actived(Long id);

    boolean disabled(Long id);
}