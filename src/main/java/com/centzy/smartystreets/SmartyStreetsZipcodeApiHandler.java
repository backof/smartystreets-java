package com.centzy.smartystreets;

import com.google.common.collect.ImmutableMap;
import org.json.simple.JSONObject;

/**
 * @author Peter Edge (peter@locality.com).
 */
class SmartyStreetsZipcodeApiHandler extends AbstractSmartyStreetsApiHandler<ZipcodeRequestHeader, ZipcodeRequestBody, ZipcodeResponse> {

  @Override
  String getApiRoute() {
    return "/zipcode";
  }

  @Override
  String getAuthId(ZipcodeRequestHeader zipcodeRequestHeader) {
    return getRequiredField(
        zipcodeRequestHeader,
        String.class,
        1
    );
  }

  @Override
  String getAuthToken(ZipcodeRequestHeader zipcodeRequestHeader) {
    return getRequiredField(
        zipcodeRequestHeader,
        String.class,
        2
    );
  }

  @Override
  ImmutableMap<String, String> getExtraRequestProperties(ZipcodeRequestHeader zipcodeRequestHeader) {
    return ImmutableMap.of();
  }

  @Override
  JSONObject getRequestJSONObject(ZipcodeRequestBody zipcodeRequestBody) {
    return null;
  }

  @Override
  ZipcodeResponse getResponse(JSONObject responseJSONObject) {
    return null;
  }
}
