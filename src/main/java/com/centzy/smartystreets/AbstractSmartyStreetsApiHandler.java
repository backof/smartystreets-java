package com.centzy.smartystreets;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Peter Edge (peter@locality.com).
 */
abstract class AbstractSmartyStreetsApiHandler<RequestHeader extends Message, RequestBody extends Message, Response extends Message> {

  static final long PICOS = 1000000000000L;

  static final ImmutableBiMap<String, State> STATE_BI_MAP;

  static {
    ImmutableBiMap.Builder<String, State> builder = new ImmutableBiMap.Builder<>();
    for (State state : State.values()) {
      builder.put(state.name().replace("STATE_", ""), state);
    }
    STATE_BI_MAP = builder.build();
  }

  static final ImmutableBiMap<String, Boolean> YES_NO_BI_MAP = new ImmutableBiMap.Builder<String, Boolean>()
      .put("Y", true)
      .put("N", false)
      .build();

  static final ImmutableBiMap<String, Boolean> TRUE_FALSE_BI_MAP = new ImmutableBiMap.Builder<String, Boolean>()
      .put("true", true)
      .put("false", false)
      .build();

  private static final String BASE_API_URL = "https://api.smartystreets.com";
  private static final String UTF_8_ENCODING = "UTF-8";

  private static final ImmutableMap<String, String> BASE_REQUEST_PROPERTIES = ImmutableMap.of(
      "Content-Type", "application/json",
      "Accept", "application/json"
  );

  Response call(RequestHeader requestHeader, RequestBody requestBody) {
    return Iterables.getOnlyElement(call(requestHeader, ImmutableList.of(requestBody)));
  }

  ImmutableList<Response> call(RequestHeader requestHeader, List<RequestBody> requestBodies) {
    ImmutableList.Builder<JSONObject> requestBuilder = ImmutableList.builder();
    for (RequestBody requestBody : requestBodies) {
      requestBuilder.add(getRequestJSONObject(requestBody));
    }

    ImmutableList<JSONObject> responseJSONObjects = call(
        getAuthId(requestHeader),
        getAuthToken(requestHeader),
        getExtraRequestProperties(requestHeader),
        requestBuilder.build()
    );

    ImmutableList.Builder<Response> responseBuilder = ImmutableList.builder();
    for (JSONObject responseJSONObject : responseJSONObjects) {
      responseBuilder.add(getResponse(responseJSONObject));
    }
    return responseBuilder.build();
  }

  abstract String getApiRoute();
  abstract String getAuthId(RequestHeader requestHeader);
  abstract String getAuthToken(RequestHeader requestHeader);
  abstract ImmutableMap<String, String> getExtraRequestProperties(RequestHeader requestHeader);
  abstract JSONObject getRequestJSONObject(RequestBody requestBody);
  abstract Response getResponse(JSONObject responseJSONObject);

  static <T> void putOptionalField(JSONObject jsonObject, String key, Message message, Class<T> fieldClass, int fieldNumber) {
    putField(jsonObject, key, message, fieldClass, fieldNumber, false);
  }

  static <T> void putRequiredField(JSONObject jsonObject, String key, Message message, Class<T> fieldClass, int fieldNumber) {
    putField(jsonObject, key, message, fieldClass, fieldNumber, true);
  }

  static <T> void putOptionalField(Message.Builder builder, Class<T> fieldClass, int fieldNumber, JSONObject jsonObject, String key) {
    putField(builder, fieldClass, fieldNumber, jsonObject, key, false);
  }

  static <T> void putRequiredField(Message.Builder builder, Class<T> fieldClass, int fieldNumber, JSONObject jsonObject, String key) {
    putField(builder, fieldClass, fieldNumber, jsonObject, key, true);
  }

  static <T> void putOptionalField(Message.Builder builder, Class<T> fieldClass, int fieldNumber,
                                   JSONObject jsonObject, String key, ImmutableBiMap<String, T> biMap) {
    putField(builder, fieldClass, fieldNumber, jsonObject, key, false, biMap);
  }

  static <T> void putRequiredField(Message.Builder builder, Class<T> fieldClass, int fieldNumber,
                                   JSONObject jsonObject, String key, ImmutableBiMap<String, T> biMap) {
    putField(builder, fieldClass, fieldNumber, jsonObject, key, true, biMap);
  }

  static <T> void putOptionalField(Message.Builder builder, Class<T> fieldClass, int fieldNumber,
                                   JSONObject jsonObject, String key, Function<String, T> handler) {
    putField(builder, fieldClass, fieldNumber, jsonObject, key, false, handler);
  }

  static <T> void putRequiredField(Message.Builder builder, Class<T> fieldClass, int fieldNumber,
                                   JSONObject jsonObject, String key, Function<String, T> handler) {
    putField(builder, fieldClass, fieldNumber, jsonObject, key, true, handler);
  }

  static <T> T getOptionalField(Message message, Class<T> fieldClass, int fieldNumber) {
    return getField(message, fieldClass, fieldNumber, true);
  }

  static <T> T getRequiredField(Message message, Class<T> fieldClass, int fieldNumber) {
    return getField(message, fieldClass, fieldNumber, true);
  }

  @Nullable
  static Coordinate getOptionalCoordinate(JSONObject responseJSONObject) {
    if (responseJSONObject.containsKey("latitude") && responseJSONObject.containsKey("longitude")) {
      Double latitude = (Double) responseJSONObject.get("latitude");
      Double longitude = (Double) responseJSONObject.get("longitude");
      return Coordinate.newBuilder()
          .setLatitudePicos((long) (latitude * PICOS))
          .setLongitudePicos((long) (longitude * PICOS))
          .build();
    }
    return null;
  }

  private ImmutableList<JSONObject> call(
      String authId,
      String authToken,
      ImmutableMap<String, String> extraRequestProperties,
      ImmutableList<JSONObject> requestJSONObjects
  ) {
    return getResponseJSONObjects(call(authId, authToken, extraRequestProperties, getRequest(requestJSONObjects)));
  }

  private String call(String authId, String authToken, Map<String, String> extraRequestProperties, String request) {
    try {
      URL url = new URL(getUrlString(authId, authToken));
      try {
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        for (Map.Entry<String, String> entry : getRequestProperties(extraRequestProperties).entrySet()) {
          urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
        dataOutputStream.writeBytes(request);
        dataOutputStream.flush();
        dataOutputStream.close();

        DataInputStream dataInputStream = new DataInputStream(urlConnection.getInputStream());

        StringBuilder responseBuilder = new StringBuilder();
        String responseLine = null;
        while((responseLine = dataInputStream.readLine()) != null) {
          responseBuilder.append(responseLine);
        }
        dataInputStream.close();
        return responseBuilder.toString();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getRequest(List<JSONObject> requestJSONObjects) {
    JSONArray jsonArray = new JSONArray();
    jsonArray.addAll(requestJSONObjects);
    return jsonArray.toString();
  }

  private String getUrlString(String authId, String authToken) throws UnsupportedEncodingException {
    return new StringBuilder()
        .append(BASE_API_URL)
        .append(getApiRoute())
        .append("?auth-id=")
        .append(URLEncoder.encode(authId, UTF_8_ENCODING))
        .append("&auth-token=")
        .append(URLEncoder.encode(authId, UTF_8_ENCODING))
        .toString();
  }

  private static ImmutableMap<String, String> getRequestProperties(Map<String, String> extraRequestProperties) {
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    builder.putAll(BASE_REQUEST_PROPERTIES);
    builder.putAll(extraRequestProperties);
    return builder.build();
  }

  private static ImmutableList<JSONObject> getResponseJSONObjects(String response) {
    JSONArray jsonArray = (JSONArray) JSONValue.parse(response);
    ImmutableList.Builder<JSONObject> builder = ImmutableList.builder();
    for (int i = 0; i < jsonArray.size(); i++) {
      builder.add((JSONObject) jsonArray.get(i));
    }
    return builder.build();
  }

  private static <T> ImmutableList<T> getRepeatedField(Message message, Class<T> fieldClass, int fieldNumber) {
    Descriptors.FieldDescriptor fieldDescriptor = getFieldDesciptor(message, fieldNumber);
    ImmutableList.Builder<T> builder = ImmutableList.builder();
    for (int i = 0; i < message.getRepeatedFieldCount(fieldDescriptor); i++) {
      builder.add((T) message.getRepeatedField(fieldDescriptor, i));
    }
    return builder.build();
  }

  private static <T> void putField(JSONObject jsonObject, String key, Message message, Class<T> fieldClass,
                                   int fieldNumber, boolean required) {
    checkClass(fieldClass, String.class, Integer.class);
    T field = getField(message, fieldClass, fieldNumber, required);
    if (field != null) {
      jsonObject.put(key, field);
    }
  }


  private static <T> void putField(Message.Builder builder, Class<T> fieldClass, int fieldNumber,
                           JSONObject jsonObject, String key, boolean required) {
    checkClass(fieldClass, String.class, Integer.class);
    if (jsonObject.containsKey(key)) {
      builder.setField(getFieldDesciptor(builder, fieldNumber), jsonObject.get(key));
    } else if (required) {
      throw new IllegalArgumentException("Required field " + key + " not set");
    }
  }

  private static <T> void putField(Message.Builder builder, Class<T> fieldClass, int fieldNumber,
                                   JSONObject jsonObject, String key, boolean required, final ImmutableBiMap<String, T> biMap) {
   putField(builder, fieldClass, fieldNumber, jsonObject, key, required, new Function<String, T>() {
     @Override
     public T apply(String input) {
       return Preconditions.checkNotNull(biMap.get(input));
     }
   });
  }

  private static <T> void putField(Message.Builder builder, Class<T> fieldClass, int fieldNumber,
                                   JSONObject jsonObject, String key, boolean required, Function<String, T> handler) {
    if (jsonObject.containsKey(key)) {
      builder.setField(getFieldDesciptor(builder, fieldNumber), handler.apply((String) jsonObject.get(key)));
    } else if (required) {
      throw new IllegalArgumentException("Required field " + key + " not set");
    }
  }

  private static <T> T getField(Message message, Class<T> fieldClass, int fieldNumber, boolean required) {
    Descriptors.FieldDescriptor fieldDescriptor = getFieldDesciptor(message, fieldNumber);
    if (!message.hasField(fieldDescriptor)) {
      if (required) {
        throw new IllegalArgumentException("No field for number " + fieldNumber + " on message " + message.toString());
      }
      return null;
    }
    return (T) message.getField(fieldDescriptor);
  }

  private static Descriptors.FieldDescriptor getFieldDesciptor(Message message, int fieldNumber) {
    return message.getDescriptorForType().findFieldByNumber(fieldNumber);
  }

  private static Descriptors.FieldDescriptor getFieldDesciptor(Message.Builder builder, int fieldNumber) {
    return builder.getDescriptorForType().findFieldByNumber(fieldNumber);
  }

  private static void checkClass(Class<?> actualClass, Class<?>...expectedClasses) {
    for (Class<?> expectedClass : expectedClasses) {
      if (actualClass.equals(expectedClass)) {
        return;
      }
    }
    throw new IllegalArgumentException("Expected one of classes " + Arrays.toString(expectedClasses)
        + " , got " + actualClass.toString());
  }
}
