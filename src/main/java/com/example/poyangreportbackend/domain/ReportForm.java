package com.example.poyangreportbackend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName report_form
 */
@TableName(value ="report_form")
@Data
public class ReportForm implements Serializable {
    /**
     * 
     */
    @TableId
    private Long id;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String detail;

    /**
     * 
     */
    private String lng;

    /**
     * 
     */
    private String lat;

    /**
     * 
     */
    private String address;

    /**
     * 
     */
    private String city;

    /**
     * 
     */
    private String district;

    /**
     * 
     */
    private String township;

    /**
     * 
     */
    private String poi;

    /**
     * 
     */
    private Integer isForbid;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private String type;

    /**
     * 
     */
    private Integer category;

    /**
     * 
     */
    private String status;

    /**
     * 
     */
    private Long operatorId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ReportForm other = (ReportForm) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getDetail() == null ? other.getDetail() == null : this.getDetail().equals(other.getDetail()))
            && (this.getLng() == null ? other.getLng() == null : this.getLng().equals(other.getLng()))
            && (this.getLat() == null ? other.getLat() == null : this.getLat().equals(other.getLat()))
            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
            && (this.getCity() == null ? other.getCity() == null : this.getCity().equals(other.getCity()))
            && (this.getDistrict() == null ? other.getDistrict() == null : this.getDistrict().equals(other.getDistrict()))
            && (this.getTownship() == null ? other.getTownship() == null : this.getTownship().equals(other.getTownship()))
            && (this.getPoi() == null ? other.getPoi() == null : this.getPoi().equals(other.getPoi()))
            && (this.getIsForbid() == null ? other.getIsForbid() == null : this.getIsForbid().equals(other.getIsForbid()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getCategory() == null ? other.getCategory() == null : this.getCategory().equals(other.getCategory()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getOperatorId() == null ? other.getOperatorId() == null : this.getOperatorId().equals(other.getOperatorId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getDetail() == null) ? 0 : getDetail().hashCode());
        result = prime * result + ((getLng() == null) ? 0 : getLng().hashCode());
        result = prime * result + ((getLat() == null) ? 0 : getLat().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getCity() == null) ? 0 : getCity().hashCode());
        result = prime * result + ((getDistrict() == null) ? 0 : getDistrict().hashCode());
        result = prime * result + ((getTownship() == null) ? 0 : getTownship().hashCode());
        result = prime * result + ((getPoi() == null) ? 0 : getPoi().hashCode());
        result = prime * result + ((getIsForbid() == null) ? 0 : getIsForbid().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getCategory() == null) ? 0 : getCategory().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getOperatorId() == null) ? 0 : getOperatorId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", title=").append(title);
        sb.append(", detail=").append(detail);
        sb.append(", lng=").append(lng);
        sb.append(", lat=").append(lat);
        sb.append(", address=").append(address);
        sb.append(", city=").append(city);
        sb.append(", district=").append(district);
        sb.append(", township=").append(township);
        sb.append(", poi=").append(poi);
        sb.append(", isForbid=").append(isForbid);
        sb.append(", createTime=").append(createTime);
        sb.append(", type=").append(type);
        sb.append(", category=").append(category);
        sb.append(", status=").append(status);
        sb.append(", operatorId=").append(operatorId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}