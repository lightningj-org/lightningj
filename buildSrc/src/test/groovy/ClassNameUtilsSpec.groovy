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
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Method

import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.*

/**
 * Created by Philip Vendil
 */
class ClassNameUtilsSpec extends Specification {


    @Unroll
    def "Verify that upperCaseFirst converts first character of a string from #value to #expected"(){
        expect:
        ClassNameUtils.upperCaseFirst(value) == expected
        where:
        value                | expected
        "someString"         | "SomeString"
        "s"                  | "S"
        "S"                  | "S"
        ""                   | ""
        null                 | null
    }

    @Unroll
    def "Verify that lowerCaseFirst converts first character of a string from #value to #expected"(){
        expect:
        ClassNameUtils.lowerCaseFirst(value) == expected
        where:
        value                | expected
        "SomeString"         | "someString"
        "S"                  | "s"
        "s"                  | "s"
        ""                   | ""
        null                 | null
    }

    @Unroll
    def "Verify that convertToJavaBean converts json name from #value to #expected java bean notation"(){
        expect:
        ClassNameUtils.convertToJavaBean(value) == expected
        where:
        value                | expected
        "some_key_value"     | "SomeKeyValue"
        "s"                  | "S"
        ""                   | ""
        null                 | null
    }

    @Unroll
    def "Verify that convertToProperty convert json name form #value to #expected java property notation"(){
        expect:
        ClassNameUtils.convertToProperty(value) == expected
        where:
        value                | expected
        "some_key_value"     | "someKeyValue"
        "s"                  | "s"
        "s_s"                | "SS"
        ""                   | ""
        null                 | null
    }

    @Ignore
    @Unroll
    def "Verify that getType returns correct string representation #expected for Java type #javaType"(){

        expect:
        ClassNameUtils.getType(descriptor) == expected
        where:
        javaType                 | expected            | descriptor
        STRING                   | "String"            | getLightningClass("Invoice").fields.find {it.name == "memo"}
        INT                      | "int"               | getLightningClass("Hop").fields.find {it.name == "expiry"}
        DOUBLE                   | "double"            | getLightningClass("NetworkInfo").fields.find {it.name == "avg_out_degree"}
        LONG                     | "long"              | getLightningClass("Payment").fields.find {it.name == "fee"}
        BYTE_STRING              | "ByteString"        | getLightningClass("Invoice").fields.find {it.name == "receipt"}
        BOOLEAN                  | "boolean"           | getLightningClass("Invoice").fields.find {it.name == "settled"}
    }

    @Ignore
    def "Verify that getType returns related message name"(){
        setup:
        Descriptors.FieldDescriptor fd = getLightningClass("OpenStatusUpdate").fields.find {it.name == "chan_pending"}
        expect:
        ClassNameUtils.getType(fd) == "PendingUpdate"
    }

    @Ignore
    def "Verify that getMappingType an array of key and value types"(){
        setup:
        Descriptors.FieldDescriptor fd = getLightningClass("SendManyRequest").fields.find {it.name == "AddrToAmount"}
        ClassNameUtils.MappingType[] result = ClassNameUtils.getMappingTypes(fd)
        expect:
        result.length == 2
        result[0].type == "String"
        result[0].descriptor.required == false
        result[1].type == "long"
        result[1].descriptor.required== false
    }

    @Unroll
    def "Verify that toObject returns type #wrappedType for type # type"(){
        expect:
        ClassNameUtils.toObject(type) == wrappedType

        where:
        type                | wrappedType
        "boolean"           | "Boolean"
        "float"             | "Float"
        "double"            | "Double"
        "int"               | "Integer"
        "long"              | "Long"
        "ByteString"        | "ByteString"
        "SomeMessage"       | "SomeMessage"
    }

    private static  Descriptors.Descriptor getLightningClass(String className){
        String ligningAPIClassPath = "org.lightningj.proto.LightningApi"
        String compileClasses = "../build/classes/main"

        def ncl = new GroovyClassLoader()
        ncl.addClasspath(compileClasses)

        Class c = ncl.loadClass(ligningAPIClassPath + '$' + className)

        Method m = c.getMethod("getDefaultInstance")
        Object o =  m.invoke(null)

        Method m2 = o.class.getMethod("getDescriptorForType")
        Descriptors.Descriptor descriptor = m2.invoke(o)


        return descriptor
    }

    @Ignore
    def "Verify that genXMLElement generates correct XMLElement annotation"(){
        setup:
        Descriptors.FieldDescriptor fd1 = getLightningClass("SendManyRequest").fields.find {it.name == "AddrToAmount"}


        expect:
        ClassNameUtils.genXMLElement(fd1) == "@XmlElement()"

    }


}
