package org.grameenfoundation.applabs.ledgerlinkmanager.helpers;

public class DataHolder {
    private String vslaName;
    private String groupRepresentativeName;
    private String groupRepresentativePost;
    private String groupRepresentativePhoneNumber;
    private String groupBankAccount;
    private String physicalAddress;
    private String regionName;
    private String groupPhoneNumber;
    private String locationCoordinates;
    public String supportTrainingType;
    private static DataHolder dataObject = null;

    private DataHolder() {
    }

    public static DataHolder getInstance() {
        if (dataObject == null)
            dataObject = new DataHolder();
        return dataObject;
    }

    public String getVslaName() {
        return vslaName;
    }

    public void setVslaName(String vslaName) {
        this.vslaName = vslaName;
    }

    public String getGroupRepresentativeName() {
        return groupRepresentativeName;
    }

    public void setGroupRepresentativeName(String groupRepresentativeName) {
        this.groupRepresentativeName = groupRepresentativeName;
    }

    public String getGroupRepresentativePost() {
        return groupRepresentativePost;
    }

    public void setGroupRepresentativePost(String groupRepresentativePost) {
        this.groupRepresentativePost = groupRepresentativePost;
    }

    public String getGroupRepresentativePhoneNumber() {
        return groupRepresentativePhoneNumber;
    }

    public void setGroupRepresentativePhoneNumber(String groupRepresentativePhoneNumber) {
        this.groupRepresentativePhoneNumber = groupRepresentativePhoneNumber;
    }

    public String getGroupBankAccount() {
        return groupBankAccount;
    }

    public void setGroupBankAccount(String groupBankAccount) {
        this.groupBankAccount = groupBankAccount;
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

    public String getGroupPhoneNumber() {
        return groupPhoneNumber;
    }

    public void setGroupPhoneNumber(String groupPhoneNumber) {
        this.groupPhoneNumber = groupPhoneNumber;
    }


    public String getLocationCoordinates() {
        return locationCoordinates;
    }

    public void setLocationCoordinates(String locationCoordinates) {
        this.locationCoordinates = locationCoordinates;
    }

   /* public static DataHolder getDataObject() {
        return dataObject;
    }

    public static void setDataObject(DataHolder dataObject) {
        DataHolder.dataObject = dataObject;
    }*/

    public String getSupportTrainingType() {
        return supportTrainingType;
    }

    public void setSupportTrainingType(String supportTrainingType) {
        this.supportTrainingType = supportTrainingType;
    }
}
