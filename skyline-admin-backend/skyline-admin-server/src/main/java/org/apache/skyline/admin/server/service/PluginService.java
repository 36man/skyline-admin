package org.apache.skyline.admin.server.service;

import org.apache.skyline.admin.commons.model.query.PluginQuery;
import org.apache.skyline.admin.commons.model.request.PluginRequest;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.vo.PluginVO;
import org.bravo.gaia.commons.base.PageBean;

/**
 * @author hejianbing
 * @version @Id: PluginService.java, v 0.1 2022年12月23日 13:39 hejianbing Exp $
 */
public interface PluginService {

    Boolean generate(PluginRequest pluginRequest) ;

    PageBean<PluginVO> pageList(PageRequest<PluginQuery> condition);

    Boolean deleted(Long id);

    Boolean actived(Long id);

    Boolean disabled(Long id);
}