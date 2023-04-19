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

package org.apache.skyline.admin.commons.enums;

import lombok.Getter;

@Getter
public enum ApiStatus implements IEnum{

    ENABLE("enable", "启用"),

    DISABLE("disable", "禁用"),

    PENDING("pending", "启用中"),

    NEW("new", "新增");

    private String code;
    private String desc;

    ApiStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    public static ApiStatus getApiStatus(String code) {
        for (ApiStatus apiStatus : ApiStatus.values()) {
            if (apiStatus.getCode().equals(code)) {
                return apiStatus;
            }
        }
        return null;
    }

}
