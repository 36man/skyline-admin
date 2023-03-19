package org.apache.skyline.admin.server.domain.service.impl;

import com.google.common.collect.Lists;
import org.apache.skyline.admin.server.domain.model.PluginDomain;
import org.apache.skyline.admin.server.domain.model.PluginVersionDomain;
import org.apache.skyline.admin.server.domain.query.PluginCombineQuery;
import org.apache.skyline.admin.server.domain.query.PluginVersionCombineQuery;
import org.apache.skyline.admin.server.domain.repository.PluginRepository;
import org.apache.skyline.admin.server.domain.repository.PluginVersionRepository;
import org.apache.skyline.admin.server.domain.request.GeneratePluginDomainRequest;
import org.apache.skyline.admin.server.domain.service.PluginDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hejianbing
 * @version @Id: PluginDomainServiceImpl.java, v 0.1 2022年12月24日 22:08 hejianbing Exp $
 */
@Service
public class PluginDomainServiceImpl implements PluginDomainService {

    @Autowired
    private PluginRepository pluginRepository;

    @Autowired
    private PluginVersionRepository pluginVersionRepository;


    @Transactional
    @Override
    public Long storePlugin(GeneratePluginDomainRequest pluginDomainRequest) {
        PluginDomain newPluginDomain = pluginDomainRequest.getPluginDomain();

        try{
            Long id = pluginRepository.create(newPluginDomain);
            newPluginDomain.setId(id);
        }catch (DuplicateKeyException exception){

            PluginCombineQuery combineQuery = PluginCombineQuery.builder()
                    .classDefine(pluginDomainRequest.getPluginDefine().getClassDefine())
                    .build();

            PluginDomain pluginDomain = pluginRepository.findOne(combineQuery);
            pluginRepository.updateById(newPluginDomain);
            newPluginDomain.setId(pluginDomain.getId());
        }

        storeVer(pluginDomainRequest.getPluginVersionDomain(), newPluginDomain);

        return newPluginDomain.getId();
    }

    @Transactional
    @Override
    public Boolean delete(Long id) {
        PluginCombineQuery combineQuery = PluginCombineQuery.builder()
                .id(id)
                .build();

        boolean success = pluginRepository.delete(combineQuery);

        PluginVersionCombineQuery versionCombineQuery = PluginVersionCombineQuery.builder()
                .pluginIdList(Lists.newArrayList(combineQuery.getId()))
                .build();

        success = pluginVersionRepository.delete(versionCombineQuery);

        return success;
    }

    @Override
    public Boolean actived(Long id) {
        PluginDomain pluginDomain = new PluginDomain();
        pluginDomain.setId(id);
        pluginDomain.setActive(true);
        boolean active = pluginRepository.updateById(pluginDomain);

        PluginVersionCombineQuery combineQuery = PluginVersionCombineQuery.builder()
                .pluginIdList(Lists.newArrayList(id)).build();

        active = pluginVersionRepository.active(combineQuery);

        return active;
    }

    @Override
    public Boolean disabled(Long id) {
        PluginDomain pluginDomain = new PluginDomain();
        pluginDomain.setId(id);
        pluginDomain.setActive(false);

        boolean disable = pluginRepository.updateById(pluginDomain);

        PluginVersionCombineQuery combineQuery = PluginVersionCombineQuery.builder()
                .pluginIdList(Lists.newArrayList(id)).build();

        disable = pluginVersionRepository.disable(combineQuery);

        return disable;

    }

    private void storeVer(PluginVersionDomain pluginVersionDomain, PluginDomain newPluginDomain) {
        pluginVersionDomain.setPluginDomain(newPluginDomain);
        try{
            pluginVersionRepository.create(pluginVersionDomain);
        }catch (DuplicateKeyException ex){
            PluginVersionCombineQuery combineQuery = PluginVersionCombineQuery.builder()
                    .pluginIdList(Lists.newArrayList(newPluginDomain.getId()))
                    .ver(pluginVersionDomain.getVer())
                    .build();
            pluginVersionRepository.update(combineQuery, pluginVersionDomain);
        }
    }

}