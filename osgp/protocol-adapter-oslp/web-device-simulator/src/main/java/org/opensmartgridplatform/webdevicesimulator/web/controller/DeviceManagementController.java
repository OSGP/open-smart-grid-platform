// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.web.controller;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Resource;
import org.apache.commons.lang3.ArrayUtils;
import org.opensmartgridplatform.oslp.Oslp.DeviceType;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.webdevicesimulator.application.services.DeviceManagementService;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.DeviceMessageStatus;
import org.opensmartgridplatform.webdevicesimulator.service.RegisterDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SuppressWarnings(
    "squid:S3753") // setting sessionStatus as complete is not necessary in the web device simulator
@SessionAttributes
public class DeviceManagementController extends AbstractController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementController.class);

  protected static final String DEVICES_URL = "/devices";
  protected static final String DEVICE_CREATE_URL = "/devices/create";
  protected static final String DEVICE_EDIT_URL = "/devices/edit/{deviceId}";

  protected static final String DEVICES_VIEW = "devices/list";
  protected static final String DEVICE_CREATE_VIEW = "devices/create";
  protected static final String DEVICE_EDIT_VIEW = "devices/edit";

  protected static final String COMMAND_REGISTER_URL = "/devices/commands/register";
  protected static final String COMMAND_REGISTER_CONFIRM_URL = "/devices/commands/register/confirm";
  protected static final String COMMAND_SENDNOTIFICATION_URL = "/devices/commands/sendnotification";
  protected static final String COMMAND_GET_SEQUENCE_NUMBER_URL =
      "/devices/commands/get-sequence-number";
  protected static final String COMMAND_SET_SEQUENCE_NUMBER_URL =
      "/devices/commands/set-sequence-number";
  protected static final String DEVICES_JSON_URL = "/devices/json";

  protected static final String DEVICE_REGISTRATION_CHECK_JSON_URL =
      "/devices/deviceRegistrationCheck/json";
  protected static final String DEVICE_REBOOT_CHECK_JSON_URL = "/devices/deviceRebootCheck/json";
  protected static final String TARIFF_SWITCHING_CHECK_JSON_URL =
      "/devices/tariffSwitchingCheck/json";
  protected static final String LIGHT_SWITCHING_CHECK_JSON_URL =
      "/devices/lightSwitchingCheck/json";
  protected static final String EVENT_NOTIFICATION_CHECK_JSON_URL =
      "/devices/eventNotificationCheck/json";

  protected static final String REBOOT_DELAY_SECONDS_JSON_URL = "/devices/rebootDelaySeconds/json";

  protected static final String DEVICE_REGISTRATION_CHECK_URL = "/devices/deviceRegistrationCheck";
  protected static final String DEVICE_REBOOT_CHECK_URL = "/devices/deviceRebootCheck";
  protected static final String TARIFF_SWITCHING_CHECK_URL = "/devices/tariffSwitchingCheck";
  protected static final String LIGHT_SWITCHING_CHECK_URL = "/devices/lightSwitchingCheck";
  protected static final String EVENT_NOTIFICATION_CHECK_URL = "/devices/eventNotificationCheck";

  protected static final String REBOOT_DELAY_SECONDS_URL = "/devices/rebootDelaySeconds";

  protected static final String MODEL_ATTRIBUTE_DEVICES = "devices";
  protected static final String MODEL_ATTRIBUTE_ISAUTONOMOUS = "isAutonomous";
  protected static final String MODEL_ATTRIBUTE_DEVICE = "device";
  protected static final String MODEL_ATTRIBUTE_DEVICETYPES = "deviceTypes";
  protected static final String MODEL_ATTRIBUTE_LINKTYPES = "linkTypes";

  protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_CREATED =
      "feedback.message.device.created";
  protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_UPDATED =
      "feedback.message.device.updated";
  protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_REGISTERED =
      "feedback.message.device.registered";
  protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_REGISTERED_CONFIRM =
      "feedback.message.device.registered.confirm";
  protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_TIMEOUT =
      "feedback.message.device.timeout";
  protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_ERROR = "feedback.message.device.error";
  protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_ERROR_CREATE_DEVICE_DUPLICATE =
      "feedback.message.device.error.createdevice.duplicate";
  protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_ERROR_CREATE_DEVICE_NO_ID =
      "feedback.message.device.error.createdevice.noid";
  protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_ERROR_CREATE_DEVICE_NO_DEVICE_TYPE =
      "feedback.message.device.error.createdevice.nodevicetype";

  private static final String FEEDBACK_MESSAGE_KEY_EVENTNOTIFICATION_SENT =
      "feedback.message.eventnotification.sent";

  private static final String FAILURE_URL = "failure";

  private static final int PAGING_LIMITER_MIN = 5;
  private static final int PAGING_LIMITER_MAX = 10;

  protected static final String MODEL_ATTRIBUTE_DEVICE_IDENTIFICATION = "deviceIdentification";
  protected static final String MODEL_ATTRIBUTE_PAGE_TOTAL = "numberOfPages";
  protected static final String MODEL_ATTRIBUTE_PAGE_BEGIN = "pageBegin";
  protected static final String MODEL_ATTRIBUTE_PAGE_END = "pageEnd";
  protected static final String MODEL_ATTRIBUTE_PAGE_CURRENT = "pageCurrent";
  protected static final String MODEL_ATTRIBUTE_DEVICES_PER_PAGE = "devicesPerPage";
  protected static final String MODEL_ATTRIBUTE_SORT_DIRECTION = "currentSortDirection";

  protected static final String DEFAULT_SORT_DIRECTION = "DESC";

  private final Random byteGenerator = new SecureRandom();

  private static final String DEFAULT_FIRMWARE_VERSION = "R01";

  @Resource private DeviceManagementService deviceManagementService;

  @Autowired private RegisterDevice registerDevice;

  @GetMapping(value = DEVICES_URL)
  public String showDevices(
      @RequestParam(value = "devicesPerPage", required = false, defaultValue = "20")
          final Integer devicesPerPage,
      @RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT_DIRECTION)
          final String sort,
      final Model model) {
    this.fetchData(model, null, null, devicesPerPage, sort);

    return DEVICES_VIEW;
  }

  @GetMapping(value = "/devices/{pageNumber}")
  public String showPageOFDevices(
      @PathVariable final Integer pageNumber,
      @RequestParam(value = "devicesPerPage", required = false, defaultValue = "20")
          final Integer devicesPerPage,
      @RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT_DIRECTION)
          final String sort,
      final Model model) {
    this.fetchData(model, null, pageNumber, devicesPerPage, sort);

    return DEVICES_VIEW;
  }

  @GetMapping(value = "/devices/{deviceIdentification}/{pageNumber}")
  public String showPageOFDevices(
      @PathVariable final String deviceIdentification,
      @PathVariable final Integer pageNumber,
      @RequestParam(value = "devicesPerPage", required = false, defaultValue = "20")
          final Integer devicesPerPage,
      @RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT_DIRECTION)
          final String sort,
      final Model model) {
    this.fetchData(model, deviceIdentification, pageNumber, devicesPerPage, sort);

    return DEVICES_VIEW;
  }

  private void fetchData(
      final Model model,
      final String deviceIdentification,
      final Integer pageNumber,
      final Integer devicesPerPage,
      final String sortDirection) {
    final int pageNr;
    if (pageNumber == null) {
      pageNr = 1;
    } else {
      pageNr = pageNumber;
    }

    final Page<Device> page =
        this.deviceManagementService.findPageOfDevices(
            deviceIdentification, pageNr - 1, devicesPerPage, sortDirection);

    // Set the correct model elements
    final int current = pageNr;
    final int begin = Math.max(1, current - PAGING_LIMITER_MIN);
    final int end = Math.min(begin + PAGING_LIMITER_MAX, page.getTotalPages());

    model.addAttribute(MODEL_ATTRIBUTE_DEVICE_IDENTIFICATION, deviceIdentification);
    model.addAttribute(MODEL_ATTRIBUTE_PAGE_TOTAL, page.getTotalPages());
    model.addAttribute(MODEL_ATTRIBUTE_PAGE_BEGIN, begin);
    model.addAttribute(MODEL_ATTRIBUTE_PAGE_END, end);
    model.addAttribute(MODEL_ATTRIBUTE_PAGE_CURRENT, current);

    model.addAttribute(MODEL_ATTRIBUTE_DEVICES_PER_PAGE, devicesPerPage);
    model.addAttribute(MODEL_ATTRIBUTE_SORT_DIRECTION, sortDirection);
    model.addAttribute(MODEL_ATTRIBUTE_DEVICES, page.getContent());

    final List<String> deviceTypes = new ArrayList<>();
    for (final DeviceType devicetype : DeviceType.values()) {
      deviceTypes.add(devicetype.name());
    }
    model.addAttribute(MODEL_ATTRIBUTE_DEVICETYPES, deviceTypes);

    LOGGER.debug(
        "Fetched data: {} records, {} pages", page.getContent().size(), page.getTotalPages());
  }

  private ResponseEntity<String> badRequestResponse(final Object request) {
    LOGGER.warn("Bad Request: {}", request);
    return ResponseEntity.badRequest().build();
  }

  @PostMapping(value = DEVICE_REGISTRATION_CHECK_URL)
  public ResponseEntity<String> setDeviceRegistrationValue(
      @RequestBody final AutonomousRequest request) {
    if (request == null || request.getAutonomousStatus() == null) {
      return this.badRequestResponse(request);
    }

    this.deviceManagementService.setdeviceRegistration(request.getAutonomousStatus());

    return ResponseEntity.ok().build();
  }

  @GetMapping(value = DEVICE_REGISTRATION_CHECK_JSON_URL)
  @ResponseBody
  public Boolean getDeviceRegistrationState() {
    return this.deviceManagementService.getDevRegistration();
  }

  @PostMapping(value = DEVICE_REBOOT_CHECK_URL)
  public ResponseEntity<String> setDeviceRebootValue(@RequestBody final AutonomousRequest request) {
    if (request == null || request.getAutonomousStatus() == null) {
      return this.badRequestResponse(request);
    }

    this.deviceManagementService.setDeviceReboot(request.getAutonomousStatus());

    return ResponseEntity.ok().build();
  }

  @GetMapping(value = DEVICE_REBOOT_CHECK_JSON_URL)
  @ResponseBody
  public Boolean getDeviceRebootState() {
    return this.deviceManagementService.getDevReboot();
  }

  @PostMapping(value = TARIFF_SWITCHING_CHECK_URL)
  public ResponseEntity<String> setTariffSwitchingValue(
      @RequestBody final AutonomousRequest request) {
    if (request == null || request.getAutonomousStatus() == null) {
      return this.badRequestResponse(request);
    }

    this.deviceManagementService.setTariffSwitching(request.getAutonomousStatus());

    return ResponseEntity.ok().build();
  }

  @GetMapping(value = TARIFF_SWITCHING_CHECK_JSON_URL)
  @ResponseBody
  public Boolean getTariffSwitchingState() {
    return this.deviceManagementService.getTariffSwitching();
  }

  @PostMapping(value = LIGHT_SWITCHING_CHECK_URL)
  public ResponseEntity<String> setLightSwitchingValue(
      @RequestBody final AutonomousRequest request) {
    if (request == null || request.getAutonomousStatus() == null) {
      return this.badRequestResponse(request);
    }

    this.deviceManagementService.setLightSwitching(request.getAutonomousStatus());

    return ResponseEntity.ok().build();
  }

  @GetMapping(value = LIGHT_SWITCHING_CHECK_JSON_URL)
  @ResponseBody
  public Boolean getLightSwitchingState() {
    return this.deviceManagementService.getLightSwitching();
  }

  @PostMapping(value = EVENT_NOTIFICATION_CHECK_URL)
  public ResponseEntity<String> setEventNotificationValue(
      @RequestBody final AutonomousRequest request) {
    if (request == null || request.getAutonomousStatus() == null) {
      return this.badRequestResponse(request);
    }

    this.deviceManagementService.setEventNotification(request.getAutonomousStatus());

    return ResponseEntity.ok().build();
  }

  @GetMapping(value = EVENT_NOTIFICATION_CHECK_JSON_URL)
  @ResponseBody
  public Boolean getEventNotificationState() {
    return this.deviceManagementService.getEventNotification();
  }

  @PostMapping(value = REBOOT_DELAY_SECONDS_URL)
  public ResponseEntity<String> setRebootDelaySeconds(@RequestBody final DelayRequest request) {
    if (request == null || request.getDelay() == null || request.getDelay() < 0) {
      return this.badRequestResponse(request);
    }

    this.deviceManagementService.setRebootDelay(request.getDelay());

    return ResponseEntity.ok().build();
  }

  @GetMapping(value = REBOOT_DELAY_SECONDS_JSON_URL)
  @ResponseBody
  public int getRebootDelaySeconds() {
    return this.deviceManagementService.getRebootDelay();
  }

  @GetMapping(value = DEVICE_CREATE_URL)
  public String showCreateDevice(final Model model) {
    model.addAttribute(MODEL_ATTRIBUTE_DEVICE, new Device());

    return DEVICE_CREATE_VIEW;
  }

  private byte[] createRandomDeviceUid() {
    // Generate random bytes for UID
    final byte[] deviceUid = new byte[OslpEnvelope.DEVICE_ID_LENGTH];
    this.byteGenerator.nextBytes(deviceUid);
    // Combine manufacturer id of 2 bytes (1 is AME) and device UID of 10
    // bytes.
    return ArrayUtils.addAll(new byte[] {0, 1}, deviceUid);
  }

  @PostMapping(value = DEVICE_CREATE_URL)
  public String createDevice(
      @SuppressWarnings("squid:S4684") @ModelAttribute(MODEL_ATTRIBUTE_DEVICE)
          final Device
              created, // webdevicesimulator doesn't have to be as strict in this Sonar issue
      final BindingResult bindingResult,
      final RedirectAttributes attributes) {

    if (bindingResult.hasErrors()) {
      return DEVICE_CREATE_VIEW;
    }

    created.setDeviceUid(this.createRandomDeviceUid());
    created.setFirmwareVersion(DEFAULT_FIRMWARE_VERSION);

    final Device device;
    try {
      // Store device
      device = this.deviceManagementService.addDevice(created);
      this.addFeedbackMessage(
          attributes, FEEDBACK_MESSAGE_KEY_DEVICE_CREATED, device.getDeviceIdentification());
    } catch (final Exception e) {
      LOGGER.error("Error creating device", e);
      this.setErrorFeedbackMessage(created, attributes);
    }

    return this.createRedirectViewPath(DEVICES_URL);
  }

  @GetMapping(value = DEVICE_EDIT_URL)
  public String showEditDevice(@PathVariable final Long deviceId, final Model model) {
    model.addAttribute(MODEL_ATTRIBUTE_DEVICE, this.deviceManagementService.findDevice(deviceId));
    return DEVICE_EDIT_VIEW;
  }

  @PostMapping(value = DEVICE_EDIT_URL)
  public String editDevice(
      // web device simulator doesn't have to be as strict in this Sonar issue
      @SuppressWarnings("squid:S4684") @ModelAttribute(MODEL_ATTRIBUTE_DEVICE) final Device updated,
      @PathVariable final Long deviceId,
      final BindingResult bindingResult,
      final RedirectAttributes attributes,
      final Model model) {

    if (!bindingResult.hasErrors()) {
      this.updateDevice(updated, deviceId);
      this.addFeedbackMessage(
          attributes, FEEDBACK_MESSAGE_KEY_DEVICE_UPDATED, updated.getDeviceIdentification());
      model.addAttribute(MODEL_ATTRIBUTE_DEVICE, updated);
    }

    return DEVICE_EDIT_VIEW;
  }

  private void updateDevice(final Device updated, final Long deviceId) {
    // Find device
    final Device deviceToUpdate = this.deviceManagementService.findDevice(deviceId);
    if (deviceToUpdate != null) {

      // Update data
      deviceToUpdate.setIpAddress(updated.getIpAddress());
      deviceToUpdate.setDeviceType(updated.getDeviceType());
      deviceToUpdate.setActualLinkType(updated.getActualLinkType());
      deviceToUpdate.setTariffOn(updated.isTariffOn());
      deviceToUpdate.setProtocol(updated.getProtocol());
      deviceToUpdate.setFirmwareVersion(updated.getFirmwareVersion());

      // Store device
      this.deviceManagementService.updateDevice(deviceToUpdate);
    }
  }

  @PostMapping(value = COMMAND_REGISTER_URL)
  @ResponseBody
  public String sendRegisterDeviceCommand(@RequestBody final RegisterDeviceRequest request) {
    // Find device
    final Device device = this.deviceManagementService.findDevice(request.getDeviceId());
    final DeviceMessageStatus status =
        this.registerDevice.sendRegisterDeviceCommand(
            request.getDeviceId(), request.getHasSchedule());
    if (status == DeviceMessageStatus.OK) {
      return this.getFeedbackMessage(
          FEEDBACK_MESSAGE_KEY_DEVICE_REGISTERED,
          device.getDeviceIdentification(),
          this.registerDevice.getCurrentTime());
    } else if (status == DeviceMessageStatus.FAILURE) {
      return this.getFeedbackMessage(
          FEEDBACK_MESSAGE_KEY_DEVICE_ERROR,
          device.getDeviceIdentification(),
          this.registerDevice.getErrorMessage());
    } else {
      return FAILURE_URL;
    }
  }

  @PostMapping(value = COMMAND_REGISTER_CONFIRM_URL)
  @ResponseBody
  public String sendConfirmDeviceRegistrationCommand(
      @RequestBody final ConfirmDeviceRegistrationRequest request) {

    // Find device
    final Device device = this.deviceManagementService.findDevice(request.getDeviceId());
    final DeviceMessageStatus status =
        this.registerDevice.sendConfirmDeviceRegistrationCommand(request.getDeviceId());
    if (status == DeviceMessageStatus.OK) {
      return this.getFeedbackMessage(
          FEEDBACK_MESSAGE_KEY_DEVICE_REGISTERED_CONFIRM, device.getDeviceIdentification());
    } else if (status == DeviceMessageStatus.FAILURE) {
      return this.getFeedbackMessage(
          FEEDBACK_MESSAGE_KEY_DEVICE_ERROR,
          device.getDeviceIdentification(),
          this.registerDevice.getErrorMessage());
    } else {
      return FAILURE_URL;
    }
  }

  @PostMapping(value = COMMAND_SENDNOTIFICATION_URL)
  @ResponseBody
  public String sendEventNotificationCommandAll(
      @RequestBody final SendEventNotificationRequest request) {

    // Find device
    final Device device = this.deviceManagementService.findDevice(request.getDeviceId());
    final DeviceMessageStatus status =
        this.registerDevice.sendEventNotificationCommand(
            request.getDeviceId(),
            request.getEvent(),
            request.getDescription(),
            request.getIndex(),
            request.getHasTimestamp());
    if (status == DeviceMessageStatus.OK) {
      return this.getFeedbackMessage(
          FEEDBACK_MESSAGE_KEY_EVENTNOTIFICATION_SENT, device.getDeviceIdentification());
    } else if (status == DeviceMessageStatus.FAILURE) {
      return this.getFeedbackMessage(
          FEEDBACK_MESSAGE_KEY_DEVICE_ERROR,
          device.getDeviceIdentification(),
          this.registerDevice.getErrorMessage());
    } else {
      return FAILURE_URL;
    }
  }

  @GetMapping(value = DEVICES_JSON_URL)
  @ResponseBody
  public List<Device> getLightStates() {
    return this.deviceManagementService.findAllDevices();
  }

  @PostMapping(value = COMMAND_GET_SEQUENCE_NUMBER_URL)
  @ResponseBody
  public Integer getSequenceNumber(
      @RequestBody final GetSequenceNumberRequest getSequenceNumberRequest) {
    final Long deviceId = getSequenceNumberRequest.getDeviceId();

    final Device device = this.deviceManagementService.findDevice(deviceId);
    if (device != null) {
      return device.getSequenceNumber();
    } else {
      return -1;
    }
  }

  @PostMapping(value = COMMAND_SET_SEQUENCE_NUMBER_URL)
  @ResponseBody
  public Integer setSequenceNumber(
      @RequestBody final SetSequenceNumberRequest setSequenceNumberRequest) {
    final Long deviceId = setSequenceNumberRequest.getDeviceId();
    final Integer sequenceNumber = setSequenceNumberRequest.getSequenceNumber();

    Device device = this.deviceManagementService.findDevice(deviceId);
    if (device != null) {
      device.setSequenceNumber(sequenceNumber);
      device = this.deviceManagementService.updateDevice(device);
      return device.getSequenceNumber();
    } else {
      return -1;
    }
  }

  /** Set error messages for device. */
  private void setErrorFeedbackMessage(final Device device, final RedirectAttributes attributes) {
    if (device.getDeviceIdentification() == null) {
      this.addErrorMessage(attributes, FEEDBACK_MESSAGE_KEY_DEVICE_ERROR_CREATE_DEVICE_NO_ID);
    } else if (device.getDeviceType() == null) {
      this.addErrorMessage(
          attributes,
          FEEDBACK_MESSAGE_KEY_DEVICE_ERROR_CREATE_DEVICE_NO_DEVICE_TYPE,
          device.getDeviceIdentification());
    } else {
      this.addErrorMessage(
          attributes,
          FEEDBACK_MESSAGE_KEY_DEVICE_ERROR_CREATE_DEVICE_DUPLICATE,
          device.getDeviceIdentification());
    }
  }
}
