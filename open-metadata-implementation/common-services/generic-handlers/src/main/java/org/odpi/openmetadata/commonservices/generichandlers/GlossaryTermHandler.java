/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.commonservices.generichandlers;

import org.odpi.openmetadata.commonservices.ffdc.InvalidParameterHandler;
import org.odpi.openmetadata.commonservices.repositoryhandler.RepositoryHandler;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.metadatasecurity.server.OpenMetadataServerSecurityVerifier;
import org.odpi.openmetadata.frameworks.auditlog.AuditLog;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.InstanceStatus;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * GlossaryTermHandler retrieves Glossary Term objects from the property server.  It runs server-side
 * and retrieves Glossary Term entities through the OMRSRepositoryConnector.
 *
 * @param <B> class for the glossary term bean
 */
public class GlossaryTermHandler<B> extends ReferenceableHandler<B>
{
    /**
     * Construct the discovery engine configuration handler caching the objects
     * needed to operate within a single server instance.
     *
     * @param converter specific converter for this bean class
     * @param beanClass name of bean class that is represented by the generic class B
     * @param serviceName      name of this service
     * @param serverName       name of the local server
     * @param invalidParameterHandler handler for managing parameter errors
     * @param repositoryHandler     manages calls to the repository services
     * @param repositoryHelper provides utilities for manipulating the repository services objects
     * @param localServerUserId userId for this server
     * @param securityVerifier open metadata security services verifier
     * @param supportedZones list of zones that the access service is allowed to serve Asset instances from.
     * @param defaultZones list of zones that the access service should set in all new Asset instances.
     * @param publishZones list of zones that the access service sets up in published Asset instances.
     * @param auditLog destination for audit log events.
     */
    public GlossaryTermHandler(OpenMetadataAPIGenericConverter<B> converter,
                               Class<B>                           beanClass,
                               String                             serviceName,
                               String                             serverName,
                               InvalidParameterHandler            invalidParameterHandler,
                               RepositoryHandler                  repositoryHandler,
                               OMRSRepositoryHelper               repositoryHelper,
                               String                             localServerUserId,
                               OpenMetadataServerSecurityVerifier securityVerifier,
                               List<String>                       supportedZones,
                               List<String>                       defaultZones,
                               List<String>                       publishZones,
                               AuditLog                           auditLog)
    {
        super(converter,
              beanClass,
              serviceName,
              serverName,
              invalidParameterHandler,
              repositoryHandler,
              repositoryHelper,
              localServerUserId,
              securityVerifier,
              supportedZones,
              defaultZones,
              publishZones,
              auditLog);
    }


    /**
     * Create a new metadata element to represent a glossary term (or a subtype).
     *
     * @param userId calling user
     * @param glossaryGUID unique identifier of the owning glossary
     * @param glossaryGUIDParameterName parameter supplying glossaryGUID
     * @param qualifiedName unique name for the category - used in other configuration
     * @param displayName  display name for the term
     * @param summary short description
     * @param description description of the term
     * @param examples examples of this term
     * @param abbreviation abbreviation used for the term
     * @param usage illustrations of how the term is used
     * @param additionalProperties additional properties for a term
     * @param suppliedTypeName type name from the caller (enables creation of subtypes)
     * @param extendedProperties  properties for a term subtype
     * @param initialStatus glossary term status to use when the object is created
     * @param methodName calling method
     *
     * @return unique identifier of the new metadata element for the glossary term
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public String createGlossaryTerm(String              userId,
                                     String              glossaryGUID,
                                     String              glossaryGUIDParameterName,
                                     String              qualifiedName,
                                     String              displayName,
                                     String              summary,
                                     String              description,
                                     String              examples,
                                     String              abbreviation,
                                     String              usage,
                                     Map<String, String> additionalProperties,
                                     String              suppliedTypeName,
                                     Map<String, Object> extendedProperties,
                                     InstanceStatus      initialStatus,
                                     String              methodName) throws InvalidParameterException,
                                                                            UserNotAuthorizedException,
                                                                            PropertyServerException
    {
        final String qualifiedNameParameterName = "qualifiedName";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateName(qualifiedName, qualifiedNameParameterName, methodName);

        InstanceStatus instanceStatus = InstanceStatus.ACTIVE;

        if (initialStatus != null)
        {
            instanceStatus = initialStatus;
        }

        String typeName = OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME;

        if (suppliedTypeName != null)
        {
            typeName = suppliedTypeName;
        }

        String typeGUID = invalidParameterHandler.validateTypeName(typeName,
                                                                   OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                                                   serviceName,
                                                                   methodName,
                                                                   repositoryHelper);

        GlossaryTermBuilder builder = new GlossaryTermBuilder(qualifiedName,
                                                              displayName,
                                                              summary,
                                                              description,
                                                              examples,
                                                              abbreviation,
                                                              usage,
                                                              additionalProperties,
                                                              extendedProperties,
                                                              instanceStatus,
                                                              repositoryHelper,
                                                              serviceName,
                                                              serverName);

        builder.setAnchors(userId, glossaryGUID, methodName);

        String glossaryTermGUID = this.createBeanInRepository(userId,
                                                              null,
                                                              null,
                                                              typeGUID,
                                                              typeName,
                                                              qualifiedName,
                                                              OpenMetadataAPIMapper.QUALIFIED_NAME_PROPERTY_NAME,
                                                              builder,
                                                              methodName);

        if (glossaryTermGUID != null)
        {
            /*
             * Link the term to its glossary.
             */
            final String glossaryTermGUIDParameterName = "glossaryTermGUID";

            this.linkElementToElement(userId,
                                      null,
                                      null,
                                      glossaryGUID,
                                      glossaryGUIDParameterName,
                                      OpenMetadataAPIMapper.GLOSSARY_TYPE_NAME,
                                      glossaryTermGUID,
                                      glossaryTermGUIDParameterName,
                                      OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                      false,
                                      false,
                                      supportedZones,
                                      OpenMetadataAPIMapper.TERM_ANCHOR_TYPE_GUID,
                                      OpenMetadataAPIMapper.TERM_ANCHOR_TYPE_NAME,
                                      null,
                                      methodName);
        }

        return glossaryTermGUID;
    }


    /**
     * Create a new metadata element to represent a glossary term using an existing metadata element as a template.
     *
     * @param userId calling user
     * @param templateGUID unique identifier of the metadata element to copy
     * @param qualifiedName unique name for the term - used in other configuration
     * @param displayName short display name for the term
     * @param description description of the  term
     * @param methodName calling method
     *
     * @return unique identifier of the new metadata element for the glossary term
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public String createGlossaryTermFromTemplate(String userId,
                                                 String templateGUID,
                                                 String qualifiedName,
                                                 String displayName,
                                                 String description,
                                                 String methodName) throws InvalidParameterException,
                                                                           UserNotAuthorizedException,
                                                                           PropertyServerException
    {
        final String templateGUIDParameterName   = "templateGUID";
        final String qualifiedNameParameterName  = "qualifiedName";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(templateGUID, templateGUIDParameterName, methodName);
        invalidParameterHandler.validateName(qualifiedName, qualifiedNameParameterName, methodName);

        GlossaryTermBuilder builder = new GlossaryTermBuilder(qualifiedName,
                                                              displayName,
                                                              description,
                                                              repositoryHelper,
                                                              serviceName,
                                                              serverName);

        return this.createBeanFromTemplate(userId,
                                           null,
                                           null,
                                           templateGUID,
                                           templateGUIDParameterName,
                                           OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_GUID,
                                           OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                           qualifiedName,
                                           OpenMetadataAPIMapper.QUALIFIED_NAME_PROPERTY_NAME,
                                           builder,
                                           methodName);
    }


    /**
     * Update the properties of the metadata element representing a glossary term.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the glossary term to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryGUID
     * @param qualifiedName unique name for the category - used in other configuration
     * @param displayName short display name for the term
     * @param summary string text
     * @param description description of the  term
     * @param examples string text
     * @param abbreviation string text
     * @param usage string text
     * @param additionalProperties additional properties for a term
     * @param typeName type name from the caller (enables creation of subtypes)
     * @param extendedProperties  properties for a term subtype
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void updateGlossaryTerm(String              userId,
                                   String              glossaryTermGUID,
                                   String              glossaryTermGUIDParameterName,
                                   String              qualifiedName,
                                   String              displayName,
                                   String              summary,
                                   String              description,
                                   String              examples,
                                   String              abbreviation,
                                   String              usage,
                                   Map<String, String> additionalProperties,
                                   String              typeName,
                                   Map<String, Object> extendedProperties,
                                   String              methodName) throws InvalidParameterException,
                                                                          UserNotAuthorizedException,
                                                                          PropertyServerException
    {
        final String qualifiedNameParameterName = "qualifiedName";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);
        invalidParameterHandler.validateName(qualifiedName, qualifiedNameParameterName, methodName);

        String typeGUID = invalidParameterHandler.validateTypeName(typeName,
                                                                   OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                                                   serviceName,
                                                                   methodName,
                                                                   repositoryHelper);

        GlossaryTermBuilder builder = new GlossaryTermBuilder(qualifiedName,
                                                              displayName,
                                                              summary,
                                                              description,
                                                              examples,
                                                              abbreviation,
                                                              usage,
                                                              additionalProperties,
                                                              extendedProperties,
                                                              InstanceStatus.ACTIVE,
                                                              repositoryHelper,
                                                              serviceName,
                                                              serverName);

        this.updateBeanInRepository(userId,
                                    null,
                                    null,
                                    glossaryTermGUID,
                                    glossaryTermGUIDParameterName,
                                    typeGUID,
                                    typeName,
                                    false,
                                    false,
                                    supportedZones,
                                    builder.getInstanceProperties(methodName),
                                    false,
                                    new Date(),
                                    methodName);
    }


    /**
     * Update the status of the metadata element representing a glossary term.  This is only valid on
     * a controlled glossary term.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the glossary term to update
     * @param glossaryTermGUIDParameterName parameter name for glossaryTermGUID
     * @param glossaryTermStatus new status value for the glossary term
     * @param glossaryTermStatusParameterName parameter name for the status value
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void updateGlossaryTermStatus(String         userId,
                                         String         glossaryTermGUID,
                                         String         glossaryTermGUIDParameterName,
                                         InstanceStatus glossaryTermStatus,
                                         String         glossaryTermStatusParameterName,
                                         String         methodName) throws InvalidParameterException,
                                                                           UserNotAuthorizedException,
                                                                           PropertyServerException
    {
        this.updateBeanStatusInRepository(userId,
                                          null,
                                          null,
                                          glossaryTermGUID,
                                          glossaryTermGUIDParameterName,
                                          OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_GUID,
                                          OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                          false,
                                          false,
                                          supportedZones,
                                          glossaryTermStatus,
                                          glossaryTermStatusParameterName,
                                          new Date(),
                                          methodName);
    }


    /**
     * Link a term to a category.
     *
     * @param userId calling user
     * @param glossaryCategoryGUID unique identifier of the glossary category
     * @param glossaryCategoryGUIDParameterName parameter supplying glossaryCategoryGUID
     * @param glossaryTermGUID unique identifier of the glossary term
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param description description of the categorization
     * @param relationshipStatus ordinal for the relationship status enum
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void setupTermCategory(String userId,
                                  String glossaryCategoryGUID,
                                  String glossaryCategoryGUIDParameterName,
                                  String glossaryTermGUID,
                                  String glossaryTermGUIDParameterName,
                                  String description,
                                  int    relationshipStatus,
                                  String methodName) throws InvalidParameterException,
                                                            UserNotAuthorizedException,
                                                            PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryCategoryGUID, glossaryCategoryGUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        GlossaryTermBuilder builder = new GlossaryTermBuilder(repositoryHelper, serviceName, serverName);

        this.linkElementToElement(userId,
                                  null,
                                  null,
                                  glossaryCategoryGUID,
                                  glossaryCategoryGUIDParameterName,
                                  OpenMetadataAPIMapper.GLOSSARY_CATEGORY_TYPE_NAME,
                                  glossaryTermGUID,
                                  glossaryTermGUIDParameterName,
                                  OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                  false,
                                  false,
                                  supportedZones,
                                  OpenMetadataAPIMapper.TERM_CATEGORIZATION_TYPE_GUID,
                                  OpenMetadataAPIMapper.TERM_CATEGORIZATION_TYPE_NAME,
                                  builder.getTermCategorizationProperties(description, relationshipStatus, methodName),
                                  methodName);
    }


    /**
     * Unlink a term from a category.
     *
     * @param userId calling user
     * @param glossaryCategoryGUID unique identifier of the glossary category
     * @param glossaryCategoryGUIDParameterName parameter supplying glossaryCategoryGUID
     * @param glossaryTermGUID unique identifier of the glossary term
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void clearTermCategory(String userId,
                                  String glossaryCategoryGUID,
                                  String glossaryCategoryGUIDParameterName,
                                  String glossaryTermGUID,
                                  String glossaryTermGUIDParameterName,
                                  String methodName) throws InvalidParameterException,
                                                            UserNotAuthorizedException,
                                                            PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryCategoryGUID, glossaryCategoryGUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.unlinkElementFromElement(userId,
                                      false,
                                      null,
                                      null,
                                      glossaryCategoryGUID,
                                      glossaryCategoryGUIDParameterName,
                                      OpenMetadataAPIMapper.GLOSSARY_CATEGORY_TYPE_NAME,
                                      glossaryTermGUID,
                                      glossaryTermGUIDParameterName,
                                      OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_GUID,
                                      OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                      false,
                                      false,
                                      OpenMetadataAPIMapper.TERM_CATEGORIZATION_TYPE_GUID,
                                      OpenMetadataAPIMapper.TERM_CATEGORIZATION_TYPE_NAME,
                                      null,
                                      methodName);
    }


    /**
     * Link two terms together using a specialist relationship.
     *
     * @param userId calling user
     * @param glossaryTermOneGUID unique identifier of the glossary term at end 1
     * @param glossaryTermOneGUIDParameterName parameter supplying glossaryTermOneGUID
     * @param relationshipTypeName name of the type of relationship to create
     * @param relationshipTypeParameterName name of parameter passing the relationship
     * @param glossaryTermTwoGUID unique identifier of the glossary term at end 2
     * @param glossaryTermTwoGUIDParameterName parameter supplying glossaryTermTwoGUID
     * @param description description of the relationship
     * @param expression expression that describes the relationship
     * @param relationshipStatus ordinal for the relationship status enum (draft, active, deprecated, obsolete, other)
     * @param steward user id or name of steward id who assigned the relationship (or approved the discovered value).
     * @param source id of the source of the knowledge of the relationship
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void setupTermRelationship(String userId,
                                      String glossaryTermOneGUID,
                                      String glossaryTermOneGUIDParameterName,
                                      String relationshipTypeName,
                                      String relationshipTypeParameterName,
                                      String glossaryTermTwoGUID,
                                      String glossaryTermTwoGUIDParameterName,
                                      String expression,
                                      String description,
                                      int    relationshipStatus,
                                      String steward,
                                      String source,
                                      String methodName) throws InvalidParameterException,
                                                                UserNotAuthorizedException,
                                                                PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermOneGUID, glossaryTermOneGUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(glossaryTermTwoGUID, glossaryTermTwoGUIDParameterName, methodName);
        invalidParameterHandler.validateName(relationshipTypeName, relationshipTypeParameterName, methodName);

        String relationshipTypeGUID = invalidParameterHandler.validateTypeName(relationshipTypeName,
                                                                               relationshipTypeName,
                                                                               serviceName,
                                                                               methodName,
                                                                               repositoryHelper);

        GlossaryTermBuilder builder = new GlossaryTermBuilder(repositoryHelper, serviceName, serverName);


        this.linkElementToElement(userId,
                                  null,
                                  null,
                                  glossaryTermOneGUID,
                                  glossaryTermOneGUIDParameterName,
                                  OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                  glossaryTermTwoGUID,
                                  glossaryTermTwoGUIDParameterName,
                                  OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                  false,
                                  false,
                                  supportedZones,
                                  relationshipTypeGUID,
                                  relationshipTypeName,
                                  builder.getTermRelationshipProperties(expression,
                                                                        description,
                                                                        relationshipStatus,
                                                                        steward,
                                                                        source,
                                                                        methodName),
                                  methodName);
    }


    /**
     * Update the relationship properties for the two terms.
     *
     * @param userId calling user
     * @param glossaryTermOneGUID unique identifier of the glossary term at end 1
     * @param glossaryTermOneGUIDParameterName parameter supplying glossaryTermOneGUID
     * @param relationshipTypeName name of the type of relationship to create
     * @param relationshipTypeParameterName name of parameter passing the relationship
     * @param glossaryTermTwoGUID unique identifier of the glossary term at end 2
     * @param glossaryTermTwoGUIDParameterName parameter supplying glossaryTermTwoGUID
     * @param description description of the relationship
     * @param expression expression that describes the relationship
     * @param relationshipStatus ordinal for the relationship status enum (draft, active, deprecated, obsolete, other)
     * @param steward user id or name of steward id who assigned the relationship (or approved the discovered value).
     * @param source id of the source of the knowledge of the relationship
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void updateTermRelationship(String userId,
                                       String glossaryTermOneGUID,
                                       String glossaryTermOneGUIDParameterName,
                                       String relationshipTypeName,
                                       String relationshipTypeParameterName,
                                       String glossaryTermTwoGUID,
                                       String glossaryTermTwoGUIDParameterName,
                                       String expression,
                                       String description,
                                       int    relationshipStatus,
                                       String steward,
                                       String source,
                                       String methodName) throws InvalidParameterException,
                                                                 UserNotAuthorizedException,
                                                                 PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermOneGUID, glossaryTermOneGUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(glossaryTermTwoGUID, glossaryTermTwoGUIDParameterName, methodName);
        invalidParameterHandler.validateName(relationshipTypeName, relationshipTypeParameterName, methodName);

        String relationshipTypeGUID = invalidParameterHandler.validateTypeName(relationshipTypeName,
                                                                               relationshipTypeName,
                                                                               serviceName,
                                                                               methodName,
                                                                               repositoryHelper);

        GlossaryTermBuilder builder = new GlossaryTermBuilder(repositoryHelper, serviceName, serverName);


        this.updateElementToElementLink(userId,
                                        null,
                                        null,
                                        glossaryTermOneGUID,
                                        glossaryTermOneGUIDParameterName,
                                        OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                        glossaryTermTwoGUID,
                                        glossaryTermTwoGUIDParameterName,
                                        OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                        relationshipTypeGUID,
                                        relationshipTypeName,
                                        builder.getTermRelationshipProperties(expression,
                                                                              description,
                                                                              relationshipStatus,
                                                                              steward,
                                                                              source,
                                                                              methodName),
                                        methodName);
    }


    /**
     * Remove the relationship between two terms.
     *
     * @param userId calling user
     * @param glossaryTermOneGUID unique identifier of the glossary term at end 1
     * @param glossaryTermOneGUIDParameterName parameter supplying glossaryTermOneGUID
     * @param relationshipTypeName name of the type of relationship to create
     * @param relationshipTypeParameterName name of parameter passing the relationship
     * @param glossaryTermTwoGUID unique identifier of the glossary term at end 2
     * @param glossaryTermTwoGUIDParameterName parameter supplying glossaryTermTwoGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void clearTermRelationship(String userId,
                                      String glossaryTermOneGUID,
                                      String glossaryTermOneGUIDParameterName,
                                      String relationshipTypeName,
                                      String relationshipTypeParameterName,
                                      String glossaryTermTwoGUID,
                                      String glossaryTermTwoGUIDParameterName,
                                      String methodName) throws InvalidParameterException,
                                                                UserNotAuthorizedException,
                                                                PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermOneGUID, glossaryTermOneGUIDParameterName, methodName);
        invalidParameterHandler.validateGUID(glossaryTermTwoGUID, glossaryTermTwoGUIDParameterName, methodName);
        invalidParameterHandler.validateName(relationshipTypeName, relationshipTypeParameterName, methodName);

        String relationshipTypeGUID = invalidParameterHandler.validateTypeName(relationshipTypeName,
                                                                               relationshipTypeName,
                                                                               serviceName,
                                                                               methodName,
                                                                               repositoryHelper);

        this.unlinkElementFromElement(userId,
                                      false,
                                      null,
                                      null,
                                      glossaryTermOneGUID,
                                      glossaryTermOneGUIDParameterName,
                                      OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                      glossaryTermTwoGUID,
                                      glossaryTermTwoGUIDParameterName,
                                      OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_GUID,
                                      OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                      false,
                                      false,
                                      relationshipTypeGUID,
                                      relationshipTypeName,
                                      null,
                                      methodName);
    }


    /**
     * Classify the glossary term to indicate that it describes an abstract concept.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void setTermAsAbstractConcept(String userId,
                                         String glossaryTermGUID,
                                         String glossaryTermGUIDParameterName,
                                         String methodName) throws InvalidParameterException,
                                                                   UserNotAuthorizedException,
                                                                   PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.setClassificationInRepository(userId,
                                           glossaryTermGUID,
                                           glossaryTermGUIDParameterName,
                                           OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                           OpenMetadataAPIMapper.ABSTRACT_CONCEPT_CLASSIFICATION_TYPE_GUID,
                                           OpenMetadataAPIMapper.ABSTRACT_CONCEPT_CLASSIFICATION_TYPE_NAME,
                                           null,
                                           methodName);
    }


    /**
     * Remove the abstract concept designation from the glossary term.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void clearTermAsAbstractConcept(String userId,
                                           String glossaryTermGUID,
                                           String glossaryTermGUIDParameterName,
                                           String methodName) throws InvalidParameterException,
                                                                     UserNotAuthorizedException,
                                                                     PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.removeClassificationFromRepository(userId,
                                                glossaryTermGUID,
                                                glossaryTermGUIDParameterName,
                                                OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                                OpenMetadataAPIMapper.ABSTRACT_CONCEPT_CLASSIFICATION_TYPE_GUID,
                                                OpenMetadataAPIMapper.ABSTRACT_CONCEPT_CLASSIFICATION_TYPE_NAME,
                                                methodName);
    }


    /**
     * Classify the glossary term to indicate that it describes a data value.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void setTermAsDataValue(String userId,
                                   String glossaryTermGUID,
                                   String glossaryTermGUIDParameterName,
                                   String methodName) throws InvalidParameterException,
                                                             UserNotAuthorizedException,
                                                             PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.setClassificationInRepository(userId,
                                           glossaryTermGUID,
                                           glossaryTermGUIDParameterName,
                                           OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                           OpenMetadataAPIMapper.DATA_VALUE_CLASSIFICATION_TYPE_GUID,
                                           OpenMetadataAPIMapper.DATA_VALUE_CLASSIFICATION_TYPE_NAME,
                                           null,
                                           methodName);
    }


    /**
     * Remove the data value designation from the glossary term.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void clearTermAsDataValue(String userId,
                                     String glossaryTermGUID,
                                     String glossaryTermGUIDParameterName,
                                     String methodName) throws InvalidParameterException,
                                                               UserNotAuthorizedException,
                                                               PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.removeClassificationFromRepository(userId,
                                                glossaryTermGUID,
                                                glossaryTermGUIDParameterName,
                                                OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                                OpenMetadataAPIMapper.DATA_VALUE_CLASSIFICATION_TYPE_GUID,
                                                OpenMetadataAPIMapper.DATA_VALUE_CLASSIFICATION_TYPE_NAME,
                                                methodName);
    }


    /**
     * Classify the glossary term to indicate that it describes a data value.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param activityType ordinal for type of activity
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void setTermAsActivity(String userId,
                                  String glossaryTermGUID,
                                  String glossaryTermGUIDParameterName,
                                  int    activityType,
                                  String methodName) throws InvalidParameterException,
                                                            UserNotAuthorizedException,
                                                            PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        GlossaryTermBuilder builder = new GlossaryTermBuilder(repositoryHelper, serviceName, serverName);

        this.setClassificationInRepository(userId,
                                           glossaryTermGUID,
                                           glossaryTermGUIDParameterName,
                                           OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                           OpenMetadataAPIMapper.ACTIVITY_DESC_CLASSIFICATION_TYPE_GUID,
                                           OpenMetadataAPIMapper.ACTIVITY_DESC_CLASSIFICATION_TYPE_NAME,
                                           builder.getActivityTypeProperties(activityType, methodName),
                                           methodName);
    }


    /**
     * Remove the activity designation from the glossary term.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void clearTermAsActivity(String userId,
                                    String glossaryTermGUID,
                                    String glossaryTermGUIDParameterName,
                                    String methodName) throws InvalidParameterException,
                                                              UserNotAuthorizedException,
                                                              PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.removeClassificationFromRepository(userId,
                                                glossaryTermGUID,
                                                glossaryTermGUIDParameterName,
                                                OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                                OpenMetadataAPIMapper.ACTIVITY_DESC_CLASSIFICATION_TYPE_GUID,
                                                OpenMetadataAPIMapper.ACTIVITY_DESC_CLASSIFICATION_TYPE_NAME,
                                                methodName);
    }


    /**
     * Classify the glossary term to indicate that it describes a context.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param description description of the context
     * @param scope the scope of where the context applies
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void setTermAsContext(String userId,
                                 String glossaryTermGUID,
                                 String glossaryTermGUIDParameterName,
                                 String description,
                                 String scope,
                                 String methodName) throws InvalidParameterException,
                                                           UserNotAuthorizedException,
                                                           PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        GlossaryTermBuilder builder = new GlossaryTermBuilder(repositoryHelper, serviceName, serverName);

        this.setClassificationInRepository(userId,
                                           glossaryTermGUID,
                                           glossaryTermGUIDParameterName,
                                           OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                           OpenMetadataAPIMapper.CONTEXT_DEFINITION_CLASSIFICATION_TYPE_GUID,
                                           OpenMetadataAPIMapper.CONTEXT_DEFINITION_CLASSIFICATION_TYPE_NAME,
                                           builder.getContextDescriptionProperties(description, scope, methodName),
                                           methodName);
    }


    /**
     * Remove the context definition designation from the glossary term.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void clearTermAsContext(String userId,
                                   String glossaryTermGUID,
                                   String glossaryTermGUIDParameterName,
                                   String methodName) throws InvalidParameterException,
                                                             UserNotAuthorizedException,
                                                             PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.removeClassificationFromRepository(userId,
                                                glossaryTermGUID,
                                                glossaryTermGUIDParameterName,
                                                OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                                OpenMetadataAPIMapper.CONTEXT_DEFINITION_CLASSIFICATION_TYPE_GUID,
                                                OpenMetadataAPIMapper.CONTEXT_DEFINITION_CLASSIFICATION_TYPE_NAME,
                                                methodName);
    }


    /**
     * Classify the glossary term to indicate that it describes a spine object.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void setTermAsSpineObject(String userId,
                                     String glossaryTermGUID,
                                     String glossaryTermGUIDParameterName,
                                     String methodName) throws InvalidParameterException,
                                                               UserNotAuthorizedException,
                                                               PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.setClassificationInRepository(userId,
                                           glossaryTermGUID,
                                           glossaryTermGUIDParameterName,
                                           OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                           OpenMetadataAPIMapper.SPINE_OBJECT_CLASSIFICATION_TYPE_GUID,
                                           OpenMetadataAPIMapper.SPINE_OBJECT_CLASSIFICATION_TYPE_NAME,
                                           null,
                                           methodName);
    }


    /**
     * Remove the spine object designation from the glossary term.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void clearTermAsSpineObject(String userId,
                                       String glossaryTermGUID,
                                       String glossaryTermGUIDParameterName,
                                       String methodName) throws InvalidParameterException,
                                                                 UserNotAuthorizedException,
                                                                 PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.removeClassificationFromRepository(userId,
                                                glossaryTermGUID,
                                                glossaryTermGUIDParameterName,
                                                OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                                OpenMetadataAPIMapper.SPINE_OBJECT_CLASSIFICATION_TYPE_GUID,
                                                OpenMetadataAPIMapper.SPINE_OBJECT_CLASSIFICATION_TYPE_NAME,
                                                methodName);
    }


    /**
     * Classify the glossary term to indicate that it describes a spine attribute.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void setTermAsSpineAttribute(String userId,
                                        String glossaryTermGUID,
                                        String glossaryTermGUIDParameterName,
                                        String methodName) throws InvalidParameterException,
                                                                  UserNotAuthorizedException,
                                                                  PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.setClassificationInRepository(userId,
                                           glossaryTermGUID,
                                           glossaryTermGUIDParameterName,
                                           OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                           OpenMetadataAPIMapper.SPINE_ATTRIBUTE_CLASSIFICATION_TYPE_GUID,
                                           OpenMetadataAPIMapper.SPINE_ATTRIBUTE_CLASSIFICATION_TYPE_NAME,
                                           null,
                                           methodName);
    }


    /**
     * Remove the spine attribute designation from the glossary term.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void clearTermAsSpineAttribute(String userId,
                                          String glossaryTermGUID,
                                          String glossaryTermGUIDParameterName,
                                          String methodName) throws InvalidParameterException,
                                                                    UserNotAuthorizedException,
                                                                    PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.removeClassificationFromRepository(userId,
                                                glossaryTermGUID,
                                                glossaryTermGUIDParameterName,
                                                OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                                OpenMetadataAPIMapper.SPINE_ATTRIBUTE_CLASSIFICATION_TYPE_GUID,
                                                OpenMetadataAPIMapper.SPINE_ATTRIBUTE_CLASSIFICATION_TYPE_NAME,
                                                methodName);
    }


    /**
     * Classify the glossary term to indicate that it describes an object identifier.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void setTermAsObjectIdentifier(String userId,
                                          String glossaryTermGUID,
                                          String glossaryTermGUIDParameterName,
                                          String methodName) throws InvalidParameterException,
                                                                    UserNotAuthorizedException,
                                                                    PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.setClassificationInRepository(userId,
                                           glossaryTermGUID,
                                           glossaryTermGUIDParameterName,
                                           OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                           OpenMetadataAPIMapper.OBJECT_IDENTIFIER_CLASSIFICATION_TYPE_GUID,
                                           OpenMetadataAPIMapper.OBJECT_IDENTIFIER_CLASSIFICATION_TYPE_NAME,
                                           null,
                                           methodName);
    }


    /**
     * Remove the object identifier designation from the glossary term.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void clearTermAsObjectIdentifier(String userId,
                                            String glossaryTermGUID,
                                            String glossaryTermGUIDParameterName,
                                            String methodName) throws InvalidParameterException,
                                                                      UserNotAuthorizedException,
                                                                      PropertyServerException
    {
        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermGUIDParameterName, methodName);

        this.removeClassificationFromRepository(userId,
                                                glossaryTermGUID,
                                                glossaryTermGUIDParameterName,
                                                OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                                OpenMetadataAPIMapper.OBJECT_IDENTIFIER_CLASSIFICATION_TYPE_GUID,
                                                OpenMetadataAPIMapper.OBJECT_IDENTIFIER_CLASSIFICATION_TYPE_NAME,
                                                methodName);
    }


    /**
     * Remove the metadata element representing a glossary term.
     *
     * @param userId calling user
     * @param glossaryTermGUID unique identifier of the metadata element to update
     * @param glossaryTermGUIDParameterName parameter supplying glossaryTermGUID
     * @param methodName calling method
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public void removeGlossaryTerm(String userId,
                                   String glossaryTermGUID,
                                   String glossaryTermGUIDParameterName,
                                   String methodName) throws InvalidParameterException,
                                                             UserNotAuthorizedException,
                                                             PropertyServerException
    {
        this.deleteBeanInRepository(userId,
                                    null,
                                    null,
                                    glossaryTermGUID,
                                    glossaryTermGUIDParameterName,
                                    OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_GUID,
                                    OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                    null,
                                    null,
                                    false,
                                    false,
                                    new Date(),
                                    methodName);
    }


    /**
     * Returns the glossary term object corresponding to the supplied term name.
     *
     * @param userId  String - userId of user making request.
     * @param name  this may be the qualifiedName or displayName of the term.
     * @param nameParameterName property that provided the name
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     * @param methodName calling method
     *
     * @return List of glossary terms retrieved from property server
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property (metadata) server.
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public List<B> getTermsByName(String    userId,
                                  String    name,
                                  String    nameParameterName,
                                  int       startFrom,
                                  int       pageSize,
                                  String    methodName) throws InvalidParameterException,
                                                               PropertyServerException,
                                                               UserNotAuthorizedException
    {
        List<String> specificMatchPropertyNames = new ArrayList<>();
        specificMatchPropertyNames.add(OpenMetadataAPIMapper.QUALIFIED_NAME_PROPERTY_NAME);
        specificMatchPropertyNames.add(OpenMetadataAPIMapper.DISPLAY_NAME_PROPERTY_NAME);

        return this.getBeansByValue(userId,
                                    name,
                                    nameParameterName,
                                    OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_GUID,
                                    OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                    specificMatchPropertyNames,
                                    true,
                                    null,
                                    null,
                                    false,
                                    false,
                                    supportedZones,
                                    null,
                                    startFrom,
                                    pageSize,
                                    null,
                                    methodName);
    }


    /**
     * Returns the glossary term object containing the supplied term name.  This may include wildcard characters
     *
     * @param userId  String - userId of user making request.
     * @param name  this may be the qualifiedName or displayName of the term
     * @param nameParameterName property that provided the name - interpreted as a to be a regular expression
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     * @param methodName calling method
     *
     * @return List of glossary terms retrieved from property server
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property (metadata) server.
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public List<B> findTerms(String    userId,
                             String    name,
                             String    nameParameterName,
                             int       startFrom,
                             int       pageSize,
                             String    methodName) throws InvalidParameterException,
                                                          PropertyServerException,
                                                          UserNotAuthorizedException
    {
        List<String> specificMatchPropertyNames = new ArrayList<>();
        specificMatchPropertyNames.add(OpenMetadataAPIMapper.QUALIFIED_NAME_PROPERTY_NAME);
        specificMatchPropertyNames.add(OpenMetadataAPIMapper.DISPLAY_NAME_PROPERTY_NAME);

        return this.getBeansByValue(userId,
                                    name,
                                    nameParameterName,
                                    OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_GUID,
                                    OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                    specificMatchPropertyNames,
                                    false,
                                    null,
                                    null,
                                    false,
                                    false,
                                    supportedZones,
                                    null,
                                    startFrom,
                                    pageSize,
                                    null,
                                    methodName);
    }


    /**
     * Returns the glossary term object corresponding to the supplied glossary term GUID.
     *
     * @param userId  String - userId of user making request
     * @param guid  the unique id for the glossary term within the property server
     * @param guidParameter name of parameter supplying the guid
     * @param methodName calling method
     *
     * @return Glossary Term retrieved from the property server
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property (metadata) server.
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public B getTerm(String     userId,
                     String     guid,
                     String     guidParameter,
                     String     methodName) throws InvalidParameterException,
                                                   PropertyServerException,
                                                   UserNotAuthorizedException
    {
        return this.getBeanFromRepository(userId,
                                          guid,
                                          guidParameter,
                                          OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                          false,
                                          false,
                                          null,
                                          methodName);
    }


    /**
     * Retrieve the list of glossary terms associated with a glossary.
     *
     * @param userId calling user
     * @param glossaryGUID unique identifier of the glossary of interest
     * @param glossaryGUIDParameterName property supplying the glossaryGUID
     * @param startFrom paging start point
     * @param pageSize maximum results that can be returned
     * @param methodName calling method
     *
     * @return list of associated metadata elements
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public List<B>  getTermsForGlossary(String userId,
                                        String glossaryGUID,
                                        String glossaryGUIDParameterName,
                                        int    startFrom,
                                        int    pageSize,
                                        String methodName) throws InvalidParameterException,
                                                                  UserNotAuthorizedException,
                                                                  PropertyServerException
    {
        return this.getAttachedElements(userId,
                                        glossaryGUID,
                                        glossaryGUIDParameterName,
                                        OpenMetadataAPIMapper.GLOSSARY_TYPE_NAME,
                                        OpenMetadataAPIMapper.TERM_ANCHOR_TYPE_GUID,
                                        OpenMetadataAPIMapper.TERM_ANCHOR_TYPE_NAME,
                                        OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                        startFrom,
                                        pageSize,
                                        null,
                                        methodName);
    }


    /**
     * Retrieve the list of glossary terms associated with a glossary category.
     *
     * @param userId calling user
     * @param glossaryCategoryGUID unique identifier of the glossary category of interest
     * @param glossaryCategoryGUIDParameterName property supplying the glossaryCategoryGUID
     * @param startFrom paging start point
     * @param pageSize maximum results that can be returned
     * @param methodName calling method
     *
     * @return list of associated metadata elements
     *
     * @throws InvalidParameterException  one of the parameters is invalid
     * @throws UserNotAuthorizedException the user is not authorized to issue this request
     * @throws PropertyServerException    there is a problem reported in the open metadata server(s)
     */
    public List<B>    getTermsForGlossaryCategory(String userId,
                                                  String glossaryCategoryGUID,
                                                  String glossaryCategoryGUIDParameterName,
                                                  int    startFrom,
                                                  int    pageSize,
                                                  String methodName) throws InvalidParameterException,
                                                                            UserNotAuthorizedException,
                                                                            PropertyServerException
    {
        return this.getAttachedElements(userId,
                                        glossaryCategoryGUID,
                                        glossaryCategoryGUIDParameterName,
                                        OpenMetadataAPIMapper.GLOSSARY_CATEGORY_TYPE_NAME,
                                        OpenMetadataAPIMapper.TERM_CATEGORIZATION_TYPE_GUID,
                                        OpenMetadataAPIMapper.TERM_CATEGORIZATION_TYPE_NAME,
                                        OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                        startFrom,
                                        pageSize,
                                        null,
                                        methodName);
    }


    /**
     * Return the glossary terms attached to a supplied entity through the semantic assignment.
     *
     * @param userId     calling user
     * @param elementGUID identifier for the entity that the feedback is attached to
     * @param elementGUIDParameterName name of parameter supplying the GUID
     * @param elementTypeName name of the type of object being attached to
     * @param serviceSupportedZones supported zones for calling service
     * @param startingFrom where to start from in the list
     * @param pageSize maximum number of results that can be returned
     * @param methodName calling method
     * @return list of objects or null if none found
     * @throws InvalidParameterException  the input properties are invalid
     * @throws UserNotAuthorizedException user not authorized to issue this request
     * @throws PropertyServerException    problem accessing the property server
     */
    public List<B>  getAttachedMeanings(String       userId,
                                        String       elementGUID,
                                        String       elementGUIDParameterName,
                                        String       elementTypeName,
                                        List<String> serviceSupportedZones,
                                        int          startingFrom,
                                        int          pageSize,
                                        String       methodName) throws InvalidParameterException,
                                                                        PropertyServerException,
                                                                        UserNotAuthorizedException
    {
        return this.getAttachedElements(userId,
                                        null,
                                        null,
                                        elementGUID,
                                        elementGUIDParameterName,
                                        elementTypeName,
                                        OpenMetadataAPIMapper.REFERENCEABLE_TO_MEANING_TYPE_GUID,
                                        OpenMetadataAPIMapper.REFERENCEABLE_TO_MEANING_TYPE_NAME,
                                        OpenMetadataAPIMapper.GLOSSARY_TERM_TYPE_NAME,
                                        null,
                                        null,
                                        0,
                                        false,
                                        false,
                                        serviceSupportedZones,
                                        startingFrom,
                                        pageSize,
                                        new Date(),
                                        methodName);
    }

}
