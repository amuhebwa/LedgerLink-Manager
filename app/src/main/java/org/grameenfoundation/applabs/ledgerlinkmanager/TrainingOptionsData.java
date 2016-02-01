package org.grameenfoundation.applabs.ledgerlinkmanager;

/**
 * Load the training options data.This is because they are loaded
 * as activities by launching them from the vsla training fragment
 */
public class TrainingOptionsData {
    public static TrainingOptionsData mInstance = null;
    private String starredFragment = null;
    private String trainingModules;
    public TrainingOptionsData(){

    }
    public static TrainingOptionsData getInstance(){
        if (mInstance == null){
            mInstance = new TrainingOptionsData();
        }
        return mInstance;
    }

    public String getStarredFragment() {
        return starredFragment;
    }

    public void setStarredFragment(String starredFragment) {
        this.starredFragment = starredFragment;
    }

    public String getTrainingModules() {
        return trainingModules;
    }

    public void setTrainingModules(String trainingModules) {
        this.trainingModules = trainingModules;
    }
    public void clearFields(){
        if (mInstance != null){
            mInstance = null;
            starredFragment = null;
        }
    }
}
