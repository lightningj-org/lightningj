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

import static ApiGenerator.*
/**
 * Class containing settings for an api point to be generated defined in protocol settings.
 *
 *
 * Created by Philip Vendil on 2019-03-30.
 */
class ApiSettings {

    String baseGrpcClassPath
    String grpcClassName
    String baseApiClassName
    String baseStubClass
    String baseProtoClassPath
    String baseFileName

    String getGrpcClassPath(String type){
        if(type == TYPE_SYNCHRONOUS){
            return baseGrpcClassPath + "BlockingStub"
        }
        return baseGrpcClassPath + "Stub"
    }

    String getApiClassName(String type){
        if(type == TYPE_SYNCHRONOUS){
            return "Synchronous" + baseApiClassName
        }
        return "Asynchronous" + baseApiClassName
    }

    String getStubClass(String type){
        if(type == TYPE_SYNCHRONOUS){
            return baseStubClass + "BlockingStub"
        }
        return baseStubClass + "Stub"
    }

    String getFileName(String type){
        if(type == TYPE_SYNCHRONOUS){
            return "Synchronous" + baseFileName
        }
        return "Asynchronous" + baseFileName
    }
}
