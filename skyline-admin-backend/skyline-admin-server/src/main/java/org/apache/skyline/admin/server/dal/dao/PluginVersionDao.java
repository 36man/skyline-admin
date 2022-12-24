package org.apache.skyline.admin.server.dal.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.skyline.admin.server.dal.dataobject.PluginVersionDO;

/**
 * @author hejianbing
 * @version @Id: PluginVersionDao.java, v 0.1 2022年12月25日 00:17 hejianbing Exp $
 */
@Mapper
public interface PluginVersionDao extends BaseMapper<PluginVersionDO> {
}