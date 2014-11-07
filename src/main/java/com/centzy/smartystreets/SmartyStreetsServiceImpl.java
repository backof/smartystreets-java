package com.centzy.smartystreets;

import com.google.common.base.Preconditions;

/**
 * @author Peter Edge (peter.edge@gmail.com).
 */
public class SmartyStreetsServiceImpl {

  private final SmartyStreetsStreetAddressApiHandler streetAddressApihandler = new SmartyStreetsStreetAddressApiHandler();
  private final SmartyStreetsZipcodeApiHandler zipcodeApiHandler = new SmartyStreetsZipcodeApiHandler();

  public StreetAddressResponse handleStreetAddress(StreetAddressRequest request) {
    Preconditions.checkArgument(request.hasStreetAddressRequestHeader());
    Preconditions.checkArgument(request.hasStreetAddressRequestBody());
    return streetAddressApihandler.call(request.getStreetAddressRequestHeader(), request.getStreetAddressRequestBody());
  }

  public RepeatedStreetAddressResponse handleRepeatedStreetAddress(RepeatedStreetAddressRequest request) {
    Preconditions.checkArgument(request.hasStreetAddressRequestHeader());
    Preconditions.checkArgument(request.getStreetAddressRequestBodyCount() > 0);
    return RepeatedStreetAddressResponse.newBuilder()
        .addAllStreetAddressResponse(
            streetAddressApihandler.call(
                request.getStreetAddressRequestHeader(),
                request.getStreetAddressRequestBodyList()
            )
        )
        .build();
  }

  public ZipcodeResponse handleZipcode(ZipcodeRequest request) {
    Preconditions.checkArgument(request.hasZipcodeRequestHeader());
    Preconditions.checkArgument(request.hasZipcodeRequestBody());
    return zipcodeApiHandler.call(request.getZipcodeRequestHeader(), request.getZipcodeRequestBody());
  }

  public RepeatedZipcodeResponse handleRepeatedZipcode(RepeatedZipcodeRequest request) {
    Preconditions.checkArgument(request.hasZipcodeRequestHeader());
    Preconditions.checkArgument(request.getZipcodeRequestBodyCount() > 0);
    return RepeatedZipcodeResponse.newBuilder()
        .addAllZipcodeResponse(
            zipcodeApiHandler.call(
                request.getZipcodeRequestHeader(),
                request.getZipcodeRequestBodyList()
            )
        )
        .build();
  }
}
