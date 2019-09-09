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


/**
 * Class for generating Wrapper classes for LND API.
 *
 * Created by philip on 2017-12-04.
 */
class WrapperClassGenerator extends DefaultTask{

    List protocols



    @TaskAction
    def generate() {

        ProtocolManager.init(project, protocols)

        for(String protocol : protocols) {
            ProtocolSettings protocolSettings = new ProtocolSettings(project: project, protocol: protocol)

            Descriptors.FileDescriptor descriptor = protocolSettings.getAPIFileDescriptor()

            createOutputDir(protocolSettings)

            descriptor.enumTypes.each {
                ClassGenerator.genEnum(it, protocolSettings)
            }
            descriptor.messageTypes.each {
                ClassGenerator.genClass(it, protocolSettings)
            }

            ClassGenerator.genJaxbIndex(protocolSettings, descriptor)

            ClassGenerator.genPackageInfo(protocolSettings)

            ApiGenerator.generateBlockingAPIs(protocolSettings, ProtocolManager.compileClasses, descriptor)

        }

    }

    private File createOutputDir(ProtocolSettings protocolSettings){
        File dir = project.file(protocolSettings.messageOutputDir)
        dir.mkdirs()
        return dir
    }
}
