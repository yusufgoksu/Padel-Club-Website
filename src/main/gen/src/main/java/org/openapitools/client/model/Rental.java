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
import java.time.OffsetDateTime;
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
 * Rental
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-05-07T04:14:58.751546900+01:00[Europe/London]", comments = "Generator version: 7.7.0")
public class Rental {
  public static final String SERIALIZED_NAME_RENTAL_I_D = "rentalID";
  @SerializedName(SERIALIZED_NAME_RENTAL_I_D)
  private String rentalID;

  public static final String SERIALIZED_NAME_USER_ID = "userId";
  @SerializedName(SERIALIZED_NAME_USER_ID)
  private String userId;

  public static final String SERIALIZED_NAME_CLUB_ID = "clubId";
  @SerializedName(SERIALIZED_NAME_CLUB_ID)
  private String clubId;

  public static final String SERIALIZED_NAME_COURT_ID = "courtId";
  @SerializedName(SERIALIZED_NAME_COURT_ID)
  private String courtId;

  public static final String SERIALIZED_NAME_START_TIME = "startTime";
  @SerializedName(SERIALIZED_NAME_START_TIME)
  private OffsetDateTime startTime;

  public static final String SERIALIZED_NAME_DURATION = "duration";
  @SerializedName(SERIALIZED_NAME_DURATION)
  private Integer duration;

  public Rental() {
  }

  public Rental rentalID(String rentalID) {
    this.rentalID = rentalID;
    return this;
  }

  /**
   * Get rentalID
   * @return rentalID
   */
  @javax.annotation.Nullable
  public String getRentalID() {
    return rentalID;
  }

  public void setRentalID(String rentalID) {
    this.rentalID = rentalID;
  }


  public Rental userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  @javax.annotation.Nonnull
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }


  public Rental clubId(String clubId) {
    this.clubId = clubId;
    return this;
  }

  /**
   * Get clubId
   * @return clubId
   */
  @javax.annotation.Nonnull
  public String getClubId() {
    return clubId;
  }

  public void setClubId(String clubId) {
    this.clubId = clubId;
  }


  public Rental courtId(String courtId) {
    this.courtId = courtId;
    return this;
  }

  /**
   * Get courtId
   * @return courtId
   */
  @javax.annotation.Nonnull
  public String getCourtId() {
    return courtId;
  }

  public void setCourtId(String courtId) {
    this.courtId = courtId;
  }


  public Rental startTime(OffsetDateTime startTime) {
    this.startTime = startTime;
    return this;
  }

  /**
   * Get startTime
   * @return startTime
   */
  @javax.annotation.Nonnull
  public OffsetDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(OffsetDateTime startTime) {
    this.startTime = startTime;
  }


  public Rental duration(Integer duration) {
    this.duration = duration;
    return this;
  }

  /**
   * Get duration
   * @return duration
   */
  @javax.annotation.Nonnull
  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Rental rental = (Rental) o;
    return Objects.equals(this.rentalID, rental.rentalID) &&
        Objects.equals(this.userId, rental.userId) &&
        Objects.equals(this.clubId, rental.clubId) &&
        Objects.equals(this.courtId, rental.courtId) &&
        Objects.equals(this.startTime, rental.startTime) &&
        Objects.equals(this.duration, rental.duration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rentalID, userId, clubId, courtId, startTime, duration);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Rental {\n");
    sb.append("    rentalID: ").append(toIndentedString(rentalID)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    clubId: ").append(toIndentedString(clubId)).append("\n");
    sb.append("    courtId: ").append(toIndentedString(courtId)).append("\n");
    sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
    sb.append("    duration: ").append(toIndentedString(duration)).append("\n");
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
    openapiFields.add("rentalID");
    openapiFields.add("userId");
    openapiFields.add("clubId");
    openapiFields.add("courtId");
    openapiFields.add("startTime");
    openapiFields.add("duration");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("userId");
    openapiRequiredFields.add("clubId");
    openapiRequiredFields.add("courtId");
    openapiRequiredFields.add("startTime");
    openapiRequiredFields.add("duration");
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to Rental
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!Rental.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in Rental is not found in the empty JSON string", Rental.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!Rental.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `Rental` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }

      // check to make sure all required properties/fields are present in the JSON string
      for (String requiredField : Rental.openapiRequiredFields) {
        if (jsonElement.getAsJsonObject().get(requiredField) == null) {
          throw new IllegalArgumentException(String.format("The required field `%s` is not found in the JSON string: %s", requiredField, jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("rentalID") != null && !jsonObj.get("rentalID").isJsonNull()) && !jsonObj.get("rentalID").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `rentalID` to be a primitive type in the JSON string but got `%s`", jsonObj.get("rentalID").toString()));
      }
      if (!jsonObj.get("userId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `userId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("userId").toString()));
      }
      if (!jsonObj.get("clubId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `clubId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("clubId").toString()));
      }
      if (!jsonObj.get("courtId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `courtId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("courtId").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!Rental.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'Rental' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<Rental> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(Rental.class));

       return (TypeAdapter<T>) new TypeAdapter<Rental>() {
           @Override
           public void write(JsonWriter out, Rental value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public Rental read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of Rental given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of Rental
   * @throws IOException if the JSON string is invalid with respect to Rental
   */
  public static Rental fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, Rental.class);
  }

  /**
   * Convert an instance of Rental to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

