// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.user

import com.azure.cosmos.models.CosmosContainerProperties
import com.cosmotech.api.AbstractCosmosBackedService
import com.cosmotech.api.events.OrganizationRegistered
import com.cosmotech.api.events.OrganizationUnregistered
import com.cosmotech.api.events.UserRegistered
import com.cosmotech.api.events.UserUnregistered
import com.cosmotech.api.utils.findAll
import com.cosmotech.api.utils.findByIdOrThrow
import com.cosmotech.user.api.UserApiService
import com.cosmotech.user.domain.User
import java.lang.IllegalStateException
import java.util.*
import javax.annotation.PostConstruct
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(name = ["csm.platform.vendor"], havingValue = "azure", matchIfMissing = true)
class UserServiceImpl : AbstractCosmosBackedService(), UserApiService {

  private lateinit var coreUserContainer: String

  @PostConstruct
  fun initService() {
    this.coreUserContainer = csmPlatformProperties.azure!!.cosmos.coreDatabase.users.container
    cosmosCoreDatabase.createContainerIfNotExists(
        CosmosContainerProperties(coreUserContainer, "/id"))
  }

  override fun authorizeUser() {
    TODO("Not yet implemented")
  }

  override fun findAllUsers() = cosmosTemplate.findAll<User>(coreUserContainer)

  override fun findUserById(userId: String): User =
      cosmosTemplate.findByIdOrThrow(coreUserContainer, userId)

  override fun getCurrentUser(): User {
    TODO("Not yet implemented")
  }

  override fun getOrganizationCurrentUser(organizationId: kotlin.String): User {
    TODO("Not yet implemented")
  }

  override fun getWorkspaceCurrentUser(
      organizationId: kotlin.String,
      workspaceId: kotlin.String
  ): User {
    TODO("Not yet implemented")
  }

  override fun registerUser(user: User): User {
    val userRegistered =
        cosmosTemplate.insert(coreUserContainer, user.copy(id = idGenerator.generate("user")))
    val userId =
        userRegistered.id
            ?: throw IllegalStateException(
                "No ID returned for organization registered: $userRegistered")
    this.eventPublisher.publishEvent(UserRegistered(this, userId))
    return userRegistered
  }

  override fun unregisterUser(userId: String) {
    cosmosTemplate.deleteEntity(coreUserContainer, findUserById(userId))
    this.eventPublisher.publishEvent(UserUnregistered(this, userId))
  }

  override fun updateUser(userId: String, user: User): User {
    TODO("Not yet implemented")
  }

  @EventListener(OrganizationRegistered::class)
  fun onOrganizationRegistered(organizationRegistered: OrganizationRegistered) {
    cosmosCoreDatabase.createContainerIfNotExists(
        CosmosContainerProperties("${organizationRegistered.organizationId}_user-data", "/ownerId"))
  }

  @EventListener(OrganizationUnregistered::class)
  @Async("csm-in-process-event-executor")
  fun onOrganizationUnregistered(organizationUnregistered: OrganizationUnregistered) {
    cosmosTemplate.deleteContainer("${organizationUnregistered.organizationId}_user-data")
  }
}
