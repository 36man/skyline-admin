package org.apache.skyline.admin.server.service;

import org.apache.skyline.admin.commons.model.query.PluginQuery;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.request.GeneratePluginRequest;
import org.apache.skyline.admin.commons.model.request.UpdatePluginRequest;
import org.apache.skyline.admin.commons.model.vo.PluginVO;
import org.bravo.gaia.commons.base.PageBean;

import java.util.List;

/**
 * @author hejianbing
 * @version @Id: PluginService.java, v 0.1 2022年12月23日 13:39 hejianbing Exp $
 */
public interface PluginService {

    Boolean generate(GeneratePluginRequest pluginGenerateRequest) ;

    PageBean<PluginVO> pageList(PageRequest<PluginQuery> condition);

    Boolean deleted(Long id);

    Boolean active(Long id,boolean active);

    List<PluginVO> queryForList(PluginQuery pluginQuery);

    List<PluginVO> matchQuery(PluginQuery pluginQuery);

    Boolean update(UpdatePluginRequest updatePlugin);
}