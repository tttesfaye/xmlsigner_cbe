package org.ips.xml.signer.xmlsigner.utils;
import javax.xml.namespace.QName;

public class Constants {

    private Constants(){}
    public static final String SECUREMENT_ACTION_TRANSFORMER_EXCLUSION = "AppHdr";
    public static final String SECUREMENT_ACTION_EXCLUSION = "Document";
    public static final QName BAH_NAME = new QName("urn:iso:std:iso:20022:tech:xsd:head.001.001.03", SECUREMENT_ACTION_TRANSFORMER_EXCLUSION);
    public static final QName DOCUMENT_NAME = new QName("urn:iso:std:iso:20022:tech:xsd:pacs.008.001.10", SECUREMENT_ACTION_EXCLUSION);
    public static final QName WS_SECURITY_NAME = new QName("urn:iso:std:iso:20022:tech:xsd:head.001.001.03", "Sgntr","document");
    public static final String SECUREMENT_ACTION_SEPARATOR = " | ";
    public static final String DS_NS = "http://www.w3.org/2000/09/xmldsig#";
    public static final String SIGNATURE_LOCAL_NAME = "Signature";

    public static final String CERTEFICATE_SERIAL_NUMBER   = "423714158842744355552720259575245055968935949";
    public static final String  CERTEFICATE_ISSUER_NAME  = "CN=TEST ETS IPS Issuing CA, O=EthSwitch, C=ET";
    public static final String  EXECULISIO_CANNONICAL  = "http://www.w3.org/2001/10/xml-exc-c14n#";



    public static final String  ETSI_SIGNER_URI  = "http://uri.etsi.org/01903/v1.3.2#SignedProperties";
}