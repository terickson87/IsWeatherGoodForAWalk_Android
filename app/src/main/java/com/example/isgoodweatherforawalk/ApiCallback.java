package com.example.isgoodweatherforawalk;

import org.json.JSONObject;

public interface ApiCallback {
    void onSuccess(JSONObject response);
}
