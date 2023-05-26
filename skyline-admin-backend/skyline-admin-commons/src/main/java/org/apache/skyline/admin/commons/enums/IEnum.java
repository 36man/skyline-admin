package org.apache.skyline.admin.commons.enums;

public interface IEnum{

    default String getDesc(){
        return "";
    }

    default Integer sn(){
        return 0;
    }

    String getCode();
}
