package org.apache.skyline.admin.server.service.impl;

import org.apache.skyline.admin.commons.model.query.PluginQuery;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.request.PluginRequest;
import org.apache.skyline.admin.commons.model.vo.PluginVO;
import org.apache.skyline.admin.server.commons.utils.PageCommonUtils;
import org.apache.skyline.admin.server.domain.model.PluginDomain;
import org.apache.skyline.admin.server.domain.query.PluginCombineQuery;
import org.apache.skyline.admin.server.domain.repository.PluginRepository;
import org.apache.skyline.admin.server.domain.request.GeneratePluginDomainRequest;
import org.apache.skyline.admin.server.domain.service.PluginDomainService;
import org.apache.skyline.admin.server.service.PluginService;
import org.apache.skyline.admin.server.support.codec.ObjectMapperCodec;
import org.apache.skyline.admin.server.support.oss.OSSExecutor;
import org.apache.skyline.admin.server.support.oss.builder.FileKeyBuilder;
import org.apache.skyline.admin.server.support.oss.config.OSSProperties;
import org.apache.skyline.admin.server.support.oss.config.StoreType;
import org.apache.skyline.admin.server.support.oss.request.ObjectStoreRequest;
import org.apache.skyline.admin.server.support.oss.response.ObjectStoreResponse;
import org.apache.skyline.admin.server.support.parse.PluginDefine;
import org.apache.skyline.admin.server.support.parse.PluginDefineResolver;
import org.bravo.gaia.commons.base.PageBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginService.java, v 0.1 2022年12月23日 13:31 hejianbing Exp $
 */
@Service
public class PluginServiceImpl implements PluginService {

    @Autowired
    private OSSExecutor ossExecutor;

    @Autowired
    private PluginDomainService pluginDomainService;

    @Autowired
    private OSSProperties ossProperties;

    @Autowired
    private ObjectMapperCodec objectMapperCodec;

    @Autowired
    private PluginRepository pluginRepository;

    public Boolean generate(PluginRequest pluginRequest) {
        ObjectStoreResponse result = storeLocal(pluginRequest);

        String storePath = result.getStorePath();

        PluginDefineResolver pluginDefineResolver = new PluginDefineResolver(objectMapperCodec,storePath);

        PluginDefine pluginDefine = pluginDefineResolver.resolve();

        ObjectStoreResponse objectStoreResponse = this.storeOSS(pluginDefine, pluginRequest);

        objectStoreResponse = Optional.ofNullable(objectStoreResponse).orElseGet(()->result);

        GeneratePluginDomainRequest pluginDomainRequest = GeneratePluginDomainRequest.builder()
                .pluginDefine(pluginDefine)
                .fileKey(objectStoreResponse.getFileKey())
                .jarUrl(objectStoreResponse.getResourceUrl())
                .size(pluginRequest.getSize())
                .build();

        pluginDomainService.storePlugin(pluginDomainRequest);

        deleteIfNecessary(storePath);

        return true;
    }

    @Override
    public PageBean<PluginVO> pageList(PageRequest<PluginQuery> pageRequest) {
        PluginQuery condition = pageRequest.getCondition();

        PluginCombineQuery queryCondition = PluginCombineQuery.builder()
                .maintainer(condition.getMaintainer())
                .pluginName(condition.getPluginName())
                .classDefine(condition.getClassDefine())
                .build();

        PageBean<PluginDomain> pageBean = pluginRepository.pageQuery(queryCondition, pageRequest.getPageNo(), pageRequest.getPageSize());

        return PageCommonUtils.convert(pageBean, clusterDomains -> convert(clusterDomains));
    }

    @Transactional
    @Override
    public Boolean deleted(Long id) {
        PluginCombineQuery combineQuery = PluginCombineQuery.builder()
                .id(id)
                .build();
        return pluginDomainService.delete(combineQuery);
    }

    @Override
    public Boolean actived(Long id) {
        return null;
    }

    @Override
    public Boolean disabled(Long id) {
        return null;
    }

    private ObjectStoreResponse storeOSS(PluginDefine pluginDefine, PluginRequest pluginRequest) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

        String fileKey = date + "_" + pluginDefine.getVer() + ".jar";

        ObjectStoreRequest uploadObjectRequest = new ObjectStoreRequest();

        uploadObjectRequest.setBytes(pluginRequest.getBytes());
        uploadObjectRequest.setContentType(pluginRequest.getContentType());
        uploadObjectRequest.setFileName(FileKeyBuilder.newBuilder(pluginDefine.getPluginName(),fileKey).build());
        uploadObjectRequest.setSize(pluginRequest.getSize());

        if (!ossProperties.getStoreType().equals(StoreType.LOCAL)) {
            return ossExecutor.doExecute(ossProperties.getStoreType(),service->service.store(uploadObjectRequest));
        }

        return null;

    }

    private List<PluginVO> convert(List<PluginDomain> items) {
        return Optional.ofNullable(items).orElseGet(ArrayList::new)
                .stream()
                .map(item->{
                    PluginVO vo = new PluginVO();
                    BeanUtils.copyProperties(item,vo);
                    return vo;
                }).collect(Collectors.toList());
    }

    private void deleteIfNecessary(String storePath) {
        if (!ossProperties.getStoreType().equals(StoreType.LOCAL)) {
            ossExecutor.doExecute(StoreType.LOCAL,service-> {
                service.deleteObject(storePath);
                return null;
            });
        }
    }

    private ObjectStoreResponse storeLocal(PluginRequest pluginRequest) {
        ObjectStoreRequest storeRequest = new ObjectStoreRequest();

        storeRequest.setBytes(pluginRequest.getBytes());
        storeRequest.setSize(pluginRequest.getSize());
        storeRequest.setContentType(pluginRequest.getContentType());
        storeRequest.setFileName(pluginRequest.getFileName());

        ObjectStoreResponse result = ossExecutor.doExecute(StoreType.LOCAL,service-> service.store(storeRequest));
        return result;
    }
}

