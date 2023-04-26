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

package org.apache.skyline.admin.server.service;

import org.apache.skyline.admin.commons.model.query.ApiQuery;
import org.apache.skyline.admin.commons.model.request.ApiConfigPluginRequest;
import org.apache.skyline.admin.commons.model.request.ApiRequest;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.vo.ApiVO;
import org.bravo.gaia.commons.base.PageBean;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Validated
public interface ApiService {

    Long create(@Valid ApiRequest apiRequest);

    Boolean update(@Valid Long id, ApiRequest apiRequest);

    PageBean<ApiVO> pageList(PageRequest<ApiQuery> pageRequest);

    boolean deleteByIds(@NotEmpty List<Long> ids);

    boolean configPlugin(@Valid ApiConfigPluginRequest configPluginRequest);

    boolean enable(@NotEmpty List<Long> id);

    boolean disable(List<Long> ids);
}
