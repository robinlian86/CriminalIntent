package com.ambitious.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lianhuibin on 2016/9/5.
 */
public class Photo {
    private static final String JSON_FILENAME = "filename";
    private static final String JSON_ORIENTATION = "orientation";

    private String mFilename;
    private int mOrientation;

    public Photo(String filename, int orientation) {
        mFilename = filename;
        mOrientation = orientation;
    }

    public Photo(JSONObject json) throws JSONException {
        mFilename = json.getString(JSON_FILENAME);
        mOrientation = json.getInt(JSON_ORIENTATION);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, mFilename);
        json.put(JSON_ORIENTATION, mOrientation);
        return json;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public String getFilename() {
        return mFilename;
    }

}
