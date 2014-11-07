package com.centzy.smartystreets;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import org.json.simple.JSONObject;

/**
 * @author Peter Edge (peter.edge@gmail.com).
 */
class SmartyStreetsStreetAddressApiHandler
    extends AbstractSmartyStreetsApiHandler<StreetAddressRequestHeader, StreetAddressRequestBody, StreetAddressResponse> {

  private static final ImmutableBiMap<String, ZipcodeType> ZIPCODE_TYPE_BI_MAP = new ImmutableBiMap.Builder<String, ZipcodeType>()
      .put("Unique", ZipcodeType.ZIPCODE_TYPE_UNIQUE)
      .put("Military", ZipcodeType.ZIPCODE_TYPE_MILITARY)
      .put("POBox", ZipcodeType.ZIPCODE_TYPE_PO_BOX)
      .put("Standard", ZipcodeType.ZIPCODE_TYPE_STANDARD)
      .build();

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
      .put("Residential", StreetAddressResponse.Metadata.ResidentialDeliveryIndicator.RESIDENTIAL_DELIVERY_INDICATOR_RESIDENTIAL)
      .put("Commercial", StreetAddressResponse.Metadata.ResidentialDeliveryIndicator.RESIDENTIAL_DELIVERY_INDICATOR_COMMERCIAL)
      .put("Unknown", StreetAddressResponse.Metadata.ResidentialDeliveryIndicator.RESIDENTIAL_DELIVERY_INDICATOR_UNKNOWN)
      .build();

  private static final ImmutableBiMap<String, StreetAddressResponse.Metadata.ElotSort> ELOT_SORT_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Metadata.ElotSort>()
      .put("A", StreetAddressResponse.Metadata.ElotSort.ELOT_SORT_A)
      .put("D", StreetAddressResponse.Metadata.ElotSort.ELOT_SORT_D)
      .build();

  private static final ImmutableBiMap<String, StreetAddressResponse.Metadata.Precision> PRECISION_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Metadata.Precision>()
      .put("Unknown", StreetAddressResponse.Metadata.Precision.PRECISION_UNKNOWN)
      .put("None", StreetAddressResponse.Metadata.Precision.PRECISION_NONE)
      .put("State", StreetAddressResponse.Metadata.Precision.PRECISION_STATE)
      .put("SolutionArea", StreetAddressResponse.Metadata.Precision.PRECISION_SOLUTION_AREA)
      .put("City", StreetAddressResponse.Metadata.Precision.PRECISION_CITY)
      .put("Zip5", StreetAddressResponse.Metadata.Precision.PRECISION_ZIP_5)
      .put("Zip6", StreetAddressResponse.Metadata.Precision.PRECISION_ZIP_6)
      .put("Zip7", StreetAddressResponse.Metadata.Precision.PRECISION_ZIP_7)
      .put("Zip8", StreetAddressResponse.Metadata.Precision.PRECISION_ZIP_8)
      .put("Zip9", StreetAddressResponse.Metadata.Precision.PRECISION_ZIP_9)
      .put("Structure", StreetAddressResponse.Metadata.Precision.PRECISION_STRUCTURE)
      .build();

  private static final ImmutableBiMap<String, StreetAddressResponse.Metadata.TimeZone> TIME_ZONE_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Metadata.TimeZone>()
      .put("Alaska", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_ALASKA)
      .put("Atlantic", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_ATLANTIC)
      .put("Central", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_CENTRAL)
      .put("Eastern", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_EASTERN)
      .put("Hawaii", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_HAWAII)
      .put("Mountain", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_MOUNTAIN)
      .put("None", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_NONE)
      .put("Pacific", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_PACIFIC)
      .put("Samoa", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_SAMOA)
      .put("UTC+9", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_UTC_PLUS_9)
      .put("UTC+10", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_UTC_PLUS_10)
      .put("UTC+11", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_UTC_PLUS_11)
      .put("UTC+12", StreetAddressResponse.Metadata.TimeZone.TIME_ZONE_UTC_PLUS_12)
      .build();

  private static final ImmutableBiMap<String, StreetAddressResponse.Analysis.DpvMatchCode> DPV_MATCH_CODE_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Analysis.DpvMatchCode>()
      .put("Y", StreetAddressResponse.Analysis.DpvMatchCode.DPV_MATCH_CODE_Y)
      .put("N", StreetAddressResponse.Analysis.DpvMatchCode.DPV_MATCH_CODE_N)
      .put("S", StreetAddressResponse.Analysis.DpvMatchCode.DPV_MATCH_CODE_S)
      .put("D", StreetAddressResponse.Analysis.DpvMatchCode.DPV_MATCH_CODE_D)
      .build();

  private static final ImmutableBiMap<String, StreetAddressResponse.Analysis.DpvFootnote> DPV_FOOTNOTE_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Analysis.DpvFootnote>()
      .put("AA", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_AA)
      .put("A1", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_A1)
      .put("BB", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_BB)
      .put("CC", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_CC)
      .put("F1", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_F1)
      .put("G1", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_G1)
      .put("M1", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_M1)
      .put("M3", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_M3)
      .put("N1", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_N1)
      .put("P1", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_P1)
      .put("P3", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_P3)
      .put("RR", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_RR)
      .put("R1", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_R1)
      .put("U1", StreetAddressResponse.Analysis.DpvFootnote.DPV_FOOTNOTE_U1)
      .build();

  private static final ImmutableBiMap<String, StreetAddressResponse.Analysis.Footnote> FOOTNOTE_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Analysis.Footnote>()
      .put("A", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_A)
      .put("B", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_B)
      .put("C", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_C)
      .put("D", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_D)
      .put("E", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_E)
      .put("F", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_F)
      .put("G", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_G)
      .put("H", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_H)
      .put("I", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_I)
      .put("J", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_J)
      .put("K", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_K)
      .put("L", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_L)
      .put("LL", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_LL)
      .put("LI", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_LI)
      .put("M", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_M)
      .put("N", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_N)
      .put("O", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_O)
      .put("P", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_P)
      .put("Q", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_Q)
      .put("R", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_R)
      .put("S", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_S)
      .put("T", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_T)
      .put("U", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_U)
      .put("V", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_V)
      .put("W", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_W)
      .put("X", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_X)
      .put("Y", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_Y)
      .put("Z", StreetAddressResponse.Analysis.Footnote.FOOTNOTE_Z)
      .build();


  private static final ImmutableBiMap<String, StreetAddressResponse.Analysis.LacslinkCode> LACSLINK_CODE_CODE_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Analysis.LacslinkCode>()
      .put("A", StreetAddressResponse.Analysis.LacslinkCode.LACSLINK_CODE_A)
      .put("00", StreetAddressResponse.Analysis.LacslinkCode.LACSLINK_CODE_00)
      .put("09", StreetAddressResponse.Analysis.LacslinkCode.LACSLINK_CODE_09)
      .put("14", StreetAddressResponse.Analysis.LacslinkCode.LACSLINK_CODE_14)
      .put("92", StreetAddressResponse.Analysis.LacslinkCode.LACSLINK_CODE_92)
      .build();

  private static final ImmutableBiMap<String, StreetAddressResponse.Analysis.LacslinkIndicator> LACSLINK_INDICATOR_BI_MAP
      = new ImmutableBiMap.Builder<String, StreetAddressResponse.Analysis.LacslinkIndicator>()
      .put("Y", StreetAddressResponse.Analysis.LacslinkIndicator.LACSLINK_INDICATOR_Y)
      .put("S", StreetAddressResponse.Analysis.LacslinkIndicator.LACSLINK_INDICATOR_S)
      .put("N", StreetAddressResponse.Analysis.LacslinkIndicator.LACSLINK_INDICATOR_N)
      .put("F", StreetAddressResponse.Analysis.LacslinkIndicator.LACSLINK_INDICATOR_F)
      .build();

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
      JSONObject componentsJSONObject = (JSONObject) responseJSONObject.get("components");
      StreetAddressResponse.Components.Builder componentsBuilder = StreetAddressResponse.Components.newBuilder();
      putOptionalField(componentsBuilder, String.class, 1, componentsJSONObject, "urbanization");
      putOptionalField(componentsBuilder, String.class, 2, componentsJSONObject, "primary_number");
      putOptionalField(componentsBuilder, String.class, 3, componentsJSONObject, "street_name");
      putOptionalField(componentsBuilder, StreetAddressResponse.Components.StreetDirection.class, 4,
          componentsJSONObject, "street_predirection", STREET_DIRECTION_BI_MAP);
      putOptionalField(componentsBuilder, StreetAddressResponse.Components.StreetDirection.class, 5,
          componentsJSONObject, "street_postdirection", STREET_DIRECTION_BI_MAP);
      putOptionalField(componentsBuilder, String.class, 6, componentsJSONObject, "street_suffix");
      putOptionalField(componentsBuilder, String.class, 7, componentsJSONObject, "secondary_number");
      putOptionalField(componentsBuilder, String.class, 8, componentsJSONObject, "secondary_designator");
      putOptionalField(componentsBuilder, String.class, 9, componentsJSONObject, "extra_secondary_number");
      putOptionalField(componentsBuilder, String.class, 10, componentsJSONObject, "extra_secondary_designator");
      putOptionalField(componentsBuilder, String.class, 11, componentsJSONObject, "pmb_designator");
      putOptionalField(componentsBuilder, String.class, 12, componentsJSONObject, "pmb_number");
      putOptionalField(componentsBuilder, String.class, 13, componentsJSONObject, "city_name");
      putOptionalField(componentsBuilder, String.class, 14, componentsJSONObject, "default_city_name");
      putOptionalField(componentsBuilder, State.class, 15, componentsJSONObject, "state_abbreviation", STATE_BI_MAP);
      putOptionalField(componentsBuilder, String.class, 16, componentsJSONObject, "zipcode");
      putOptionalField(componentsBuilder, String.class, 17, componentsJSONObject, "plus4_code");
      putOptionalField(componentsBuilder, String.class, 18, componentsJSONObject, "delivery_point");
      putOptionalField(componentsBuilder, String.class, 19, componentsJSONObject, "delivery_point_check_digit");
      builder.setComponents(componentsBuilder.build());
    }
    if (responseJSONObject.containsKey("metadata")) {
      JSONObject metadataJSONObject = (JSONObject) responseJSONObject.get("metadata");
      StreetAddressResponse.Metadata.Builder metadataBuilder = StreetAddressResponse.Metadata.newBuilder();
      putOptionalField(metadataBuilder, StreetAddressResponse.Metadata.RecordType.class, 1,
          metadataJSONObject, "record_type", RECORD_TYPE_BI_MAP);
      putOptionalField(metadataBuilder, ZipcodeType.class, 2, metadataJSONObject, "zip_type", ZIPCODE_TYPE_BI_MAP);
      putOptionalField(metadataBuilder, String.class, 3, metadataJSONObject, "county_fips");
      putOptionalField(metadataBuilder, String.class, 4, metadataJSONObject, "county_name");
      putOptionalField(metadataBuilder, String.class, 5, metadataJSONObject, "carrier_route");
      putOptionalField(metadataBuilder, String.class, 6, metadataJSONObject, "congressional_district");
      putOptionalField(metadataBuilder, String.class, 7, metadataJSONObject, "building_default_indicator");
      putOptionalField(metadataBuilder, StreetAddressResponse.Metadata.ResidentialDeliveryIndicator.class, 8,
          metadataJSONObject, "rdi", RESIDENTIAL_DELIVERY_INDICATOR_BI_MAP);
      putOptionalField(metadataBuilder, StreetAddressResponse.Metadata.ElotSort.class, 9,
          metadataJSONObject, "elot_sort", ELOT_SORT_BI_MAP);
      Coordinate coordinate = getOptionalCoordinate(metadataJSONObject);
      if (coordinate != null) {
        metadataBuilder.setCoordinate(coordinate);
      }
      putOptionalField(metadataBuilder, StreetAddressResponse.Metadata.Precision.class, 11,
          metadataJSONObject, "precision", PRECISION_BI_MAP);
      putOptionalField(metadataBuilder, StreetAddressResponse.Metadata.TimeZone.class, 12,
          metadataJSONObject, "time_zone", TIME_ZONE_BI_MAP);
      Integer utcOffset = getOptionalUtcOffset(metadataJSONObject);
      if (utcOffset != null) {
        metadataBuilder.setUtcOffset(utcOffset);
      }
      // TODO(pedge): will not set to false if not set
      putOptionalField(metadataBuilder, Boolean.class, 14, metadataJSONObject, "dst");
      builder.setMetadata(metadataBuilder.build());
    }
    if (responseJSONObject.containsKey("analysis")) {
      JSONObject analysisJSONObject = (JSONObject) responseJSONObject.get("analysis");
      StreetAddressResponse.Analysis.Builder analysisBuilder = StreetAddressResponse.Analysis.newBuilder();
      putOptionalField(analysisBuilder, StreetAddressResponse.Analysis.DpvMatchCode.class, 1,
          analysisJSONObject, "dpv_match_code", DPV_MATCH_CODE_BI_MAP);
      if (analysisJSONObject.containsKey("dpv_footnotes")) {
        String dpv_footnotes = (String) analysisJSONObject.get("dpv_footnotes");
        for (int i = 0; i < dpv_footnotes.length(); i += 2) {
          analysisBuilder.addDpvFootnote(DPV_FOOTNOTE_BI_MAP.get(dpv_footnotes.substring(i, i + 2)));
        }
      }
      // TODO(pedge): Y/N/BLANK?
      putOptionalField(analysisBuilder, Boolean.class, 3, analysisJSONObject, "dpv_cmra", YES_NO_BI_MAP);
      putOptionalField(analysisBuilder, Boolean.class, 4, analysisJSONObject, "dpv_vacant", YES_NO_BI_MAP);
      putOptionalField(analysisBuilder, Boolean.class, 5, analysisJSONObject, "active", YES_NO_BI_MAP);
      putOptionalField(analysisBuilder, Boolean.class, 6, analysisJSONObject, "ews_match");
      if (analysisJSONObject.containsKey("footnotes")) {
        for (String footnote : ((String) analysisJSONObject.get("footnotes")).split("#")) {
          analysisBuilder.addFootnote(FOOTNOTE_BI_MAP.get(footnote));
        }
      }
      putOptionalField(analysisBuilder, StreetAddressResponse.Analysis.LacslinkCode.class, 8,
          analysisJSONObject, "lacslink_code", LACSLINK_CODE_CODE_BI_MAP);
      putOptionalField(analysisBuilder, StreetAddressResponse.Analysis.LacslinkIndicator.class, 9,
          analysisJSONObject, "lacslink_indicator", LACSLINK_INDICATOR_BI_MAP);
      putOptionalField(analysisBuilder, Boolean.class, 10, analysisJSONObject, "suitelink_match");
      builder.setAnalysis(analysisBuilder.build());
    }
    return builder.build();
  }

  private static String getTrueOrFalse(boolean b) {
    return b ? "true" : " false";
  }
}
