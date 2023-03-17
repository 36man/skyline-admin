package org.apache.skyline.admin.server.domain.service.impl;

import org.apache.skyline.admin.server.domain.model.PluginDomain;
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

        try {
            pluginRepository.create(newPluginDomain);
        } catch (DuplicateKeyException exception) {
            PluginCombineQuery combineQuery = PluginCombineQuery.builder()
                    .classDefine(pluginDomainRequest.getPluginDefine().getClassDefine())
                    .build();

            PluginDomain pluginDomain = pluginRepository.findOne(combineQuery);

            pluginDomain.setId(pluginDomain.getId());

            pluginRepository.updateById(pluginDomain);
        }
        pluginVersionRepository.create(pluginDomainRequest.getPluginVersionDomain());

        return newPluginDomain.getId();
    }

    @Transactional
    @Override
    public Boolean delete(PluginCombineQuery combineQuery) {
        boolean success = pluginRepository.delete(combineQuery);

        PluginVersionCombineQuery versionCombineQuery = PluginVersionCombineQuery.builder()
                .pluginId(combineQuery.getId())
                .build();

        success = pluginVersionRepository.delete(versionCombineQuery);

        return success;
    }

}