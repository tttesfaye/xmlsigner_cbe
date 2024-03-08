/*
 * Copyright (c) 2020 Mastercard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This class provide the sign and verify methods for XML document digital signature as per ISO20022 standards.
 */

package org.ips.xml.signer.xmlsigner.utils;


import lombok.Setter;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.ips.xml.signer.xmlsigner.context.DSNamespaceContext;
import org.ips.xml.signer.xmlsigner.crypto.CerteficateAndKeysUtility;
import org.ips.xml.signer.xmlsigner.info.SignatureInfo;
import org.ips.xml.signer.xmlsigner.info.SignatureKeyInfo;
import org.ips.xml.signer.xmlsigner.resolvers.XmlSignBAHResolver;
import org.ips.xml.signer.xmlsigner.resolvers.XmlSignDocumentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.ips.xml.signer.xmlsigner.context.Constants.*;

@Service
@Setter
public class XmlSignUtil {

    private static final Logger LOG = LoggerFactory.getLogger(XmlSignUtil.class);

    private static final String EXPRESSION;
    private final String expression = String.format("//*[local-name()='%s']", "SignedProperties");

    private static Set<String> securementActionSet = new HashSet<>(Arrays.asList(SECUREMENT_SINATURE_INFO_EXCLUSION, SECUREMENT_KEY_INFO_EXCLUSION, SECUREMENT_ACTION_EXCLUSION));
    @Autowired
    private CerteficateAndKeysUtility certeficateAndKeysUtility;

    public XmlSignUtil() {
    }

    static {
        org.apache.xml.security.Init.init();
        StringBuilder securementActionBuffer = new StringBuilder();
        for (String securementAction : securementActionSet) {
            securementActionBuffer.append(String.format("//*[local-name()='%s']", securementAction));
            securementActionBuffer.append(String.format("%s", SECUREMENT_ACTION_SEPARATOR));
        }
        String returnValue = securementActionBuffer.toString();
        EXPRESSION = returnValue.substring(0, returnValue.length() - SECUREMENT_ACTION_SEPARATOR.length());
    }

    /**
     * Sign the xml Document
     *
     * @param document         - teh unsigned document payload
     * @param signatureInfo    - signature info which will used in signing xml payload
     * @param signatureKeyInfo - signature key info which hold private key and ski bytes to be set in X509 Data
     * @return - the signed xml document
     */
    public Document sign(Document document, SignatureInfo signatureInfo, SignatureKeyInfo signatureKeyInfo) throws XMLSecurityException, XPathExpressionException {
        final NodeList bahNodes = document.getElementsByTagNameNS(BAH_NAME.getNamespaceURI(), BAH_NAME.getLocalPart());
        if (bahNodes.getLength() == 0) {
            LOG.error("No BAH element is provided in request");
            throw new SecurityException("No BAH element is provided in request");
        }
        Element bahElement = (Element) bahNodes.item(0);

        Element sgntrElement = document.createElementNS(WS_SECURITY_NAME.getNamespaceURI(), WS_SECURITY_NAME.getLocalPart());

        Element dsObject = document.createElementNS(DS_NS, "ds:Object");
        Element QualifyingProperties = document.createElementNS(XADES_QUALIFYING_PROPERTIES_NAME.getNamespaceURI(), XADES_QUALIFYING_PROPERTIES_NAME.getPrefix() + ":" + XADES_QUALIFYING_PROPERTIES_NAME.getLocalPart());
        Element SignedProperties = document.createElementNS(XADES_SIGNED_PROPERTIES_NAME.getNamespaceURI(), XADES_QUALIFYING_PROPERTIES_NAME.getPrefix() + ":" + XADES_SIGNED_PROPERTIES_NAME.getLocalPart());
        Element SignedSignatureProperties = document.createElementNS(XADES_SIGNED_SIG_PROPERTIES_NAME.getNamespaceURI(), XADES_QUALIFYING_PROPERTIES_NAME.getPrefix() + ":" + XADES_SIGNED_SIG_PROPERTIES_NAME.getLocalPart());
        Element SigningTime = document.createElementNS(XADES_SIGNED_SIGN_TIME_NAME.getNamespaceURI(), XADES_QUALIFYING_PROPERTIES_NAME.getPrefix() + ":" + XADES_SIGNED_SIGN_TIME_NAME.getLocalPart());
        SigningTime.setTextContent("2023-11-06T11:12:25Z");
        dsObject.appendChild(QualifyingProperties);
        QualifyingProperties.appendChild(SignedProperties);
        SignedProperties.appendChild(SignedSignatureProperties);
        SignedSignatureProperties.appendChild(SigningTime);
        sgntrElement.setPrefix("document");
        bahElement.appendChild(sgntrElement);

        final XMLSignature xmlSignature = new XMLSignature(document,
                BAH_NAME.getNamespaceURI(),
                signatureInfo.getSignatureMethodAlgorithm(),
                signatureInfo.getSignatureCanonicalizationMethodAlgorithm());
        xmlSignature.getElement().appendChild(dsObject);
        sgntrElement.appendChild(xmlSignature.getElement());

        xmlSignature.addResourceResolver(new XmlSignBAHResolver());
        xmlSignature.addResourceResolver(new XmlSignDocumentResolver(document));

        KeyInfo ki = xmlSignature.getKeyInfo();

        ki.add(new X509Data(document));
        X509Certificate key = certeficateAndKeysUtility.getStoredCerteficate();
        BigInteger serialNumber = key.getSerialNumber();
        String issuer = key.getIssuerX500Principal().getName();
        ki.itemX509Data(0).addIssuerSerial(issuer, serialNumber);


        XPathFactory xpf = new net.sf.saxon.xpath.XPathFactoryImpl();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());
        NodeList elementsToSign = (NodeList) xpath.evaluate(EXPRESSION, document, XPathConstants.NODESET);
        for (int i = 0; i < elementsToSign.getLength(); i++) {
            Element elementToSign = (Element) elementsToSign.item(i);
            String elementName = elementToSign.getLocalName();
            String id = UUID.randomUUID().toString();
            Transforms transforms = getSecurementTransformer(document);
            if (SECUREMENT_SINATURE_INFO_EXCLUSION.equals(elementName)) {

                transforms.addTransform(signatureInfo.getSignatureExclusionTransformer());
                elementToSign.setAttributeNS(null, "Id", id);
                elementToSign.setIdAttributeNS(null, "Id", true);
                xmlSignature.addDocument("#" + id, transforms, signatureInfo.getAppHdrReferenceSignInfo().getDigestMethodAlgorithm(), null, "http://uri.etsi.org/01903/v1.3.2#SignedProperties");
            } else if (SECUREMENT_ACTION_EXCLUSION.equals(elementName)) {
                transforms.addTransform(signatureInfo.getDocumentReferenceSignInfo().getTransformAlgorithm());
                xmlSignature.addDocument(null, transforms, signatureInfo.getDocumentReferenceSignInfo().getDigestMethodAlgorithm());
            } else {
                transforms.addTransform(signatureInfo.getKeyReferenceSignInfo().getTransformAlgorithm());
                elementToSign.setAttributeNS(null, "Id", id);
                elementToSign.setIdAttributeNS(null, "Id", true);

                xmlSignature.addDocument("#" + id, transforms, signatureInfo.getKeyReferenceSignInfo().getDigestMethodAlgorithm());
            }
        }

        xmlSignature.sign(signatureKeyInfo.getPrivateKey());
        return document;
    }


    /**
     * Verify the signed document with supplied public key
     *
     * @param document  - the signed payload
     * @param publicKey - the public key
     * @return - result true if sign verification is success otherwise false
     */
    public boolean verify(Document document, PublicKey publicKey) throws XMLSecurityException {
        Element signatureElementInMessage = (Element) document.getElementsByTagNameNS(DS_NS, SIGNATURE_LOCAL_NAME).item(0);
        engineResolveURI(document);
        XMLSignature signature = new XMLSignature(signatureElementInMessage, document.getBaseURI(), false);
        signature.addResourceResolver(new XmlSignDocumentResolver(document));
        signature.addResourceResolver(new XmlSignBAHResolver());
        return signature.checkSignatureValue(publicKey);
    }

    private static Transforms getSecurementTransformer(Document envelopeAsDocument) {
        return new Transforms(envelopeAsDocument);
    }

    public void engineResolveURI(Document doc) {
        Element selectedElem;
        NodeList documentNodes;
        try {
            XPathFactory xpf = new net.sf.saxon.xpath.XPathFactoryImpl();
            XPath xpath = xpf.newXPath();
            xpath.setNamespaceContext(new DSNamespaceContext());
            documentNodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
        } catch (Exception e) {
            throw new SecurityException("Error occurred in document resolver:", e);
        }
        selectedElem = (Element) documentNodes.item(0);
        selectedElem.setIdAttributeNS(null, "Id", true);
    }
}