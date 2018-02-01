/************************************************************************
 *                                                                       *
 *  LightningJ                                                           *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU General Public License          *
 *  License as published by the Free Software Foundation; either         *
 *  version 3 of the License, or any later version.                      *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.xml.bind.JAXBContext
import javax.xml.bind.SchemaOutputResolver
import javax.xml.transform.Result
import javax.xml.transform.stream.StreamResult
import java.lang.reflect.Method


/**
 * Gradle task for generating XSD from generated JAXB annotations by WrapperClassGenerator
 *
 * Created by Philip Vendil.
 */
class XSDGenerator extends DefaultTask{

    def classpath

    String compileClasses = "build/classes/java/main"

    String generatedResourcesDir = "build/resources/main"

    String systemId = "http://SomeURL"

    String jaxbSrcDirectory = "org.lightningj.lnd.wrapper.message"

    String schemaName = "lnd_v1.xsd"

    @TaskAction
    def generate() {

        JAXBContext jaxbContext = getJAXBContext()
        ByteArraySchemaOutputResolver sor = new ByteArraySchemaOutputResolver()
        jaxbContext.generateSchema(sor)

        new File(generatedResourcesDir+ "/" + schemaName).write(new String(sor.bytes,"UTF-8"))

    }

    JAXBContext getJAXBContext(){

        List classPaths = classpath.split(":")
        def ncl = new GroovyClassLoader(this.class.classLoader)
        classPaths.each {
            ncl.addClasspath(it)
        }
        ncl.addClasspath(compileClasses)
        ncl.addClasspath(generatedResourcesDir)
        URL f = ncl.getResource("org/lightningj/lnd/wrapper/message/jaxb.index")
        Class c = ncl.loadClass("javax.xml.bind.JAXBContext")

        Method m = c.getMethod("newInstance",String.class,ClassLoader.class)
        return  m.invoke(null,jaxbSrcDirectory,ncl)
    }



    class ByteArraySchemaOutputResolver extends SchemaOutputResolver {

        ByteArrayOutputStream baos = new ByteArrayOutputStream()

        Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            StreamResult result = new StreamResult(baos)
            result.setSystemId(systemId)
            return result
        }

        byte[] getBytes(){
            return baos.toByteArray()
        }

    }
}
