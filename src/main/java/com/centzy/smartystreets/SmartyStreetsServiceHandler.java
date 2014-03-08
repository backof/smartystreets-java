package com.centzy.smartystreets;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

/**
 * @author Peter Edge (peter@locality.com).
 */
public class SmartyStreetsServiceHandler {

  private final SmartyStreetsServiceImpl smartyStreetsService;
  private final String authId;
  private final String authToken;
  private final boolean standardizeOnly;
  private final boolean includeInvalid;
  private final boolean acceptKeypair;

  public SmartyStreetsServiceHandler(String authId, String authToken) {
    this(new SmartyStreetsServiceImpl(), authId, authToken);
  }

  // standardize-only probably should be true
  public SmartyStreetsServiceHandler(String authId, String authToken,
                                     boolean standardizeOnly, boolean includeInvalid, boolean acceptKeypair) {
    this(new SmartyStreetsServiceImpl(), authId, authToken, standardizeOnly, includeInvalid, acceptKeypair);
  }

  public SmartyStreetsServiceHandler(SmartyStreetsServiceImpl smartyStreetsService, String authId, String authToken) {
    this(smartyStreetsService, authId, authToken, false, false, false);
  }

  public SmartyStreetsServiceHandler(SmartyStreetsServiceImpl smartyStreetsService, String authId, String authToken,
                              boolean standardizeOnly, boolean includeInvalid, boolean acceptKeypair) {
    this.smartyStreetsService = Preconditions.checkNotNull(smartyStreetsService);
    this.authId = Preconditions.checkNotNull(authId);
    this.authToken = Preconditions.checkNotNull(authToken);
    this.standardizeOnly = standardizeOnly;
    this.includeInvalid = includeInvalid;
    this.acceptKeypair = acceptKeypair;
  }

  public StreetAddressResponse handleStreetAddress(StreetAddressRequestBody requestBody) {
    return Iterables.getOnlyElement(handleStreetAddress(ImmutableList.of(requestBody)));
  }

  public ImmutableList<StreetAddressResponse> handleStreetAddress(Collection<StreetAddressRequestBody> requestBodies) {
    return ImmutableList.copyOf(
        smartyStreetsService.handleRepeatedStreetAddress(
            RepeatedStreetAddressRequest.newBuilder()
                .setStreetAddressRequestHeader(
                    StreetAddressRequestHeader.newBuilder()
                        .setAuthId(authId)
                        .setAuthToken(authToken)
                        .setStandardizeOnly(standardizeOnly)
                        .setIncludeInvalid(includeInvalid)
                        .setAcceptKeypair(acceptKeypair)
                        .build()
                )
                .addAllStreetAddressRequestBody(requestBodies)
                .build()
        ).getStreetAddressResponseList()
    );
  }

  public ZipcodeResponse handleZipcode(ZipcodeRequestBody requestBody) {
    return Iterables.getOnlyElement(handleZipcode(ImmutableList.of(requestBody)));
  }

  public ImmutableList<ZipcodeResponse> handleZipcode(Collection<ZipcodeRequestBody> requestBodies) {
    return ImmutableList.copyOf(
        smartyStreetsService.handleRepeatedZipcode(
            RepeatedZipcodeRequest.newBuilder()
                .setZipcodeRequestHeader(
                    ZipcodeRequestHeader.newBuilder()
                        .setAuthId(authId)
                        .setAuthToken(authToken)
                        .build()
                ).addAllZipcodeRequestBody(requestBodies)
                .build()
        ).getZipcodeResponseList()
    );
  }
}
