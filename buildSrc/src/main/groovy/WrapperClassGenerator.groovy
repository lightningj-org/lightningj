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

    String ligtningAPIClassPath = "org.lightningj.lnd.proto.LightningApi"

    String compileClasses = "build/classes/java/main"

    String messageOutputDir = "build/generated/source/wrapper/main/java/org/lightningj/lnd/wrapper/message/"
    String callerOutputDir = "build/generated/source/wrapper/main/java/org/lightningj/lnd/wrapper/"
    String resourcesOutputDir = "build/resources/main/org/lightningj/lnd/wrapper/message/"


    @TaskAction
    def generate() {

        Descriptors.FileDescriptor descriptor = getLightningAPIFileDescriptor()

        createOutputDir()

        descriptor.enumTypes.each {
            ClassGenerator.genEnum(it,messageOutputDir)
        }
        descriptor.messageTypes.each {
            ClassGenerator.genClass(it,messageOutputDir)
        }

        ClassGenerator.genJaxbIndex(descriptor,resourcesOutputDir)

        ClassGenerator.genPackageInfo(messageOutputDir)

        ApiGenerator.generateBlockingAPIs(callerOutputDir,compileClasses,descriptor)

    }

    private Descriptors.FileDescriptor getLightningAPIFileDescriptor(){
        // Load LightningAPI Class
        def ncl = new GroovyClassLoader()
        ncl.addClasspath(compileClasses)
        Class c = ncl.loadClass(ligtningAPIClassPath)

        // Call getDescriptor
        Method m = c.getMethod("getDescriptor")
        return  m.invoke(null)
    }

    private File createOutputDir(){
        File dir = new File(messageOutputDir)
        dir.mkdirs()
        return dir
    }
}
