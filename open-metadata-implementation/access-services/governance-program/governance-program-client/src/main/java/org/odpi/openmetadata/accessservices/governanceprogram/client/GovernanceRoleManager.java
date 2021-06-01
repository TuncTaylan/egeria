/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.governanceprogram.client;

import org.odpi.openmetadata.accessservices.governanceprogram.api.GovernanceRolesInterface;
import org.odpi.openmetadata.accessservices.governanceprogram.client.rest.GovernanceProgramRESTClient;
import org.odpi.openmetadata.accessservices.governanceprogram.metadataelements.GovernanceRoleAppointee;
import org.odpi.openmetadata.accessservices.governanceprogram.metadataelements.GovernanceRoleElement;
import org.odpi.openmetadata.accessservices.governanceprogram.metadataelements.GovernanceRoleHistory;
import org.odpi.openmetadata.accessservices.governanceprogram.properties.GovernanceRoleProperties;
import org.odpi.openmetadata.accessservices.governanceprogram.rest.*;
import org.odpi.openmetadata.commonservices.ffdc.InvalidParameterHandler;
import org.odpi.openmetadata.commonservices.ffdc.rest.GUIDResponse;
import org.odpi.openmetadata.commonservices.ffdc.rest.NullRequestBody;
import org.odpi.openmetadata.commonservices.ffdc.rest.SearchStringRequestBody;
import org.odpi.openmetadata.frameworks.auditlog.AuditLog;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;

import java.util.Date;
import java.util.List;

/**
 * GovernanceRoleManager provides one of the client-side interface for the Governance Program Open Metadata Access Service (OMAS).
 * This client manages all of the interaction with an open metadata repository.  It is initialized with the URL
 * of the server that is running the Open Metadata Access Services.  This server is responsible for locating and
 * managing the governance program definitions exchanged with this client.
 */
public class GovernanceRoleManager implements GovernanceRolesInterface
{
    private String                      serverName;               /* Initialized in constructor */
    private String                      serverPlatformURLRoot;    /* Initialized in constructor */
    private GovernanceProgramRESTClient restClient;               /* Initialized in constructor */

    private InvalidParameterHandler invalidParameterHandler = new InvalidParameterHandler();
    private NullRequestBody         nullRequestBody         = new NullRequestBody();

    /**
     * Create a new client with no authentication embedded in the HTTP request.
     *
     * @param serverName name of the server to connect to
     * @param serverPlatformURLRoot the network address of the server running the OMAS REST servers
     *
     * @throws InvalidParameterException bad input parameters
     */
    public GovernanceRoleManager(String serverName,
                                 String serverPlatformURLRoot) throws InvalidParameterException
    {
        final String methodName = "Constructor (no security)";

        invalidParameterHandler.validateOMAGServerPlatformURL(serverPlatformURLRoot, serverName, methodName);

        this.serverName            = serverName;
        this.serverPlatformURLRoot = serverPlatformURLRoot;
        this.restClient            = new GovernanceProgramRESTClient(serverName, serverPlatformURLRoot);
    }


    /**
     * Create a new client that passes userId and password in each HTTP request.  This is the
     * userId/password of the calling server.  The end user's userId is sent on each request.
     *
     * @param serverName name of the server to connect to
     * @param serverPlatformURLRoot the network address of the server running the OMAS REST servers
     * @param userId caller's userId embedded in all HTTP requests
     * @param password caller's userId embedded in all HTTP requests
     *
     * @throws InvalidParameterException bad input parameters
     */
    public GovernanceRoleManager(String     serverName,
                                 String     serverPlatformURLRoot,
                                 String     userId,
                                 String     password) throws InvalidParameterException
    {
        final String methodName = "Constructor (with security)";

        invalidParameterHandler.validateOMAGServerPlatformURL(serverPlatformURLRoot, serverName, methodName);

        this.serverName            = serverName;
        this.serverPlatformURLRoot = serverPlatformURLRoot;
        this.restClient            = new GovernanceProgramRESTClient(serverName, serverPlatformURLRoot, userId, password);
    }



    /**
     * Create a new client with no authentication embedded in the HTTP request.
     *
     * @param serverName name of the server to connect to
     * @param serverPlatformURLRoot the network address of the server running the OMAS REST servers
     * @param maxPageSize pre-initialized parameter limit
     * @param auditLog logging destination
     *
     * @throws InvalidParameterException bad input parameters
     */
    public GovernanceRoleManager(String   serverName,
                                 String   serverPlatformURLRoot,
                                 int      maxPageSize,
                                 AuditLog auditLog) throws InvalidParameterException
    {
        final String methodName = "Constructor (no security)";

        invalidParameterHandler.setMaxPagingSize(maxPageSize);
        invalidParameterHandler.validateOMAGServerPlatformURL(serverPlatformURLRoot, serverName, methodName);

        this.serverName            = serverName;
        this.serverPlatformURLRoot = serverPlatformURLRoot;
        this.restClient            = new GovernanceProgramRESTClient(serverName, serverPlatformURLRoot, auditLog);
    }


    /**
     * Create a new client that passes userId and password in each HTTP request.  This is the
     * userId/password of the calling server.  The end user's userId is sent on each request.
     *
     * @param serverName name of the server to connect to
     * @param serverPlatformURLRoot the network address of the server running the OMAS REST servers
     * @param userId caller's userId embedded in all HTTP requests
     * @param password caller's userId embedded in all HTTP requests
     * @param maxPageSize pre-initialized parameter limit
     * @param auditLog logging destination
     *
     * @throws InvalidParameterException bad input parameters
     */
    public GovernanceRoleManager(String     serverName,
                                 String     serverPlatformURLRoot,
                                 String     userId,
                                 String     password,
                                 int        maxPageSize,
                                 AuditLog   auditLog) throws InvalidParameterException
    {
        final String methodName = "Constructor (with security)";

        invalidParameterHandler.setMaxPagingSize(maxPageSize);
        invalidParameterHandler.validateOMAGServerPlatformURL(serverPlatformURLRoot, serverName, methodName);

        this.serverName            = serverName;
        this.serverPlatformURLRoot = serverPlatformURLRoot;
        this.restClient            = new GovernanceProgramRESTClient(serverName, serverPlatformURLRoot, userId, password, auditLog);
    }


    /**
     * Create a new client that uses the supplied rest client.  This is typically used when called fro manother OMAG Server.
     *
     * @param serverName name of the server to connect to
     * @param serverPlatformURLRoot the network address of the server running the OMAS REST servers
     * @param restClient internal client for rest calls
     * @param maxPageSize pre-initialized parameter limit
     *
     * @throws InvalidParameterException bad input parameters
     */
    public GovernanceRoleManager(String                      serverName,
                                 String                      serverPlatformURLRoot,
                                 GovernanceProgramRESTClient restClient,
                                 int                         maxPageSize) throws InvalidParameterException
    {
        final String methodName = "Constructor (with security)";

        invalidParameterHandler.setMaxPagingSize(maxPageSize);
        invalidParameterHandler.validateOMAGServerPlatformURL(serverPlatformURLRoot, serverName, methodName);

        this.serverName            = serverName;
        this.serverPlatformURLRoot = serverPlatformURLRoot;
        this.restClient            = restClient;
    }


    /**
     * Create the governance role.
     *
     * @param userId the name of the calling user.
     * @param properties the description of the governance role
     *
     * @return Unique identifier (guid) of the governance role
     *
     * @throws InvalidParameterException the governance domain, title or appointment id is null.
     * @throws PropertyServerException the server is not available.
     * @throws UserNotAuthorizedException the calling user is not authorized to issue the call.
     */
    @Override
    public String createGovernanceRole(String                   userId,
                                       GovernanceRoleProperties properties) throws InvalidParameterException,
                                                                                   PropertyServerException,
                                                                                   UserNotAuthorizedException
    {
        final String   methodName = "createGovernanceRole";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles";

        final String   roleIdParameterName = "roleId";
        final String   titleParameterName = "title";
        final String   propertiesParameterName = "properties";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateObject(properties, propertiesParameterName, methodName);
        invalidParameterHandler.validateName(properties.getRoleId(), roleIdParameterName, methodName);
        invalidParameterHandler.validateName(properties.getTitle(), titleParameterName, methodName);

        GUIDResponse restResult = restClient.callGUIDPostRESTCall(methodName,
                                                                  serverPlatformURLRoot + urlTemplate,
                                                                  properties,
                                                                  serverName,
                                                                  userId);

        return restResult.getGUID();
    }


    /**
     * Update selected fields for the governance role.
     *
     * @param userId the name of the calling user.
     * @param governanceRoleGUID unique identifier (guid) of the governance role
     * @param isMergeUpdate are unspecified properties unchanged (true) or replaced with null?
     * @param properties the description of the governance role
     *
     * @throws InvalidParameterException the title is null or the governanceDomain/appointmentId does not match the
     *                                   the existing values associated with the governanceRoleGUID.
     * @throws PropertyServerException the server is not available.
     * @throws UserNotAuthorizedException the calling user is not authorized to issue the call.
     */
    @Override
    public void   updateGovernanceRole(String                   userId,
                                       String                   governanceRoleGUID,
                                       boolean                  isMergeUpdate,
                                       GovernanceRoleProperties properties)  throws InvalidParameterException,
                                                                                    PropertyServerException,
                                                                                    UserNotAuthorizedException
    {
        final String   methodName = "updateGovernanceRole";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/{2}?isMergeUpdate={3}";

        final String   guidParameterName = "governanceRoleGUID";
        final String   roleIdParameterName = "roleId";
        final String   titleParameterName = "title";
        final String   propertiesParameterName = "properties";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(governanceRoleGUID, guidParameterName, methodName);
        invalidParameterHandler.validateObject(properties, propertiesParameterName, methodName);

        if (! isMergeUpdate)
        {
            invalidParameterHandler.validateName(properties.getRoleId(), roleIdParameterName, methodName);
            invalidParameterHandler.validateName(properties.getTitle(), titleParameterName, methodName);
        }

        restClient.callVoidPostRESTCall(methodName,
                                        serverPlatformURLRoot + urlTemplate,
                                        properties,
                                        serverName,
                                        userId,
                                        governanceRoleGUID,
                                        isMergeUpdate);
    }



    /**
     * Link a governance role to a governance control that defines a governance responsibility that a person fulfils.
     *
     * @param userId calling user
     * @param governanceRoleGUID unique identifier of the governance role
     * @param responsibilityGUID unique identifier of the governance responsibility control
     *
     * @throws InvalidParameterException one of the guids is null or not known
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    @Override
    public void linkRoleToResponsibility(String userId,
                                         String governanceRoleGUID,
                                         String responsibilityGUID) throws InvalidParameterException,
                                                                           UserNotAuthorizedException,
                                                                           PropertyServerException
    {
        final String   methodName = "linkRoleToResponsibility";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/{2}/governance-responsibility/{3}/link";

        final String   governanceRoleGUIDParameterName = "governanceRoleGUID";
        final String   responsibilityGUIDParameterName = "responsibilityGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(governanceRoleGUID, governanceRoleGUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(responsibilityGUID, responsibilityGUIDParameterName, methodName);

        restClient.callVoidPostRESTCall(methodName,
                                        serverPlatformURLRoot + urlTemplate,
                                        nullRequestBody,
                                        serverName,
                                        userId,
                                        governanceRoleGUID,
                                        responsibilityGUID);
    }


    /**
     * Remove the link between a governance role and a governance responsibility.
     *
     * @param userId calling user
     * @param governanceRoleGUID unique identifier of the governance role
     * @param responsibilityGUID unique identifier of the governance responsibility control
     *
     * @throws InvalidParameterException one of the guids is null or not known
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    @Override
    public void unlinkRoleFromResponsibility(String userId,
                                             String governanceRoleGUID,
                                             String responsibilityGUID) throws InvalidParameterException,
                                                                               UserNotAuthorizedException,
                                                                               PropertyServerException
    {
        final String   methodName = "unlinkRoleToResponsibility";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/{2}/governance-responsibility/{3}/unlink";

        final String   governanceRoleGUIDParameterName = "governanceRoleGUID";
        final String   responsibilityGUIDParameterName = "responsibilityGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(governanceRoleGUID, governanceRoleGUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(responsibilityGUID, responsibilityGUIDParameterName, methodName);

        restClient.callVoidPostRESTCall(methodName,
                                        serverPlatformURLRoot + urlTemplate,
                                        nullRequestBody,
                                        serverName,
                                        userId,
                                        governanceRoleGUID,
                                        responsibilityGUID);
    }


    /**
     * Link a governance role to the description of a resource that the role is responsible for.
     *
     * @param userId calling user
     * @param governanceRoleGUID unique identifier of the governance role
     * @param resourceGUID unique identifier of the resource description
     *
     * @throws InvalidParameterException one of the guids is null or not known
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    @Override
    public void linkRoleToResource(String userId,
                                   String governanceRoleGUID,
                                   String resourceGUID) throws InvalidParameterException,
                                                               UserNotAuthorizedException,
                                                               PropertyServerException
    {
        final String   methodName = "linkRoleToResource";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/{2}/resource/{3}/link";

        final String   governanceRoleGUIDParameterName = "governanceRoleGUID";
        final String   resourceGUIDParameterName = "resourceGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(governanceRoleGUID, governanceRoleGUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(resourceGUID, resourceGUIDParameterName, methodName);

        restClient.callVoidPostRESTCall(methodName,
                                        serverPlatformURLRoot + urlTemplate,
                                        nullRequestBody,
                                        serverName,
                                        userId,
                                        governanceRoleGUID,
                                        resourceGUID);
    }


    /**
     * Remove the link between a governance role and a resource.
     *
     * @param userId calling user
     * @param governanceRoleGUID unique identifier of the governance role
     * @param resourceGUID unique identifier of the resource description
     *
     * @throws InvalidParameterException one of the guids is null or not known
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    @Override
    public void unlinkRoleFromResource(String userId,
                                       String governanceRoleGUID,
                                       String resourceGUID) throws InvalidParameterException,
                                                                   UserNotAuthorizedException,
                                                                   PropertyServerException
    {
        final String   methodName = "unlinkRoleToResource";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/{2}/resource/{3}/unlink";

        final String   governanceRoleGUIDParameterName = "governanceRoleGUID";
        final String   resourceGUIDParameterName = "resourceGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(governanceRoleGUID, governanceRoleGUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(resourceGUID, resourceGUIDParameterName, methodName);

        restClient.callVoidPostRESTCall(methodName,
                                        serverPlatformURLRoot + urlTemplate,
                                        nullRequestBody,
                                        serverName,
                                        userId,
                                        governanceRoleGUID,
                                        resourceGUID);
    }


    /**
     * Remove the requested governance role.
     *
     * @param userId the name of the calling user
     * @param governanceRoleGUID unique identifier (guid) of the governance role
     *
     * @throws InvalidParameterException the appointmentId or governance domain is either null or invalid.
     * @throws PropertyServerException the server is not available.
     * @throws UserNotAuthorizedException the calling user is not authorized to issue the call.
     */
    @Override
    public void   deleteGovernanceRole(String userId,
                                       String governanceRoleGUID) throws InvalidParameterException,
                                                                         PropertyServerException,
                                                                         UserNotAuthorizedException
    {
        final String   methodName = "deleteGovernanceRole";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/{2}/delete";
        final String   guidParameterName = "governanceRoleGUID";

        invalidParameterHandler.validateGUID(governanceRoleGUID, guidParameterName, methodName);
        invalidParameterHandler.validateUserId(userId, methodName);

        restClient.callVoidPostRESTCall(methodName,
                                        serverPlatformURLRoot + urlTemplate,
                                        nullRequestBody,
                                        serverName,
                                        userId,
                                        governanceRoleGUID);
    }


    /**
     * Retrieve a governance role description by unique guid.
     *
     * @param userId the name of the calling user.
     * @param governanceRoleGUID unique identifier (guid) of the governance role.
     * @return governance role object
     * @throws InvalidParameterException the unique identifier of the governance role is either null or invalid.
     * @throws PropertyServerException the server is not available.
     * @throws UserNotAuthorizedException the calling user is not authorized to issue the call.
     */
    @Override
    public GovernanceRoleElement getGovernanceRoleByGUID(String userId,
                                                         String governanceRoleGUID) throws InvalidParameterException,
                                                                                           PropertyServerException,
                                                                                           UserNotAuthorizedException
    {
        final String   methodName = "getGovernanceRoleByGUID";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/{2}";

        final String   guidParameterName = "governanceRoleGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(governanceRoleGUID, guidParameterName, methodName);

        GovernanceRoleResponse restResult = restClient.callGovernanceRoleGetRESTCall(methodName,
                                                                                     serverPlatformURLRoot + urlTemplate,
                                                                                     serverName,
                                                                                     userId,
                                                                                     governanceRoleGUID);

        return restResult.getElement();
    }


    /**
     * Retrieve a governance role description by unique guid along with the history of who has been appointed
     * to the role.
     *
     * @param userId the name of the calling user.
     * @param governanceRoleGUID unique identifier (guid) of the governance role.
     * @return governance role object
     * @throws InvalidParameterException the unique identifier of the governance role is either null or invalid.
     * @throws PropertyServerException the server is not available.
     * @throws UserNotAuthorizedException the calling user is not authorized to issue the call.
     */
    @Override
    public GovernanceRoleHistory getGovernanceRoleHistoryByGUID(String userId,
                                                                String governanceRoleGUID) throws InvalidParameterException,
                                                                                                  PropertyServerException,
                                                                                                  UserNotAuthorizedException
    {
        final String methodName = "getGovernanceRoleHistoryByGUID";
        final String urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/{2}/history";

        final String guidParameterName = "governanceRoleGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(governanceRoleGUID, guidParameterName, methodName);

        GovernanceRoleHistoryResponse restResult = restClient.callGovernanceRoleHistoryGetRESTCall(methodName,
                                                                                                   serverPlatformURLRoot + urlTemplate,
                                                                                                   serverName,
                                                                                                   userId,
                                                                                                   governanceRoleGUID);

        return restResult.getElement();
    }


    /**
     * Retrieve the properties of a governance role using its unique name.  The results are returned as a list
     * since it is possible that two roles have the same identifier due to the distributed nature of the
     * open metadata ecosystem.  By returning all of the search results here it is possible to manage the
     * duplicates through this interface.
     *
     * @param userId calling user
     * @param roleId unique name
     *
     * @return list of roles retrieved
     *
     * @throws InvalidParameterException name or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    @Override
    public List<GovernanceRoleElement> getGovernanceRoleByRoleId(String userId,
                                                                 String roleId) throws InvalidParameterException,
                                                                                       PropertyServerException,
                                                                                       UserNotAuthorizedException
    {
        final String   methodName = "getGovernanceRoleByRoleId";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/by-role-id/{2}";

        final String   appointmentIdParameterName = "appointmentId";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateName(roleId, appointmentIdParameterName, methodName);

        GovernanceRoleListResponse restResult = restClient.callGovernanceRoleListGetRESTCall(methodName,
                                                                                             serverPlatformURLRoot + urlTemplate,
                                                                                             serverName,
                                                                                             userId,
                                                                                             roleId);

        return restResult.getElements();
    }


    /**
     * Return all of the defined governance roles for a specific governance domain.
     *
     * @param userId the name of the calling user
     * @param domainIdentifier domain of interest - 0 means all domains
     * @param startFrom where to start from in the list of definitions
     * @param pageSize max number of results to return in one call
     *
     * @return list of governance role objects
     *
     * @throws InvalidParameterException the governance domain is either null or invalid.
     * @throws PropertyServerException the server is not available.
     * @throws UserNotAuthorizedException the calling user is not authorized to issue the call.
     */
    @Override
    public List<GovernanceRoleElement> getGovernanceRolesByDomainId(String userId,
                                                                    int    domainIdentifier,
                                                                    int    startFrom,
                                                                    int    pageSize) throws InvalidParameterException,
                                                                                            PropertyServerException,
                                                                                            UserNotAuthorizedException
    {
        final String methodName = "getGovernanceRolesByDomainId";
        final String urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/by-domain/{2}?startFrom={3}&pageSize={4}";

        invalidParameterHandler.validateOMAGServerPlatformURL(serverPlatformURLRoot, serverName, methodName);
        invalidParameterHandler.validateUserId(userId, methodName);

        int queryPageSize = invalidParameterHandler.validatePaging(startFrom, pageSize, methodName);

        GovernanceRoleListResponse restResult = restClient.callGovernanceRoleListGetRESTCall(methodName,
                                                                                             serverPlatformURLRoot + urlTemplate,
                                                                                             serverName,
                                                                                             userId,
                                                                                             domainIdentifier,
                                                                                             startFrom,
                                                                                             queryPageSize);

        return restResult.getElements();
    }


    /**
     * Retrieve all of the governance roles for a particular title.  The title can include regEx wildcards.
     *
     * @param userId calling user
     * @param title short description of the role
     * @param startFrom where to start from in the list of definitions
     * @param pageSize max number of results to return in one call
     *
     * @return list of matching roles (null if no matching elements)
     *
     * @throws InvalidParameterException title or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    @Override
    public List<GovernanceRoleElement> getGovernanceRolesByTitle(String userId,
                                                                 String title,
                                                                 int    startFrom,
                                                                 int    pageSize) throws UserNotAuthorizedException,
                                                                                         InvalidParameterException,
                                                                                         PropertyServerException
    {
        final String methodName = "getGovernanceRolesByTitle";
        final String urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/by-title?startFrom={2}&pageSize={3}";
        final String titleParameterName = "title";

        invalidParameterHandler.validateOMAGServerPlatformURL(serverPlatformURLRoot, serverName, methodName);
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateSearchString(title, titleParameterName, methodName);

        int queryPageSize = invalidParameterHandler.validatePaging(startFrom, pageSize, methodName);

        SearchStringRequestBody requestBody = new SearchStringRequestBody();
        requestBody.setSearchString(title);
        requestBody.setSearchStringParameterName(titleParameterName);

        GovernanceRoleListResponse restResult = restClient.callGovernanceRoleListPostRESTCall(methodName,
                                                                                             serverPlatformURLRoot + urlTemplate,
                                                                                             requestBody,
                                                                                             serverName,
                                                                                             userId,
                                                                                             startFrom,
                                                                                             queryPageSize);

        return restResult.getElements();
    }


    /**
     * Return all of the governance roles and their incumbents (if any).
     *
     * @param userId the name of the calling user
     * @param domainIdentifier identifier of domain - 0 means all
     * @param startFrom where to start from in the list of definitions
     * @param pageSize max number of results to return in one call
     *
     * @return list of governance role objects
     *
     * @throws InvalidParameterException the userId is either null or invalid.
     * @throws PropertyServerException the server is not available.
     * @throws UserNotAuthorizedException the calling user is not authorized to issue the call.
     */
    @Override
    public List<GovernanceRoleAppointee> getCurrentGovernanceRoleAppointments(String userId,
                                                                              int    domainIdentifier,
                                                                              int    startFrom,
                                                                              int    pageSize) throws InvalidParameterException,
                                                                                                      PropertyServerException,
                                                                                                      UserNotAuthorizedException
    {
        final String   methodName = "getCurrentGovernanceRoleAppointments";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/by-domain/{2}/current-appointments?startFrom={3}&pageSize={4}";

        invalidParameterHandler.validateOMAGServerPlatformURL(serverPlatformURLRoot, serverName, methodName);
        invalidParameterHandler.validateUserId(userId, methodName);

        int queryPageSize = invalidParameterHandler.validatePaging(startFrom, pageSize, methodName);

        GovernanceRoleAppointeeListResponse restResult = restClient.callGovernanceRoleAppointeeListGetRESTCall(methodName,
                                                                                                               serverPlatformURLRoot + urlTemplate,
                                                                                                               serverName,
                                                                                                               userId,
                                                                                                               domainIdentifier,
                                                                                                               startFrom,
                                                                                                               queryPageSize);

        return restResult.getElements();
    }


    /**
     * Link a person to a governance role.
     *
     * @param userId the name of the calling user.
     * @param governanceRoleGUID unique identifier (guid) of the governance role
     * @param profileGUID unique identifier for the profile
     * @param startDate the official start date of the appointment - null means effective immediately
     *
     * @return unique identifier (guid) of the appointment relationship
     * @throws InvalidParameterException the unique identifier of the governance role or profile is either null or invalid.
     * @throws PropertyServerException the server is not available.
     * @throws UserNotAuthorizedException the calling user is not authorized to issue the call.
     */
    @Override
    public String appointGovernanceRole(String  userId,
                                        String  governanceRoleGUID,
                                        String  profileGUID,
                                        Date    startDate) throws InvalidParameterException,
                                                                  PropertyServerException,
                                                                  UserNotAuthorizedException
    {
        final String methodName = "appointGovernanceRole";
        final String urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/{2}/appoint";

        final String governanceRoleGUIDParameterName = "governanceRoleGUID";
        final String profileGUIDParameterName = "profileGUID";

        invalidParameterHandler.validateOMAGServerPlatformURL(serverPlatformURLRoot, serverName, methodName);
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(governanceRoleGUID, governanceRoleGUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(profileGUID, profileGUIDParameterName, methodName);

        AppointmentRequestBody  requestBody = new AppointmentRequestBody();
        requestBody.setProfileGUID(profileGUID);
        requestBody.setEffectiveDate(startDate);

        GUIDResponse restResult = restClient.callGUIDPostRESTCall(methodName,
                                                                  serverPlatformURLRoot + urlTemplate,
                                                                  requestBody,
                                                                  serverName,
                                                                  userId,
                                                                  governanceRoleGUID);

        return restResult.getGUID();
    }


    /**
     * Unlink a person from a governance role appointment.
     *
     * @param userId the name of the calling user
     * @param governanceRoleGUID unique identifier (guid) of the governance role
     * @param profileGUID unique identifier for the profile
     * @param appointmentGUID unique identifier (guid) of the appointment relationship
     * @param endDate the official end of the appointment - null means effective immediately
     *
     * @throws InvalidParameterException the profile is not linked to this governance role.
     * @throws PropertyServerException the server is not available.
     * @throws UserNotAuthorizedException the calling user is not authorized to issue the call.
     */
    @Override
    public void relieveGovernanceRole(String  userId,
                                      String  governanceRoleGUID,
                                      String  profileGUID,
                                      String  appointmentGUID,
                                      Date    endDate) throws InvalidParameterException,
                                                              PropertyServerException,
                                                              UserNotAuthorizedException
    {
        final String methodName = "relieveGovernanceRole";
        final String urlTemplate = "/servers/{0}/open-metadata/access-services/governance-program/users/{1}/governance-roles/{2}/relieve/{3}";

        final String governanceRoleGUIDParameterName = "governanceRoleGUID";
        final String profileGUIDParameterName = "profileGUID";

        invalidParameterHandler.validateOMAGServerPlatformURL(serverPlatformURLRoot, serverName, methodName);
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(governanceRoleGUID, governanceRoleGUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(profileGUID, profileGUIDParameterName, methodName);

        AppointmentRequestBody  requestBody = new AppointmentRequestBody();
        requestBody.setProfileGUID(profileGUID);
        requestBody.setEffectiveDate(endDate);

        restClient.callVoidPostRESTCall(methodName,
                                        serverPlatformURLRoot + urlTemplate,
                                        requestBody,
                                        serverName,
                                        userId,
                                        governanceRoleGUID,
                                        appointmentGUID);
    }
}
