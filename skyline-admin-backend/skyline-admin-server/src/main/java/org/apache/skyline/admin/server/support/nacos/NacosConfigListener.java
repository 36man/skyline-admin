package org.apache.skyline.admin.server.support.nacos;

import com.alibaba.nacos.api.config.listener.AbstractListener;

public abstract class NacosConfigListener<T> extends AbstractListener {

        public abstract String getDataId();

        public  T getContent(){
            return null;
        }
    }