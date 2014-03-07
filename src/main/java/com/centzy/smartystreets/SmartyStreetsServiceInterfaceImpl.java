package com.centzy.smartystreets;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Peter Edge (peter@locality.com).
 */
public class SmartyStreetsServiceInterfaceImpl implements SmartyStreetsService.Interface {

  private static final int DEFAULT_NUM_THREADS = 4;

  private final SmartyStreetsServiceImpl smartyStreetsService = new SmartyStreetsServiceImpl();
  private final ExecutorService executorService;

  SmartyStreetsServiceInterfaceImpl() {
    this(DEFAULT_NUM_THREADS);
  }

  SmartyStreetsServiceInterfaceImpl(int nThreads) {
    this(Executors.newFixedThreadPool(nThreads));
  }

  SmartyStreetsServiceInterfaceImpl(ExecutorService executorService) {
    this.executorService = Preconditions.checkNotNull(executorService);
  }

  @Override
  public void handleStreetAddress(
      RpcController controller,
      StreetAddressRequest request,
      RpcCallback<StreetAddressResponse> done
  ) {
    handleServiceCall(
        controller,
        request,
        done,
        new Function<StreetAddressRequest, StreetAddressResponse>() {
          @Override
          public StreetAddressResponse apply(StreetAddressRequest input) {
            return smartyStreetsService.handleStreetAddress(input);
          }
        }
    );
  }

  @Override
  public void handleRepeatedStreetAddress(
      RpcController controller,
      RepeatedStreetAddressRequest request,
      RpcCallback<RepeatedStreetAddressResponse> done
  ) {
    handleServiceCall(
        controller,
        request,
        done,
        new Function<RepeatedStreetAddressRequest, RepeatedStreetAddressResponse>() {
          @Override
          public RepeatedStreetAddressResponse apply(RepeatedStreetAddressRequest input) {
            return smartyStreetsService.handleRepeatedStreetAddress(input);
          }
        }
    );
  }

  @Override
  public void handleZipcode(
      RpcController controller,
      ZipcodeRequest request,
      RpcCallback<ZipcodeResponse> done
  ) {
    handleServiceCall(
        controller,
        request,
        done,
        new Function<ZipcodeRequest, ZipcodeResponse>() {
          @Override
          public ZipcodeResponse apply(ZipcodeRequest input) {
            return smartyStreetsService.handleZipcode(input);
          }
        }
    );
  }

  @Override
  public void handleRepeatedZipcode(
      RpcController controller,
      RepeatedZipcodeRequest request,
      RpcCallback<RepeatedZipcodeResponse> done
  ) {
    handleServiceCall(
        controller,
        request,
        done,
        new Function<RepeatedZipcodeRequest, RepeatedZipcodeResponse>() {
          @Override
          public RepeatedZipcodeResponse apply(RepeatedZipcodeRequest input) {
            return smartyStreetsService.handleRepeatedZipcode(input);
          }
        }
    );
  }

  private <Request extends Message, Response extends Message> void handleServiceCall(
      final RpcController controller,
      final Request request,
      final RpcCallback<Response> done,
      final Function<Request, Response> function
  ) {
    executorService.submit(
        new Runnable() {
          @Override
          public void run() {
            try {
              done.run(function.apply(request));
            } catch (RuntimeException e) {
              controller.setFailed(e.getMessage());
            }
          }
        }
    );
  }
}
