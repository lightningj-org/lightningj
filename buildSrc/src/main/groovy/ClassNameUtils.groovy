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
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType


/**
 * Contains util method to calculate generated class names.
 *
 * Created by Philip Vendil
 */
class ClassNameUtils {

    /**
     * Help method to convert first character in a string to uppercase character.
     * <p>
     * For empty or null strings is the same value returned.
     */
    static String upperCaseFirst(String value){
        if(value != null && value.length() > 0){
            return value[0].toUpperCase() + value.substring(1)
        }
        return value
    }

    /**
     * Help method to convert first character in a string to lowercase character.
     * <p>
     * For empty or null strings is the same value returned.
     */
    static String lowerCaseFirst(String value){
        if(value != null && value.length() > 0){
            return value[0].toLowerCase() + value.substring(1)
        }
        return value
    }

    /**
     * Help method to convert a json name with _ separator to Java Bean style with capital letter
     * for word separator. (class naming).
     * <p>
     * For empty or null strings is the same value returned.
     */
    static String convertToJavaBean(String name){
        if(!name || name.empty){
            return name
        }

        return name.split("_").collect{ capitalizeAfterNumber(it.capitalize())}.join("")
    }

    static String capitalizeAfterNumber(String inputString){
        String outputString = ""
        for(int i = 0; i < inputString.length(); i++) {
            def ch = inputString.charAt(i)
            if(ch.isDigit() && i < inputString.length() - 1) {
                outputString += ch
                outputString += inputString.charAt(++i).toUpperCase()
            } else {
                outputString += ch
            }
        }
        return outputString
    }

    /**
     * Returns the related property name for a given field name.
     */
    static String convertToProperty(String name){
        if(!name || name.empty){
            return name
        }
        String javaBeanName = ClassNameUtils.convertToJavaBean(name)
        char secondChar = javaBeanName.length()>1 ? javaBeanName.charAt(1) : ' ' as char
        return Character.isUpperCase(secondChar) ? javaBeanName : ClassNameUtils.lowerCaseFirst(javaBeanName)
    }

    /**
     * Help method for returning a String representation of a given GRPC Java Type
     *
     * @param javaType the type of the field to convert
     * @param fieldDescriptor the field descriptor (class naming)
     */
    static String getType(Descriptors.FieldDescriptor fieldDescriptor){
        String type
        JavaType javaType = fieldDescriptor.javaType

        switch (javaType){
            case JavaType.MESSAGE:
                type= fieldDescriptor.messageType.name
                break
            case JavaType.ENUM:
                type= fieldDescriptor.enumType.name
                break
            case JavaType.BYTE_STRING:
                type= "byte[]"
                break
            case JavaType.STRING:
                type= "String"
                break
            case JavaType.BOOLEAN:
            case JavaType.DOUBLE:
            case JavaType.INT:
            case JavaType.FLOAT:
            case JavaType.LONG:
                type= javaType.name().toLowerCase()
        }

        return type

    }

    /**
     * Returns mapping types for a given mapped field descriptor
     *
     * @param fieldDescriptor the map field to fetch types for.
     * @param protocolSettings the related protocol settings
     * @param appendTypePrefix if a prefix such as "LightningAPI." should be inserted before the Type
     * <p>
     * The returned matrix have following content:
     * <li>0 : Key Type
     * <li>1 : Value Type
     * </p>
     * @return an array of size 2.
     */
    static MappingType[] getMappingTypes(Descriptors.FieldDescriptor fieldDescriptor, ProtocolSettings protocolSettings, boolean appendTypePrefix=false){
        assert fieldDescriptor.isMapField()
        assert fieldDescriptor.messageType.fields.size() == 2
        Descriptors.FieldDescriptor keyType = fieldDescriptor.messageType.fields.find { it.name == "key"}
        Descriptors.FieldDescriptor valueType = fieldDescriptor.messageType.fields.find { it.name == "value"}

        String keyTypeName = getType(keyType)
        if(keyType.getJavaType() == JavaType.MESSAGE && appendTypePrefix) {
            keyTypeName = protocolSettings.getApiTypeName(keyTypeName)
        }


        String valueTypeName = getType(valueType)
        if(valueType.getJavaType() == JavaType.MESSAGE && appendTypePrefix) {
            valueTypeName = protocolSettings.getApiTypeName(valueTypeName)
        }

        return [ new MappingType(type: keyTypeName, descriptor: keyType), new MappingType(type: valueTypeName, descriptor: valueType)] as MappingType[]
    }

    /**
     * Value object class for one mapped type, (key or value) containing its java type and field descriptor.
     */
    static class MappingType{
        String type
        Descriptors.FieldDescriptor descriptor
    }

    /**
     * Method to get related wrapper type, ex Integer for int for a given type.
     */
    static String toObject(String type){
        switch (type){
            case "boolean":
                return "Boolean"
            case "double":
                return "Double"
            case "float":
                return "Float"
            case "int":
                return "Integer"
            case "long":
                return "Long"
            default:
                return type

        }

    }

    static genXMLElement(Descriptors.FieldDescriptor fieldDescriptor){
        String retval = "@XmlElement("
        List attributes = []
        if(fieldDescriptor.isRepeated() && fieldDescriptor.javaType == JavaType.MESSAGE && !fieldDescriptor.isMapField()){
            attributes << 'name="' + fieldDescriptor.messageType.name +'"'
        }
        if(fieldDescriptor.isRequired()){
            attributes << "required=true"
        }else {
            if (fieldDescriptor.hasDefaultValue()) {
                attributes << 'defaultValue="' + fieldDescriptor.getDefaultValue().toString() + '"'
            }
        }

        return retval + attributes.join(", ") + ")"
    }

    static genXMLElementWrapper(Descriptors.FieldDescriptor fieldDescriptor){
        if(fieldDescriptor.isRepeated()){
            return '@XmlElementWrapper(name="' +  fieldDescriptor.name + '")'
        }
        return ""
    }


}
