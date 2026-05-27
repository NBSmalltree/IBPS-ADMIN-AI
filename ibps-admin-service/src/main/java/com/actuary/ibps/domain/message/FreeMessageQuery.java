package com.actuary.ibps.domain.message;

import com.actuary.ibps.domain.common.PageQuery;

import java.util.Date;

public class FreeMessageQuery extends PageQuery {

    private static final long serialVersionUID = 1L;

    private String direction;
    private Date startDate;
    private Date endDate;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
