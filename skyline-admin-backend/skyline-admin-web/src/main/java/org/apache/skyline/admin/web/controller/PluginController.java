package org.apache.skyline.admin.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hejianbing
 * @version @Id: PluginController.java, v 0.1 2022年12月28日 21:15 hejianbing Exp $
 */
@RestController
@RequestMapping("/plugin")
public class PluginController {

//    @Autowired
//    private SkylinePluginService skylinePluginService;
//
//    @PostMapping
//    public boolean upload(@RequestParam("file") CommonsMultipartFile multipartFile) {
//        GeneratePluginRequest pluginRequest = new GeneratePluginRequest();
//        pluginRequest.setFileName(multipartFile.getOriginalFilename());
//        pluginRequest.setContentType(multipartFile.getContentType());
//        pluginRequest.setSize(multipartFile.getSize());
//        pluginRequest.setBytes(multipartFile.getBytes());
//
//        return skylinePluginService.uploadPlugin(pluginRequest);
//
//    }

}