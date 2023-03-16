package org.apache.skyline.admin.server.service.impl;

import org.apache.skyline.admin.commons.model.query.PluginQuery;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.request.PluginRequest;
import org.apache.skyline.admin.commons.model.vo.PluginVO;
import org.apache.skyline.admin.server.domain.model.PluginDomain;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

    public Boolean generate(PluginRequest pluginRequest) {
        ObjectStoreRequest storeRequest = new ObjectStoreRequest();

        storeRequest.setBytes(pluginRequest.getBytes());
        storeRequest.setSize(pluginRequest.getSize());
        storeRequest.setContentType(pluginRequest.getContentType());
        storeRequest.setFileName(pluginRequest.getFileName());

        ObjectStoreResponse result = ossExecutor.doExecute(StoreType.LOCAL,service-> service.store(storeRequest));

        String storePath = result.getStorePath();

        PluginDefineResolver pluginDefineResolver = new PluginDefineResolver(objectMapperCodec,storePath);

        PluginDefine pluginDefine = pluginDefineResolver.resolve();

        ObjectStoreResponse objectStoreResponse = this.storeOSS(pluginDefine, pluginRequest);

        if (objectStoreResponse == null) {
            objectStoreResponse = result;
        }

        GeneratePluginDomainRequest pluginDomainRequest = GeneratePluginDomainRequest.builder()
                .fileKey(objectStoreResponse.getFileKey())
                .jarUrl(objectStoreResponse.getResourceUrl())
                .size(pluginRequest.getSize())
                .build();

        pluginDomainService.storePlugin(pluginDomainRequest);

        if (!ossProperties.getStoreType().equals(StoreType.LOCAL)) {
            ossExecutor.doExecute(StoreType.LOCAL,service-> {
                service.deleteObject(storePath);
                return null;
            });
        }
        return true;
    }

    @Override
    public PageBean<PluginVO> pageList(PageRequest<PluginQuery> condition) {
        //PageBean<SkylinePluginDomain> pageBean = skylinePluginRepository.pageList(condition);

        //return PageCommonUtils.convert(pageBean,this::convertList);

        return null;
    }

    @Override
    public Boolean deleted(Long id) {
        return null;
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

    private List<PluginVO> convertList(List<PluginDomain> items) {
        return items.stream()
                .map(item->{
                    PluginVO vo = new PluginVO();
                    BeanUtils.copyProperties(item,vo);
                    return vo;
                }).collect(Collectors.toList());

    }
}

























