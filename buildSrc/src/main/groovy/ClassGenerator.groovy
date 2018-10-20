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
    static void genClass(Descriptor classDescriptor, String outputDir){
        String classTemplate = getTemplate("WrappedMessageClass.java.template")

        String className = classDescriptor.name
        String fields = genFields(classDescriptor, className)
        String messageType = getMessageType(classDescriptor)

        String innerEnums = genInnerEnums(classDescriptor,className)
        String getterAndSetters = genGetterAndSetters(classDescriptor, className)
        String innerClasses = genInnerClasses(classDescriptor, className)
        String xmlType = genXMLType(classDescriptor,className)
        String populateRepeatableFields = genPopulateRepeatableFields(classDescriptor,className)

        def engine = new SimpleTemplateEngine()
        String generatedClass = engine.createTemplate(classTemplate).make([className: className,
                                                   messageType: messageType, innerEnums:innerEnums,
                                                   fields: fields,
                                                   getterAndSetters: getterAndSetters,
                                                   innerClasses: innerClasses,
                                                   xmlType: xmlType,
                                                   populateRepeatableFields: populateRepeatableFields]).toString()

        new File(outputDir + className + ".java").write(generatedClass)


    }

    /**
     * Method to generate the package-info.java file for JAXB context.
     */
    static void genPackageInfo(String outputDir){
        String classTemplate = getTemplate("package-info.java.template")
        new File(outputDir + "package-info.java").write(classTemplate)
    }

    static void genJaxbIndex(Descriptors.FileDescriptor fileDescriptor, String resourcesOutputDir){
        String content = ""
        fileDescriptor.messageTypes.each {
            content += it.name + "\n"
        }
        //new File(outputDir + "jaxb.index").write(content)
        File classDir = new File(resourcesOutputDir)
        classDir.mkdirs()
        new File(resourcesOutputDir + "jaxb.index").write(content)
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
    static String genInnerEnums(Descriptor classDescriptor, String className){
        String retval = ""
        classDescriptor.enumTypes.each{
            String innerEnumTemplate = getTemplate("InnerEnum.java.template")

            String enumName = ClassNameUtils.convertToJavaBean(it.name)
            String xmlType = "@XmlEnum()"
            List enumValueList = []
            it.values.each{ def value ->
                enumValueList << "        ${value.name}(LightningApi.${className}.${enumName}.${value.name})"
            }
            String enumValues = enumValueList.join(",\n") + ";"

            def engine = new SimpleTemplateEngine()
            retval += engine.createTemplate(innerEnumTemplate).make([enumName: enumName,
                                                                     enumValues: enumValues,
                                                                     className: className,
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
    static String genGetterAndSetters(Descriptor classDescriptor, String className, String indentation=""){
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
    static String genInnerClasses(Descriptor classDescriptor, String className){
        String retval = ""
        classDescriptor.getNestedTypes().each{
            if(getMapFields(classDescriptor).find{FieldDescriptor fd -> fd.messageType.fullName == it.fullName} != null){
                retval += genInnerMapClass(it,className)
            }else {
                retval += genInnerClass(it, className)
            }
        }
        return retval
    }

    /**
     * Method to generate a inner class file.
     */
    static String genInnerClass(Descriptor innerClassDescriptor, String className){
        String innerClassTemplate = getTemplate("InnerClass.java.template")

        String innerClassName = innerClassDescriptor.name
        String messageType = getMessageType(innerClassDescriptor)
        String xmlType = genXMLType(innerClassDescriptor,innerClassName,"    ")

        String innerEnums = genInnerEnums(innerClassDescriptor,className+"."+innerClassName)
        String getterAndSetters = genGetterAndSetters(innerClassDescriptor, className+"."+innerClassName, "     ")
        String populateRepeatableFields = genPopulateRepeatableFields(innerClassDescriptor, className+"."+innerClassName)

        def engine = new SimpleTemplateEngine()
        String generatedInnerClass = engine.createTemplate(innerClassTemplate).make([className: className,
                                                                           innerClassName: innerClassName,
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
    static String genInnerMapClass(Descriptor innerClassDescriptor, String className){
        String innerClassTemplate = getTemplate("InnerMapClass.java.template")

        String innerClassName = innerClassDescriptor.name
        String wrappingClassName = innerClassName.replaceAll("Entry", "Entries")
        String messageType = getMessageType(innerClassDescriptor)
        String xmlType = genXMLType(innerClassDescriptor, innerClassName, "    ")
        String entriesXmlType = genXMLType(innerClassDescriptor,wrappingClassName,"    ", ["entry"])
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

        if(ft.javaType == FieldDescriptor.JavaType.BYTE_STRING){
            return "ByteStringGetterAndSetterSection.java.template"
        }

        return "BasicGetterAndSetterSection.java.template"
    }

    static genPopulateRepeatableFields(Descriptor classDescriptor, String className){
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

        ((LightningApi.${className}.Builder) builder).clear${fieldJavaName}();
        if(${fieldName} != null){
          for(${fieldJavaType} next : ${fieldName}){
            ((LightningApi.${className}.Builder) builder).add${fieldJavaName}(next.getApiObject());
          }
        }"""
                    } else {
                        repeatableFields += """

        ((LightningApi.${className}.Builder) builder).clear${fieldJavaName}();
        if(${fieldName} != null){
          for(${fieldJavaType} next : ${fieldName}){
            ((LightningApi.${className}.Builder) builder).add${fieldJavaName}(next);
          }
        }"""
                    }

                } else {

                    repeatableFields += """

        ((LightningApi.${className}.Builder) builder).clear${fieldJavaName}();
        if(${fieldName}Entries != null){
          for(${fieldJavaType} entry : ${fieldName}Entries.getEntry()){
            ((LightningApi.${className}.Builder) builder).put${fieldJavaName}(entry.getKey(),entry.getValue());
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
