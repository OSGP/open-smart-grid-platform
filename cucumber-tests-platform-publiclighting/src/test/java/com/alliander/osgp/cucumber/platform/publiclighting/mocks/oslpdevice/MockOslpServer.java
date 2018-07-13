/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.publiclighting.mocks.oslpdevice;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.config.CoreDeviceConfiguration;
import com.alliander.osgp.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.DaliConfiguration;
import com.alliander.osgp.oslp.Oslp.GetConfigurationResponse;
import com.alliander.osgp.oslp.Oslp.GetFirmwareVersionResponse;
import com.alliander.osgp.oslp.Oslp.GetStatusResponse;
import com.alliander.osgp.oslp.Oslp.GetStatusResponse.Builder;
import com.alliander.osgp.oslp.Oslp.IndexAddressMap;
import com.alliander.osgp.oslp.Oslp.LightType;
import com.alliander.osgp.oslp.Oslp.LightValue;
import com.alliander.osgp.oslp.Oslp.LinkType;
import com.alliander.osgp.oslp.Oslp.LongTermIntervalType;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.MeterType;
import com.alliander.osgp.oslp.Oslp.PowerUsageData;
import com.alliander.osgp.oslp.Oslp.PsldData;
import com.alliander.osgp.oslp.Oslp.RelayConfiguration;
import com.alliander.osgp.oslp.Oslp.RelayData;
import com.alliander.osgp.oslp.Oslp.RelayType;
import com.alliander.osgp.oslp.Oslp.ResumeScheduleResponse;
import com.alliander.osgp.oslp.Oslp.SetConfigurationResponse;
import com.alliander.osgp.oslp.Oslp.SetDeviceVerificationKeyResponse;
import com.alliander.osgp.oslp.Oslp.SetEventNotificationsResponse;
import com.alliander.osgp.oslp.Oslp.SetLightResponse;
import com.alliander.osgp.oslp.Oslp.SetRebootResponse;
import com.alliander.osgp.oslp.Oslp.SetScheduleResponse;
import com.alliander.osgp.oslp.Oslp.SetTransitionResponse;
import com.alliander.osgp.oslp.Oslp.SsldData;
import com.alliander.osgp.oslp.Oslp.StartSelfTestResponse;
import com.alliander.osgp.oslp.Oslp.StopSelfTestResponse;
import com.alliander.osgp.oslp.Oslp.UpdateFirmwareResponse;
import com.alliander.osgp.oslp.OslpDecoder;
import com.alliander.osgp.oslp.OslpEncoder;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.oslp.OslpUtils;
import com.alliander.osgp.shared.security.CertificateHelper;
import com.google.common.base.Strings;
import com.google.protobuf.ByteString;

public class MockOslpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockOslpServer.class);

    private final CoreDeviceConfiguration configuration;

    private final int oslpPortServer;

    private final int oslpElsterPortServer;

    private final String oslpSignature;

    private final String oslpSignatureProvider;

    private final int connectionTimeout;

    private final String signKeyPath;

    private final String verifyKeyPath;

    private final String keytype;

    private final Integer sequenceNumberWindow;

    private final Integer sequenceNumberMaximum;

    private final Long responseDelayTime;

    private final Long reponseDelayRandomRange;

    private ServerBootstrap serverOslp;
    private ServerBootstrap serverOslpElster;

    // TODO split channel handler in client/server
    private MockOslpChannelHandler channelHandler;

    private final ConcurrentMap<DeviceRequestMessageType, Message> mockResponses = new ConcurrentHashMap<>();
    private final ConcurrentMap<DeviceRequestMessageType, Message> receivedRequests = new ConcurrentHashMap<>();
    private final List<Message> receivedResponses = new ArrayList<>();

    public MockOslpServer(final CoreDeviceConfiguration configuration, final int oslpPortServer,
            final int oslpElsterPortServer, final String oslpSignature, final String oslpSignatureProvider,
            final int connectionTimeout, final String signKeyPath, final String verifyKeyPath, final String keytype,
            final Integer sequenceNumberWindow, final Integer sequenceNumberMaximum, final Long responseDelayTime,
            final Long reponseDelayRandomRange) {
        this.configuration = configuration;
        this.oslpPortServer = oslpPortServer;
        this.oslpElsterPortServer = oslpElsterPortServer;
        this.oslpSignature = oslpSignature;
        this.oslpSignatureProvider = oslpSignatureProvider;
        this.connectionTimeout = connectionTimeout;
        this.signKeyPath = signKeyPath;
        this.verifyKeyPath = verifyKeyPath;
        this.keytype = keytype;
        this.sequenceNumberWindow = sequenceNumberWindow;
        this.sequenceNumberMaximum = sequenceNumberMaximum;
        this.responseDelayTime = responseDelayTime;
        this.reponseDelayRandomRange = reponseDelayRandomRange;
    }

    public Integer getSequenceNumber() {
        return this.channelHandler.getSequenceNumber();
    }

    public void start() throws Throwable {
        this.channelHandler = new MockOslpChannelHandler(this.oslpSignature, this.oslpSignatureProvider,
                this.connectionTimeout, this.sequenceNumberWindow, this.sequenceNumberMaximum, this.responseDelayTime,
                this.reponseDelayRandomRange, this.privateKey(), this.clientBootstrap(), this.mockResponses,
                this.receivedRequests, this.receivedResponses);

        LOGGER.debug("OSLP Mock server starting on port {}", this.oslpPortServer);
        this.serverOslp = this.serverBootstrap();
        this.serverOslp.bind(new InetSocketAddress(this.oslpPortServer));
        LOGGER.debug("OSLP Elster Mock server starting on port {}", this.oslpElsterPortServer);
        this.serverOslpElster = this.serverBootstrap();
        this.serverOslpElster.bind(new InetSocketAddress(this.oslpElsterPortServer));
        LOGGER.info("OSLP Mock servers started.");
    }

    public void stop() {
        if (this.serverOslp != null) {
            this.serverOslp.releaseExternalResources();
            this.serverOslp.shutdown();
        }
        if (this.serverOslpElster != null) {
            this.serverOslpElster.releaseExternalResources();
            this.serverOslpElster.shutdown();
        }
        this.channelHandler = null;
        this.resetServer();
        LOGGER.info("OSLP Mock servers shutdown.");
    }

    public void resetServer() {
        this.receivedRequests.clear();
        this.receivedResponses.clear();
        this.mockResponses.clear();
        this.channelHandler.reset();
    }

    public Message waitForRequest(final DeviceRequestMessageType requestType) {
        int count = 0;
        while (!this.receivedRequests.containsKey(requestType)) {
            try {
                count++;
                LOGGER.info("Sleeping 1s " + count);
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                Assert.fail("Polling for response interrupted");
            }

            if (count > this.configuration.getTimeout()) {
                Assert.fail("Polling for response failed, no reponse found");
            }
        }

        return this.receivedRequests.get(requestType);
    }

    public OslpEnvelope send(final InetSocketAddress address, final OslpEnvelope request,
            final String deviceIdentification) throws IOException, DeviceSimulatorException {
        return this.channelHandler.send(address, request, deviceIdentification);
    }

    public Message sendRequest(final Message message) throws DeviceSimulatorException, IOException, ParseException {

        final OslpEnvelope envelope = new OslpEnvelope();
        envelope.setPayloadMessage(message);

        return this.channelHandler.handleRequest(envelope, this.channelHandler.getSequenceNumber());
    }

    private ServerBootstrap serverBootstrap() {
        final ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newFixedThreadPool(1),
                Executors.newFixedThreadPool(1));

        final ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline()
                    throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, NoSuchProviderException {
                final ChannelPipeline pipeline = MockOslpServer.this.createPipeLine();
                return pipeline;
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", false);

        return bootstrap;
    }

    private ClientBootstrap clientBootstrap() {
        final ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline()
                    throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, NoSuchProviderException {
                final ChannelPipeline pipeline = MockOslpServer.this.createPipeLine();
                return pipeline;
            }
        };

        final ClientBootstrap bootstrap = new ClientBootstrap(factory);

        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", false);
        bootstrap.setOption("connectTimeoutMillis", this.connectionTimeout);

        bootstrap.setPipelineFactory(pipelineFactory);

        return bootstrap;
    }

    private ChannelPipeline createPipeLine()
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException {
        final ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("oslpEncoder", new OslpEncoder());
        pipeline.addLast("oslpDecoder", new OslpDecoder(this.oslpSignature, this.oslpSignatureProvider));
        pipeline.addLast("oslpSecurity", new OslpSecurityHandler(this.publicKey()));
        pipeline.addLast("oslpChannelHandler", this.channelHandler);

        return pipeline;
    }

    private PublicKey publicKey()
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, NoSuchProviderException {
        return CertificateHelper.createPublicKey(this.verifyKeyPath, this.keytype, this.oslpSignatureProvider);
    }

    public PrivateKey privateKey() {
        PrivateKey privateKey = null;

        try {
            privateKey = CertificateHelper.createPrivateKey(this.signKeyPath, this.keytype, this.oslpSignatureProvider);
        } catch (final Exception ex) {
            //
        }

        return privateKey;
    }

    public void mockGetConfigurationResponse(final Oslp.Status oslpStatus, final LightType lightType,
            final String dcLights, final String dcMap, final String rcMap, final LinkType preferredLinkType,
            final MeterType meterType, final Integer shortInterval, final Integer longInterval,
            final LongTermIntervalType intervalType, final String osgpIpAddress, final Integer osgpPort)
            throws UnknownHostException {

        final String[] dcMapArray;
        final String[] rcMapArray;
        if (dcMap != null) {
            dcMapArray = dcMap.split(";");
        } else {
            dcMapArray = null;
        }
        if (rcMap != null) {
            rcMapArray = rcMap.split(";");
        } else {
            rcMapArray = null;
        }

        final GetConfigurationResponse.Builder builder = GetConfigurationResponse.newBuilder();

        builder.setStatus(oslpStatus);
        if (lightType != null && lightType != LightType.LT_NOT_SET) {
            builder.setLightType(lightType);
        }

        final ByteString bsDcLights = ByteString.copyFromUtf8(dcLights);
        if (dcMapArray != null && !dcMapArray[0].isEmpty()) {
            final DaliConfiguration.Builder dcBuilder = DaliConfiguration.newBuilder();

            for (int i = 0; i < dcMapArray.length; i++) {
                final String[] dcSubMapArray = dcMapArray[i].split(",");
                if (dcSubMapArray[i] != null && !dcSubMapArray[i].isEmpty()) {
                    dcBuilder.addAddressMap(IndexAddressMap.newBuilder()
                            .setIndex(OslpUtils.integerToByteString(Integer.parseInt(dcSubMapArray[0])))
                            .setAddress(OslpUtils.integerToByteString(Integer.parseInt(dcSubMapArray[1])))
                            .setRelayType(RelayType.RT_NOT_SET));
                }
            }

            if (!bsDcLights.isEmpty()) {
                dcBuilder.setNumberOfLights(bsDcLights);
            }

            if (!dcLights.isEmpty() && dcBuilder.getAddressMapCount() != Integer.parseInt(dcLights)) {
                builder.setDaliConfiguration(dcBuilder.build());
            }

            if (rcMapArray != null && !rcMapArray[0].isEmpty()) {
                final RelayConfiguration.Builder rcBuilder = RelayConfiguration.newBuilder();

                for (int i = 0; i < rcMapArray.length; i++) {
                    final String[] rcSubMapArray = rcMapArray[i].split(",");
                    if (rcSubMapArray[i] != null && !rcSubMapArray[i].isEmpty()) {
                        final RelayType rcRelayType = RelayType.valueOf(rcSubMapArray[2]);
                        rcBuilder.addAddressMap(IndexAddressMap.newBuilder()
                                .setIndex(OslpUtils.integerToByteString(Integer.parseInt(rcSubMapArray[0])))
                                .setAddress(OslpUtils.integerToByteString(Integer.parseInt(rcSubMapArray[1])))
                                .setRelayType(rcRelayType));
                    }
                }

                builder.setRelayConfiguration(rcBuilder.build());
            }
        }

        if (preferredLinkType != null && preferredLinkType != LinkType.LINK_NOT_SET) {
            builder.setPreferredLinkType(preferredLinkType);
        }

        if (meterType != null && meterType != MeterType.MT_NOT_SET) {
            builder.setMeterType(meterType);
        }

        builder.setShortTermHistoryIntervalMinutes(shortInterval);
        builder.setLongTermHistoryInterval(longInterval);

        if (intervalType != null && intervalType != LongTermIntervalType.LT_INT_NOT_SET) {
            builder.setLongTermHistoryIntervalType(intervalType);
        }

        if (StringUtils.isNotEmpty(osgpIpAddress)) {
            builder.setOspgIpAddress(ByteString.copyFrom(InetAddress.getByName(osgpIpAddress).getAddress()));
        }

        if (osgpPort != null) {
            builder.setOsgpPortNumber(osgpPort);
        }

        this.mockResponses.put(DeviceRequestMessageType.GET_CONFIGURATION,
                Oslp.Message.newBuilder().setGetConfigurationResponse(builder).build());
    }

    public void mockSetConfigurationResponse(final Oslp.Status oslpStatus) {

        this.mockResponses.put(DeviceRequestMessageType.SET_CONFIGURATION, Oslp.Message.newBuilder()
                .setSetConfigurationResponse(SetConfigurationResponse.newBuilder().setStatus(oslpStatus).build())
                .build());
    }

    public void mockGetFirmwareVersionResponse(final String fwVersion) {
        this.mockResponses.put(DeviceRequestMessageType.GET_FIRMWARE_VERSION, Oslp.Message.newBuilder()
                .setGetFirmwareVersionResponse(GetFirmwareVersionResponse.newBuilder().setFirmwareVersion(fwVersion))
                .build());
    }

    public void mockUpdateFirmwareResponse(final Oslp.Status status) {
        this.mockResponses.put(DeviceRequestMessageType.UPDATE_FIRMWARE, Oslp.Message.newBuilder()
                .setUpdateFirmwareResponse(UpdateFirmwareResponse.newBuilder().setStatus(status)).build());
    }

    public void mockSetLightResponse(final Oslp.Status status) {
        this.mockResponses.put(DeviceRequestMessageType.SET_LIGHT,
                Oslp.Message.newBuilder().setSetLightResponse(SetLightResponse.newBuilder().setStatus(status)).build());
    }

    public void mockSetEventNotificationResponse(final Oslp.Status status) {
        this.mockResponses.put(DeviceRequestMessageType.SET_EVENT_NOTIFICATIONS,
                Oslp.Message.newBuilder()
                        .setSetEventNotificationsResponse(SetEventNotificationsResponse.newBuilder().setStatus(status))
                        .build());
    }

    public void mockStartDeviceResponse(final Oslp.Status status) {
        this.mockResponses.put(DeviceRequestMessageType.START_SELF_TEST, Oslp.Message.newBuilder()
                .setStartSelfTestResponse(StartSelfTestResponse.newBuilder().setStatus(status)).build());
    }

    public void mockStopDeviceResponse(final com.google.protobuf.ByteString value, final Oslp.Status status) {
        this.mockResponses.put(DeviceRequestMessageType.STOP_SELF_TEST, Oslp.Message.newBuilder()
                .setStopSelfTestResponse(StopSelfTestResponse.newBuilder().setSelfTestResult(value).setStatus(status))
                .build());
    }

    public void mockGetStatusResponse(final LinkType preferred, final LinkType actual, final LightType lightType,
            final int eventNotificationMask, final Oslp.Status status, final List<LightValue> lightValues,
            final List<LightValue> tariffValues) {

        final Builder response = GetStatusResponse.newBuilder().setPreferredLinktype(preferred)
                .setActualLinktype(actual).setLightType(lightType).setEventNotificationMask(eventNotificationMask)
                .setStatus(status);

        for (final LightValue lightValue : lightValues) {
            response.addValue(lightValue);
        }
        for (final LightValue tariffValue : tariffValues) {
            response.addValue(tariffValue);
        }

        this.mockResponses.put(DeviceRequestMessageType.GET_STATUS,
                Oslp.Message.newBuilder().setGetStatusResponse(response).build());
    }

    public void mockResumeScheduleResponse(final Oslp.Status status) {
        this.mockResponses.put(DeviceRequestMessageType.RESUME_SCHEDULE, Oslp.Message.newBuilder()
                .setResumeScheduleResponse(ResumeScheduleResponse.newBuilder().setStatus(status)).build());
    }

    public void mockSetRebootResponse(final Oslp.Status status) {
        this.mockResponses.put(DeviceRequestMessageType.SET_REBOOT, Oslp.Message.newBuilder()
                .setSetRebootResponse(SetRebootResponse.newBuilder().setStatus(status)).build());
    }

    public void mockSetTransitionResponse(final Oslp.Status status) {
        this.mockResponses.put(DeviceRequestMessageType.SET_TRANSITION, Oslp.Message.newBuilder()
                .setSetTransitionResponse(SetTransitionResponse.newBuilder().setStatus(status)).build());
    }

    public void mockUpdateKeyResponse(final Oslp.Status status) {
        this.mockResponses.put(DeviceRequestMessageType.UPDATE_KEY, Oslp.Message.newBuilder()
                .setSetDeviceVerificationKeyResponse(SetDeviceVerificationKeyResponse.newBuilder().setStatus(status))
                .build());
    }

    public void mockGetLightStatusResponse(final LinkType preferred, final LinkType actual, final LightType lightType,
            final int eventNotificationMask, final Oslp.Status status, final List<LightValue> lightValues) {
        final Builder response = GetStatusResponse.newBuilder().setPreferredLinktype(preferred)
                .setActualLinktype(actual).setLightType(lightType).setEventNotificationMask(eventNotificationMask)
                .setStatus(status);

        for (final LightValue lightValue : lightValues) {
            response.addValue(lightValue);
        }

        this.mockResponses.put(DeviceRequestMessageType.GET_LIGHT_STATUS,
                Oslp.Message.newBuilder().setGetStatusResponse(response).build());
    }

    public void mockSetScheduleResponse(final DeviceRequestMessageType type, final Oslp.Status status) {
        this.mockResponses.put(type, Oslp.Message.newBuilder()
                .setSetScheduleResponse(SetScheduleResponse.newBuilder().setStatus(status)).build());
    }

    public void mockGetActualPowerUsageResponse(final Oslp.Status status, final Integer actualConsumedPower,
            final MeterType meterType, final String recordTime, final Integer totalConsumedEnergy,
            final Integer totalLightingHours, final Integer actualCurrent1, final Integer actualCurrent2,
            final Integer actualCurrent3, final Integer actualPower1, final Integer actualPower2,
            final Integer actualPower3, final Integer averagePowerFactor1, final Integer averagePowerFactor2,
            final Integer averagePowerFactor3, final String relayData) {

        final com.alliander.osgp.oslp.Oslp.SsldData.Builder ssldData = SsldData.newBuilder();

        if (relayData != null && !relayData.isEmpty()) {
            for (final String data : relayData.split(PlatformKeys.SEPARATOR_SEMICOLON)) {
                final String[] dataParts = data.split(PlatformKeys.SEPARATOR_COMMA);

                final RelayData r = RelayData.newBuilder()
                        .setIndex(OslpUtils.integerToByteString(Integer.parseInt(dataParts[0])))
                        .setTotalLightingMinutes(Integer.parseInt(dataParts[1])).build();
                ssldData.addRelayData(r);
            }
        }

        final com.alliander.osgp.oslp.Oslp.PowerUsageData.Builder powerUsageData = PowerUsageData.newBuilder();

        if (meterType != null) {
            powerUsageData.setMeterType(meterType);
        }

        if (totalLightingHours != null) {
            powerUsageData.setPsldData(PsldData.newBuilder().setTotalLightingHours(totalLightingHours).build());
        }

        if (!recordTime.isEmpty()) {
            powerUsageData.setRecordTime(recordTime);
        }

        final com.alliander.osgp.oslp.Oslp.GetActualPowerUsageResponse response = com.alliander.osgp.oslp.Oslp.GetActualPowerUsageResponse
                .newBuilder().setStatus(status)
                .setPowerUsageData(powerUsageData.setActualConsumedPower(actualConsumedPower)
                        .setTotalConsumedEnergy(totalConsumedEnergy)
                        .setSsldData(ssldData.setActualCurrent1(actualCurrent1).setActualCurrent2(actualCurrent2)
                                .setActualCurrent3(actualCurrent3).setActualPower1(actualPower1)
                                .setActualPower2(actualPower2).setActualPower3(actualPower3)
                                .setAveragePowerFactor1(averagePowerFactor1).setAveragePowerFactor2(averagePowerFactor2)
                                .setAveragePowerFactor3(averagePowerFactor3).build())
                        .build())
                .build();

        this.mockResponses.put(DeviceRequestMessageType.GET_ACTUAL_POWER_USAGE,
                Oslp.Message.newBuilder().setGetActualPowerUsageResponse(response).build());
    }

    public void mockGetPowerUsageHistoryResponse(final Oslp.Status status, final Map<String, String[]> requestMap) {

        com.alliander.osgp.oslp.Oslp.GetPowerUsageHistoryResponse response = null;

        final com.alliander.osgp.oslp.Oslp.GetPowerUsageHistoryResponse.Builder builder = com.alliander.osgp.oslp.Oslp.GetPowerUsageHistoryResponse
                .newBuilder();

        if (requestMap.containsKey(PlatformPubliclightingKeys.RECORD_TIME)) {

            this.creatingPowerUsageDataAndAddingToBuilder(builder, requestMap);

            response = builder.setStatus(status).build();
        } else {
            response = builder.setStatus(status).build();
        }

        this.mockResponses.put(DeviceRequestMessageType.GET_POWER_USAGE_HISTORY,
                Oslp.Message.newBuilder().setGetPowerUsageHistoryResponse(response).build());
    }

    private void creatingPowerUsageDataAndAddingToBuilder(
            final com.alliander.osgp.oslp.Oslp.GetPowerUsageHistoryResponse.Builder builder,
            final Map<String, String[]> requestMap) {

        for (int i = 0; i < requestMap.get(PlatformPubliclightingKeys.RECORD_TIME).length; i++) {

            final com.alliander.osgp.oslp.Oslp.SsldData.Builder ssldData = SsldData.newBuilder();

            this.addRelayDataToSsldData(ssldData, requestMap, i);

            final com.alliander.osgp.oslp.Oslp.PowerUsageData.Builder powerUsageData = PowerUsageData.newBuilder();

            this.addDataToPowerUsageData(powerUsageData, requestMap, i);

            final Integer actualConsumedPower = Integer
                    .parseInt(requestMap.get(PlatformPubliclightingKeys.ACTUAL_CONSUMED_POWER)[i]),
                    totalConsumedEnergy = Integer
                            .parseInt(requestMap.get(PlatformPubliclightingKeys.TOTAL_CONSUMED_ENERGY)[i]),
                    actualCurrent1 = Integer.parseInt(requestMap.get(PlatformPubliclightingKeys.ACTUAL_CURRENT1)[i]),
                    actualCurrent2 = Integer.parseInt(requestMap.get(PlatformPubliclightingKeys.ACTUAL_CURRENT2)[i]),
                    actualCurrent3 = Integer.parseInt(requestMap.get(PlatformPubliclightingKeys.ACTUAL_CURRENT3)[i]),
                    actualPower1 = Integer.parseInt(requestMap.get(PlatformPubliclightingKeys.ACTUAL_POWER1)[i]),
                    actualPower2 = Integer.parseInt(requestMap.get(PlatformPubliclightingKeys.ACTUAL_POWER2)[i]),
                    actualPower3 = Integer.parseInt(requestMap.get(PlatformPubliclightingKeys.ACTUAL_POWER3)[i]),
                    averagePowerFactor1 = Integer
                            .parseInt(requestMap.get(PlatformPubliclightingKeys.AVERAGE_POWER_FACTOR1)[i]),
                    averagePowerFactor2 = Integer
                            .parseInt(requestMap.get(PlatformPubliclightingKeys.AVERAGE_POWER_FACTOR2)[i]),
                    averagePowerFactor3 = Integer
                            .parseInt(requestMap.get(PlatformPubliclightingKeys.AVERAGE_POWER_FACTOR3)[i]);

            if (powerUsageData.hasMeterType() && powerUsageData.getMeterType() != null
                    && Strings.isNullOrEmpty(powerUsageData.getMeterType().toString())) {
                return;
            }

            builder.addPowerUsageData(powerUsageData.setActualConsumedPower(actualConsumedPower)
                    .setTotalConsumedEnergy(totalConsumedEnergy)
                    .setSsldData(ssldData.setActualCurrent1(actualCurrent1).setActualCurrent2(actualCurrent2)
                            .setActualCurrent3(actualCurrent3).setActualPower1(actualPower1)
                            .setActualPower2(actualPower2).setActualPower3(actualPower3)
                            .setAveragePowerFactor1(averagePowerFactor1).setAveragePowerFactor2(averagePowerFactor2)
                            .setAveragePowerFactor3(averagePowerFactor3).build())
                    .build());
        }
    }

    private void addDataToPowerUsageData(final com.alliander.osgp.oslp.Oslp.PowerUsageData.Builder powerUsageData,
            final Map<String, String[]> requestMap, final Integer currentItem) {
        final String[] meterTypeArray = requestMap.get(PlatformPubliclightingKeys.METER_TYPE);
        if (meterTypeArray != null && meterTypeArray.length > 0
                && !Strings.isNullOrEmpty(meterTypeArray[currentItem])) {
            final MeterType meterType = MeterType.valueOf(meterTypeArray[currentItem]);
            powerUsageData.setMeterType(meterType);
        }

        final String[] totalLightingHoursArray = requestMap.get(PlatformPubliclightingKeys.TOTAL_LIGHTING_HOURS);
        if (totalLightingHoursArray != null && totalLightingHoursArray.length > 0
                && !Strings.isNullOrEmpty(totalLightingHoursArray[currentItem])) {
            final Integer totalLightingHours = Integer.parseInt(totalLightingHoursArray[currentItem]);
            powerUsageData.setPsldData(PsldData.newBuilder().setTotalLightingHours(totalLightingHours).build());
        }

        final String[] recordTimeArray = requestMap.get(PlatformPubliclightingKeys.RECORD_TIME);
        if (recordTimeArray != null && recordTimeArray.length > 0
                && !Strings.isNullOrEmpty(recordTimeArray[currentItem])) {
            final String recordTime = DateTime.parse(recordTimeArray[currentItem]).toDateTime(DateTimeZone.UTC)
                    .toString("yyyyMMddHHmmss");
            if ((!recordTime.isEmpty())) {
                powerUsageData.setRecordTime(recordTime);
            }
        }
    }

    private void addRelayDataToSsldData(final com.alliander.osgp.oslp.Oslp.SsldData.Builder ssldData,
            final Map<String, String[]> requestMap, final Integer currentItem) {
        final String relayData = requestMap.get(PlatformPubliclightingKeys.RELAY_DATA)[currentItem];
        if (relayData != null && !relayData.isEmpty()) {
            for (final String data : relayData.split(PlatformKeys.SEPARATOR_SEMICOLON)) {
                final String[] dataParts = data.split(PlatformKeys.SEPARATOR_COMMA);

                final RelayData r = RelayData.newBuilder()
                        .setIndex(OslpUtils.integerToByteString(Integer.parseInt(dataParts[0])))
                        .setTotalLightingMinutes(Integer.parseInt(dataParts[1])).build();
                ssldData.addRelayData(r);
            }
        }
    }

    public String getOslpSignature() {
        return this.oslpSignature;
    }

    public String getOslpSignatureProvider() {
        return this.oslpSignatureProvider;
    }

    public Message waitForResponse() {
        return Wait.untilAndReturn(() -> {
            if (this.receivedResponses.isEmpty()) {
                throw new Exception("no response yet");
            }

            return this.receivedResponses.get(0);
        });
    }

    public void doNextSequenceNumber() {
        this.channelHandler.doGetNextSequence();
    }
}
