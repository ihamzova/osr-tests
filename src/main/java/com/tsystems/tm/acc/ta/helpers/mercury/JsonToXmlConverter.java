package com.tsystems.tm.acc.ta.helpers.mercury;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonToXmlConverter {
		public static String convertPslJsonToXml(String fileName, String stringJson) throws Exception {
				log.info("ConverterUtil::convertPslJsonToXml stringJson = {}", stringJson);

				String stringXml;
				Document docXml;
				StringBuilder stringBuilderXml;

				try {
						stringBuilderXml = new StringBuilder()
										.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
										.append("<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">")
										.append("<SOAP:Header/>")
										.append("<SOAP:Body>")
										.append("<ns1:MT_EQDATA_RESPV2 xmlns:ns1=\"http://telekom.de/xi/NGERP_EXTD_PSL_INT/global/WMS/Equipment/Read/V2\">")
										.append("<eiMessageContext>")
										.append("<target xmlns=\"http://messaging.ei.tmobile.net/datatypes\">SI_ReadEquipmentResponseV2In_DigiOSS</target>")
										.append("<requestId xmlns=\"http://messaging.ei.tmobile.net/datatypes\">urn:uuid:").append(UUID.randomUUID()).append("</requestId>")
										.append("<timeLeft xmlns=\"http://messaging.ei.tmobile.net/datatypes\">172800000</timeLeft>")
										.append("<priority xmlns=\"http://messaging.ei.tmobile.net/datatypes\">4</priority>")
										.append("<sender xmlns=\"http://messaging.ei.tmobile.net/datatypes\">sndr:tmo.erp.sap.architecture.SAP:PI</sender>")
										.append("<correlationId xmlns=\"http://messaging.ei.tmobile.net/datatypes\">").append(UUID.randomUUID()).append("</correlationId>")
										.append("</eiMessageContext>")
										.append(XML.toString(new JSONObject(loadFile(fileName, stringJson))))
										.append("</ns1:MT_EQDATA_RESPV2></SOAP:Body></SOAP:Envelope>");

				} catch (IOException | JSONException ex) {
						log.error("ConverterUtil::convertPslJsonToXml:Error: convert to JSON failed with exception: {}", ex.getMessage());
						throw new Exception(ex.getMessage());
				}

				try {
						// Build xml doc from string
						stringXml = stringBuilderXml.toString();
						docXml =
										DocumentBuilderFactory.newInstance()
														.newDocumentBuilder()
														.parse(new InputSource(new StringReader(stringXml)));
				} catch (IOException | ParserConfigurationException | SAXException ex) {
						log.error("ConverterUtil::convertPslJsonToXml:Error: build xml from string failed with exception: {}", ex.getMessage());
						throw new Exception(ex.getMessage());
				}

				// rename nodes
				docXml.renameNode(docXml.getElementsByTagName("response").item(0), null, "data");
				docXml.renameNode(docXml.getElementsByTagName("responseData").item(0), null, "MT_EQDATA_RESPV2");
				asList(docXml.getElementsByTagName("equipment")).forEach(node -> docXml.renameNode(node, null, "EQUI"));
				asList(docXml.getElementsByTagName("endsz")).forEach(node -> docXml.renameNode(node, null, "ENDSZ"));
				asList(docXml.getElementsByTagName("hequi")).forEach(node -> docXml.renameNode(node, null, "HEQUI"));
				asList(docXml.getElementsByTagName("heqnr")).forEach(node -> docXml.renameNode(node, null, "HEQNR"));
				asList(docXml.getElementsByTagName("asb")).forEach(node -> docXml.renameNode(node, null, "ASB"));
				asList(docXml.getElementsByTagName("tplnr")).forEach(node -> docXml.renameNode(node, null, "TPLNR"));
				asList(docXml.getElementsByTagName("eqart")).forEach(node -> docXml.renameNode(node, null, "EQART"));
				asList(docXml.getElementsByTagName("anzEbenen")).forEach(node -> docXml.renameNode(node, null, "ANZ_EBENEN"));
				asList(docXml.getElementsByTagName("equnr")).forEach(node -> docXml.renameNode(node, null, "EQUNR"));
				asList(docXml.getElementsByTagName("serge")).forEach(node -> docXml.renameNode(node, null, "SERGE"));
				asList(docXml.getElementsByTagName("submt")).forEach(node -> docXml.renameNode(node, null, "SUBMT"));
				asList(docXml.getElementsByTagName("adrId")).forEach(node -> docXml.renameNode(node, null, "ADR_ID"));
				docXml.renameNode(docXml.getElementsByTagName("header").item(0), null, "KOPF");
				docXml.renameNode(docXml.getElementsByTagName("partner").item(0), null, "PARTNER");
				docXml.renameNode(docXml.getElementsByTagName("anfoKen").item(0), null, "ANFO_KEN");
				docXml.renameNode(docXml.getElementsByTagName("status").item(0), null, "STATUS");
				docXml.renameNode(docXml.getElementsByTagName("logNo").item(0), null, "LOG_NO");
				docXml.renameNode(docXml.getElementsByTagName("number").item(0), null, "NUMBER");
				docXml.renameNode(docXml.getElementsByTagName("system").item(0), null, "SYSTEM");
				docXml.renameNode(docXml.getElementsByTagName("logMsgNo").item(0), null, "LOG_NO");
				docXml.renameNode(docXml.getElementsByTagName("id").item(0), null, "ID");
				docXml.renameNode(docXml.getElementsByTagName("message").item(0), null, "MESSAGE");
				docXml.renameNode(docXml.getElementsByTagName("type").item(0), null, "TYPE");
				// remove <messageContext> because double
				docXml
								.getElementsByTagName("messageContext")
								.item(0)
								.getParentNode()
								.removeChild(docXml.getElementsByTagName("messageContext").item(0));
				// remove <messageV1/> because not in PSL example
				docXml
								.getElementsByTagName("messageV1")
								.item(0)
								.getParentNode()
								.removeChild(docXml.getElementsByTagName("messageV1").item(0));
				// remove <success> because not in PSL example
				docXml
								.getElementsByTagName("success")
								.item(0)
								.getParentNode()
								.removeChild(docXml.getElementsByTagName("success").item(0));
				// remove <error> because not in PSL example
				docXml
								.getElementsByTagName("error")
								.item(0)
								.getParentNode()
								.removeChild(docXml.getElementsByTagName("error").item(0));

			    // replace json path place holder with xPath
				docXml
								.getElementsByTagName("ANFO_KEN")
								.item(0)
								.setTextContent("{{xPath request.body '//data/MT_EQDATA_REQ/KOPF/ANFO_KEN/text()'}}");

				// replace json path place holder with xPath
				docXml
								.getElementsByTagName("PARTNER")
								.item(0)
								.setTextContent("{{xPath request.body '//data/MT_EQDATA_REQ/KOPF/PARTNER/text()'}}");

			    // Build string from xml doc
				StringWriter stringWriter =
								new StringWriter();
				try {
						TransformerFactory
										.newInstance()
										.newTransformer()
										.transform(new DOMSource(docXml), new StreamResult(stringWriter));
				} catch (TransformerException ex) {
						log.error("ConverterUtil::convertPslJsonToXml:Error: build string from xml failed with exception: {}", ex.getMessage());
						throw new Exception(ex.getMessage());
				}

				stringXml = stringWriter
								.toString()
								.replace("\"", "'");

				log.info("ConverterUtil::convertPslJsonToXml result stringXml = {}", stringXml);
				return stringXml;
		}

		private static String loadFile(String fileName, String stringJson) throws Exception {
				if ((Objects.isNull(fileName) || fileName.isEmpty())
								&& (Objects.isNull(stringJson) || stringJson.isEmpty() || stringJson.equals("{}"))) {
						log.error("ConverterUtil::convertPslJsonToXml:Error: no input parameter found for loadFile()");
						throw new Exception("ConverterUtil::convertPslJsonToXml:Error: no input parameter found for loadFile()");
				}

				if (Objects.isNull(fileName)) {
						return stringJson;
				} else {
						StringBuilder stringBuilder;
						File file = ResourceUtils.getFile(fileName);
						try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
								String line;
								stringBuilder = new StringBuilder();

								while ((line = bufferedReader.readLine()) != null) {
										stringBuilder.append(line.trim());
								}
						}
						return stringBuilder.toString();
				}
		}

		private static List<Node> asList(NodeList nodeList) {
				return (nodeList.getLength() == 0) ?
								Collections.<Node>emptyList() : new NodeListWrapper(nodeList);
		}

		private static final class NodeListWrapper extends AbstractList<Node> implements RandomAccess {
				private final NodeList list;

				NodeListWrapper(NodeList l) {
						list = l;
				}

				public Node get(int index) {
						return list.item(index);
				}

				public int size() {
						return list.getLength();
				}
		}
}
