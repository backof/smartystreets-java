package com.centzy.smartystreets;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import org.json.simple.JSONObject;

/**
 * @author Peter Edge (peter@locality.com).
 */
class SmartyStreetsStreetAddressApiHandler
    extends AbstractSmartyStreetsApiHandler<StreetAddressRequestHeader, StreetAddressRequestBody, StreetAddressResponse> {

  private static final ImmutableBiMap<String, StreetAddressResponse.Components.StreetDirection> STREET_DIRECTION_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Components.StreetDirection>()
      .put("N", StreetAddressResponse.Components.StreetDirection.STREET_DIRECTION_N)
      .put("S", StreetAddressResponse.Components.StreetDirection.STREET_DIRECTION_S)
      .put("E", StreetAddressResponse.Components.StreetDirection.STREET_DIRECTION_E)
      .put("W", StreetAddressResponse.Components.StreetDirection.STREET_DIRECTION_W)
      .put("NE", StreetAddressResponse.Components.StreetDirection.STREET_DIRECTION_NE)
      .put("SE", StreetAddressResponse.Components.StreetDirection.STREET_DIRECTION_SE)
      .put("NW", StreetAddressResponse.Components.StreetDirection.STREET_DIRECTION_NW)
      .put("SW", StreetAddressResponse.Components.StreetDirection.STREET_DIRECTION_SW)
      .build();

  private static final ImmutableBiMap<String, StreetAddressResponse.Metadata.RecordType> RECORD_TYPE_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Metadata.RecordType>()
      .put("F", StreetAddressResponse.Metadata.RecordType.RECORD_TYPE_F)
      .put("G", StreetAddressResponse.Metadata.RecordType.RECORD_TYPE_G)
      .put("H", StreetAddressResponse.Metadata.RecordType.RECORD_TYPE_H)
      .put("P", StreetAddressResponse.Metadata.RecordType.RECORD_TYPE_P)
      .put("R", StreetAddressResponse.Metadata.RecordType.RECORD_TYPE_R)
      .put("S", StreetAddressResponse.Metadata.RecordType.RECORD_TYPE_S)
      .build();

  private static final ImmutableBiMap<String, StreetAddressResponse.Metadata.ResidentialDeliveryIndicator>
      RESIDENTIAL_DELIVERY_INDICATOR_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Metadata.ResidentialDeliveryIndicator>()
      .put("residential", StreetAddressResponse.Metadata.ResidentialDeliveryIndicator.RESIDENTIAL_DELIVERY_INDICATOR_RESIDENTIAL)
      .put("commercial", StreetAddressResponse.Metadata.ResidentialDeliveryIndicator.RESIDENTIAL_DELIVERY_INDICATOR_COMMERCIAL)
      .put("unknown", StreetAddressResponse.Metadata.ResidentialDeliveryIndicator.RESIDENTIAL_DELIVERY_INDICATOR_UNKNOWN)
      .build();

  private static final ImmutableBiMap<String, StreetAddressResponse.Metadata.ElotSort> ELOT_SORT_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Metadata.ElotSort>()
      .put("A", StreetAddressResponse.Metadata.ElotSort.ELOT_SORT_A)
      .put("D", StreetAddressResponse.Metadata.ElotSort.ELOT_SORT_D)
      .build();

  private static final ImmutableBiMap<String, StreetAddressResponse.Metadata.Precision> PRECISION_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Metadata.Precision>().build();

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
    StreetAddressResponse.Builder builder = StreetAddressResponse.newBuilder();
    putOptionalField(builder, String.class, 1, responseJSONObject, "input_id");
    putOptionalField(builder, Integer.class, 2, responseJSONObject, "input_index");
    putOptionalField(builder, Integer.class, 3, responseJSONObject, "candidate_index");
    putOptionalField(builder, String.class, 4, responseJSONObject, "addressee");
    putOptionalField(builder, String.class, 5, responseJSONObject, "delivery_line_1");
    putOptionalField(builder, String.class, 6, responseJSONObject, "delivery_line_2");
    putOptionalField(builder, String.class, 7, responseJSONObject, "last_line");
    putOptionalField(builder, String.class, 8, responseJSONObject, "delivery_line_barcode");
    if (responseJSONObject.containsKey("components")) {
      StreetAddressResponse.Components.Builder componentsBuilder = StreetAddressResponse.Components.newBuilder();
      putOptionalField(componentsBuilder, String.class, 1, responseJSONObject, "urbanization");
      putOptionalField(componentsBuilder, String.class, 2, responseJSONObject, "primary_number");
      putOptionalField(componentsBuilder, String.class, 3, responseJSONObject, "street_name");

      putOptionalField(componentsBuilder, String.class, 6, responseJSONObject, "street_suffix");
      putOptionalField(componentsBuilder, String.class, 7, responseJSONObject, "secondary_number");
      putOptionalField(componentsBuilder, String.class, 8, responseJSONObject, "secondary_designator");
      putOptionalField(componentsBuilder, String.class, 9, responseJSONObject, "extra_secondary_number");
      putOptionalField(componentsBuilder, String.class, 10, responseJSONObject, "extra_secondary_designator");
      putOptionalField(componentsBuilder, String.class, 11, responseJSONObject, "pmb_designator");
      putOptionalField(componentsBuilder, String.class, 12, responseJSONObject, "pmb_number");
      putOptionalField(componentsBuilder, String.class, 13, responseJSONObject, "city_name");
      putOptionalField(componentsBuilder, String.class, 14, responseJSONObject, "default_city_name");


      putOptionalField(componentsBuilder, String.class, 16, responseJSONObject, "zipcode");
      putOptionalField(componentsBuilder, String.class, 17, responseJSONObject, "plus4_code");
      putOptionalField(componentsBuilder, String.class, 18, responseJSONObject, "delivery_point");
      putOptionalField(componentsBuilder, String.class, 19, responseJSONObject, "delivery_point_check_digit");
      builder.setComponents(componentsBuilder.build());
    }
    if (responseJSONObject.containsKey("metadata")) {
      StreetAddressResponse.Metadata.Builder metadataBuilder = StreetAddressResponse.Metadata.newBuilder();

      putOptionalField(metadataBuilder, String.class, 3, responseJSONObject, "county_fips");
      putOptionalField(metadataBuilder, String.class, 4, responseJSONObject, "county_name");
      putOptionalField(metadataBuilder, String.class, 5, responseJSONObject, "carrier_route");
      putOptionalField(metadataBuilder, String.class, 6, responseJSONObject, "congressional_district");
      putOptionalField(metadataBuilder, String.class, 7, responseJSONObject, "building_default_indicator");

      putOptionalField(metadataBuilder, Integer.class, 13, responseJSONObject, "utc_offset");

      builder.setMetadata(metadataBuilder.build());
    }
    if (responseJSONObject.containsKey("analysis")) {
      StreetAddressResponse.Analysis.Builder analysisBuilder = StreetAddressResponse.Analysis.newBuilder();

      builder.setAnalysis(analysisBuilder.build());
    }
    return builder.build();
  }

  private static String getTrueOrFalse(boolean b) {
    return b ? "true" : " false";
  }
}
