/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.skyline.admin.web.controller;

import org.apache.skyline.admin.commons.model.query.PluginVersionQuery;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.vo.PluginVersionVO;
import org.apache.skyline.admin.server.service.PluginVersionService;
import org.bravo.gaia.commons.base.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plugin/ver")
public class PluginVersionController {

    @Autowired
    private PluginVersionService pluginVersionService;

    @GetMapping("/pageList")
    public PageBean<PluginVersionVO> pageList(@RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                                                    @RequestParam(name = "pluginId") Long pluginId,
                                                    @RequestParam(name = "ver",required = false) String ver){
        PageRequest<PluginVersionQuery> pageRequest = new PageRequest<>();
        pageRequest.setPageNo(pageNo);
        pageRequest.setPageSize(pageSize);

        PluginVersionQuery condition = new PluginVersionQuery();
        condition.setPluginId(pluginId);
        condition.setVer(ver);

        pageRequest.setCondition(condition);

        return pluginVersionService.pageList(pageRequest);
    }

    @GetMapping("/search")
    public PageBean<PluginVersionVO> search(@RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                                            @RequestParam(name = "keyword",required = false) String keyword){
        PageRequest<String> pageRequest = new PageRequest<>();
        pageRequest.setPageNo(pageNo);
        pageRequest.setPageSize(pageSize);

        pageRequest.setCondition(keyword);

        return pluginVersionService.search(pageRequest);
    }

    @PostMapping("active")
    public Boolean active(@RequestParam(name="id") Long id,
                          @RequestParam(name="active") boolean active){
        return pluginVersionService.active(id, active);
    }
}
