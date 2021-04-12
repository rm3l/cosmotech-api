// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.scenario

import com.cosmotech.api.AbstractPhoenixService
import com.cosmotech.scenario.api.ScenarioApiService
import com.cosmotech.scenario.domain.Scenario
import org.springframework.stereotype.Service

@Service
class ScenarioServiceImpl : AbstractPhoenixService(), ScenarioApiService {
  override fun findAllScenarios(organizationId: String, workspaceId: String): List<Scenario> {
    TODO("Not yet implemented")
  }

  override fun findScenarioById(
      organizationId: String,
      workspaceId: String,
      scenarioId: String
  ): Scenario {
    TODO("Not yet implemented")
  }

  override fun createScenario(
      organizationId: String,
      workspaceId: String,
      scenario: Scenario
  ): Scenario {
    TODO("Not yet implemented")
  }

  override fun updateScenario(
      organizationId: String,
      workspaceId: String,
      scenarioId: String,
      scenario: Scenario
  ): Scenario {
    TODO("Not yet implemented")
  }

  override fun deleteScenario(
      organizationId: String,
      workspaceId: String,
      scenarioId: String
  ): Scenario {
    TODO("Not yet implemented")
  }
}
