package org.apache.skyline.admin.server.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.skyline.admin.commons.model.query.PluginQuery;
import org.apache.skyline.admin.commons.model.request.GeneratePluginRequest;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.vo.PluginVO;
import org.apache.skyline.admin.server.domain.model.PluginDomain;
import org.apache.skyline.admin.server.domain.repository.SkylinePluginRepository;
import org.apache.skyline.admin.server.domain.request.GenerateSkylinePluginDomainRequest;
import org.apache.skyline.admin.server.domain.service.SkylinePluginDomainService;
import org.apache.skyline.admin.server.oss.builder.FileKeyBuilder;
import org.apache.skyline.admin.server.oss.request.ObjectStoreRequest;
import org.apache.skyline.admin.server.oss.response.ObjectStoreResponse;
import org.apache.skyline.admin.server.oss.service.OssService;
import org.apache.skyline.admin.server.service.SkylinePluginService;
import org.apache.skyline.admin.server.support.parse.PluginDefine;
import org.apache.skyline.admin.server.support.parse.PluginDefineParser;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.commons.util.DigestUtil;
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
public class SkylinePluginServiceImpl implements SkylinePluginService {

    @Autowired
    private OssService                 ossService;

    @Autowired
    private SkylinePluginDomainService skylinePluginDomainService;

    @Autowired
    private SkylinePluginRepository skylinePluginRepository;

    @Override
    public Boolean uploadPlugin(GeneratePluginRequest pluginRequest) {
        PluginDefineParser pluginDefineParser = new PluginDefineParser(pluginRequest.getBytes());

        PluginDefine pluginDefine = pluginDefineParser.parse();

        ObjectStoreResponse uploadMultipleFileResponse = this.storeToOss(pluginDefine, pluginRequest);

        GenerateSkylinePluginDomainRequest pluginDomainRequest = GenerateSkylinePluginDomainRequest.builder()
                .fileKey(uploadMultipleFileResponse.getFileKey())
                .md5(new String(DigestUtil.md5(pluginRequest.getBytes())))
                .jarUrl(uploadMultipleFileResponse.getResourceUrl())
                .size(pluginRequest.getSize())
                .build();

        skylinePluginDomainService.createPlugin(pluginDomainRequest);

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


    private ObjectStoreResponse storeToOss(PluginDefine pluginDefine, GeneratePluginRequest pluginRequest) {
        String fileKey = genFileKey();

        ObjectStoreRequest uploadObjectRequest = new ObjectStoreRequest();

        uploadObjectRequest.setBytes(pluginRequest.getBytes());
        uploadObjectRequest.setContentType(pluginRequest.getContentType());
        uploadObjectRequest.setFileName(FileKeyBuilder.newBuilder(pluginDefine.getPluginName(),fileKey).build());
        uploadObjectRequest.setSize(pluginRequest.getSize());

        return ossService.store(uploadObjectRequest);
    }

    private static String genFileKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date()).concat("_").concat(RandomStringUtils.randomNumeric(30)).concat(".jar");
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

























