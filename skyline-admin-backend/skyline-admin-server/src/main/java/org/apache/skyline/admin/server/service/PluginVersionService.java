package org.apache.skyline.admin.server.service;

import org.apache.skyline.admin.commons.model.query.PluginVersionQuery;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.vo.PluginVersionVO;
import org.bravo.gaia.commons.base.PageBean;

/**
 * @author hejianbing
 * @version @Id: PluginVersionService.java, v 0.1 2023年01月05日 14:51 hejianbing Exp $
 */
public interface PluginVersionService {

    PageBean<PluginVersionVO> pageList(PageRequest<PluginVersionQuery> pageRequest);

    boolean deleted(Long id);

    boolean actived(Long id);

    boolean disabled(Long id);
}