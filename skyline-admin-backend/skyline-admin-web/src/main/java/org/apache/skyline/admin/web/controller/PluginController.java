package org.apache.skyline.admin.web.controller;

import org.apache.skyline.admin.commons.model.request.PluginRequest;
import org.apache.skyline.admin.server.service.PluginService;
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
    private PluginService pluginService;

    @PostMapping
    public boolean upload(@RequestParam("file") CommonsMultipartFile file) {
        PluginRequest pluginRequest = new PluginRequest();
        pluginRequest.setFileName(file.getOriginalFilename());
        pluginRequest.setContentType(file.getContentType());
        pluginRequest.setSize(file.getSize());
        pluginRequest.setBytes(file.getBytes());

        return pluginService.generate(pluginRequest);

    }

}