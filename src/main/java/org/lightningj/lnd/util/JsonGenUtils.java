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
package org.lightningj.lnd.util;

import com.google.protobuf.*;
import org.lightningj.lnd.proto.LightningApi;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


/**
 * Class containing utility methods for generating/parsing JSON code.
 *
 * Created by Philip Vendil.
 */
public class JsonGenUtils {

    public static final String INFINITE = "inf";
    public static final String NAN = "nan";

    private static Map<String,Boolean> jsonPrettyPrintProperties = new HashMap(1);
    private static JsonGeneratorFactory jsonGeneratorFactory = Json.createGeneratorFactory( new HashMap<String,Boolean>(0));
    private static JsonGeneratorFactory jsonPrettyPrintGeneratorFactory;
    static{
        jsonPrettyPrintProperties.put(JsonGenerator.PRETTY_PRINTING, true);
        jsonPrettyPrintGeneratorFactory = Json.createGeneratorFactory(jsonPrettyPrintProperties);
    }


    /**
     * Help method to convert a json object to string representation.
     * @param jsonBuilder the json data to convert, either JsonObjectBuilder or JsonArrayBuilder.
     * @param prettyPrint true if json data should be pretty printed with indentation and newlines.
     * @return string representation of json data.
     * @throws JsonException if problems occurred converting to string.
     */
    public static String jsonToString(Object jsonBuilder, boolean prettyPrint) throws JsonException{
        Object jsonData;


        try{
            StringWriter sw = new StringWriter();
            JsonGenerator jsonGenerator;
            if(prettyPrint){
                jsonGenerator = jsonPrettyPrintGeneratorFactory.createGenerator(sw);
            }else{
                jsonGenerator = jsonGeneratorFactory.createGenerator(sw);
            }
            if(jsonBuilder instanceof JsonObjectBuilder){
                jsonGenerator.write(((JsonObjectBuilder) jsonBuilder).build());
            }else{
                if(jsonBuilder instanceof JsonArrayBuilder){
                    jsonGenerator.write(((JsonArrayBuilder) jsonBuilder).build());
                }else{
                    throw new JsonException("Invalid json builder " + jsonBuilder.getClass() + " must be either JsonObjectBuilder or JsonArrayBuilder.");
                }
            }

            jsonGenerator.close();
            return sw.toString();
        }catch(Exception e){
            throw new JsonException("Error converting JSON to String" + (e.getMessage() != null ? ": " + e.getMessage() : ""),e);
        }
    }

    /**
     * Method to read and set a single value that is not a repeated or a map.
     *
     * @param jsonObject json object to read a value from.
     * @param apiObjectBuilder the api object builder to set value into.
     * @param fieldDescriptor the field descriptor
     * @throws JsonException if internal problems occurred converting the json data to message.
     */
    public static void readSingleValue(JsonObject jsonObject, Message.Builder apiObjectBuilder, Descriptors.FieldDescriptor fieldDescriptor) throws JsonException {
        switch (fieldDescriptor.getJavaType()){
            case INT:
                apiObjectBuilder.setField(fieldDescriptor,jsonObject.getJsonNumber(fieldDescriptor.getJsonName()).intValueExact());
                break;
            case LONG:
                apiObjectBuilder.setField(fieldDescriptor,jsonObject.getJsonNumber(fieldDescriptor.getJsonName()).longValueExact());
                break;
            case FLOAT:
                float floatVal;
                Object fObject = jsonObject.get(fieldDescriptor.getJsonName());
                if(fObject instanceof JsonString){
                    String fStringValue = ((JsonString) fObject).getString();
                    if(fStringValue.equals(INFINITE)){
                        floatVal = Float.POSITIVE_INFINITY;
                    }else if(fStringValue.equals(NAN)){
                        floatVal = Float.NaN;
                    }else{
                        throw new JsonException("Invalid Json String value for float value, It cannot be: " + fStringValue);
                    }
                }else{
                    floatVal = (float) jsonObject.getJsonNumber(fieldDescriptor.getJsonName()).doubleValue();
                }
                apiObjectBuilder.setField(fieldDescriptor,floatVal);
                break;
            case DOUBLE:
                // Here Infinite and NaN
                double doubleVal;
                Object dObject = jsonObject.get(fieldDescriptor.getJsonName());
                if(dObject instanceof JsonString){
                    String dStringValue = ((JsonString) dObject).getString();
                    if(dStringValue.equals(INFINITE)){
                        doubleVal = Double.POSITIVE_INFINITY;
                    }else if(dStringValue.equals(NAN)){
                        doubleVal = Double.NaN;
                    }else{
                        throw new JsonException("Invalid Json String value for double value, It cannot be: " + dStringValue);
                    }
                }else{
                    doubleVal = jsonObject.getJsonNumber(fieldDescriptor.getJsonName()).doubleValue();
                }

                apiObjectBuilder.setField(fieldDescriptor,doubleVal);
                break;
            case BOOLEAN:
                apiObjectBuilder.setField(fieldDescriptor,jsonObject.getBoolean(fieldDescriptor.getJsonName()));
                break;
            case STRING:
                apiObjectBuilder.setField(fieldDescriptor,jsonObject.getString(fieldDescriptor.getJsonName()));
                break;
            case ENUM:
                String enumVal = jsonObject.getString(fieldDescriptor.getJsonName());
                Descriptors.EnumValueDescriptor enumValueDescriptor = fieldDescriptor.getEnumType().findValueByName(enumVal);
                apiObjectBuilder.setField(fieldDescriptor,enumValueDescriptor);
                break;
            case BYTE_STRING:
                byte[] data = Base64.getDecoder().decode(jsonObject.getString(fieldDescriptor.getJsonName()));
                apiObjectBuilder.setField(fieldDescriptor,ByteString.copyFrom(data));
                break;
            case MESSAGE:
                JsonObject subJsonObject = jsonObject.getJsonObject(fieldDescriptor.getJsonName());
                apiObjectBuilder.setField(fieldDescriptor,jsonToMessage(subJsonObject,fieldDescriptor.getMessageType()).build());
                break;
        }
    }


    /**
     * Sets a json value of an non repeated nor mapped field.
     *
     * @param jsonObjectBuilder the json object being built
     * @param apiObjectBuilder the related apiObject
     * @param fieldDescriptor the field descriptor
     */
    public static void setSingleValue(JsonObjectBuilder jsonObjectBuilder, Message.Builder apiObjectBuilder, Descriptors.FieldDescriptor fieldDescriptor) {
        Object fieldValue = apiObjectBuilder.getField(fieldDescriptor);
        if(fieldValue != null){
          switch (fieldDescriptor.getJavaType()){
              case INT:
                  jsonObjectBuilder.add(fieldDescriptor.getJsonName(),(int) fieldValue);
                  break;
              case LONG:
                  jsonObjectBuilder.add(fieldDescriptor.getJsonName(),(long) fieldValue);
                  break;
              case FLOAT:
                  if(((Float) fieldValue).isInfinite()){
                      jsonObjectBuilder.add(fieldDescriptor.getJsonName(), INFINITE);
                      break;
                  }
                  if(((Float) fieldValue).isNaN()){
                      jsonObjectBuilder.add(fieldDescriptor.getJsonName(), NAN);
                      break;
                  }
                  jsonObjectBuilder.add(fieldDescriptor.getJsonName(),(float) fieldValue);
                  break;
              case DOUBLE:
                  if(((Double) fieldValue).isInfinite()){
                      jsonObjectBuilder.add(fieldDescriptor.getJsonName(), INFINITE);
                      break;
                  }
                  if(((Double) fieldValue).isNaN()){
                      jsonObjectBuilder.add(fieldDescriptor.getJsonName(), NAN);
                      break;
                  }
                  jsonObjectBuilder.add(fieldDescriptor.getJsonName(),(double) fieldValue);
                  break;
              case BOOLEAN:
                  jsonObjectBuilder.add(fieldDescriptor.getJsonName(),(boolean) fieldValue);
                  break;
              case STRING:
                  jsonObjectBuilder.add(fieldDescriptor.getJsonName(),(String) fieldValue);
                  break;
              case ENUM:
                  jsonObjectBuilder.add(fieldDescriptor.getJsonName(),fieldValue.toString());
                  break;
              case BYTE_STRING:
                  String encoded = Base64.getEncoder().encodeToString(((ByteString) fieldValue).toByteArray());
                  jsonObjectBuilder.add(fieldDescriptor.getJsonName(),encoded);
                  break;
              case MESSAGE:
                  GeneratedMessageV3 subMessage = (GeneratedMessageV3) fieldValue;
                  jsonObjectBuilder.add(fieldDescriptor.getJsonName(),
                          messageToJson(subMessage.toBuilder(),fieldDescriptor.getMessageType()));
                  break;
          }
        }
    }

    /**
     * Method to convert a Proto Message to JSON.
     *
     * @param apiObjectBuilder the proto message builder to convert.
     * @param messageDescriptor the descriptor of the message.
     * @return a new JsonObjectBuilder with all fields set.
     */
    public static JsonObjectBuilder messageToJson(Message.Builder apiObjectBuilder, Descriptors.Descriptor messageDescriptor){
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        for(Descriptors.FieldDescriptor fieldDescriptor : messageDescriptor.getFields()){
            if(fieldDescriptor.isMapField() || fieldDescriptor.isRepeated()){
                setRepeatedValue(jsonObjectBuilder,apiObjectBuilder,fieldDescriptor);
            }else{
                setSingleValue(jsonObjectBuilder,apiObjectBuilder,fieldDescriptor);
            }
        }
        return jsonObjectBuilder;
    }


    /**
     * Method to convert a jsonObject to a Message Builder.
     *
     * @param jsonObject the json object to return.
     * @param messageDescriptor the descriptor of the message to convert.
     * @return a new Message Builder of the descriptor type.
     * @throws JsonException if internal problems occurred converting the json data to message.
     */
    public static Message.Builder jsonToMessage(JsonObject jsonObject, Descriptors.Descriptor messageDescriptor) throws JsonException {

        GeneratedMessageV3 defaultInstance;
        try {
            Class<?> messageClass = LightningApi.class.getClassLoader().loadClass(LightningApi.class.getName() + "$" + messageDescriptor.getName());
            Method m = messageClass.getMethod("getDefaultInstance");
            defaultInstance = (GeneratedMessageV3) m.invoke(null);
        }catch(Exception e){
            throw new JsonException("Error creating a instance of class " + messageDescriptor.getName() +", message: " +e.getMessage(),e);
        }

        return jsonToMessage(jsonObject,defaultInstance.toBuilder(),messageDescriptor);
    }

    /**
     * Method to convert a jsonObject to a Message Builder.
     *
     * @param jsonObject the json object to return.
     * @param messageDescriptor the descriptor of the message to convert.
     * @return a new Message Builder of the descriptor type.
     * @throws JsonException if internal problems occurred converting the json data to message.
     */
    public static Message.Builder jsonToMessage(JsonObject jsonObject, Message.Builder builder, Descriptors.Descriptor messageDescriptor) throws JsonException {

        for(Descriptors.FieldDescriptor fieldDescriptor : messageDescriptor.getFields()){
            if(jsonObject.containsKey(fieldDescriptor.getJsonName())) {
                if (fieldDescriptor.isMapField() || fieldDescriptor.isRepeated()) {
                    readRepeatedValue(jsonObject.getJsonArray(fieldDescriptor.getJsonName()),builder,fieldDescriptor);
                } else {
                    readSingleValue(jsonObject, builder, fieldDescriptor);
                }
            }
        }

        return builder;
    }

    /**
     * Method to create json array of a repeated field.
     *
     * @param jsonObjectBuilder the json object to add json array to.
     * @param apiObjectBuilder the proto message builder to convert.
     * @param fieldDescriptor the descriptor of the field.
     */
    public static void setRepeatedValue(JsonObjectBuilder jsonObjectBuilder, Message.Builder apiObjectBuilder, Descriptors.FieldDescriptor fieldDescriptor) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        int fieldCount = apiObjectBuilder.getRepeatedFieldCount(fieldDescriptor);
        for(int i=0;i<fieldCount;i++){

            switch (fieldDescriptor.getJavaType()){
                case INT:
                    jsonArrayBuilder.add((int) apiObjectBuilder.getRepeatedField(fieldDescriptor,i));
                    break;
                case LONG:
                    jsonArrayBuilder.add((long) apiObjectBuilder.getRepeatedField(fieldDescriptor,i));
                    break;
                case FLOAT:
                    jsonArrayBuilder.add((float) apiObjectBuilder.getRepeatedField(fieldDescriptor,i));
                    break;
                case DOUBLE:
                    jsonArrayBuilder.add((double) apiObjectBuilder.getRepeatedField(fieldDescriptor,i));
                    break;
                case BOOLEAN:
                    jsonArrayBuilder.add((boolean) apiObjectBuilder.getRepeatedField(fieldDescriptor,i));
                    break;
                case STRING:
                    jsonArrayBuilder.add((String) apiObjectBuilder.getRepeatedField(fieldDescriptor,i));
                    break;
                case ENUM:
                    jsonArrayBuilder.add(apiObjectBuilder.getRepeatedField(fieldDescriptor,i).toString());
                    break;
                case BYTE_STRING:
                    String encoded = Base64.getEncoder().encodeToString(((ByteString) apiObjectBuilder.getRepeatedField(fieldDescriptor,i)).toByteArray());
                    jsonArrayBuilder.add(encoded);
                    break;
                case MESSAGE:
                    if(fieldDescriptor.isMapField()){
                        MapEntry mapEntry = (MapEntry) apiObjectBuilder.getRepeatedField(fieldDescriptor,i);
                        jsonArrayBuilder.add(messageToJson( mapEntry.toBuilder(), mapEntry.getDescriptorForType()));
                    }else {
                        jsonArrayBuilder.add(messageToJson(apiObjectBuilder.getRepeatedFieldBuilder(fieldDescriptor, i), fieldDescriptor.getMessageType()));
                    }
                    break;
            }

        }

        jsonObjectBuilder.add(fieldDescriptor.getJsonName(),jsonArrayBuilder);
    }

    /**
     * Method to read a json array into a repeated value in related message.
     *
     * @param jsonArray the json array to read values from.
     * @param apiObjectBuilder the api object builder to set value into.
     * @param fieldDescriptor the field descriptor
     * @throws JsonException if internal problems occurred converting the json data to message.
     */
    public static void readRepeatedValue(JsonArray jsonArray, Message.Builder apiObjectBuilder, Descriptors.FieldDescriptor fieldDescriptor) throws JsonException {
        for(int i=0;i<jsonArray.size();i++){
            switch (fieldDescriptor.getJavaType()){
                case INT:
                    apiObjectBuilder.addRepeatedField(fieldDescriptor,jsonArray.getJsonNumber(i).intValueExact());
                    break;
                case LONG:
                    apiObjectBuilder.addRepeatedField(fieldDescriptor,jsonArray.getJsonNumber(i).longValueExact());
                    break;
                case FLOAT:
                    Float value = (float) jsonArray.getJsonNumber(i).doubleValue();
                    apiObjectBuilder.addRepeatedField(fieldDescriptor,value);
                    break;
                case DOUBLE:
                    apiObjectBuilder.addRepeatedField(fieldDescriptor,jsonArray.getJsonNumber(i).doubleValue());
                    break;
                case BOOLEAN:
                    apiObjectBuilder.addRepeatedField(fieldDescriptor,jsonArray.getBoolean(i));
                    break;
                case STRING:
                    apiObjectBuilder.addRepeatedField(fieldDescriptor,jsonArray.getString(i));
                    break;
                case ENUM:
                    String enumVal = jsonArray.getString(i);
                    Descriptors.EnumValueDescriptor enumValueDescriptor = fieldDescriptor.getEnumType().findValueByName(enumVal);
                    apiObjectBuilder.addRepeatedField(fieldDescriptor,enumValueDescriptor);
                    break;
                case BYTE_STRING:
                    byte[] data = Base64.getDecoder().decode(jsonArray.getString(i));
                    apiObjectBuilder.addRepeatedField(fieldDescriptor,ByteString.copyFrom(data));
                    break;
                case MESSAGE:
                    JsonObject subJsonObject = jsonArray.getJsonObject(i);
                    if(fieldDescriptor.isMapField()){
                        Descriptors.FieldDescriptor keyFieldDesc = fieldDescriptor.getMessageType().getFields().get(0);
                        Descriptors.FieldDescriptor valueFieldDesc = fieldDescriptor.getMessageType().getFields().get(1);
                        MapEntry mapEntry = MapEntry.newDefaultInstance(fieldDescriptor.getMessageType(),keyFieldDesc.getLiteType(),keyFieldDesc.getDefaultValue(), valueFieldDesc.getLiteType(), valueFieldDesc.getDefaultValue());
                        MapEntry.Builder mapBuilder = mapEntry.toBuilder();
                        readSingleValue(subJsonObject,mapBuilder,keyFieldDesc);
                        readSingleValue(subJsonObject,mapBuilder,valueFieldDesc);
                        apiObjectBuilder.addRepeatedField(fieldDescriptor, mapBuilder.build());
                    }else {
                        apiObjectBuilder.addRepeatedField(fieldDescriptor, jsonToMessage(subJsonObject, fieldDescriptor.getMessageType()).build());
                    }
                    break;
            }
        }
    }
}
