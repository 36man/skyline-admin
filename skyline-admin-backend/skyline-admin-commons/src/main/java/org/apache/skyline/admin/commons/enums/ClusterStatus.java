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

public enum ClusterStatus implements IEnum{

    PENDING("PENDING","创建成功,未开始调度"),
    RUNNING("RUNNING","已经被调度至节点,至少有一个启动成功"),
    SUCCEEDED("SUCCEEDED","启动成功"),

    FAILED("FAILED","容器终止"),
    UNKNOWN("UNKNOWN","API Server无法正常获取到Pod对象的状态信息");

    private String desc;
    private String code;

    ClusterStatus(String code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public String getCode() {
        return code;
    }


}
