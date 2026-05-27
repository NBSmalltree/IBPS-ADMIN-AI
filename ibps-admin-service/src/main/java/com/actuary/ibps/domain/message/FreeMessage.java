package com.actuary.ibps.domain.message;

import com.actuary.ibps.domain.common.BaseEntity;

public class FreeMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String busiSerial;
    private String msgDirection;
    private String senderCode;
    private String receiverCode;
    private String msgContent;
    private Integer priority;
    private String sendStatus;
    private String msgRef;
    private String crossBorderType;
    private String crossBorderOrigSerial;

    public String getBusiSerial() {
        return busiSerial;
    }

    public void setBusiSerial(String busiSerial) {
        this.busiSerial = busiSerial;
    }

    public String getMsgDirection() {
        return msgDirection;
    }

    public void setMsgDirection(String msgDirection) {
        this.msgDirection = msgDirection;
    }

    public String getSenderCode() {
        return senderCode;
    }

    public void setSenderCode(String senderCode) {
        this.senderCode = senderCode;
    }

    public String getReceiverCode() {
        return receiverCode;
    }

    public void setReceiverCode(String receiverCode) {
        this.receiverCode = receiverCode;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getMsgRef() {
        return msgRef;
    }

    public void setMsgRef(String msgRef) {
        this.msgRef = msgRef;
    }

    public String getCrossBorderType() {
        return crossBorderType;
    }

    public void setCrossBorderType(String crossBorderType) {
        this.crossBorderType = crossBorderType;
    }

    public String getCrossBorderOrigSerial() {
        return crossBorderOrigSerial;
    }

    public void setCrossBorderOrigSerial(String crossBorderOrigSerial) {
        this.crossBorderOrigSerial = crossBorderOrigSerial;
    }
}
