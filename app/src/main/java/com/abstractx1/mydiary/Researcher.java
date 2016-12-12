package com.abstractx1.mydiary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfisher on 07/12/2016.
 */
public class Researcher {
    private static Researcher ourInstance = new Researcher();

    public static Researcher getInstance() {
        return ourInstance;
    }

    private Researcher() {
        this.dataCollections = new ArrayList<>();
    }

    private List<DataCollection> dataCollections;

    public List<DataCollection> getDataCollections() {
        return dataCollections;
    }

    public DataCollection getDataCollection(int questionNumber) {
        for (DataCollection dataCollection : dataCollections) {
            if (dataCollection.getQuestionNumber() == questionNumber) {
                return dataCollection;
            }
        }

        return null;
    }

    public boolean hasData() {
        return dataCollections.size() > 0;
    }
}
