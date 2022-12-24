package org.apache.skyline.admin.server.dal.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.skyline.admin.server.dal.dataobject.SkylinePluginDO;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginMapper.java, v 0.1 2022年12月23日 13:42 hejianbing Exp $
 */
@Mapper
public interface SkylinePluginDao extends BaseMapper<SkylinePluginDO> {
}