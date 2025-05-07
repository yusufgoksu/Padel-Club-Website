# DefaultApi

All URIs are relative to *http://localhost:9000/api*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**clubsClubIDJsonGet**](DefaultApi.md#clubsClubIDJsonGet) | **GET** /clubs/{clubID}/json | Get a club by ID |
| [**clubsJsonGet**](DefaultApi.md#clubsJsonGet) | **GET** /clubs/json | List all clubs |
| [**clubsPost**](DefaultApi.md#clubsPost) | **POST** /clubs | Create a new club |
| [**courtsCourtIDJsonGet**](DefaultApi.md#courtsCourtIDJsonGet) | **GET** /courts/{courtID}/json | Get a court by ID |
| [**courtsJsonGet**](DefaultApi.md#courtsJsonGet) | **GET** /courts/json | List all courts |
| [**rentalsAvailableGet**](DefaultApi.md#rentalsAvailableGet) | **GET** /rentals/available | Get available hours for a court |
| [**rentalsClubClubIdCourtCourtIdGet**](DefaultApi.md#rentalsClubClubIdCourtCourtIdGet) | **GET** /rentals/club/{clubId}/court/{courtId} | Get rentals for a specific club and court |
| [**rentalsGet**](DefaultApi.md#rentalsGet) | **GET** /rentals | List all rentals |
| [**rentalsPost**](DefaultApi.md#rentalsPost) | **POST** /rentals | Create a new rental |
| [**usersByEmailGet**](DefaultApi.md#usersByEmailGet) | **GET** /users/by-email | Get a user by email |
| [**usersGet**](DefaultApi.md#usersGet) | **GET** /users | List all users |
| [**usersPost**](DefaultApi.md#usersPost) | **POST** /users | Create a new user |
| [**usersUserIDGet**](DefaultApi.md#usersUserIDGet) | **GET** /users/{userID} | Get a user by ID |
| [**usersUserIDRentalsGet**](DefaultApi.md#usersUserIDRentalsGet) | **GET** /users/{userID}/rentals | List rentals for a user |
| [**usersUserIDTokenPost**](DefaultApi.md#usersUserIDTokenPost) | **POST** /users/{userID}/token | Generate a token for a user |


<a id="clubsClubIDJsonGet"></a>
# **clubsClubIDJsonGet**
> Club clubsClubIDJsonGet(clubID)

Get a club by ID

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String clubID = "clubID_example"; // String | 
    try {
      Club result = apiInstance.clubsClubIDJsonGet(clubID);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#clubsClubIDJsonGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **clubID** | **String**|  | |

### Return type

[**Club**](Club.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Club details |  -  |
| **404** | Club not found |  -  |

<a id="clubsJsonGet"></a>
# **clubsJsonGet**
> List&lt;Club&gt; clubsJsonGet()

List all clubs

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    try {
      List<Club> result = apiInstance.clubsJsonGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#clubsJsonGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;Club&gt;**](Club.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A JSON array of clubs |  -  |

<a id="clubsPost"></a>
# **clubsPost**
> Club clubsPost(clubCreate)

Create a new club

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    ClubCreate clubCreate = new ClubCreate(); // ClubCreate | 
    try {
      Club result = apiInstance.clubsPost(clubCreate);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#clubsPost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **clubCreate** | [**ClubCreate**](ClubCreate.md)|  | |

### Return type

[**Club**](Club.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Created club |  -  |

<a id="courtsCourtIDJsonGet"></a>
# **courtsCourtIDJsonGet**
> Court courtsCourtIDJsonGet(courtID)

Get a court by ID

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String courtID = "courtID_example"; // String | 
    try {
      Court result = apiInstance.courtsCourtIDJsonGet(courtID);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#courtsCourtIDJsonGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **courtID** | **String**|  | |

### Return type

[**Court**](Court.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Court details |  -  |
| **404** | Court not found |  -  |

<a id="courtsJsonGet"></a>
# **courtsJsonGet**
> List&lt;Court&gt; courtsJsonGet()

List all courts

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    try {
      List<Court> result = apiInstance.courtsJsonGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#courtsJsonGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;Court&gt;**](Court.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A JSON array of courts |  -  |

<a id="rentalsAvailableGet"></a>
# **rentalsAvailableGet**
> String rentalsAvailableGet(clubId, courtId, date)

Get available hours for a court

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String clubId = "clubId_example"; // String | 
    String courtId = "courtId_example"; // String | 
    LocalDate date = LocalDate.now(); // LocalDate | 
    try {
      String result = apiInstance.rentalsAvailableGet(clubId, courtId, date);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#rentalsAvailableGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **clubId** | **String**|  | |
| **courtId** | **String**|  | |
| **date** | **LocalDate**|  | |

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Comma-separated available hours |  -  |

<a id="rentalsClubClubIdCourtCourtIdGet"></a>
# **rentalsClubClubIdCourtCourtIdGet**
> List&lt;Rental&gt; rentalsClubClubIdCourtCourtIdGet(clubId, courtId, date)

Get rentals for a specific club and court

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String clubId = "clubId_example"; // String | 
    String courtId = "courtId_example"; // String | 
    LocalDate date = LocalDate.now(); // LocalDate | 
    try {
      List<Rental> result = apiInstance.rentalsClubClubIdCourtCourtIdGet(clubId, courtId, date);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#rentalsClubClubIdCourtCourtIdGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **clubId** | **String**|  | |
| **courtId** | **String**|  | |
| **date** | **LocalDate**|  | [optional] |

### Return type

[**List&lt;Rental&gt;**](Rental.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A JSON array of rentals |  -  |

<a id="rentalsGet"></a>
# **rentalsGet**
> List&lt;Rental&gt; rentalsGet()

List all rentals

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    try {
      List<Rental> result = apiInstance.rentalsGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#rentalsGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;Rental&gt;**](Rental.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A JSON array of rentals |  -  |

<a id="rentalsPost"></a>
# **rentalsPost**
> Rental rentalsPost(rentalCreate)

Create a new rental

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    RentalCreate rentalCreate = new RentalCreate(); // RentalCreate | 
    try {
      Rental result = apiInstance.rentalsPost(rentalCreate);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#rentalsPost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **rentalCreate** | [**RentalCreate**](RentalCreate.md)|  | |

### Return type

[**Rental**](Rental.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Created rental |  -  |

<a id="usersByEmailGet"></a>
# **usersByEmailGet**
> User usersByEmailGet(email)

Get a user by email

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String email = "email_example"; // String | 
    try {
      User result = apiInstance.usersByEmailGet(email);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#usersByEmailGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **email** | **String**|  | |

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | User details |  -  |
| **404** | User not found |  -  |

<a id="usersGet"></a>
# **usersGet**
> List&lt;User&gt; usersGet()

List all users

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    try {
      List<User> result = apiInstance.usersGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#usersGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;User&gt;**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A JSON array of users |  -  |

<a id="usersPost"></a>
# **usersPost**
> User usersPost(userCreate)

Create a new user

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    UserCreate userCreate = new UserCreate(); // UserCreate | 
    try {
      User result = apiInstance.usersPost(userCreate);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#usersPost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userCreate** | [**UserCreate**](UserCreate.md)|  | |

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Created user |  -  |

<a id="usersUserIDGet"></a>
# **usersUserIDGet**
> User usersUserIDGet(userID)

Get a user by ID

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String userID = "userID_example"; // String | 
    try {
      User result = apiInstance.usersUserIDGet(userID);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#usersUserIDGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userID** | **String**|  | |

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | User details |  -  |
| **404** | User not found |  -  |

<a id="usersUserIDRentalsGet"></a>
# **usersUserIDRentalsGet**
> List&lt;Rental&gt; usersUserIDRentalsGet(userID)

List rentals for a user

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String userID = "userID_example"; // String | 
    try {
      List<Rental> result = apiInstance.usersUserIDRentalsGet(userID);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#usersUserIDRentalsGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userID** | **String**|  | |

### Return type

[**List&lt;Rental&gt;**](Rental.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A JSON array of rentals for the user |  -  |

<a id="usersUserIDTokenPost"></a>
# **usersUserIDTokenPost**
> UsersUserIDTokenPost200Response usersUserIDTokenPost(userID)

Generate a token for a user

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:9000/api");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String userID = "userID_example"; // String | 
    try {
      UsersUserIDTokenPost200Response result = apiInstance.usersUserIDTokenPost(userID);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#usersUserIDTokenPost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **userID** | **String**|  | |

### Return type

[**UsersUserIDTokenPost200Response**](UsersUserIDTokenPost200Response.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Generated token |  -  |

