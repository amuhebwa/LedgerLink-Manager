package org.grameenfoundation.applabs.ledgerlinkmanager;

/**
 * Singleton class to handle json data searched from the web
 */
public class JsonData {
    private static JsonData mInstance = null;
    private boolean isEditing = false;
    private String VslaId = "-1";
    private String VslaJsonStringData;

    public JsonData() {
    }

    public static JsonData getInstance() {
        if (mInstance == null) {
            mInstance = new JsonData();
        }
        return mInstance;
    }


    public boolean isEditing() {
        return isEditing;
    }

    public void setIsEditing(boolean isEditing) {
        this.isEditing = isEditing;
    }

    public String getVslaId() {
        return VslaId;
    }

    public void setVslaId(String vslaId) {
        VslaId = vslaId;
    }

    public String getVslaJsonStringData() {
        return VslaJsonStringData;
    }

    public void setVslaJsonStringData(String vslaJsonStringData) {
        VslaJsonStringData = vslaJsonStringData;
    }
}
