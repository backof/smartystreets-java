package com.centzy.smartystreets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * @author Peter Edge (peter@locality.com).
 */
abstract class AbstractSmartyStreetsApiHandler<RequestHeader extends Message, RequestBody extends Message, Response extends Message> {

  private static final String BASE_API_URL = "https://api.smartystreets.com";

  private static final ImmutableMap<String, String> BASE_REQUEST_PROPERTIES = ImmutableMap.of(
      "Content-Type", "application/json",
      "Accept", "applicaiton/json"
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
    T optionalField = getOptionalField(message, fieldClass, fieldNumber);
    if (optionalField != null) {
      jsonObject.put(key, optionalField);
    }
  }

  static <T> void putRequiredField(JSONObject jsonObject, String key, Message message, Class<T> fieldClass, int fieldNumber) {
    jsonObject.put(key, getRequiredField(message, fieldClass, fieldNumber));
  }

  static <T> T getOptionalField(Message message, Class<T> fieldClass, int fieldNumber) {
    Descriptors.FieldDescriptor fieldDescriptor = getFieldDesciptor(message, fieldNumber);
    if (!message.hasField(fieldDescriptor)) {
      return null;
    }
    return (T) message.getField(fieldDescriptor);
  }

  static <T> T getRequiredField(Message message, Class<T> fieldClass, int fieldNumber) {
    Descriptors.FieldDescriptor fieldDescriptor = getFieldDesciptor(message, fieldNumber);
    if (!message.hasField(fieldDescriptor)) {
      throw new IllegalArgumentException("No field for number " + fieldNumber + " on message " + message.toString());
    }
    return (T) message.getField(fieldDescriptor);
  }

  static <T> ImmutableList<T> getRepeatedField(Message message, Class<T> fieldClass, int fieldNumber) {
    Descriptors.FieldDescriptor fieldDescriptor = getFieldDesciptor(message, fieldNumber);
    ImmutableList.Builder<T> builder = ImmutableList.builder();
    for (int i = 0; i < message.getRepeatedFieldCount(fieldDescriptor); i++) {
      builder.add((T) message.getRepeatedField(fieldDescriptor, i));
    }
    return builder.build();
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
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getRequest(List<JSONObject> requestJSONObjects) {
    JSONArray jsonArray = new JSONArray();
    jsonArray.addAll(requestJSONObjects);
    return jsonArray.toString();
  }

  private String getUrlString(String authId, String authToken) {
    return new StringBuilder()
        .append(BASE_API_URL)
        .append(getApiRoute())
        .append("?auth-id=")
        .append(authId)
        .append("&auth-token=")
        .append(authToken)
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

  private static Descriptors.FieldDescriptor getFieldDesciptor(Message message, int fieldNumber) {
    return message.getDescriptorForType().findFieldByNumber(fieldNumber);
  }
}
