// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.web.controller;

import java.util.Locale;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * An abstract controller class which provides utility methods useful to actual controller classes.
 */
@ControllerAdvice
public abstract class AbstractController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);

  private static final String FLASH_ERROR_MESSAGE = "errorMessage";
  private static final String FLASH_FEEDBACK_MESSAGE = "feedbackMessage";

  private static final String ADDING_ERROR_MESSAGE_WITH_CODE = "adding error message with code: ";
  private static final String AND_PARAMS = " and params: ";
  private static final String PARAM = "param";
  private static final String CURRENT_LOCALE = "Current locale is ";
  private static final String LOCALIZED_MESSAGE_IS = "Localized message is: ";

  private static final String VIEW_REDIRECT_PREFIX = "redirect:";

  @Resource private MessageSource messageSource;

  /**
   * Adds a new error message
   *
   * @param model A model which stores the the error message.
   * @param code A message code which is used to fetch the correct message from the message source.
   * @param params The parameters attached to the actual error message.
   */
  protected void addErrorMessage(
      final RedirectAttributes model, final String code, final Object... params) {
    LOGGER.debug(ADDING_ERROR_MESSAGE_WITH_CODE + code + AND_PARAMS);
    for (final Object param : params) {
      LOGGER.debug(PARAM + param.getClass().toString());
    }
    final Locale current = LocaleContextHolder.getLocale();
    LOGGER.debug(CURRENT_LOCALE + current);
    final String localizedErrorMessage = this.messageSource.getMessage(code, params, current);
    LOGGER.debug(LOCALIZED_MESSAGE_IS + localizedErrorMessage);
    model.addFlashAttribute(FLASH_ERROR_MESSAGE, localizedErrorMessage);
  }

  /**
   * Adds a new feedback message.
   *
   * @param model A model which stores the feedback message.
   * @param code A message code which is used to fetch the actual message from the message source.
   * @param params The parameters which are attached to the actual feedback message.
   */
  protected void addFeedbackMessage(
      final RedirectAttributes model, final String code, final Object... params) {
    LOGGER.debug(ADDING_ERROR_MESSAGE_WITH_CODE + code + AND_PARAMS);
    for (final Object param : params) {
      LOGGER.debug(PARAM + param.getClass().toString());
    }
    final Locale current = LocaleContextHolder.getLocale();
    LOGGER.debug(CURRENT_LOCALE + current);
    final String localizedFeedbackMessage = this.messageSource.getMessage(code, params, current);
    LOGGER.debug(LOCALIZED_MESSAGE_IS + localizedFeedbackMessage);
    model.addFlashAttribute(FLASH_FEEDBACK_MESSAGE, localizedFeedbackMessage);
  }

  protected String getFeedbackMessage(final String code, final Object... params) {
    LOGGER.debug(ADDING_ERROR_MESSAGE_WITH_CODE + code + AND_PARAMS);
    for (final Object param : params) {
      LOGGER.debug(PARAM + param.getClass().toString());
    }
    final Locale current = LocaleContextHolder.getLocale();
    LOGGER.debug(CURRENT_LOCALE + current);
    final String localizedFeedbackMessage = this.messageSource.getMessage(code, params, current);
    LOGGER.debug(LOCALIZED_MESSAGE_IS + localizedFeedbackMessage);
    return localizedFeedbackMessage;
  }

  /**
   * Creates a redirect view path for a specific controller action
   *
   * @param path The path processed by the controller method.
   * @return A redirect view path to the given controller method.
   */
  protected String createRedirectViewPath(final String path) {
    return VIEW_REDIRECT_PREFIX + path;
  }

  /** This method should only be used by unit tests. */
  protected void setMessageSource(final MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  /** Replace empty strings "" (default behavior) with null values on model binding */
  @InitBinder
  protected void initBinder(
      final HttpServletRequest request, final ServletRequestDataBinder binder) {
    // bind empty strings as null
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
  }
}
