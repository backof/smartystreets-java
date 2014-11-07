package com.centzy.smartystreets;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Peter Edge (peter.edge@gmail.com).
 */
class SmartyStreetsZipcodeApiHandler extends AbstractSmartyStreetsApiHandler<ZipcodeRequestHeader, ZipcodeRequestBody, ZipcodeResponse> {

  private static final ImmutableBiMap<String, ZipcodeType> ZIPCODE_TYPE_BI_MAP = new ImmutableBiMap.Builder<String, ZipcodeType>()
      .put("U", ZipcodeType.ZIPCODE_TYPE_UNIQUE)
      .put("M", ZipcodeType.ZIPCODE_TYPE_MILITARY)
      .put("P", ZipcodeType.ZIPCODE_TYPE_PO_BOX)
      .put("S", ZipcodeType.ZIPCODE_TYPE_STANDARD)
      .build();

  private static final ImmutableBiMap<String, ZipcodeResponse.Error.Status> ERROR_STATUS_BI_MAP
      = new ImmutableBiMap.Builder<String, ZipcodeResponse.Error.Status>()
      .put("blank", ZipcodeResponse.Error.Status.BLANK)
      .put("invalid_state", ZipcodeResponse.Error.Status.INVALID_STATE)
      .put("invalid_city", ZipcodeResponse.Error.Status.INVALID_CITY)
      .put("invalid_zipcode", ZipcodeResponse.Error.Status.INVALID_ZIPCODE)
      .put("conflict", ZipcodeResponse.Error.Status.CONFLICT)
      .build();

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
    JSONObject jsonObject = new JSONObject();
    putOptionalField(jsonObject, "city", zipcodeRequestBody, String.class, 1);
    putOptionalField(jsonObject, "state", zipcodeRequestBody, String.class, 2);
    putOptionalField(jsonObject, "zipcode", zipcodeRequestBody, String.class, 3);
    return jsonObject;
  }

  @Override
  ZipcodeResponse getResponse(JSONObject responseJSONObject) {
    ZipcodeResponse.Builder builder = ZipcodeResponse.newBuilder();
    if (responseJSONObject.containsKey("status")) {
      ZipcodeResponse.Error.Builder errorBuilder = ZipcodeResponse.Error.newBuilder();
      putOptionalField(errorBuilder, ZipcodeResponse.Error.Status.class, 1, responseJSONObject, "status", ERROR_STATUS_BI_MAP);
      putOptionalField(errorBuilder, String.class, 2, responseJSONObject, "reason");
      builder.setError(errorBuilder.build());
      return builder.build();
    }

    if (responseJSONObject.containsKey("city_states")) {
      JSONArray cityStatesJSONArray = (JSONArray) responseJSONObject.get("city_states");
      for (int i = 0; i < cityStatesJSONArray.size(); i++) {
        JSONObject cityStateJSONObject = (JSONObject) cityStatesJSONArray.get(i);
        ZipcodeResponse.CityState.Builder cityStateBuilder = ZipcodeResponse.CityState.newBuilder();
        putOptionalField(cityStateBuilder, String.class, 1, cityStateJSONObject, "city");
        putOptionalField(cityStateBuilder, State.class, 2, cityStateJSONObject, "state_abbreviation", STATE_BI_MAP);
        builder.addCityState(cityStateBuilder.build());
      }
    }
    if (responseJSONObject.containsKey("zipcodes")) {
      JSONArray zipcodesJSONArray = (JSONArray) responseJSONObject.get("zipcodes");
      for (int i = 0; i < zipcodesJSONArray.size(); i++) {
        JSONObject zipcodeJSONObject = (JSONObject) zipcodesJSONArray.get(i);
        ZipcodeResponse.Zipcode.Builder zipcodeBuilder = ZipcodeResponse.Zipcode.newBuilder();
        putOptionalField(zipcodeBuilder, String.class, 1, zipcodeJSONObject, "zipcode");
        putOptionalField(zipcodeBuilder, ZipcodeType.class, 2, zipcodeJSONObject, "zipcode_type", ZIPCODE_TYPE_BI_MAP);
        putOptionalField(zipcodeBuilder, String.class, 3, zipcodeJSONObject, "county_fips");
        putOptionalField(zipcodeBuilder, String.class, 4, zipcodeJSONObject, "county_name");
        Coordinate coordinate = getOptionalCoordinate(zipcodeJSONObject);
        if (coordinate != null) {
          zipcodeBuilder.setCoordinate(coordinate);
        }
        builder.addZipcode(zipcodeBuilder.build());
      }
    }
    return builder.build();
  }
}
