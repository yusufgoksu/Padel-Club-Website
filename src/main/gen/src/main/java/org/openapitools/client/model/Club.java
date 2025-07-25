/*
 * Padel Club Booking API
 * API for managing users, clubs, courts, and rentals in a padel club booking system.
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.openapitools.client.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openapitools.client.JSON;

/**
 * Club
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-05-07T04:14:58.751546900+01:00[Europe/London]", comments = "Generator version: 7.7.0")
public class Club {
  public static final String SERIALIZED_NAME_CLUB_I_D = "clubID";
  @SerializedName(SERIALIZED_NAME_CLUB_I_D)
  private String clubID;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_OWNER_UID = "ownerUid";
  @SerializedName(SERIALIZED_NAME_OWNER_UID)
  private String ownerUid;

  public Club() {
  }

  public Club clubID(String clubID) {
    this.clubID = clubID;
    return this;
  }

  /**
   * Get clubID
   * @return clubID
   */
  @javax.annotation.Nullable
  public String getClubID() {
    return clubID;
  }

  public void setClubID(String clubID) {
    this.clubID = clubID;
  }


  public Club name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @javax.annotation.Nonnull
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public Club ownerUid(String ownerUid) {
    this.ownerUid = ownerUid;
    return this;
  }

  /**
   * Get ownerUid
   * @return ownerUid
   */
  @javax.annotation.Nonnull
  public String getOwnerUid() {
    return ownerUid;
  }

  public void setOwnerUid(String ownerUid) {
    this.ownerUid = ownerUid;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Club club = (Club) o;
    return Objects.equals(this.clubID, club.clubID) &&
        Objects.equals(this.name, club.name) &&
        Objects.equals(this.ownerUid, club.ownerUid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clubID, name, ownerUid);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Club {\n");
    sb.append("    clubID: ").append(toIndentedString(clubID)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    ownerUid: ").append(toIndentedString(ownerUid)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


  public static HashSet<String> openapiFields;
  public static HashSet<String> openapiRequiredFields;

  static {
    // a set of all properties/fields (JSON key names)
    openapiFields = new HashSet<String>();
    openapiFields.add("clubID");
    openapiFields.add("name");
    openapiFields.add("ownerUid");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("name");
    openapiRequiredFields.add("ownerUid");
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to Club
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!Club.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in Club is not found in the empty JSON string", Club.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!Club.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `Club` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : Club.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("clubID") != null && !jsonObj.get("clubID").isJsonNull()) && !jsonObj.get("clubID").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `clubID` to be a primitive type in the JSON string but got `%s`", jsonObj.get("clubID").toString()));
      }
      if (!jsonObj.get("name").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `name` to be a primitive type in the JSON string but got `%s`", jsonObj.get("name").toString()));
      }
      if (!jsonObj.get("ownerUid").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `ownerUid` to be a primitive type in the JSON string but got `%s`", jsonObj.get("ownerUid").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!Club.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'Club' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<Club> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(Club.class));

       return (TypeAdapter<T>) new TypeAdapter<Club>() {
           @Override
           public void write(JsonWriter out, Club value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public Club read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of Club given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of Club
   * @throws IOException if the JSON string is invalid with respect to Club
   */
  public static Club fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, Club.class);
  }

  /**
   * Convert an instance of Club to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

