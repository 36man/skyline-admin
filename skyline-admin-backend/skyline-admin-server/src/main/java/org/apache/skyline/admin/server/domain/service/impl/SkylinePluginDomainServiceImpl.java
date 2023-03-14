package org.apache.skyline.admin.server.domain.service.impl;

import org.apache.skyline.admin.server.domain.model.PluginDomain;
import org.apache.skyline.admin.server.domain.repository.PluginVersionRepository;
import org.apache.skyline.admin.server.domain.repository.SkylinePluginRepository;
import org.apache.skyline.admin.server.domain.request.GenerateSkylinePluginDomainRequest;
import org.apache.skyline.admin.server.domain.service.SkylinePluginDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginDomainServiceImpl.java, v 0.1 2022年12月24日 22:08 hejianbing Exp $
 */
@Service
public class SkylinePluginDomainServiceImpl implements SkylinePluginDomainService {

    @Autowired
    private SkylinePluginRepository skylinePluginRepository;

    @Autowired
    private PluginVersionRepository pluginVersionRepository;


    @Transactional
    @Override
    public Long createPlugin(GenerateSkylinePluginDomainRequest pluginDomainRequest) {
        PluginDomain skylinePlugin = pluginDomainRequest.getPluginDomain();

        try {
            skylinePluginRepository.create(skylinePlugin);
        } catch (DuplicateKeyException exception) {
            PluginDomain pluginDomain = skylinePluginRepository.findByClassDefine(skylinePlugin.getDefineClass());

            skylinePlugin.setId(pluginDomain.getId());

            pluginDomain.setKeywords(skylinePlugin.getKeywords());

            skylinePluginRepository.updateById(pluginDomain);
        }

        pluginVersionRepository.create(pluginDomainRequest.getPluginVersionDomain());

        return skylinePlugin.getId();
    }

}