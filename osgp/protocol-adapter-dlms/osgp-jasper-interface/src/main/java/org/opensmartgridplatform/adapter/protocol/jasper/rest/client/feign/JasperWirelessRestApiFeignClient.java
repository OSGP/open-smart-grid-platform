// package org.opensmartgridplatform.adapter.protocol.jasper.rest.client.feign;
//
// import org.opensmartgridplatform.adapter.protocol.jasper.json.GetSessionInfoRequest;
// import org.opensmartgridplatform.adapter.protocol.jasper.json.GetSessionInfoResponse;
// import org.opensmartgridplatform.adapter.protocol.jasper.json.SendSMSRequest;
// import org.opensmartgridplatform.adapter.protocol.jasper.json.SendSMSResponse;
// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestHeader;
//
// @FeignClient(
//    name = "jasper-wireless-api",
//    url = "${jasper-wireless-api.url}",
//    configuration = JasperWirelessRestApiFeignConfig.class)
// public interface JasperWirelessRestApiFeignClient {
//
//  @PostMapping(
//      value = "/rws/api/{apiVersion}1/devices/{iccId}/smsMessages",
//      consumes = MediaType.APPLICATION_JSON_VALUE,
//      produces = MediaType.APPLICATION_JSON_VALUE)
//  SendSMSResponse sendSmsMessages(
//      @PathVariable("apiVersion") String apiVersion,
//      @PathVariable("iccId") String iccId,
//      @RequestBody SendSMSRequest sendSMSRequest,
//      @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorization);
//
//  @GetMapping(
//      value = "/rws/api/{apiVersion}1/devices/{iccId}/sessionInfo",
//      consumes = MediaType.APPLICATION_JSON_VALUE,
//      produces = MediaType.APPLICATION_JSON_VALUE)
//  GetSessionInfoResponse getSessionInfo(
//      @PathVariable("apiVersion") String apiVersion,
//      @PathVariable("iccId") String iccId,
//      @RequestBody GetSessionInfoRequest getSessionInfoRequest,
//      @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorization);
// }
