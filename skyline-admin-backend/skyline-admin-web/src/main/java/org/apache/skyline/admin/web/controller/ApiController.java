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

import org.apache.skyline.admin.commons.model.query.ApiQuery;
import org.apache.skyline.admin.commons.model.request.ApiRequest;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.vo.ApiVO;
import org.apache.skyline.admin.server.service.ApiService;
import org.bravo.gaia.commons.base.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/pageList")
    public PageBean<ApiVO> pageList(@RequestParam("pageNo") Integer pageNo,
                                    @RequestParam("pageSize") Integer pageSize,
                                    @RequestParam(value = "clusterId") Long clusterId,
                                    @RequestParam(value = "matchCondition") String matchCondition) {

        PageRequest<ApiQuery> pageRequest = new PageRequest<>();
        pageRequest.setPageNo(pageNo);
        pageRequest.setPageSize(pageSize);

        ApiQuery condition = new ApiQuery();
        condition.setClusterId(clusterId);
        condition.setMatchCondition(matchCondition);
        pageRequest.setCondition(condition);

        return apiService.pageList(pageRequest);
    }

    @PostMapping
    public Long create(@RequestBody ApiRequest apiRequest) {
        return apiService.create(apiRequest);
    }

    @PutMapping("/{id}")
    public Boolean update(@PathVariable("id") Long id, @RequestBody ApiRequest apiRequest) {
        return apiService.update(id,apiRequest);
    }

    @DeleteMapping("/{ids}")
    public Boolean delete(@PathVariable("[]ids") List<Long> ids) {
        return apiService.deleteByIds(ids);
    }

    @PutMapping("/publish/{ids}")
    public Boolean apply(@PathVariable("[]ids") List<Long> ids) {
        return apiService.publish(ids);
    }

}
