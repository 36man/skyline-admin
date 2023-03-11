package org.apache.skyline.admin.web.controller;

import org.apache.skyline.admin.commons.model.request.GeneratePluginRequest;
import org.apache.skyline.admin.server.service.SkylinePluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author hejianbing
 * @version @Id: PluginController.java, v 0.1 2022年12月28日 21:15 hejianbing Exp $
 */
@RestController
@RequestMapping("/plugin")
public class PluginController {

    @Autowired
    private SkylinePluginService skylinePluginService;

    @PostMapping
    public boolean upload(@RequestParam("file") CommonsMultipartFile multipartFile) {
        GeneratePluginRequest pluginRequest = new GeneratePluginRequest();
        pluginRequest.setFileName(multipartFile.getOriginalFilename());
        pluginRequest.setContentType(multipartFile.getContentType());
        pluginRequest.setSize(multipartFile.getSize());
        pluginRequest.setBytes(multipartFile.getBytes());

        return skylinePluginService.uploadPlugin(pluginRequest);

    }

}