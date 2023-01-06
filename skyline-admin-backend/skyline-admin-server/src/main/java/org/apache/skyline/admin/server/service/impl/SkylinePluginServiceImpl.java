package org.apache.skyline.admin.server.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.skyline.admin.server.domain.request.GenerateSkylinePluginDomainRequest;
import org.apache.skyline.admin.server.domain.service.SkylinePluginDomainService;
import org.apache.skyline.admin.server.model.query.SkylinePluginQuery;
import org.apache.skyline.admin.server.model.request.GenerateSkylinePluginRequest;
import org.apache.skyline.admin.server.model.vo.SkylinePluginVO;
import org.apache.skyline.admin.server.oss.builder.FileKeyBuilder;
import org.apache.skyline.admin.server.oss.request.UploadMultipleFileRequest;
import org.apache.skyline.admin.server.oss.response.UploadMultipleFileResponse;
import org.apache.skyline.admin.server.oss.service.OssService;
import org.apache.skyline.admin.server.service.SkylinePluginService;
import org.apache.skyline.admin.server.support.parse.PluginDefine;
import org.apache.skyline.admin.server.support.parse.PluginDefineParser;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.commons.base.PageCondition;
import org.bravo.gaia.commons.util.DigestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Boolean uploadPlugin(GenerateSkylinePluginRequest pluginRequest) {
        PluginDefineParser pluginDefineParser = new PluginDefineParser(pluginRequest.getBytes());

        PluginDefine pluginDefine = pluginDefineParser.parse();

        UploadMultipleFileResponse uploadMultipleFileResponse = this.storeToOss(pluginDefine, pluginRequest);

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
    public PageBean<SkylinePluginVO> pageList(PageCondition<SkylinePluginQuery> condition) {
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


    private UploadMultipleFileResponse storeToOss(PluginDefine pluginDefine, GenerateSkylinePluginRequest pluginRequest) {
        String fileKey = genFileKey();

        UploadMultipleFileRequest uploadObjectRequest = new UploadMultipleFileRequest();

        uploadObjectRequest.setBytes(pluginRequest.getBytes());
        uploadObjectRequest.setContentType(pluginRequest.getContentType());
        uploadObjectRequest.setFileName(FileKeyBuilder.newBuilder(pluginDefine.getPluginName(),fileKey).build());
        uploadObjectRequest.setSize(pluginRequest.getSize());

        return ossService.upload(uploadObjectRequest);
    }

    private static String genFileKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date()).concat("_").concat(RandomStringUtils.randomNumeric(30)).concat(".jar");
    }
}

























