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
import com.google.protobuf.Descriptors
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.lang.reflect.Method

/**
 * Class for generating Wrapper classes for LND API.
 *
 * Created by philip on 2017-12-04.
 */
class WrapperClassGenerator extends DefaultTask{

    List protocols

    String compileClasses = "build/classes/java/main"

    @TaskAction
    def generate() {

        for(String protocol : protocols) {
            ProtocolSettings protocolSettings = new ProtocolSettings(protocol: protocol)

            Descriptors.FileDescriptor descriptor = getAPIFileDescriptor(protocolSettings)

            createOutputDir(protocolSettings)

            descriptor.enumTypes.each {
                ClassGenerator.genEnum(it, protocolSettings)
            }
            descriptor.messageTypes.each {
                ClassGenerator.genClass(it, protocolSettings)
            }

            ClassGenerator.genJaxbIndex(descriptor, protocolSettings.resourcesOutputDir)

            ClassGenerator.genPackageInfo(protocolSettings)

            ApiGenerator.generateBlockingAPIs(protocolSettings, compileClasses, descriptor)

        }

    }

    private Descriptors.FileDescriptor getAPIFileDescriptor(ProtocolSettings protocolSettings){
        // Load  Class
        def ncl = new GroovyClassLoader()
        ncl.addClasspath(compileClasses)
        Class c = ncl.loadClass(protocolSettings.getAPIClassPath())

        // Call getDescriptor
        Method m = c.getMethod("getDescriptor")
        return  m.invoke(null)
    }

    private File createOutputDir(ProtocolSettings protocolSettings){
        File dir = new File(protocolSettings.messageOutputDir)
        dir.mkdirs()
        return dir
    }
}
