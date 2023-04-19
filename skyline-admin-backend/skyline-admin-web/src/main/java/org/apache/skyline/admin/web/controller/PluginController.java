package org.apache.skyline.admin.web.controller;

import org.apache.skyline.admin.commons.model.query.PluginQuery;
import org.apache.skyline.admin.commons.model.request.GeneratePluginRequest;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.request.UpdatePluginRequest;
import org.apache.skyline.admin.commons.model.vo.PluginVO;
import org.apache.skyline.admin.server.service.PluginService;
import org.bravo.gaia.commons.base.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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


    @PostMapping("/upload")
    public boolean upload(@RequestParam("file") CommonsMultipartFile file) {
        GeneratePluginRequest pluginRequest = new GeneratePluginRequest();
        pluginRequest.setFileName(file.getOriginalFilename());
        pluginRequest.setContentType(file.getContentType());
        pluginRequest.setSize(file.getSize());
        pluginRequest.setBytes(file.getBytes());

        return pluginService.generate(pluginRequest);
    }

    @GetMapping("pageList")
    public PageBean<PluginVO> pageList(@RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
                                @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                                @RequestParam(name = "pluginName",required = false) String pluginName,
                                @RequestParam(name = "classDefine",required = false) String classDefine,
                                @RequestParam(name = "maintainer",required = false) String maintainer){
        PageRequest<PluginQuery> pageRequest = new PageRequest<>();
        pageRequest.setPageNo(pageNo);
        pageRequest.setPageSize(pageSize);

        PluginQuery pluginQuery = new PluginQuery();
        pluginQuery.setPluginName(pluginName);
        pluginQuery.setClassDefine(classDefine);
        pluginQuery.setMaintainer(maintainer);

        pageRequest.setCondition(pluginQuery);

        return pluginService.pageList(pageRequest);
    }

    @DeleteMapping("/{id}")
    public Boolean deleted(@PathVariable(name="id") Long id){
        return pluginService.deleted(id);
    }

    @PostMapping("/enable/{id}")
    public Boolean enable(@PathVariable(name="id") Long id){
        return pluginService.active(id, true);
    }

    @PostMapping("/disable/{id}")
    public Boolean disable(@PathVariable(name="id") Long id){
        return pluginService.active(id, false);
    }

    @PostMapping
    public Boolean update(@RequestBody UpdatePluginRequest pluginUpdateRequest) {
        return pluginService.update(pluginUpdateRequest);
    }


}