# SmartyStreets

SmartyStreets API Wrapper for Java.

Wraps both the Street Address API and Zipcode API.

http://smartystreets.com/  
http://smartystreets.com/kb/liveaddress-api/  
https://github.com/smartystreets/LiveAddressSamples/tree/master/java

## Usage

You must have a valid auth-id and auth-token from SmartyStreets.

```java
SmartyStreetsServiceHandler handler = new SmartyStreetsServiceHandler("AUTH_ID", "AUTH_TOKEN");

StreetAddressRequestBody streetAddress1 = StreetAddressRequestBody.newBuilder()
  .setStreet("222 Broadway Fl 20")
  .setCity("New York")
  .setState("NY")
  .setZipcode("10038")
  .build();
StreetAddressRequestBody streetAddress2 = StreetAddressRequestBody.newBuilder()
  .setStreet("150 Spear St Fl 14")
  .setCity("San Francisco")
  .setState("CA")
  .setZipcode("94105")
  .build();

# single call
StreetAddressResponse streetAddressResponse = handler.handleStreetAddress(streetAddress1);
# batch call
ImmutableList<StreetAddressResponse> streetAddressResponses = handler.handleStreetAddress(
  ImmutableList.of(streetAddress1, streetAddress2)
);

ZipcodeRequestBody zipcode1 = ZipcodeRequestBody.newBuilder()
  .setCity("New York")
  .setState("NY")
  .build();
ZipcodeRequestBody zipcode2 = ZipcodeRequestBody.newBuilder()
  .setZipcode("94105")
  .build();

# single call
ZipcodeResponse zipcodeResponse = handler.handleZipcode(zipcode1);
# batch call
ImmutableList<ZipcodeResponse> zipcodeResponses = handler.handleZipcode(
  ImmutableList.of(zipcode1, zipcode2)
);

# new interface implementation
SmartyStreetsService.Interface smartyStreetsServiceInterface = new SmartyStreetsServiceInterfaceImpl();
# new blocking interface implementation
SmartyStreetsService.BlockingInterface smartyStreetsServiceBlockingInterface = new SmartyStreetsServiceBlockingInterfaceImpl();
```

Written for Locality  
http://www.locality.com

## Authors

Peter Edge  
peter@locality.com  
http://github.com/peter-edge

## License

MIT
