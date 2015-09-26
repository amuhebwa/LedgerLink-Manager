package org.grameenfoundation.applabs.ledgerlinkmanager.models;

/**
 * POJO for group information, phone information
 * and location information
 */
public class VslaInfo {
    private int id;
    private int vslaId;
    private String groupName;
    private String memberName;
    private String memberPost;
    private String memberPhoneNumber;
    private String groupAccountNumber;
    private String physicalAddress;
    private String regionName;
    private String locationCordinates;
    private String issuedPhoneNumber;
    private String isDataSent;
    private int uploadDataIcon;
    private String supportType;

    public VslaInfo() {
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVslaId() {
        return vslaId;
    }

    public void setVslaId(int vslaId) {
        this.vslaId = vslaId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberPost() {
        return memberPost;
    }

    public void setMemberPost(String memberPost) {
        this.memberPost = memberPost;
    }

    public String getMemberPhoneNumber() {
        return memberPhoneNumber;
    }

    public void setMemberPhoneNumber(String memberPhoneNumber) {
        this.memberPhoneNumber = memberPhoneNumber;
    }

    public String getGroupAccountNumber() {
        return groupAccountNumber;
    }

    public void setGroupAccountNumber(String groupAccountNumber) {
        this.groupAccountNumber = groupAccountNumber;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getLocationCordinates() {
        return locationCordinates;
    }

    public void setLocationCordinates(String locationCordinates) {
        this.locationCordinates = locationCordinates;
    }

    public String getIssuedPhoneNumber() {
        return issuedPhoneNumber;
    }

    public void setIssuedPhoneNumber(String issuedPhoneNumber) {
        this.issuedPhoneNumber = issuedPhoneNumber;
    }

    public String getIsDataSent() {
        return isDataSent;
    }

    public void setIsDataSent(String isDataSent) {
        this.isDataSent = isDataSent;
    }

    public int getUploadDataIcon() {
        return uploadDataIcon;
    }

    public void setUploadDataIcon(int uploadDataIcon) {
        this.uploadDataIcon = uploadDataIcon;
    }

    public String getSupportType() {
        return supportType;
    }

    public void setSupportType(String supportType) {
        this.supportType = supportType;
    }
}
