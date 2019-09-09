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
import org.gradle.api.Project

import java.lang.reflect.Method

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

/**
 * Static class managing all configured protocols to provide lookups
 * when other apis is imported in the main API.
 */
class ProtocolManager {

    final static String compileClasses = "build/classes/java/main"

    static Map<String,Descriptors.FileDescriptor> fileDescriptorMap
    static Map<String,ProtocolSettings> protocolMap

    static Project project

    /**
     * Initializes the ProtocolManager, should be called before
     * any methods is called.
     * @param project, reference to callin project.
     * @param protocols a list of enabled protocols
     */
    static void init(Project project, List<String> protocols){
        this.project = project
        fileDescriptorMap = [:]
        protocolMap = [:]
        for(String protocol : protocols){
            ProtocolSettings settings = new ProtocolSettings(project: project, protocol: protocol)
            fileDescriptorMap[protocol] = genAPIFileDescriptor(settings)
            protocolMap[protocol] = settings
        }
    }

    /**
     * Returns the FileDescriptor for the given protocol.
     */
    static Descriptors.FileDescriptor getAPIFileDescriptor(String protocol){
        return fileDescriptorMap[protocol]
    }

    /**
     * Method that goes through all enabled protocols to find related name.
     *
     * @param messageName the message name to look up related descriptor.
     * @return the first found Descriptor or null if no Descriptor found.
     */
    static Descriptors.Descriptor allProtocolFindMessageTypeByName(String messageName){
        Descriptors.Descriptor retval
        for(Descriptors.FileDescriptor descriptor : fileDescriptorMap.values()){
            retval = descriptor.findMessageTypeByName(messageName)
            if(retval != null){
                break
            }
        }

        return retval
    }

    /**
     * Mehtod that finds API class path name with stripped path.
     * @param name the name of the inner class.
     * @return the related API class path name.
     */
    static String allProtocolsStrippedName(String name){
        for(ProtocolSettings settings : protocolMap.values()) {
            if (name.contains(settings.getAPIClassPath())) {
                // return name for protocols importing lnrpc
                name = name.replace(settings.getAPIClassPath()+"\$", "")
            }
        }
        return name
    }

    private static Descriptors.FileDescriptor genAPIFileDescriptor(ProtocolSettings settings){
        // Load  Class
        def ncl = new GroovyClassLoader()
        File file = project.file(compileClasses)
        def path = file.absolutePath
        ncl.addClasspath(path)
        Class c = ncl.loadClass(settings.getAPIClassPath())

        // Call getDescriptor
        Method m = c.getMethod("getDescriptor")
        return  m.invoke(null)
    }

}
