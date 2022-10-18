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
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.SchemaOutputResolver
import javax.xml.transform.Result
import javax.xml.transform.stream.StreamResult
import java.lang.reflect.Method


/**
 * Gradle task for generating XSD from generated JAXB annotations by WrapperClassGenerator
 *
 * Created by Philip Vendil.
 */
class XSDGenerator extends DefaultTask{

    @Input
    List protocols

    @Input
    def classpath

    @Internal
    String compileClasses = "build/classes/java/main"

    @Internal
    String generatedResourcesDir = "build/resources/main"

    @Internal
    String systemId = "http://SomeURL"

    @TaskAction
    def generate() {
        for(String protocol : protocols) {
            ProtocolSettings protocolSettings = new ProtocolSettings(protocol: protocol)

            JAXBContext jaxbContext = getJAXBContext(protocolSettings)
            ByteArraySchemaOutputResolver sor = new ByteArraySchemaOutputResolver(protocolSettings.getXMLNameSpace())
            jaxbContext.generateSchema(sor)

            project.file(generatedResourcesDir+ "/" + protocolSettings.getXSDName()).write(new String(sor.bytes,"UTF-8"))
        }
    }

    JAXBContext getJAXBContext(ProtocolSettings protocolSettings){
        List classPaths = classpath.split(":")
        def ncl = new GroovyClassLoader(this.class.classLoader)
        classPaths.each {
            ncl.addClasspath(findProjectClassPath(it))
        }
        ncl.addClasspath(findProjectClassPath(compileClasses))
        ncl.addClasspath(findProjectClassPath(generatedResourcesDir))
        URL f = ncl.getResource(findProjectClassPath(protocolSettings.getJAXBIndexResouceLocation()))
        Class c = ncl.loadClass("jakarta.xml.bind.JAXBContext")

        Method m = c.getMethod("newInstance",String.class,ClassLoader.class)
        return  m.invoke(null,protocolSettings.getJaxbSrcDirectory(),ncl)
    }

    String findProjectClassPath(String classpath){
        File f = project.file(classpath)
        return f.absolutePath
    }



    class ByteArraySchemaOutputResolver extends SchemaOutputResolver {

        String expectedNameSpace

        ByteArraySchemaOutputResolver(String expectedNameSpace){
           this.expectedNameSpace = expectedNameSpace
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream()

        Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            if(namespaceURI == expectedNameSpace) {
                StreamResult result = new StreamResult(baos)
                result.setSystemId(systemId)
                return result
            }else{
                StreamResult result = new StreamResult(new ByteArrayOutputStream())
                result.setSystemId(getImportXSDReference(namespaceURI))
                return result
            }

        }

        byte[] getBytes(){
            return baos.toByteArray()
        }

    }

    private String getImportXSDReference(String namespaceURI){
        if(namespaceURI == "http://lightningj.org/xsd/lndjapi_1_0"){
            return "./lnd_v1.xsd"
        }
        return "./" + namespaceURI.split("/").last().replaceAll("_1_0","_v1.xsd")
    }
}
