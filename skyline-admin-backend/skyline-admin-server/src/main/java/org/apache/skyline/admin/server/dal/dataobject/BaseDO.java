package org.apache.skyline.admin.server.dal.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

/**
 * @author hejianbing
 * @version @Id: BaseDO.java, v 0.1 2022年12月23日 10:47 hejianbing Exp $
 */
@Data
public class BaseDO {

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;
}