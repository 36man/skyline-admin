package org.apache.skyline.admin.web.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skyline.admin.commons.exception.SkylineAdminErrorCode;
import org.apache.skyline.admin.server.model.request.GenerateSkylinePluginRequest;
import org.apache.skyline.admin.server.service.SkylinePluginService;
import org.bravo.gaia.commons.exception.PlatformException;
import org.bravo.gaia.log.GaiaLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginController.java, v 0.1 2022年12月28日 21:15 hejianbing Exp $
 */
@RestController
@RequestMapping("/skyline/plugin")
public class SkylinePluginController {

    @Autowired
    private SkylinePluginService skylinePluginService;

    @PostMapping
    public boolean upload(@RequestParam("file") CommonsMultipartFile multipartFile){
        try{
            GenerateSkylinePluginRequest pluginRequest = new GenerateSkylinePluginRequest();
            pluginRequest.setFileName(multipartFile.getOriginalFilename());
            pluginRequest.setContentType(multipartFile.getContentType());
            pluginRequest.setSize(multipartFile.getSize());
            pluginRequest.setBytes(multipartFile.getBytes());

            return skylinePluginService.uploadPlugin(pluginRequest);
        }catch(Exception ex){
            GaiaLogger.getGlobalErrorLogger().error("skyline plugin upload error: {}", ExceptionUtils.getRootCauseMessage(ex));

            if (ex instanceof PlatformException) {
                throw (PlatformException) ex;
            }

            throw new PlatformException(SkylineAdminErrorCode.PLUGIN_UPLOAD_ERROR.getCode());
        }
    }



}