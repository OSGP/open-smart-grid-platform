package org.opensmartgridplatform.adapter.ws.da.presentation.ws;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetMeasurementReportRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetMeasurementReportResponse;

public class MonitoringEndpointTest {

    private MonitoringEndpoint endpoint = new MonitoringEndpoint();

    @Test
    public void printGetMeasurementReportResponse() throws Exception {
        final GetMeasurementReportResponse response = this.endpoint.getMeasurementReport("test-org",
                new GetMeasurementReportRequest());

        final JAXBContext jaxbContext = JAXBContext.newInstance(GetMeasurementReportResponse.class);
        final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        final StringWriter writer = new StringWriter();
        jaxbMarshaller.marshal(response, writer);
        final String xml = writer.toString();
        this.prettyPrintXml(xml);
    }

    private void prettyPrintXml(final String xml) throws TransformerException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final int INDENT = 4;
        transformerFactory.setAttribute("indent-number", INDENT);

        final Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        final StringWriter writer = new StringWriter();
        final StreamResult xmlOutput = new StreamResult(writer);

        final Source xmlInput = new StreamSource(new StringReader(xml));
        transformer.transform(xmlInput, xmlOutput);

        System.out.println(xmlOutput.getWriter().toString());
    }

}
