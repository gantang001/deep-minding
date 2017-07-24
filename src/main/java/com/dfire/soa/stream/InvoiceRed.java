package com.dfire.soa.stream;

import java.io.Serializable;

/**
 * Created by shuyu on 2017/6/15 0015.
 */
public class InvoiceRed implements Serializable {


    private static final long serialVersionUID = 5398514647610939407L;
    /**
     * 红冲原因
     */
    private String redReason;

    /**
     * 原发票id
     */
    private long invoiceId;

    /**
     * 用于扩充
     */
    private String ext;

    /**
     * 合作方id
     */
    private String partnerId;

    /**
     * 申请时间
     */
    private long applyTime;

    /**
     * 出票时间
     */
    private long outTime;

    /**
     * 下载地址
     */
    private String downloadUrl;
    /**
     * 红冲状态 1.红冲中 2.红冲成功 3.红冲失败
     */
    private int status;

    /**
     * 红冲后大账房返回的新的发票下载地址
     */
    private String originDownloadUrl;

    /**
     * 发票二维码地址
     */
    private String qrCodeUrl;

    /**
     * 开票方的店铺ID（真正持有税控盘的商家）
     */
    private String entityId;

    /**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 发票号码
     */
    private String invoiceNo;


    /**
     * 适用方的门店ID
     */
    private String applyEntityId;

    /**
     * 发票请求流水号（16位）
     */
    private String invoiceReqSerialNo;
}
