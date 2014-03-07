package com.centzy.smartystreets;

import com.google.common.collect.ImmutableMap;
import org.json.simple.JSONObject;

/**
 * @author Peter Edge (peter@locality.com).
 */
class SmartyStreetsStreetAddressApiHandler
    extends AbstractSmartyStreetsApiHandler<StreetAddressRequestHeader, StreetAddressRequestBody, StreetAddressResponse> {

  @Override
  String getApiRoute() {
    return "/street-address";
  }

  @Override
  String getAuthId(StreetAddressRequestHeader streetAddressRequestHeader) {
    return getRequiredField(
        streetAddressRequestHeader,
        String.class,
        1
    );
  }

  @Override
  String getAuthToken(StreetAddressRequestHeader streetAddressRequestHeader) {
    return getRequiredField(
        streetAddressRequestHeader,
        String.class,
        2
    );
  }

  @Override
  ImmutableMap<String, String> getExtraRequestProperties(StreetAddressRequestHeader streetAddressRequestHeader) {
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    builder.put("x-standardize-only", getTrueOrFalse(streetAddressRequestHeader.hasStandardizeOnly()
        ? streetAddressRequestHeader.getStandardizeOnly() : false));
    builder.put("x-include-invalid", getTrueOrFalse(streetAddressRequestHeader.hasIncludeInvalid()
        ? streetAddressRequestHeader.getIncludeInvalid() : false));
    builder.put("x-accept-keypair", getTrueOrFalse(streetAddressRequestHeader.hasAcceptKeypair()
        ? streetAddressRequestHeader.getAcceptKeypair() : false));
    return builder.build();
  }

  @Override
  JSONObject getRequestJSONObject(StreetAddressRequestBody streetAddressRequestBody) {
    JSONObject jsonObject = new JSONObject();
    putOptionalField(jsonObject, "input_id", streetAddressRequestBody, String.class, 1);
    putRequiredField(jsonObject, "street", streetAddressRequestBody, String.class, 2);
    putOptionalField(jsonObject, "street2", streetAddressRequestBody, String.class, 3);
    putOptionalField(jsonObject, "secondary", streetAddressRequestBody, String.class, 4);
    putOptionalField(jsonObject, "city", streetAddressRequestBody, String.class, 5);
    putOptionalField(jsonObject, "state", streetAddressRequestBody, String.class, 6);
    putOptionalField(jsonObject, "zipcode", streetAddressRequestBody, String.class, 7);
    putOptionalField(jsonObject, "lastline", streetAddressRequestBody, String.class, 8);
    putOptionalField(jsonObject, "addressee", streetAddressRequestBody, String.class, 9);
    putOptionalField(jsonObject, "urbanization", streetAddressRequestBody, String.class, 10);
    putOptionalField(jsonObject, "candidates", streetAddressRequestBody, Integer.class, 11);
    return jsonObject;
  }

  @Override
  StreetAddressResponse getResponse(JSONObject responseJSONObject) {
    return null;
  }

  private static String getTrueOrFalse(boolean b) {
    return b ? "true" : " false";
  }
}
