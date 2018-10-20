/************************************************************************
 *                                                                       *
 *  LightningJ                                                           *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public License   *
 *  (LGPL-3.0-or-later)                                                  *
 *  License as published by the Free Software Foundation; either         *
 *  version 3 of the License, or any later version.                      *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.lightningj.lnd.wrapper;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Class containing help methods to marshall and unmarshall XML data to messages.
 *
 * Created by Philip Vendil.
 */
public abstract class XMLParser {

    /**
     *
     * @return return the version of the related Lnd API.
     */
    protected abstract String getVersion();

    /**
     *
     * @return the resource location of the related schema.
     */
    protected abstract String getSchemaLocation();

    /**
     *
     * @return the JAXB class path used for JAXBContext separated with ':'
     */
    protected abstract String getJAXBClassPath();

    /**
     * Method to unmarshall a byte[] xml document into a Message.
     * <p>
     *     When unmarshalling is the supplied data validated against XSD Schema.
     * </p>
     *
     * @param xmlData the XML data to convert into Message Object
     * @return the converted message object.
     * @throws JAXBException if problems occurred converting the object from
     * XML to Message.
     */
    public Message unmarshall(byte[] xmlData) throws JAXBException{
        Object retval = getUnmarshaller().unmarshal(new ByteArrayInputStream(xmlData));
        if(retval instanceof Message){
            return (Message) retval;
        }
        throw new JAXBException("Invalid XML message type, expected LND Message but got object of type: " + retval.getClass().getSimpleName());
    }

    // TODO Schema location

    /**
     * Method to convert a Message into byte array.
     *
     * @param message the message to convert into XML data
     * @return byte array XML representation (UTF-8).
     * @throws JAXBException if problems occurred converting
     * the object from message into XML data.
     * @throws IllegalArgumentException if supplied message was null or otherwise illegal
     */
    public byte[] marshall(Message message) throws JAXBException, IllegalArgumentException{
        return marshall(message,false);
    }

    /**
     * Method to convert a Message into byte array with option to
     * have the output formatted into indented (pretty print) format.
     *
     * @param message the message to convert into XML data.
     * @param prettyPrint if the XML output should be in pretty print format.
     * @return byte array XML representation (UTF-8).
     * @throws JAXBException if problems occurred converting
     * the object from message into XML data.
     * @throws IllegalArgumentException if supplied message was null or otherwise illegal
     */
    public byte[] marshall(Message message, boolean prettyPrint) throws JAXBException, IllegalArgumentException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(prettyPrint){
            getPrettyPrintMarshaller().marshal(message,baos);
        }else{
            getMarshaller().marshal(message,baos);
        }
        return baos.toByteArray();
    }


    /**
     * Loads related XSD schema into Schema object.
     * @return generated schema used for validation of XML objects.
     * @throws SAXException if loading of schema failed.
     */
    public Schema getSchema() throws SAXException {
        URL xsdURL = getClass().getResource(getSchemaLocation());
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = schemaFactory.newSchema(xsdURL);

        return schema;
    }

    /**
     * Help method to get related XSD schema as byte array.
     * @return a byte array representation of schema (UTF-8)
     * @throws IOException if problems occurred reading the schema.
     */
    public byte[] getSchemaData() throws IOException{
        InputStream is = getClass().getResourceAsStream(getSchemaLocation());
        byte[] data = new byte[is.available()];
        is.read(data);
        return data;
    }

    /**
     * Help method maintaining the JAXB Context.
     */
    private JAXBContext jaxbContext = null;
    protected JAXBContext getJAXBContext() throws JAXBException{
        if(jaxbContext== null){
            jaxbContext = JAXBContext.newInstance(getJAXBClassPath());
        }
        return jaxbContext;
    }


    private Unmarshaller unmarshaller = null;
    protected Unmarshaller getUnmarshaller() throws JAXBException{
        if(unmarshaller == null){
            unmarshaller = getJAXBContext().createUnmarshaller();
            try {
                unmarshaller.setSchema(getSchema());
            }catch(SAXException e){
                throw new JAXBException("Error generating XML Schema: " + e.getMessage(),e);
            }
        }
        return unmarshaller;
    }

    private Marshaller marshaller = null;
    protected Marshaller getMarshaller() throws JAXBException{
        if(marshaller == null){
            marshaller = createMarshaller();
        }
        return marshaller;
    }

    private Marshaller prettyPrintMarshaller = null;
    protected Marshaller getPrettyPrintMarshaller() throws JAXBException{
        if(prettyPrintMarshaller == null){
            prettyPrintMarshaller = createMarshaller();
            prettyPrintMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        }
        return prettyPrintMarshaller;
    }

    private Marshaller createMarshaller() throws JAXBException{
        Marshaller retval = getJAXBContext().createMarshaller();
        try {
            retval.setSchema(getSchema());
        }catch(SAXException e){
            throw new JAXBException("Error generating XML Schema: " + e.getMessage(),e);
        }
        return retval;
    }


}
