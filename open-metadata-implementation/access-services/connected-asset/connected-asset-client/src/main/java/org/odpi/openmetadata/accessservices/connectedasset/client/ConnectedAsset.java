/* SPDX-License-Identifier: Apache-2.0 */
package org.odpi.openmetadata.accessservices.connectedasset.client;

import org.odpi.openmetadata.accessservices.connectedasset.ffdc.ConnectedAssetErrorCode;
import org.odpi.openmetadata.accessservices.connectedasset.ffdc.exceptions.*;
import org.odpi.openmetadata.accessservices.connectedasset.rest.AssetResponse;
import org.odpi.openmetadata.accessservices.connectedasset.rest.ConnectedAssetOMASAPIResponse;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.connectors.properties.*;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.SchemaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


/**
 * ConnectedAsset is the OMAS client library implementation of the ConnectedAsset OMAS.
 * ConnectedAsset provides the metadata for the ConnectedAssetProperties API that is
 * supported by all Open Connector Framework (OCF)
 * connectors.   It provides access to the metadata about the Asset that the connector is linked to.
 */
public class ConnectedAsset extends AssetUniverse
{
    /*
     * The name and URL of the server where OMAS is active
     */
    private String                    serverName;
    private String                    omasServerURL;

    private final int   maxCacheSize = 100;


    /**
     * Constructor used by Asset Consumer OMAS and Connected AssetProperties.refresh().
     *
     * @param serverName  name of the server.
     * @param omasServerURL  url used to call the server.
     * @param userId  userId of user making request.
     * @param assetGUID  unique id for asset.
     *
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws UnrecognizedAssetGUIDException the assetGUID is not recognized
     * @throws PropertyServerException There is a problem retrieving the asset properties from
     *                                   the property server.
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public ConnectedAsset(String   serverName,
                          String   omasServerURL,
                          String   userId,
                          String   assetGUID) throws UnrecognizedAssetGUIDException,
                                                     InvalidParameterException,
                                                     PropertyServerException,
                                                     UserNotAuthorizedException
    {
        super();

        this.serverName = serverName;
        this.omasServerURL = omasServerURL;

        AssetResponse assetResponse = this.getAssetSummary(serverName, userId, assetGUID);

        super.assetBean = assetResponse.getAsset();

        if (assetResponse.getExternalIdentifierCount() > 0)
        {
            super.externalIdentifiers = new ConnectedAssetExternalIdentifiers(serverName,
                                                                              userId,
                                                                              omasServerURL,
                                                                              assetGUID,
                                                                              this,
                                                                              assetResponse.getExternalIdentifierCount(),
                                                                              maxCacheSize);
        }

        if (assetResponse.getRelatedMediaReferenceCount() > 0)
        {
            super.relatedMediaReferences = new ConnectedAssetRelatedMediaReferences(serverName,
                                                                                    userId,
                                                                                    omasServerURL,
                                                                                    assetGUID,
                                                                                    this,
                                                                                    assetResponse.getRelatedMediaReferenceCount(),
                                                                                    maxCacheSize);
        }

        if (assetResponse.getNoteLogsCount() > 0)
        {
            super.noteLogs = new ConnectedAssetNoteLogs(serverName,
                                                        userId,
                                                        omasServerURL,
                                                        assetGUID,
                                                        this,
                                                        assetResponse.getNoteLogsCount(),
                                                        maxCacheSize);
        }

        if (assetResponse.getExternalReferencesCount() > 0)
        {
            super.externalReferences = new ConnectedAssetExternalReferences(serverName,
                                                                            userId,
                                                                            omasServerURL,
                                                                            assetGUID,
                                                                            this,
                                                                            assetResponse.getExternalReferencesCount(),
                                                                            maxCacheSize);
        }

        if (assetResponse.getConnectionCount() > 0)
        {
            super.connections = new ConnectedAssetConnections(serverName,
                                                              userId,
                                                              omasServerURL,
                                                              assetGUID,
                                                              this,
                                                              assetResponse.getConnectionCount(),
                                                              maxCacheSize);
        }

        if (assetResponse.getLicenseCount() > 0)
        {
            super.licenses = new ConnectedAssetLicenses(serverName,
                                                        userId,
                                                        omasServerURL,
                                                        assetGUID,
                                                        this,
                                                        assetResponse.getLicenseCount(),
                                                        maxCacheSize);
        }

        if (assetResponse.getCertificationCount() > 0)
        {
            super.certifications = new ConnectedAssetCertifications(serverName,
                                                                    userId,
                                                                    omasServerURL,
                                                                    assetGUID,
                                                                    this,
                                                                    assetResponse.getCertificationCount(),
                                                                    maxCacheSize);
        }

        if (assetResponse.getAnnotationCount() > 0)
        {
            super.analysis = new ConnectedAssetAnnotations(serverName,
                                                           userId,
                                                           omasServerURL,
                                                           assetGUID,
                                                           this,
                                                           assetResponse.getAnnotationCount(),
                                                           maxCacheSize);
        }

        super.feedback = new ConnectedAssetFeedback(serverName,
                                                    userId,
                                                    omasServerURL,
                                                    assetGUID,
                                                    this,
                                                    assetResponse.getCommentCount(),
                                                    assetResponse.getLikeCount(),
                                                    assetResponse.getRatingsCount(),
                                                    assetResponse.getInformalTagCount(),
                                                    maxCacheSize);

        if (assetResponse.getKnownLocationsCount() > 0)
        {
            super.knownLocations = new ConnectedAssetLocations(serverName,
                                                               userId,
                                                               omasServerURL,
                                                               assetGUID,
                                                               this,
                                                               assetResponse.getKnownLocationsCount(),
                                                               maxCacheSize);
        }

        super.lineage = new ConnectedAssetLineage(serverName,
                                                  userId,
                                                  omasServerURL,
                                                  assetGUID,
                                                  this,
                                                  maxCacheSize);

        if (assetResponse.getRelatedAssetCount() > 0)
        {
            super.relatedAssets = new ConnectedAssetRelatedAssets(serverName,
                                                                  userId,
                                                                  omasServerURL,
                                                                  assetGUID,
                                                                  this,
                                                                  assetResponse.getRelatedAssetCount(),
                                                                  maxCacheSize);
        }

        if (assetResponse.getSchemaType() != null)
        {
            super.schema = this.getAssetSchemaType(serverName, userId, assetResponse.getSchemaType());
        }
    }



    /**
     * Returns the basic information about the asset.
     *
     * @param serverName  name of the server.
     * @param userId     String   userId of user making request.
     * @param assetGUID  String   unique id for asset.
     *
     * @return a bean with the basic properties about the asset.
     * @throws InvalidParameterException the GUID is null or invalid.
     * @throws UnrecognizedAssetGUIDException the GUID is not recognized by the property server.
     * @throws PropertyServerException there is a problem retrieving the asset properties from the property server.
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    private AssetResponse getAssetSummary(String   serverName,
                                          String   userId,
                                          String   assetGUID) throws InvalidParameterException,
                                                                     UnrecognizedAssetGUIDException,
                                                                     PropertyServerException,
                                                                     UserNotAuthorizedException
    {
        final String   methodName = "getAssetSummary";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/connected-asset/users/{1}/assets/{2}";

        validateOMASServerURL(methodName);

        AssetResponse  restResult;

        try
        {
            RestTemplate restTemplate = new RestTemplate();

            restResult = restTemplate.getForObject(urlTemplate, AssetResponse.class, serverName, userId, assetGUID);
        }
        catch (Throwable error)
        {
            ConnectedAssetErrorCode errorCode = ConnectedAssetErrorCode.CLIENT_SIDE_REST_API_ERROR;
            String errorMessage = errorCode.getErrorMessageId() + errorCode.getFormattedErrorMessage(methodName,
                                                                                                     omasServerURL,
                                                                                                     error.getMessage());

            throw new PropertyServerException(errorCode.getHTTPErrorCode(),
                                              this.getClass().getName(),
                                              methodName,
                                              errorMessage,
                                              errorCode.getSystemAction(),
                                              errorCode.getUserAction(),
                                              error);
        }

        this.detectAndThrowInvalidParameterException(methodName, restResult);
        this.detectAndThrowUnrecognizedAssetGUIDException(methodName, restResult);
        this.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        this.detectAndThrowPropertyServerException(methodName, restResult);

        return restResult;
    }


    /**
     * Based on the type of bean passed, return the appropriate type of AssetSchemaType.
     *
     * @param serverName  name of the server.
     * @param bean schema type bean that has the properties for the schema type.
     * @return subtype of AssetSchemaType
     */
    AssetSchemaType    getAssetSchemaType(String     serverName,
                                          String     userId,
                                          SchemaType bean)
    {
        return null;
    }


    /**
     * Throw an exception if a server URL has not been supplied on the constructor.
     *
     * @param methodName  name of the method making the call.
     *
     * @throws PropertyServerException the server URL is not set
     */
    void validateOMASServerURL(String methodName) throws PropertyServerException
    {
        if (omasServerURL == null)
        {
            /*
             * It is not possible to retrieve a connection without knowledge of where the OMAS Server is located.
             */
            ConnectedAssetErrorCode errorCode    = ConnectedAssetErrorCode.SERVER_URL_NOT_SPECIFIED;
            String                  errorMessage = errorCode.getErrorMessageId() + errorCode.getFormattedErrorMessage();

            throw new PropertyServerException(errorCode.getHTTPErrorCode(),
                                              this.getClass().getName(),
                                              methodName,
                                              errorMessage,
                                              errorCode.getSystemAction(),
                                              errorCode.getUserAction());
        }
    }


    /**
     * Throw an exception if the supplied userId is null
     *
     * @param userId      user name to validate
     * @param methodName  name of the method making the call.
     *
     * @throws InvalidParameterException the userId is null
     */
    void validateUserId(String userId,
                        String methodName) throws InvalidParameterException
    {
        if (userId == null)
        {
            ConnectedAssetErrorCode errorCode   = ConnectedAssetErrorCode.NULL_USER_ID;
            String                 errorMessage = errorCode.getErrorMessageId()
                                                + errorCode.getFormattedErrorMessage(methodName);

            throw new InvalidParameterException(errorCode.getHTTPErrorCode(),
                                                this.getClass().getName(),
                                                methodName,
                                                errorMessage,
                                                errorCode.getSystemAction(),
                                                errorCode.getUserAction(),
                                                "userId");
        }
    }


    /**
     * Throw an exception if the supplied userId is null
     *
     * @param guid           unique identifier to validate
     * @param guidParameter  name of the parameter that passed the guid.
     * @param methodName     name of the method making the call.
     *
     * @throws InvalidParameterException the guid is null
     */
    void validateGUID(String guid,
                      String guidParameter,
                      String methodName) throws InvalidParameterException
    {
        if (guid == null)
        {
            ConnectedAssetErrorCode errorCode    = ConnectedAssetErrorCode.NULL_GUID;
            String                  errorMessage = errorCode.getErrorMessageId()
                                  + errorCode.getFormattedErrorMessage(guidParameter, methodName);

            throw new InvalidParameterException(errorCode.getHTTPErrorCode(),
                                                this.getClass().getName(),
                                                methodName,
                                                errorMessage,
                                                errorCode.getSystemAction(),
                                                errorCode.getUserAction(),
                                                guidParameter);
        }
    }


    /**
     * Issue a GET REST call that returns a response object.
     *
     * @param methodName  name of the method being called.
     * @param returnClass class of the response object.
     * @param urlTemplate  template of the URL for the REST API call with place-holders for the parameters.
     * @param params  a list of parameters that are slotted into the url template.
     *
     * @return Object
     * @throws PropertyServerException something went wrong with the REST call stack.
     */
    Object callGetRESTCall(String    methodName,
                           Class     returnClass,
                           String    urlTemplate,
                           Object... params) throws PropertyServerException
    {
        try
        {
            RestTemplate  restTemplate = new RestTemplate();

            return restTemplate.getForObject(urlTemplate, returnClass, params);
        }
        catch (Throwable error)
        {
            ConnectedAssetErrorCode errorCode = ConnectedAssetErrorCode.CLIENT_SIDE_REST_API_ERROR;
            String errorMessage = errorCode.getErrorMessageId() + errorCode.getFormattedErrorMessage(methodName,
                                                                                                     omasServerURL,
                                                                                                     error.getMessage());

            throw new PropertyServerException(errorCode.getHTTPErrorCode(),
                                              this.getClass().getName(),
                                              methodName,
                                              errorMessage,
                                              errorCode.getSystemAction(),
                                              errorCode.getUserAction(),
                                              error);
        }
    }


    /**
     * Throw an InvalidParameterException if it is encoded in the REST response.
     *
     * @param methodName  name of the method called
     * @param restResult  response from the rest call.  This generated in the remote server.
     *
     * @throws InvalidParameterException encoded exception from the server
     */
    void detectAndThrowInvalidParameterException(String                        methodName,
                                                 ConnectedAssetOMASAPIResponse restResult) throws InvalidParameterException
    {
        final String   exceptionClassName = InvalidParameterException.class.getName();

        if ((restResult != null) && (exceptionClassName.equals(restResult.getExceptionClassName())))
        {
            String paramName = null;

            Map<String, Object>   exceptionProperties = restResult. getExceptionProperties();

            if (exceptionProperties != null)
            {
                Object  nameObject = exceptionProperties.get("parameterName");

                if (nameObject != null)
                {
                    paramName = (String)nameObject;
                }
            }
            throw new InvalidParameterException(restResult.getRelatedHTTPCode(),
                                                this.getClass().getName(),
                                                methodName,
                                                restResult.getExceptionErrorMessage(),
                                                restResult.getExceptionSystemAction(),
                                                restResult.getExceptionUserAction(),
                                                paramName);
        }
    }


    /**
     * Throw an PropertyServerException if it is encoded in the REST response.
     *
     * @param methodName  name of the method called
     * @param restResult  response from the rest call.  This generated in the remote server.
     *
     * @throws PropertyServerException encoded exception from the server
     */
    void detectAndThrowPropertyServerException(String                        methodName,
                                               ConnectedAssetOMASAPIResponse restResult) throws PropertyServerException
    {
        final String   exceptionClassName = PropertyServerException.class.getName();

        if ((restResult != null) && (exceptionClassName.equals(restResult.getExceptionClassName())))
        {
            throw new PropertyServerException(restResult.getRelatedHTTPCode(),
                                              this.getClass().getName(),
                                              methodName,
                                              restResult.getExceptionErrorMessage(),
                                              restResult.getExceptionSystemAction(),
                                              restResult.getExceptionUserAction());
        }
    }


    /**
     * Throw an UnrecognizedAssetGUIDException if it is encoded in the REST response.
     *
     * @param methodName  name of the method called
     * @param restResult  response from the rest call.  This generated in the remote server.
     *
     * @throws UnrecognizedAssetGUIDException encoded exception from the server
     */
    void detectAndThrowUnrecognizedAssetGUIDException(String                        methodName,
                                                      ConnectedAssetOMASAPIResponse restResult) throws UnrecognizedAssetGUIDException
    {
        final String   exceptionClassName = UnrecognizedAssetGUIDException.class.getName();

        if ((restResult != null) && (exceptionClassName.equals(restResult.getExceptionClassName())))
        {
            String assetGUID = null;

            Map<String, Object>   exceptionProperties = restResult. getExceptionProperties();

            if (exceptionProperties != null)
            {
                Object  guidObject = exceptionProperties.get("assetGUID");

                if (guidObject != null)
                {
                    assetGUID = (String)guidObject;
                }
            }
            throw new UnrecognizedAssetGUIDException(restResult.getRelatedHTTPCode(),
                                                     this.getClass().getName(),
                                                     methodName,
                                                     restResult.getExceptionErrorMessage(),
                                                     restResult.getExceptionSystemAction(),
                                                     restResult.getExceptionUserAction(),
                                                     assetGUID);
        }
    }


    /**
     * Throw an UserNotAuthorizedException if it is encoded in the REST response.
     *
     * @param methodName  name of the method called.
     * @param restResult  response from UserNotAuthorizedException encoded exception from the server.
     *
     * @throws UserNotAuthorizedException encoded exception from the server
     */
    void detectAndThrowUserNotAuthorizedException(String                        methodName,
                                                  ConnectedAssetOMASAPIResponse restResult) throws UserNotAuthorizedException
    {
        final String   exceptionClassName = UserNotAuthorizedException.class.getName();

        if ((restResult != null) && (exceptionClassName.equals(restResult.getExceptionClassName())))
        {
            String userId = null;

            Map<String, Object>   exceptionProperties = restResult. getExceptionProperties();

            if (exceptionProperties != null)
            {
                Object  userIdObject = exceptionProperties.get("userId");

                if (userIdObject != null)
                {
                    userId = (String)userIdObject;
                }
            }

            throw new UserNotAuthorizedException(restResult.getRelatedHTTPCode(),
                                                 this.getClass().getName(),
                                                 methodName,
                                                 restResult.getExceptionErrorMessage(),
                                                 restResult.getExceptionSystemAction(),
                                                 restResult.getExceptionUserAction(),
                                                 userId);
        }
    }
}
