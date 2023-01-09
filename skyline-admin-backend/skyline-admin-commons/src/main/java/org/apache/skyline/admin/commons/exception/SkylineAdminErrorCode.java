package org.apache.skyline.admin.commons.exception;

import lombok.Getter;

import org.bravo.gaia.commons.constant.SystemConstant;
import org.bravo.gaia.commons.enums.BizDomainName;
import org.bravo.gaia.commons.enums.BizUnitName;
import org.bravo.gaia.commons.errorcode.ErrorCode;
import org.bravo.gaia.commons.errorcode.ErrorCodeType;
import org.bravo.gaia.commons.errorcode.IErrorCode;

/**
 * @author jojocodex
 * @version @Id: SkylineAdminErrorCode.java, v 0.1 2022年12月22日 09:59 jojocodex Exp $
 */

public enum SkylineAdminErrorCode implements IErrorCode {

    OSS_ERROR(BizUnitName.GENERIC.getCode(), BizDomainName.GENERIC.getCode(), SystemConstant.GENERIC_MODULE_NAME,
            "0001", ErrorCodeType.SYS_ERROR, "OSS错误"),

    OSS_OBJECT_NOT_FOUND(BizUnitName.GENERIC.getCode(), BizDomainName.GENERIC.getCode(), SystemConstant.GENERIC_MODULE_NAME,
            "0002", ErrorCodeType.SYS_ERROR, "OSS资源不存在"),

    PLUGIN_PARSE_ERROR(BizUnitName.GENERIC.getCode(), BizDomainName.GENERIC.getCode(), SystemConstant.GENERIC_MODULE_NAME,
            "0003", ErrorCodeType.SYS_ERROR, "插件解析异常"),

    PLUGIN_UPLOAD_ERROR(BizUnitName.GENERIC.getCode(), BizDomainName.GENERIC.getCode(), SystemConstant.GENERIC_MODULE_NAME,
            "0004", ErrorCodeType.SYS_ERROR, "插件上传异常"),

    NOT_FOUND_ERROR(BizUnitName.GENERIC.getCode(), BizDomainName.GENERIC.getCode(), SystemConstant.GENERIC_MODULE_NAME,
            "0005", ErrorCodeType.SYS_ERROR, "插件上传异常"),

    UNIQUE_INDEX_CONFLICT_ERROR(BizUnitName.GENERIC.getCode(), BizDomainName.GENERIC.getCode(), SystemConstant.GENERIC_MODULE_NAME,
            "0006", ErrorCodeType.SYS_ERROR, "唯一索引异常");

    private String        bizUnitCode;

    /** 错误码类型 */
    private String        bizDomainCode;

    /** 系统平台 */
    private String        bizModuleCode;

    /** 错误码编号 */
    private String        codeSequence;

    private ErrorCodeType codeType;

    private ErrorCode     errorCode;

    @Getter
    /** 错误描述 */
    private String        errorDesc;

    SkylineAdminErrorCode(String bizUnitCode, String bizDomainCode,
                        String bizModuleCode, String codeSequence,
                        ErrorCodeType codeType, String errorDesc) {
        this.bizUnitCode = bizUnitCode;
        this.bizDomainCode = bizDomainCode;
        this.bizModuleCode = bizModuleCode;
        this.codeSequence = codeSequence;
        this.codeType = codeType;
        this.errorDesc = errorDesc;
        this.errorCode = new ErrorCode(this.bizUnitCode, this.bizDomainCode,
                this.bizModuleCode, this.codeSequence, this.codeType, this.errorDesc);

    }

    @Override
    public ErrorCode getCode() {
        return errorCode;
    }
}