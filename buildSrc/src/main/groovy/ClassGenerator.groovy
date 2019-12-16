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
import com.google.protobuf.Descriptors.Descriptor
import com.google.protobuf.Descriptors.FieldDescriptor
import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project


/**
 * File containing static metods to generate wrapped message classes files into
 * directory build/generated/source/wrapper/main/org/lightningj/wrapper/message/
 *
 * Created by Philip Vendil
 */
class ClassGenerator {



    /**
     * Main method to generate a wrapped class for a given proto class descriptor.
     */
    static void genClass(Descriptor classDescriptor, ProtocolSettings settings){
        Project project = settings.project
        String classTemplate = getTemplate("WrappedMessageClass.java.template")

        String className = classDescriptor.name
        String fields = genFields(classDescriptor, className)
        String messageType = getMessageType(classDescriptor)

        String innerEnums = genInnerEnums(settings, classDescriptor,className)
        String getterAndSetters = genGetterAndSetters(settings,classDescriptor, className)
        String innerClasses = genInnerClasses(settings, classDescriptor, className)
        String xmlType = genXMLType( classDescriptor,className)
        String populateRepeatableFields = genPopulateRepeatableFields(settings, classDescriptor,className)

        def engine = new SimpleTemplateEngine()
        String generatedClass = engine.createTemplate(classTemplate).make([
                specialAPIImports: settings.getSpecialAPIImports(),
                wrapperBasePackageName: settings.wrapperBasePackageName,
                apiClassPath: settings.getAPIClassPath(),
                apiClassName: settings.getAPIClassName(),
                className: className,
                messageType: messageType, innerEnums:innerEnums,
                fields: fields,
                getterAndSetters: getterAndSetters,
                innerClasses: innerClasses,
                xmlType: xmlType,
                populateRepeatableFields: populateRepeatableFields]).toString()

        project.file(settings.getMessageOutputDir() + className + ".java").write(generatedClass)
    }

    /**
     * Main method to generate a wrapped enum for a given proto class descriptor.
     */
    static void genEnum(Descriptors.EnumDescriptor enumDescriptor, ProtocolSettings settings){
        Project project = settings.project
        String enumTemplate = getTemplate("WrappedEnum.java.template")

        String enumName = ClassNameUtils.convertToJavaBean(enumDescriptor.name)
        String xmlType = "@XmlEnum()"
        List enumValueList = []
        enumDescriptor.values.each{ def value ->
            enumValueList << "        ${value.name}(${settings.getAPIClassPath()}.${enumName}.${value.name})"
        }
        String enumValues = enumValueList.join(",\n") + ";"

        def engine = new SimpleTemplateEngine()
        String generatedEnum = engine.createTemplate(enumTemplate).make([
                specialAPIImports: settings.getSpecialAPIImports(),
                wrapperBasePackageName: settings.wrapperBasePackageName,
                apiClassPath: settings.getAPIClassPath(),
                apiClassName: settings.getAPIClassName(),
                enumName: enumName,
                enumValues: enumValues,
                xmlType: xmlType]).toString()

        project.file(settings.getMessageOutputDir() + enumName + ".java").write(generatedEnum)
    }

    /**
     * Method to generate the package-info.java file for JAXB context.
     */
    static void genPackageInfo(ProtocolSettings settings){
        Project project = settings.project
        String template = getTemplate("package-info.java.template")
        def engine = new SimpleTemplateEngine()
        String generatedPackageInfo = engine.createTemplate(template).make([
                wrapperBasePackageName: settings.wrapperBasePackageName,
                externalNameSpaces: settings.getExternalNamespaces(),
                namespace: settings.getXMLNameSpace()]).toString()
        project.file(settings.messageOutputDir + "package-info.java").write(generatedPackageInfo)
    }

    static void genJaxbIndex(ProtocolSettings settings, Descriptors.FileDescriptor fileDescriptor){
        Project project = settings.project
        String content = ""
        fileDescriptor.messageTypes.each {
            content += it.name + "\n"
        }
        //new File(outputDir + "jaxb.index").write(content)
        File classDir = project.file(settings.resourcesOutputDir)
        classDir.mkdirs()
        project.file(settings.resourcesOutputDir + "jaxb.index").write(content)
    }

    /**
     * Method that generates available fields for given class.
     * @param classDescriptor the class descriptor
     * @return a string representation of the class fields
     */
    static String genFields(Descriptor classDescriptor, String className){
        String retval = ""
        getMapFields(classDescriptor).each{ FieldDescriptor it ->
            String typeName = ClassNameUtils.convertToJavaBean(it.messageType.name).replaceAll("Entry","Entries")
            String fieldName = ClassNameUtils.lowerCaseFirst(typeName).replaceAll("Entry","Entries")
            retval += "    protected ${className}.${typeName} ${fieldName};\n"
        }

        return retval
    }

    /**
     * Method that generates getter and setters for all fields in a message class.
     *
     * @param classDescriptor the class descriptor
     * @return a generated getter and setter template
     */
    static String genInnerEnums(ProtocolSettings settings, Descriptor classDescriptor, String className){
        String retval = ""
        classDescriptor.enumTypes.each{
            String innerEnumTemplate = getTemplate("InnerEnum.java.template")

            String enumName = ClassNameUtils.convertToJavaBean(it.name)
            String xmlType = "@XmlEnum()"
            List enumValueList = []
            it.values.each{ def value ->
                enumValueList << "        ${value.name}(${settings.getAPIClassName()}.${className}.${enumName}.${value.name})"
            }
            String enumValues = enumValueList.join(",\n") + ";"

            def engine = new SimpleTemplateEngine()
            retval += engine.createTemplate(innerEnumTemplate).make([enumName: enumName,
                                                                     enumValues: enumValues,
                                                                     className: className,
                                                                     apiClassPath: settings.getAPIClassPath(),
                                                                     apiClassName: settings.getAPIClassName(),
                                                                     xmlType: xmlType]).toString()

        }
        return retval
    }

    /**
     * Method that generates getter and setters for all fields in a message class.
     *
     * @param classDescriptor the class descriptor
     * @return a generated getter and setter template
     */
    static String genGetterAndSetters(ProtocolSettings settings, Descriptor classDescriptor, String className, String indentation=""){
        String retval = ""
        classDescriptor.fields.each{
            String getterAndSetterTemplate = getTemplate(getTemplateName(it))

            String fieldKeyType = ""
            String fieldValueType = ""
            String xmlKeyElement = ""
            String xmlValueElement = ""
            String xmlElement = ClassNameUtils.genXMLElement(it)
            String xmlElementWrapper = ClassNameUtils.genXMLElementWrapper(it)
            if(it.isMapField()){
                def mapTypes = ClassNameUtils.getMappingTypes(it)
                fieldKeyType = ClassNameUtils.toObject(mapTypes[0].type)
                xmlKeyElement = ClassNameUtils.genXMLElement(mapTypes[0].descriptor)
                fieldValueType = ClassNameUtils.toObject(mapTypes[1].type)
                xmlValueElement= ClassNameUtils.genXMLElement(mapTypes[1].descriptor)
            }

            String fieldJsonName = it.jsonName
            String fieldJavaName = ClassNameUtils.convertToJavaBean(it.name)
            String fieldJavaType = ClassNameUtils.getType(it)
            String fieldName = ClassNameUtils.lowerCaseFirst(fieldJavaName)

            def engine = new SimpleTemplateEngine()
            retval +=  engine.createTemplate(getterAndSetterTemplate).make([fieldJsonName: fieldJsonName,
                                                                 fieldJavaName: fieldJavaName,
                                                                 fieldJavaType: fieldJavaType,
                                                                 className: className,
                                                                 apiClassName: settings.getAPIClassName(),
                                                                 fieldKeyType: fieldKeyType,
                                                                 fieldValueType: fieldValueType,
                                                                 fieldName: fieldName,
                                                                 xmlKeyElement: xmlKeyElement,
                                                                 xmlValueElement: xmlValueElement,
                                                                 xmlElement: xmlElement,
                                                                 xmlElementWrapper: xmlElementWrapper]).toString()


        }
        retval = retval.split("\n").collect { indentation + it}.join("\n")
        return retval
    }

    /**
     * Method that iterates a class descriptors all inner message types that
     * will be generated as inner classes.
     */
    static String genInnerClasses(ProtocolSettings settings, Descriptor classDescriptor, String className){
        String retval = ""
        classDescriptor.getNestedTypes().each{
            if(getMapFields(classDescriptor).find{FieldDescriptor fd -> fd.messageType.fullName == it.fullName} != null){
                retval += genInnerMapClass(settings,it,className)
            }else {
                retval += genInnerClass(settings,it, className)
            }
        }
        return retval
    }

    /**
     * Method to generate a inner class file.
     */
    static String genInnerClass(ProtocolSettings settings, Descriptor innerClassDescriptor, String className){
        String innerClassTemplate = getTemplate("InnerClass.java.template")

        String innerClassName = innerClassDescriptor.name
        String fields = genFields(innerClassDescriptor, innerClassName)
        String messageType = getMessageType(innerClassDescriptor)
        String xmlType = genXMLType(innerClassDescriptor,className+innerClassName,"    ")

        String innerEnums = genInnerEnums(settings,innerClassDescriptor,className+"."+innerClassName)
        String getterAndSetters = genGetterAndSetters(settings,innerClassDescriptor, className+"."+innerClassName, "     ")
        String innerClasses = genInnerClasses(settings, innerClassDescriptor, className)
        innerClasses = innerClasses.replaceAll("\n","\n         ")
        String populateRepeatableFields = genPopulateRepeatableFields(settings,innerClassDescriptor, className+"."+innerClassName)

        def engine = new SimpleTemplateEngine()
        String generatedInnerClass = engine.createTemplate(innerClassTemplate).make([className: className,
                                                                                     fields: fields,
                                                                                     innerClassName: innerClassName,
                                                                                     innerClasses: innerClasses,
                                                                                     apiClassPath: settings.getAPIClassPath(),
                                                                                     apiClassName: settings.getAPIClassName(),
                                                                                     messageType: messageType, innerEnums:innerEnums,
                                                                                     getterAndSetters: getterAndSetters,
                                                                                     xmlType: xmlType,
                                                                                     populateRepeatableFields: populateRepeatableFields]).toString()

        return generatedInnerClass


    }

    /**
     * Method to generate teh XMLType class.
     *
     * @param classDescriptor the related class descriptor
     * @param className the name of the class to use as XML Type name default ""
     * @param indentation number of spaces to begin each line, default ""
     * @param entries an optional specific list of property names, if null is classDescriptor fields used instead.
     * @return generated XMLType annotation.
     */
    static String genXMLType(Descriptor classDescriptor, String className,String indentation="", List entries=null){
        String retval = indentation + "@XmlType(name = \"${className}Type\", propOrder = {\n"
        List params = []
        if(entries != null){
            entries.each {
                params << indentation + '    "' + it + '"'
            }
        }else {
            classDescriptor.fields.each {

                String propertyName = ClassNameUtils.convertToProperty(it.name)
                if(it.isMapField()){
                    propertyName =  propertyName + "Entries"
                }
                params << indentation + '    "' + propertyName + '"'
            }
        }
        retval += params.join(",\n") + "\n"
        retval += indentation + "})"

        return retval
    }

    /**
     * Method to generate code for map field type.
     */
    static String genInnerMapClass(ProtocolSettings settings, Descriptor innerClassDescriptor, String className){
        String innerClassTemplate = getTemplate("InnerMapClass.java.template")

        String innerClassName = innerClassDescriptor.name
        String wrappingClassName = innerClassName.replaceAll("Entry", "Entries")
        String messageType = getMessageType(innerClassDescriptor)
        String xmlType = genXMLType(innerClassDescriptor, className+innerClassName, "    ")
        String entriesXmlType = genXMLType(innerClassDescriptor,className+wrappingClassName,"    ", ["entry"])
        FieldDescriptor mappingFieldDesc = getMapFields(innerClassDescriptor.containingType).find{FieldDescriptor fd -> fd.messageType.fullName == innerClassDescriptor.fullName}
        ClassNameUtils.MappingType[] mappingFields = ClassNameUtils.getMappingTypes(mappingFieldDesc)
        String fieldKeyType = mappingFields[0].type
        String xmlKeyElement = ClassNameUtils.genXMLElement(mappingFields[0].descriptor)
        String fieldValueType = mappingFields[1].type
        String xmlValueElement = ClassNameUtils.genXMLElement(mappingFields[1].descriptor)
        String xmlElement = ClassNameUtils.genXMLElement(mappingFieldDesc)

        def engine = new SimpleTemplateEngine()
        String generatedInnerClass = engine.createTemplate(innerClassTemplate).make([className: className,
                                                                                     innerClassName: innerClassName,
                                                                                     messageType: messageType, fieldKeyType:fieldKeyType,
                                                                                     fieldValueType: fieldValueType,
                                                                                     wrappingClassName: wrappingClassName,
                                                                                     xmlType: xmlType,
                                                                                     entriesXmlType: entriesXmlType,
                                                                                     xmlKeyElement: xmlKeyElement,
                                                                                     xmlValueElement: xmlValueElement,
                                                                                     xmlElement: xmlElement]).toString()

        return generatedInnerClass
    }

    /**
     * Method to fetch a given template from resources.
     * @param name the filename to get.
     * @return String representation of the template
     * @throws RuntimeException
     */
    static String getTemplate(String name){
        InputStream inputStream = ClassGenerator.class.classLoader.getResourceAsStream(name)
        if(!inputStream){
            throw new RuntimeException("Error generating Wrapper Classes, cannot find resource ${name}.")
        }
        return new String(inputStream.bytes,"UTF-8")
    }

    /**
     * Returns the given type of message depending on the ending of the class name.
     * Either Request,Response,Update or regular Message.
     */
    static String getMessageType(Descriptor classDescriptor){
        return "Message"
    }

    /**
     * Returns the correct getter and setter template for a specific field.
     */
    static String getTemplateName(FieldDescriptor ft){
        if(ft.isMapField()){
            return "MapGetterAndSetterSection.java.template"
        }
        if(ft.javaType == FieldDescriptor.JavaType.MESSAGE && ft.isRepeated()){
            return "MessageListGetterAndSetterSection.java.template"
        }
        if(ft.javaType == FieldDescriptor.JavaType.MESSAGE){
            return "MessageGetterAndSetterSection.java.template"
        }

        if(ft.javaType == FieldDescriptor.JavaType.ENUM){
            return "EnumGetterAndSetterSection.java.template"
        }

        if(ft.javaType == FieldDescriptor.JavaType.STRING && ft.isRepeated()){
            return "StringListGetterAndSetterSection.java.template"
        }

        if(ft.javaType == FieldDescriptor.JavaType.BYTE_STRING && ft.isRepeated()){
            return "ByteStringListGetterAndSetterSection.java.template"
        }

        if(ft.javaType == FieldDescriptor.JavaType.BYTE_STRING){
            return "ByteStringGetterAndSetterSection.java.template"
        }

        return "BasicGetterAndSetterSection.java.template"
    }

    static genPopulateRepeatableFields(ProtocolSettings settings, Descriptor classDescriptor, String className){
        String retval = ""

        Collection fields = classDescriptor.fields.findAll{it.isRepeated()}
        if(fields.size() > 0){


            String repeatableFields = ""
            fields.each {

                String fieldJavaName = ClassNameUtils.convertToJavaBean(it.name)
                String fieldJavaType = ClassNameUtils.getType(it)
                String fieldName = ClassNameUtils.lowerCaseFirst(fieldJavaName)
                if (!it.mapField) {
                    if (it.javaType == FieldDescriptor.JavaType.MESSAGE) {
                        repeatableFields += """

        
        if(${fieldName} != null){
          ((${settings.getAPIClassName()}.${className}.Builder) builder).clear${fieldJavaName}();
          for(${fieldJavaType} next : ${fieldName}){
            ((${settings.getAPIClassName()}.${className}.Builder) builder).add${fieldJavaName}(next.getApiObject());
          }
        }"""
                    } else {
                        if (it.javaType == FieldDescriptor.JavaType.BYTE_STRING) {
                            repeatableFields += """

        if(${fieldName} != null){
          ((${settings.getAPIClassName()}.${className}.Builder) builder).clear${fieldJavaName}();
          for(byte[] next : ${fieldName}){
            ((${settings.getAPIClassName()}.${className}.Builder) builder).add${fieldJavaName}(ByteString.copyFrom(next));
          }
        }"""
                        } else {
                            repeatableFields += """
        if(${fieldName} != null){
          ((${settings.getAPIClassName()}.${className}.Builder) builder).clear${fieldJavaName}();
          for(${fieldJavaType} next : ${fieldName}){
            ((${settings.getAPIClassName()}.${className}.Builder) builder).add${fieldJavaName}(next);
          }
        }"""
                        }
                    }
                } else {
                    ClassNameUtils.MappingType[] mappingFields = ClassNameUtils.getMappingTypes(it)
                    String fieldKeyType = mappingFields[0].type
                    String fieldValueType = mappingFields[1].type

                    repeatableFields += """

        if(${fieldName}Entries != null){
          ((${settings.getAPIClassName()}.${className}.Builder) builder).clear${fieldJavaName}();
          for(${fieldJavaType} entry : ${fieldName}Entries.getEntry()){
            ((${settings.getAPIClassName()}.${className}.Builder) builder).put${fieldJavaName}(${(fieldKeyType == "byte[]"? "ByteString.copyFrom((byte[]) entry.getKey())" : "entry.getKey()")},${(fieldValueType == "byte[]"? "ByteString.copyFrom((byte[]) entry.getValue())" : "entry.getValue()")});
          }
        }"""
                }
            }


            retval = """
    @Override
    protected void populateRepeatedFields(){
       ${repeatableFields}
    }
"""


        }

        return retval

    }

    /**
     * Returns a subset of fields of a class that has isMapField set to true.
     */
    static List getMapFields(Descriptor classDescriptor){
        return classDescriptor.fields.findAll{ it.isMapField()}
    }



}
