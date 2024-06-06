// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.CLOCK;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.DEFINABLE_LOAD_PROFILE;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.POWER_QUALITY_PROFILE_1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.POWER_QUALITY_PROFILE_2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;

@Slf4j
public class DlmsProfileValidator {
  private static final String PQ_ERROR = " PQ validation error: ";
  private static final String CAPTURE_OBJECTS_ERROR = " Capture objects validation error: ";
  private static final String SOURCE_OBJECTS_ERROR = " Source objects validation error: ";

  private DlmsProfileValidator() {
    // Static class
  }

  public static void validate(final List<DlmsProfile> dlmsProfiles) throws ObjectConfigException {
    final List<String> validationErrors =
        dlmsProfiles.stream()
            .map(DlmsProfileValidator::validateProfile)
            .flatMap(Collection::stream)
            .toList();

    if (!validationErrors.isEmpty()) {
      throw new ObjectConfigException(String.join("\n", validationErrors));
    }
  }

  private static List<String> validateProfile(final DlmsProfile dlmsProfile) {
    final List<String> validationErrors = new ArrayList<>();
    dlmsProfile.createMap();

    try {
      allRegistersShouldHaveAUnit(dlmsProfile, validationErrors);
      allPQProfilesShouldHaveSelectableObjects(dlmsProfile, validationErrors);
      allPqPeriodicShouldBeInAProfileSelectableObjects(dlmsProfile, validationErrors);
      allPqRequestsShouldMatchType(dlmsProfile, validationErrors);
      allCaptureObjectsShouldExist(dlmsProfile, validationErrors);
      allSourceObjectsShouldExist(dlmsProfile, validationErrors);

      return validationErrors;
    } catch (final Exception e) {
      return Collections.singletonList(
          "Exception while validating DlmsProfile "
              + dlmsProfile.getProfileWithVersion()
              + ": "
              + e.getMessage());
    }
  }

  private static void allPqRequestsShouldMatchType(
      final DlmsProfile dlmsProfile, final List<String> validationErrors) {
    final String validationError =
        dlmsProfile.getObjects().stream()
            .filter(DlmsProfileValidator::isPeriodicRequest)
            .map(DlmsProfileValidator::pqRequestShouldMatchMeterType)
            .filter(error -> !error.isEmpty())
            .collect(Collectors.joining(", "));

    if (!validationError.isEmpty()) {
      validationErrors.add(createErrorMessage(dlmsProfile, PQ_ERROR, validationError));
    }
  }

  private static void allPqPeriodicShouldBeInAProfileSelectableObjects(
      final DlmsProfile dlmsProfile, final List<String> validationErrors) {
    final String validationError =
        dlmsProfile.getObjects().stream()
            .filter(DlmsProfileValidator::isPeriodicRequest)
            .map(object -> pqObjectShouldBeInAProfileSelectableObjects(object, dlmsProfile))
            .filter(error -> !error.isEmpty())
            .collect(Collectors.joining(", "));

    if (!validationError.isEmpty()) {
      validationErrors.add(createErrorMessage(dlmsProfile, PQ_ERROR, validationError));
    }
  }

  private static boolean isPeriodicRequest(final CosemObject object) {
    return object.getProperty(ObjectProperty.PQ_REQUEST) != null
        && (object
                .getListProperty(ObjectProperty.PQ_REQUEST)
                .contains(PowerQualityRequest.PERIODIC_SP.name())
            || object
                .getListProperty(ObjectProperty.PQ_REQUEST)
                .contains(PowerQualityRequest.PERIODIC_PP.name()));
  }

  private static String pqObjectShouldBeInAProfileSelectableObjects(
      final CosemObject pqObject, final DlmsProfile dlmsProfile) {
    final List<String> selectableObjects =
        dlmsProfile.getObjects().stream()
            .filter(
                object ->
                    Arrays.asList(
                            DEFINABLE_LOAD_PROFILE.name(),
                            POWER_QUALITY_PROFILE_1.name(),
                            POWER_QUALITY_PROFILE_2.name())
                        .contains(object.getTag()))
            .flatMap(object -> object.getListProperty(ObjectProperty.SELECTABLE_OBJECTS).stream())
            .toList();

    if (!selectableObjects.contains(pqObject.getTag())) {
      return pqObject.getTag() + " cannot be found in a selectable object list of a PQ Profile";
    }
    return "";
  }

  private static String pqRequestShouldMatchMeterType(final CosemObject pqObject) {
    final List<String> pqRequests = pqObject.getListProperty(ObjectProperty.PQ_REQUEST);
    if (pqRequests.contains(PowerQualityRequest.ACTUAL_SP.name())
        && !pqObject.getMeterTypes().contains(MeterType.SP)) {
      return pqObject.getTag() + " has request ACTUAL_SP, but meter type does not have SP";
    }
    if (pqRequests.contains(PowerQualityRequest.PERIODIC_SP.name())
        && !pqObject.getMeterTypes().contains(MeterType.SP)) {
      return pqObject.getTag() + " has request PERIODIC_SP, but meter type does not have SP";
    }
    if (pqRequests.contains(PowerQualityRequest.ACTUAL_PP.name())
        && !pqObject.getMeterTypes().contains(MeterType.PP)) {
      return pqObject.getTag() + " has request ACTUAL_PP, but meter type does not have PP";
    }
    if (pqRequests.contains(PowerQualityRequest.PERIODIC_PP.name())
        && !pqObject.getMeterTypes().contains(MeterType.PP)) {
      return pqObject.getTag() + " has request PERIODIC_PP, but meter type does not have PP";
    }
    return "";
  }

  private static void allRegistersShouldHaveAUnit(
      final DlmsProfile dlmsProfile, final List<String> validationErrors) {
    final List<CosemObject> registersWithoutUnit =
        dlmsProfile.getObjects().stream()
            .filter(object -> object.getClassId() == InterfaceClass.REGISTER.id())
            .filter(object -> !registerHasScalerUnit(object))
            .toList();

    if (!registersWithoutUnit.isEmpty()) {
      final String tags =
          registersWithoutUnit.stream().map(CosemObject::getTag).collect(Collectors.joining(", "));
      validationErrors.add(
          "DlmsProfile "
              + dlmsProfile.getProfileWithVersion()
              + " register validation error: Register(s) without scaler_unit: "
              + tags);
    }
  }

  private static boolean registerHasScalerUnit(final CosemObject object) {
    return object.getAttributes().stream()
        .anyMatch(attribute -> attribute.getId() == RegisterAttribute.SCALER_UNIT.attributeId());
  }

  private static void allPQProfilesShouldHaveSelectableObjects(
      final DlmsProfile dlmsProfile, final List<String> validationErrors) {
    final String validationError =
        dlmsProfile.getObjects().stream()
            .filter(
                object ->
                    Arrays.asList(
                            DEFINABLE_LOAD_PROFILE.name(),
                            POWER_QUALITY_PROFILE_1.name(),
                            POWER_QUALITY_PROFILE_2.name())
                        .contains(object.getTag()))
            .map(object -> pqProfileShouldHaveSelectableObjects(object, dlmsProfile))
            .filter(error -> !error.isEmpty())
            .collect(Collectors.joining(", "));

    if (!validationError.isEmpty()) {
      validationErrors.add(createErrorMessage(dlmsProfile, PQ_ERROR, validationError));
    }
  }

  private static String pqProfileShouldHaveSelectableObjects(
      final CosemObject object, final DlmsProfile dlmsProfile) {
    final List<String> selectableObjects =
        object.getListProperty(ObjectProperty.SELECTABLE_OBJECTS);

    if (selectableObjects == null || selectableObjects.isEmpty()) {
      return "PQ Profile " + object.getTag() + " has no selectable objects";
    }

    return selectableObjects.stream()
        .map(tag -> selectableObjectShouldHavePQProfileDefined(tag, dlmsProfile))
        .collect(Collectors.joining(""));
  }

  private static String selectableObjectShouldHavePQProfileDefined(
      final String tagForSelectableObject, final DlmsProfile dlmsProfile) {
    if (CLOCK.name().equals(tagForSelectableObject)) {
      return "";
    }

    final Optional<CosemObject> optionalCosemObject =
        dlmsProfile.getObjects().stream()
            .filter(object -> object.getTag().equals(tagForSelectableObject))
            .findFirst();

    if (optionalCosemObject.isEmpty()) {
      return "Profile doesn't contain object for " + tagForSelectableObject;
    }

    final CosemObject selectableObject = optionalCosemObject.get();

    final String pqProfile = (String) selectableObject.getProperty(ObjectProperty.PQ_PROFILE);
    if (pqProfile == null) {
      return tagForSelectableObject + " doesn't contain PQ Profile";
    }
    final List<String> pqRequest = selectableObject.getListProperty(ObjectProperty.PQ_REQUEST);
    if (pqRequest == null) {
      return tagForSelectableObject + " doesn't contain PQ Request";
    }

    return "";
  }

  private static void allCaptureObjectsShouldExist(
      final DlmsProfile dlmsProfile, final List<String> validationErrors) {
    final String validationError =
        dlmsProfile.getObjects().stream()
            .filter(object -> object.getClassId() == InterfaceClass.PROFILE_GENERIC.id())
            .map(ObjectConfigService::getCaptureObjectDefinitions)
            .flatMap(Collection::stream)
            .map(ObjectConfigService::getCosemObjectTypeFromCaptureObjectDefinition)
            .map(tag -> objectShouldExist(dlmsProfile, tag))
            .filter(error -> !error.isEmpty())
            .collect(Collectors.joining(", "));

    if (!validationError.isEmpty()) {
      validationErrors.add(createErrorMessage(dlmsProfile, CAPTURE_OBJECTS_ERROR, validationError));
    }
  }

  private static void allSourceObjectsShouldExist(
      final DlmsProfile dlmsProfile, final List<String> validationErrors) {
    final String validationError =
        dlmsProfile.getObjects().stream()
            .filter(object -> object.getClassId() == InterfaceClass.DATA.id())
            .map(ObjectConfigService::getSourceObjectDefinitions)
            .flatMap(Collection::stream)
            .map(ObjectConfigService::getCosemObjectTypeFromCaptureObjectDefinition)
            .map(tag -> objectShouldExist(dlmsProfile, tag))
            .filter(error -> !error.isEmpty())
            .collect(Collectors.joining(", "));

    if (!validationError.isEmpty()) {
      validationErrors.add(createErrorMessage(dlmsProfile, SOURCE_OBJECTS_ERROR, validationError));
    }
  }

  private static String objectShouldExist(
      final DlmsProfile dlmsProfile, final DlmsObjectType type) {
    if (!dlmsProfile.getObjectMap().containsKey(type)) {
      return "Profile doesn't contain object for " + type.name();
    }

    return "";
  }

  private static String createErrorMessage(
      final DlmsProfile dlmsProfile, final String validationType, final String validationError) {
    return "DlmsProfile " + dlmsProfile.getProfileWithVersion() + validationType + validationError;
  }
}
