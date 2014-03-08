package com.centzy.smartystreets;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.protobuf.Message;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

/**
 * @author Peter Edge (peter@locality.com).
 */
public class SmartyStreetsServiceBlockingInterfaceImpl implements SmartyStreetsService.BlockingInterface {

  private final SmartyStreetsServiceImpl smartyStreetsService;

  SmartyStreetsServiceBlockingInterfaceImpl(SmartyStreetsServiceImpl smartyStreetsService) {
    this.smartyStreetsService = Preconditions.checkNotNull(smartyStreetsService);
  }

  @Override
  public StreetAddressResponse handleStreetAddress(
      RpcController controller,
      StreetAddressRequest request
  ) throws ServiceException {
    return handleServiceCall(
        controller,
        request,
        new Function<StreetAddressRequest, StreetAddressResponse>() {
          @Override
          public StreetAddressResponse apply(StreetAddressRequest input) {
            return smartyStreetsService.handleStreetAddress(input);
          }
        }
    );
  }

  @Override
  public RepeatedStreetAddressResponse handleRepeatedStreetAddress(
      RpcController controller,
      RepeatedStreetAddressRequest request
  ) throws ServiceException {
    return handleServiceCall(
        controller,
        request,
        new Function<RepeatedStreetAddressRequest, RepeatedStreetAddressResponse>() {
          @Override
          public RepeatedStreetAddressResponse apply(RepeatedStreetAddressRequest input) {
            return smartyStreetsService.handleRepeatedStreetAddress(input);
          }
        }
    );
  }

  @Override
  public ZipcodeResponse handleZipcode(
      RpcController controller,
      ZipcodeRequest request
  ) throws ServiceException {
    return handleServiceCall(
        controller,
        request,
        new Function<ZipcodeRequest, ZipcodeResponse>() {
          @Override
          public ZipcodeResponse apply(ZipcodeRequest input) {
            return smartyStreetsService.handleZipcode(input);
          }
        }
    );
  }

  @Override
  public RepeatedZipcodeResponse handleRepeatedZipcode(
      RpcController controller,
      RepeatedZipcodeRequest request
  ) throws ServiceException {
    return handleServiceCall(
        controller,
        request,
        new Function<RepeatedZipcodeRequest, RepeatedZipcodeResponse>() {
          @Override
          public RepeatedZipcodeResponse apply(RepeatedZipcodeRequest input) {
            return smartyStreetsService.handleRepeatedZipcode(input);
          }
        }
    );
  }

  private static <Request extends Message, Response extends Message> Response handleServiceCall(
      RpcController controller,
      Request request,
      Function<Request, Response> function
  ) throws ServiceException {
    try {
      return function.apply(request);
    } catch (RuntimeException e) {
      controller.setFailed(e.getMessage());
      throw new ServiceException(e);
    }
  }
}
