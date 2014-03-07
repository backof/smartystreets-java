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
    return null;
  }

  @Override
  StreetAddressResponse getResponse(JSONObject responseJSONObject) {
    return null;
  }

  private static String getTrueOrFalse(boolean b) {
    return b ? "true" : " false";
  }
}
