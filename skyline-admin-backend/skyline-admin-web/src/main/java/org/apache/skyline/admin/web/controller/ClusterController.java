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

import org.apache.skyline.admin.commons.model.query.ClusterQuery;
import org.apache.skyline.admin.commons.model.request.ClusterRequest;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.vo.ClusterVO;
import org.apache.skyline.admin.server.service.ClusterService;
import org.bravo.gaia.commons.base.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cluster")
public class ClusterController {

    public ClusterController(){
        System.out.println("");
    }


    @Autowired
    private ClusterService clusterService;

    @GetMapping("/pageList")
    public PageBean<ClusterVO> pageList(@RequestParam(value = "domain",required = false) String domain,
                                        @RequestParam(value = "businessName",required = false) String businessName,
                                        @RequestParam(value = "clusterName" ,required = false) String clusterName,
                                        @RequestParam("pageNo") Integer pageNo,
                                        @RequestParam("pageSize") Integer pageSize) {

        PageRequest<ClusterQuery> pageRequest = new PageRequest<>();
        pageRequest.setPageNo(pageNo);
        pageRequest.setPageSize(pageSize);

        ClusterQuery clusterQuery = new ClusterQuery();
        clusterQuery.setClusterName(clusterName);
        clusterQuery.setDomain(domain);
        clusterQuery.setBizKey(businessName);

        return clusterService.pageList(pageRequest);
    }

    @GetMapping("/{id}")
    public ClusterVO findById(@PathVariable("id") Long id) {
        ClusterQuery clusterQuery = new ClusterQuery();
        clusterQuery.setId(id);

        return clusterService.queryForOne(clusterQuery);
    }

    @PostMapping
    public Boolean create(@RequestBody ClusterRequest clusterRequest) {
        return clusterService.create(clusterRequest);
    }

    @PutMapping("/{id}")
    public Boolean update(@PathVariable("id") Long id, @RequestBody ClusterRequest clusterRequest) {
        return clusterService.update(id,clusterRequest);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable("id") Long id) {
        return clusterService.delete(id);
    }

    @PutMapping("/applyCluster/{id}")
    public Boolean applyCluster(@PathVariable(value = "id") Long id) {
        return clusterService.applyCluster(id);
    }
}
