package org.apache.skyline.admin.server.service;

import org.apache.skyline.admin.server.model.query.SkylinePluginQuery;
import org.apache.skyline.admin.server.model.request.GenerateSkylinePluginRequest;
import org.apache.skyline.admin.server.model.vo.SkylinePluginVO;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.commons.base.PageCondition;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginService.java, v 0.1 2022年12月23日 13:39 hejianbing Exp $
 */
public interface SkylinePluginService {

    Boolean uploadPlugin(GenerateSkylinePluginRequest pluginRequest) ;

    PageBean<SkylinePluginVO> pageList(PageCondition<SkylinePluginQuery> condition);

    Boolean deleted(Long id);

    Boolean actived(Long id);

    Boolean disabled(Long id);
}