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
import com.google.protobuf.GeneratedMessageV3
import groovy.text.SimpleTemplateEngine

import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * Class used to generate Synchronous and the Asynchronous API classes
 * from proto specification.
 *
 * Created by Philip Vendil.
 */
class ApiGenerator {

    public static final String TYPE_SYNCHRONOUS = "synchronous"
    public static final String TYPE_ASYNCHRONOUS = "asynchronous"

    static final Map API_TYPES = [(TYPE_SYNCHRONOUS): "SynchronousAPI", (TYPE_ASYNCHRONOUS) : "AsynchronousAPI"]

    private static Map RESERVED_NAMES = [private: "priv"]

    static def engine = new SimpleTemplateEngine()

    static void generateBlockingAPIs(String outputDir, String compileClasses, Descriptors.FileDescriptor descriptor){

        generateAPI(outputDir,compileClasses,descriptor,'org.lightningj.lnd.proto.LightningGrpc$LightningBlockingStub',
                "SynchronousLndAPI","LightningGrpc",
                "LightningBlockingStub","SynchronousLndAPI.java",
                TYPE_SYNCHRONOUS)
        generateAPI(outputDir,compileClasses,descriptor,'org.lightningj.lnd.proto.WalletUnlockerGrpc$WalletUnlockerBlockingStub',
                "SynchronousWalletUnlockerAPI","WalletUnlockerGrpc",
                "WalletUnlockerBlockingStub","SynchronousWalletUnlockerAPI.java",
                TYPE_SYNCHRONOUS)
        generateAPI(outputDir,compileClasses,descriptor,'org.lightningj.lnd.proto.LightningGrpc$LightningStub',
                "AsynchronousLndAPI","LightningGrpc",
                "LightningStub","AsynchronousLndAPI.java",
                TYPE_ASYNCHRONOUS)
        generateAPI(outputDir,compileClasses,descriptor,'org.lightningj.lnd.proto.WalletUnlockerGrpc$WalletUnlockerStub',
                "AsynchronousWalletUnlockerAPI","WalletUnlockerGrpc",
                "WalletUnlockerStub","AsynchronousWalletUnlockerAPI.java",
                TYPE_ASYNCHRONOUS)
    }

    static void generateAPI(String outputDir, String compileClasses, Descriptors.FileDescriptor descriptor,
                                    String apiClasspath, String apiClassName, String grpcClass, String stubClass,
                                    String fileName, String type){

        Class aPIClass = getClassInstance(apiClasspath,compileClasses)

        String newStubMethodName = type == TYPE_SYNCHRONOUS ? "newBlockingStub" : "newStub"

        List<String> requestMethods = []
        aPIClass.methods.each{ Method method ->
            switch(type){
                case TYPE_SYNCHRONOUS:
                    if(method.parameterTypes.size() == 1 && method.parameterTypes[0].superclass == GeneratedMessageV3.class) {
                        requestMethods << genRequestMethod(type,method,descriptor, grpcClass,stubClass)
                    }
                    break
                case TYPE_ASYNCHRONOUS:
                    Type[] genericParameterTypes = method.getGenericParameterTypes()
                    if(method.genericParameterTypes.length == 2 && genericParameterTypes[1] instanceof ParameterizedType){
                        requestMethods << genRequestMethod(type,method,descriptor, grpcClass,stubClass)
                    }
                    break
                default:
                throw new IllegalArgumentException("Invalid API type: " + type)
            }
        }

        String classDeclarationTemplate = ClassGenerator.getTemplate("API.java.template")
        String classDeclaration = engine.createTemplate(classDeclarationTemplate).make([callMethods: requestMethods.join("\n"),
                                                                                        apiClassName: apiClassName,
                                                                                        grpcClass: grpcClass,
                                                                                        stubClass: stubClass,
                                                                                        type: type,
                                                                                        apiType: API_TYPES[type],
                                                                                        newStubMethodName: newStubMethodName
        ]).toString()


        new File(outputDir + "/" + fileName).write(classDeclaration)


    }


    static Class getClassInstance(String className,String compileClasses){
        // Load LightningAPI Class
        def ncl = new GroovyClassLoader()
        ncl.addClasspath(compileClasses)
        Class c = ncl.loadClass(className)
        return c
    }

    static String genRequestMethod(String type, Method method, Descriptors.FileDescriptor descriptor, String grpcClass, String stubClass){
        List parameters = []
        List setMethodList = []

        String methodName = method.name
        String methodTemplateSource
        String responseType = ""
        String responseObserverType = ""
        String requestType = strippedName(method.parameterTypes[0])
        String observerTypeName = ""
        String observerTypeParameterName = ""
        if(type == TYPE_SYNCHRONOUS) {
            methodTemplateSource = "SynchronousMethod.java.template"
            if (method.genericReturnType instanceof ParameterizedType) {
                ParameterizedType innerTypeParam = (ParameterizedType) method.genericReturnType
                Class<?> innerTypeClass = (Class<?>) innerTypeParam.getActualTypeArguments()[0]
                responseType = strippedName(method.returnType) + "<" + strippedName(innerTypeClass) + ">"
                methodTemplateSource = "SynchronousRepeatableResponseMethod.java.template"
            } else {
                responseType = strippedName(method.returnType)
            }
        }
        if(type == TYPE_ASYNCHRONOUS) {
            methodTemplateSource = "AsynchronousMethod.java.template"
            Type[] genericParameterTypes = method.getGenericParameterTypes()
            ParameterizedType updateType = genericParameterTypes[1]
            observerTypeName = updateType.rawType.typeName
            observerTypeParameterName = strippedName(updateType.actualTypeArguments[0].typeName)
            responseObserverType = observerTypeName + "<" + observerTypeParameterName + ">"
        }


        Descriptors.Descriptor requestMessageDesc = descriptor.findMessageTypeByName(requestType)


        requestMessageDesc.fields.each{

            String fieldJavaName = ClassNameUtils.convertToJavaBean(it.name)
            String fieldJavaType = getJavaType(it)
            String fieldName = checkReservedNames(ClassNameUtils.lowerCaseFirst(fieldJavaName))


            if(it.isMapField()){
                fieldName += "s"
                def mapTypes = ClassNameUtils.getMappingTypes(it)
                String fieldKeyType = ClassNameUtils.toObject(mapTypes[0].type)
                String fieldValueType = ClassNameUtils.toObject(mapTypes[1].type)

                fieldJavaType = "Map<${fieldKeyType},${fieldValueType}>"
            }else {
                // if not map but repeated it is probably a list.
                if (it.isRepeated()) {
                    fieldJavaType = "List<${fieldJavaType}>"
                }
            }
            parameters << "${fieldJavaType} ${fieldName}"
            if(it.optional) {
                setMethodList << "if(${fieldName} != null) request.set${fieldJavaName}(${fieldName});"
            }else{
                setMethodList << "request.set${fieldJavaName}(${fieldName});"
            }
        }

        String methodDeclarationTemplate = ClassGenerator.getTemplate(methodTemplateSource)
        String methodDeclaration = engine.createTemplate(methodDeclarationTemplate).make([requestType: requestType,
                                                                                          responseType: responseType,
                                                                                          methodName: methodName,
                                                                                          setMethods: setMethodList.join("\n      "),
                                                                                          parameters: mergeParameters(type,parameters),
                                                                                          grpcClass: grpcClass,
                                                                                          stubClass: stubClass,
                                                                                          responseObserverType: responseObserverType,
                                                                                          observerTypeName: observerTypeName,
                                                                                          observerTypeParameterName: observerTypeParameterName
                                                                                          ]).toString()
        return  methodDeclaration
    }


    private static String strippedName(Class c){
        return strippedName(c.name)
    }

    private static String strippedName(String name){
        return name.replace("org.lightningj.lnd.proto.LightningApi\$", "")
    }

    private static String getJavaType(Descriptors.FieldDescriptor fieldDescriptor){
        String retval = ClassNameUtils.getType(fieldDescriptor)
        if(retval == "ByteString"){
            return "byte[]"
        }
        if(fieldDescriptor.optional){
            switch(fieldDescriptor.javaType){
                case Descriptors.FieldDescriptor.JavaType.BOOLEAN:
                    return "Boolean"
                case Descriptors.FieldDescriptor.JavaType.DOUBLE:
                    return "Double"
                case Descriptors.FieldDescriptor.JavaType.INT:
                    return "Integer"
                case Descriptors.FieldDescriptor.JavaType.FLOAT:
                    return "Float"
                case Descriptors.FieldDescriptor.JavaType.LONG:
                    return "Long"
                default:
                    return retval
            }
        }
        return retval
    }


    private static String checkReservedNames(String fieldName){
        if(RESERVED_NAMES[fieldName] != null){
            return RESERVED_NAMES[fieldName]
        }
        return fieldName
    }

    private static String mergeParameters(String type, List parameters){
        String retval = parameters.join(", ")
        if(type == TYPE_ASYNCHRONOUS && retval != ""){
            retval += ","
        }
        return retval
    }

}
