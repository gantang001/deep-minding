package com.dfire.soa.stream;

import java.io.Serializable;

/**
 * Created by shuyu on 2017/6/14 0014.
 */

public class InvoiceInstance implements Serializable {

    private static final long serialVersionUID = -4079797681097958565L;

    /**
     * 价税合计（单位：分）
     */
    private long totalAmount;

    /**
     * 类型1.纸质发票2.电子发票
     */
    private int type;

    /**
     * 开票状态
     * <p>
     * 0.刚创建 1.申请开票 2.开票中 3.开票成功 4.拒绝开票申请 5.开票失败
     */
    private int status;

    /**
     * 红冲状态(0未红冲 1红冲中 2红冲成功)
     */
    private int isRed;

    /**
     * 开票方的店铺ID（真正持有税控盘的商家）
     */
    private String entityId;

    /**
     * 适用方的门店ID
     */
    private String applyEntityId;

    /**
     * 出票时间
     */
    private long outTime;

    public long getOutTime() {
        return outTime;
    }

    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsRed() {
        return isRed;
    }

    public void setIsRed(int isRed) {
        this.isRed = isRed;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getApplyEntityId() {
        return applyEntityId;
    }

    public void setApplyEntityId(String applyEntityId) {
        this.applyEntityId = applyEntityId;
    }

}
