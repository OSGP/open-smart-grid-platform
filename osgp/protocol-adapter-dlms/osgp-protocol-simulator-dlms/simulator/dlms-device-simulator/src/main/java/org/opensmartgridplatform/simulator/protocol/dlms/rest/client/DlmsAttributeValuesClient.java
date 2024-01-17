// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.rest.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;

public class DlmsAttributeValuesClient {

  private static final Object DYNAMIC_ATTRIBUTES_PATH = "/dynamic";

  private static final String JSON_TYPE_FIELD = "type";
  private static final String JSON_VALUE_FIELD = "value";

  private static final Map<String, Function<JsonNode, DataObject>> JSON_TO_DATA_OBJECT_CONVERTERS =
      new HashMap<>();

  static {
    JSON_TO_DATA_OBJECT_CONVERTERS.put("null-data", json -> DataObject.newNullData());
    JSON_TO_DATA_OBJECT_CONVERTERS.put("dont-care", json -> DataObject.newNullData());
    JSON_TO_DATA_OBJECT_CONVERTERS.put("bcd", json -> DataObject.newBcdData(asByteExact(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "enumerate", json -> DataObject.newEnumerateData(asIntExact(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "integer", json -> DataObject.newInteger8Data(asByteExact(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "unsigned", json -> DataObject.newUInteger8Data(asShortExact(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "long-integer", json -> DataObject.newInteger16Data(asByteExact(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "long-unsigned", json -> DataObject.newUInteger16Data(asShortExact(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "double-long", json -> DataObject.newInteger32Data(asIntExact(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "double-long-unsigned", json -> DataObject.newUInteger32Data(asLongExact(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "long64", json -> DataObject.newInteger64Data(asLongExact(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "long64-unsigned", json -> DataObject.newUInteger64Data(asLongExact(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "float32", json -> DataObject.newFloat32Data(json.floatValue()));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "float64", json -> DataObject.newFloat64Data(json.doubleValue()));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "bit-string", json -> DataObject.newBitStringData(asBitString(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "octet-string", json -> DataObject.newOctetStringData(asByteArray(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "utf8-string", json -> DataObject.newUtf8StringData(asUtf8Bytes(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "visible-string", json -> DataObject.newVisibleStringData(asAsciiBytes(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "array", json -> DataObject.newArrayData(asDataObjectList(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put(
        "structure", json -> DataObject.newStructureData(asDataObjectList(json)));
    JSON_TO_DATA_OBJECT_CONVERTERS.put("boolean", json -> DataObject.newBoolData(json.asBoolean()));
  }

  private final WebClient webClient;

  /**
   * Construct a DlmsAttributeValuesClient instance.
   *
   * @param baseAddress The base address or URL for the DlmsAttributeValuesClient.
   */
  public DlmsAttributeValuesClient(final String baseAddress) {
    this.webClient = this.configureWebClient(baseAddress);
  }

  private static byte asByteExact(final JsonNode valueNode) {
    return valueNode.bigIntegerValue().byteValueExact();
  }

  private static short asShortExact(final JsonNode valueNode) {
    return valueNode.bigIntegerValue().shortValueExact();
  }

  private static int asIntExact(final JsonNode valueNode) {
    return valueNode.bigIntegerValue().intValueExact();
  }

  private static long asLongExact(final JsonNode valueNode) {
    return valueNode.bigIntegerValue().longValueExact();
  }

  private static BitString asBitString(final JsonNode valueNode) {
    String bitStringText = valueNode.textValue();
    if (bitStringText == null) {
      throw new IllegalArgumentException(
          "JSON node for BitString must have a text value: " + valueNode);
    }
    final int numBits = bitStringText.length();
    if (numBits == 0) {
      return new BitString(new byte[0], 0);
    }
    if (numBits % 8 != 0) {
      final String trailingZerosFormat = "%0" + (8 - (numBits % 8)) + "d";
      bitStringText = bitStringText + String.format(trailingZerosFormat, 0);
    }
    final int numBytes = bitStringText.length() / 8;
    final byte[] bitStringBytes = new byte[numBytes];
    for (int i = 0; i < numBytes; i++) {
      final String bytePart = bitStringText.substring(i * 8, (i + 1) * 8);
      bitStringBytes[i] = Byte.parseByte(bytePart, 2);
    }
    return new BitString(bitStringBytes, numBits);
  }

  private static byte[] asByteArray(final JsonNode valueNode) {
    final String octetStringText = valueNode.textValue();
    if (octetStringText == null) {
      throw new IllegalArgumentException(
          "JSON node for OctetString must have a text value: " + valueNode);
    }
    try {
      return Hex.decodeHex(octetStringText.toCharArray());
    } catch (final DecoderException e) {
      throw new IllegalArgumentException(
          "JSON node for OctetString must have Hex text value: " + valueNode, e);
    }
  }

  private static byte[] asUtf8Bytes(final JsonNode valueNode) {
    final String textValue = valueNode.textValue();
    if (textValue == null) {
      throw new IllegalArgumentException(
          "JSON node for Utf8String must have a text value: " + valueNode);
    }
    return textValue.getBytes(StandardCharsets.UTF_8);
  }

  private static byte[] asAsciiBytes(final JsonNode valueNode) {
    final String textValue = valueNode.textValue();
    if (textValue == null) {
      throw new IllegalArgumentException(
          "JSON node for VisibleString must have a text value: " + valueNode);
    }
    return textValue.getBytes(StandardCharsets.US_ASCII);
  }

  private static List<DataObject> asDataObjectList(final JsonNode valueNode) {
    if (!valueNode.isArray()) {
      throw new IllegalArgumentException(
          "JSON node for complex data must be an array: " + valueNode);
    }
    final List<DataObject> dataObjectList = new ArrayList<>();
    final Iterator<JsonNode> elements = valueNode.elements();
    while (elements.hasNext()) {
      final JsonNode element = elements.next();
      if (!element.isObject()) {
        throw new IllegalArgumentException(
            "JSON node for complex data must be an array of object nodes: " + valueNode);
      }
      dataObjectList.add(jsonNodeAsDataObject((ObjectNode) element));
    }
    return dataObjectList;
  }

  private static DataObject jsonNodeAsDataObject(final ObjectNode dataObjectNode) {
    final String type = getType(dataObjectNode);
    final Function<JsonNode, DataObject> converter = JSON_TO_DATA_OBJECT_CONVERTERS.get(type);
    if (converter == null) {
      throw new IllegalArgumentException(
          "DataObject of specified type not (yet) supported from JSON: "
              + dataObjectNode.toString());
    }
    final JsonNode valueNode = getValueNode(dataObjectNode);
    return converter.apply(valueNode);
  }

  private static String getType(final ObjectNode dataObjectNode) {
    final JsonNode typeNode = dataObjectNode.get(JSON_TYPE_FIELD);
    if (typeNode == null || !typeNode.isTextual()) {
      throw new AssertionError(
          "DLMS attribute value should have a \""
              + JSON_TYPE_FIELD
              + "\" text field: "
              + dataObjectNode.toString());
    }
    return typeNode.asText();
  }

  private static JsonNode getValueNode(final ObjectNode dataObjectNode) {
    final JsonNode valueNode = dataObjectNode.get(JSON_VALUE_FIELD);
    if (valueNode == null) {
      throw new AssertionError(
          "DLMS attribute value should have a \""
              + JSON_VALUE_FIELD
              + "\" field: "
              + dataObjectNode.toString());
    }
    return valueNode;
  }

  public DataObject getDlmsAttributeValue(
      final int classId, final ObisCode obisCode, final int attributeId)
      throws DlmsAttributeValuesClientException {

    final Response response =
        this.getWebClientInstance()
            .path(DYNAMIC_ATTRIBUTES_PATH)
            .path(classId)
            .path(obisCode.asDecimalString())
            .path(attributeId)
            .get();

    this.checkResponse(response);

    if (Status.NO_CONTENT.getStatusCode() == response.getStatus()) {
      return null;
    }

    final ObjectNode dataObjectNode = response.readEntity(ObjectNode.class);
    return DlmsAttributeValuesClient.jsonNodeAsDataObject(dataObjectNode);
  }

  public void setDlmsAttributeValue(
      final int classId,
      final ObisCode obisCode,
      final Integer attributeId,
      final DataObject attributeValue)
      throws DlmsAttributeValuesClientException {

    final ObjectNode attributeNode = this.dataObjectAsJsonNode(attributeValue);
    final Response response =
        this.getWebClientInstance()
            .path(DYNAMIC_ATTRIBUTES_PATH)
            .path(classId)
            .path(obisCode.asDecimalString())
            .path(attributeId)
            .put(attributeNode);

    this.checkResponse(response);
  }

  private void throwIfDataObjectIsNotSupportedAsJson(final DataObject dataObject) {
    if ((dataObject.isCosemDateFormat() || DataObject.Type.COMPACT_ARRAY == dataObject.getType())) {
      throw new IllegalArgumentException(
          "DataObject values of type " + dataObject.getType() + " are not yet supported");
    }
  }

  private ObjectNode dataObjectAsJsonNode(final DataObject dataObject) {
    this.throwIfDataObjectIsNotSupportedAsJson(dataObject);

    final JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);
    final ObjectNode attributeNode = jsonNodeFactory.objectNode();
    this.setType(attributeNode, dataObject.getType());

    if (dataObject.isNumber()) {
      this.setNumericalValue(attributeNode, dataObject);
    } else if (dataObject.isBitString()) {
      this.setBitStringValue(attributeNode, dataObject);
    } else if (dataObject.isByteArray()) {
      this.setByteArrayValue(attributeNode, dataObject);
    } else if (dataObject.isComplex()) {
      this.setComplexValue(attributeNode, dataObject);
    }
    /*
     * No need to check for dataObject isNull. Just don't set a value for
     * null-data, and return the attributeNode with only the type.
     */

    return attributeNode;
  }

  private void setType(final ObjectNode attributeNode, final DataObject.Type type) {
    attributeNode.set(
        JSON_TYPE_FIELD, TextNode.valueOf(type.name().toLowerCase(Locale.UK).replace('_', '-')));
  }

  private void setNumericalValue(final ObjectNode attributeNode, final DataObject dataObject) {
    if (!dataObject.isNumber()) {
      throw new IllegalArgumentException("DataObject must be numerical: " + dataObject);
    }
    final Number numberValue = dataObject.getValue();
    final String numberText = String.valueOf(numberValue);
    final JsonNode valueNode;
    if (this.hasDecimalNumberType(dataObject)) {
      valueNode = DecimalNode.valueOf(new BigDecimal(numberText));
    } else {
      valueNode = BigIntegerNode.valueOf(new BigInteger(numberText));
    }
    attributeNode.set(JSON_VALUE_FIELD, valueNode);
  }

  private boolean hasDecimalNumberType(final DataObject dataObject) {
    /*
     * The only floating number types are FLOAT32 and FLOAT64. The other
     * numerical types can be expressed as BigInteger values.
     */
    return dataObject.isNumber() && dataObject.getType().name().startsWith("FLOAT");
  }

  private void setBitStringValue(final ObjectNode attributeNode, final DataObject dataObject) {
    if (!dataObject.isBitString()) {
      throw new IllegalArgumentException("DataObject must be bit-string: " + dataObject);
    }
    final BitString bitStringValue = dataObject.getValue();
    final byte[] bitStringBytes = bitStringValue.getBitString();
    final StringBuilder sb = new StringBuilder();
    for (final byte b : bitStringBytes) {
      sb.append(String.format("%08d", Integer.parseInt(Integer.toBinaryString(b & 0xFF))));
    }
    final JsonNode valueNode = TextNode.valueOf(sb.substring(0, bitStringValue.getNumBits()));
    attributeNode.set(JSON_VALUE_FIELD, valueNode);
  }

  private void setByteArrayValue(final ObjectNode attributeNode, final DataObject dataObject) {
    if (!dataObject.isByteArray()) {
      throw new IllegalArgumentException("DataObject must have a byte-array value: " + dataObject);
    }
    final byte[] bytes = dataObject.getValue();
    final String textValue;
    if (DataObject.Type.VISIBLE_STRING == dataObject.getType()) {
      textValue = new String(bytes, StandardCharsets.US_ASCII);
    } else if (DataObject.Type.UTF8_STRING == dataObject.getType()) {
      textValue = new String(bytes, StandardCharsets.UTF_8);
    } else {
      textValue = Hex.encodeHexString(bytes);
    }
    final JsonNode valueNode = TextNode.valueOf(textValue);
    attributeNode.set(JSON_VALUE_FIELD, valueNode);
  }

  private void setComplexValue(final ObjectNode attributeNode, final DataObject dataObject) {
    if (!dataObject.isComplex()) {
      throw new IllegalArgumentException("DataObject must be complex: " + dataObject);
    }
    if (dataObject.getType() == DataObject.Type.COMPACT_ARRAY) {
      throw new IllegalArgumentException(
          "DataObject values of type " + dataObject.getType() + " are not yet supported");
    }
    final List<JsonNode> jsonElements = new ArrayList<>();
    final List<DataObject> elements = dataObject.getValue();
    for (final DataObject element : elements) {
      jsonElements.add(this.dataObjectAsJsonNode(element));
    }
    final JsonNode valueNode = new ArrayNode(new JsonNodeFactory(false), jsonElements);
    attributeNode.set(JSON_VALUE_FIELD, valueNode);
  }

  private WebClient getWebClientInstance() {
    return WebClient.fromClient(this.webClient)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON + ";charset=utf-8");
  }

  private void checkResponse(final Response response) throws DlmsAttributeValuesClientException {

    if (response == null) {
      throw new DlmsAttributeValuesClientException("Response is null");
    }

    final StatusType status = response.getStatusInfo();
    if (Status.Family.SUCCESSFUL != status.getFamily()) {
      throw new DlmsAttributeValuesClientException(
          "Response is not SuccessFul: "
              + status.getFamily()
              + "("
              + status.getStatusCode()
              + ") - "
              + status.getReasonPhrase());
    }
  }

  private WebClient configureWebClient(final String baseAddress) {

    final List<Object> providers = new ArrayList<>();
    providers.add(new JacksonJaxbJsonProvider());

    final WebClient client = WebClient.create(baseAddress, providers);

    final ClientConfiguration config = WebClient.getConfig(client);
    final HTTPConduit conduit = config.getHttpConduit();

    conduit.setTlsClientParameters(new TLSClientParameters());
    /*
     * Client for simulator in use with test code only! For now don't check
     * or verify any certificates here.
     */
    conduit
        .getTlsClientParameters()
        .setTrustManagers(
            new TrustManager[] {
              new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                  return new X509Certificate[0];
                }

                @Override
                @SuppressWarnings("squid:S4830")
                public void checkServerTrusted(
                    final X509Certificate[] chain, final String authType) {
                  /*
                   * Implicitly trust the certificate chain by not throwing a
                   * CertificateException.
                   */
                }

                @Override
                @SuppressWarnings("squid:S4830")
                public void checkClientTrusted(
                    final X509Certificate[] chain, final String authType) {
                  /*
                   * Implicitly trust the certificate chain by not throwing a
                   * CertificateException.
                   */
                }
              }
            });

    return client;
  }
}
