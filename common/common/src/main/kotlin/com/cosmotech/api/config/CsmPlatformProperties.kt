// Copyright (c) Cosmo Tech.
// Licensed under the MIT license.
package com.cosmotech.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/** Configuration Properties for the Cosmo Tech Platform */
@ConstructorBinding
@ConfigurationProperties(prefix = "csm.platform")
data class CsmPlatformProperties(

    /** the Platform summary */
    val summary: String?,

    /** the Platform description */
    val description: String?,

    /** the Platform version (MAJOR.MINOR.PATCH). */
    val version: String?,

    /** the Platform exact commit ID. */
    val commitId: String? = null,

    /** the Platform exact Version-Control System reference. */
    val vcsRef: String? = null,

    /** API Configuration */
    val api: Api,

    /** Platform vendor */
    val vendor: Vendor,

    /** Id Generator */
    val idGenerator: IdGenerator,

    /** Event Publisher */
    val eventPublisher: EventPublisher,

    /** Azure Platform */
    val azure: CsmPlatformAzure?,

    /** Argo Service */
    val argo: Argo,

    /** Cosmo Tech core images */
    val images: CsmImages,

    /** Authorization Configuration */
    val authorization: Authorization = Authorization(),

    /**
     * Identity provider used for (azure : Azure Active Directory ,okta : Okta) if openapi default
     * configuration needs to be overwritten
     */
    val identityProvider: CsmIdentityProvider?,

    /** Okta configuration */
    val okta: CsmPlatformOkta?,

    /** Data Ingestion reporting behavior */
    val dataIngestion: DataIngestion = DataIngestion(),
) {

  data class Authorization(

      /** The JWT Claim to use to extract a unique identifier for the user account */
      val principalJwtClaim: String = "sub",

      /** The JWT Claim where the tenant id information is stored */
      val tenantIdJwtClaim: String = "iss",

      /**
       * List of additional tenants allowed to register, besides the configured
       * `csm.platform.azure.credentials.tenantId`
       */
      val allowedTenants: List<String> = emptyList()
  )

  data class CsmImages(
      /** Container image to fetch Scenario Parameters */
      val scenarioFetchParameters: String,

      /** Container image to send data to DataWarehouse */
      val sendDataWarehouse: String,
      val scenarioDataUpload: String = "cosmo-tech/azure-storage-publish:latest",
  )

  data class Argo(
      /** Argo service base Uri */
      val baseUri: String,

      /** Image Pull Secrets */
      val imagePullSecrets: List<String>? = null,

      /** Workflow Management */
      val workflows: Workflows,
  ) {
    data class Workflows(
        /** The Kubernetes namespace in which Argo Workflows should be submitted */
        val namespace: String,

        /** The node label to look for Workflows placement requests */
        val nodePoolLabel: String,

        /** The Kubernetes service account name */
        val serviceAccountName: String,

        /**
         * The Kubernetes storage-class to use for volume claims. Set to null or an empty string to
         * use the default storage class available in the cluster
         */
        val storageClass: String? = null,

        /** List of AccessModes for the Kubernetes Persistent Volume Claims to use for Workflows */
        val accessModes: List<String> = emptyList(),

        /**
         * Minimum resources the volumes requested by the Persistent Volume Claims should have.
         * Example: storage: 1Gi
         */
        val requests: Map<String, String> = emptyMap(),
    )
  }

  data class Api(
      /** API Version, e.g.: latest, or v1 */
      val version: String,

      /** API Base URL */
      val baseUrl: String,

      /**
       * Base path under which the API is exposed at root, e.g.: /cosmotech-api/. Typically when
       * served behind a reverse-proxy under a dedicated path, this would be such path.
       */
      val basePath: String,
  )

  data class IdGenerator(val type: Type) {
    enum class Type {
      /** short unique UIDs */
      HASHID,

      /** UUIDs */
      UUID
    }
  }

  data class EventPublisher(val type: Type) {
    enum class Type {
      /** In-process, via Spring Application Events */
      IN_PROCESS
    }
  }

  data class CsmPlatformAzure(
      /** Azure Credentials */
      val credentials: CsmPlatformAzureCredentials,
      val storage: CsmPlatformAzureStorage,
      val containerRegistries: CsmPlatformAzureContainerRegistries,
      val eventBus: CsmPlatformAzureEventBus,
      val dataWarehouseCluster: CsmPlatformAzureDataWarehouseCluster,
      val keyVault: String,
      val analytics: CsmPlatformAzureAnalytics,
      /** Azure Cosmos DB */
      val cosmos: CsmPlatformAzureCosmos,
      val appIdUri: String,
      val claimToAuthorityPrefix: Map<String, String> = mutableMapOf("roles" to "")
  ) {

    data class CsmPlatformAzureCredentials(
        /** The Azure Tenant ID (core App) */
        @Deprecated(message = "use csm.platform.azure.credentials.core.tenantId instead")
        val tenantId: String? = null,

        /** The Azure Client ID (core App) */
        @Deprecated(message = "use csm.platform.azure.credentials.core.clientId instead")
        val clientId: String? = null,

        /** The Azure Client Secret (core App) */
        @Deprecated(message = "use csm.platform.azure.credentials.core.clientSecret instead")
        val clientSecret: String? = null,

        /**
         * The Azure Active Directory Pod Id binding bound to an AKS pod identity linked to a
         * managed identity
         */
        @Deprecated(message = "use csm.platform.azure.credentials.core.aadPodIdBinding instead")
        val aadPodIdBinding: String? = null,

        /** The core App Registration credentials - provided by Cosmo Tech */
        val core: CsmPlatformAzureCredentialsCore,

        /**
         * Any customer-provided app registration. Useful for example when calling Azure Digital
         * Twins, because of security enforcement preventing from assigning permissions in the
         * context of a managed app, deployed via the Azure Marketplace
         */
        val customer: CsmPlatformAzureCredentialsCustomer? = null,
    ) {

      data class CsmPlatformAzureCredentialsCore(
          /** The Azure Tenant ID (core App) */
          val tenantId: String,

          /** The Azure Client ID (core App) */
          val clientId: String,

          /** The Azure Client Secret (core App) */
          val clientSecret: String,

          /**
           * The Azure Active Directory Pod Id binding bound to an AKS pod identity linked to a
           * managed identity
           */
          val aadPodIdBinding: String? = null,
      )

      data class CsmPlatformAzureCredentialsCustomer(
          /** The Azure Tenant ID (customer App Registration) */
          val tenantId: String?,

          /** The Azure Client ID (customer App Registration) */
          val clientId: String?,

          /** The Azure Client Secret (customer App Registration) */
          val clientSecret: String?,
      )
    }

    data class CsmPlatformAzureStorage(
        val connectionString: String,
        val baseUri: String,
        val resourceUri: String
    )

    data class CsmPlatformAzureContainerRegistries(val core: String, val solutions: String)

    data class CsmPlatformAzureEventBus(
        val baseUri: String,
        val authentication: Authentication = Authentication()
    ) {
      data class Authentication(
          val strategy: Strategy = Strategy.TENANT_CLIENT_CREDENTIALS,
          val sharedAccessPolicy: SharedAccessPolicyDetails? = null,
          val tenantClientCredentials: TenantClientCredentials? = null
      ) {
        enum class Strategy {
          TENANT_CLIENT_CREDENTIALS,
          SHARED_ACCESS_POLICY
        }

        data class SharedAccessPolicyDetails(
            val namespace: SharedAccessPolicyCredentials? = null,
        )

        data class SharedAccessPolicyCredentials(val name: String, val key: String)
        data class TenantClientCredentials(
            val tenantId: String,
            val clientId: String,
            val clientSecret: String
        )
      }
    }

    data class CsmPlatformAzureDataWarehouseCluster(val baseUri: String, val options: Options) {
      data class Options(val ingestionUri: String)
    }

    data class CsmPlatformAzureAnalytics(
        val resourceUri: String,
        val instrumentationKey: String,
        val connectionString: String
    )

    data class CsmPlatformAzureCosmos(

        /** DNS URI of the Azure Cosmos DB account */
        val uri: String,

        /** Azure Cosmos DB Database used for Phoenix */
        val coreDatabase: CoreDatabase,

        /** Access Key of the Azure Cosmos DB database */
        val key: String,

        /** Consistency level. See com.azure.cosmos.ConsistencyLevel for the possible values. */
        val consistencyLevel: String?,

        /** Whether to populate Diagnostics Strings and Query metrics */
        val populateQueryMetrics: Boolean,

        /**
         * The connection mode to be used by the clients to Azure Cosmos DB. See
         * com.azure.cosmos.ConnectionMode for the possible values.
         */
        val connectionMode: String?
    ) {
      data class CoreDatabase(
          /** The core database name in Azure Cosmos DB. Must already exist there. */
          val name: String,

          /** The Connectors configuration */
          val connectors: Connectors,

          /** The Organizations configuration */
          val organizations: Organizations,

          /** The Users configuration */
          val users: Users
      ) {
        data class Connectors(

            /** Container name for storing all Connectors */
            val container: String
        )

        data class Organizations(

            /** Container name for storing all Organizations */
            val container: String
        )

        data class Users(

            /** Container name for storing all Users */
            val container: String
        )
      }
    }
  }

  enum class Vendor {
    /** Microsoft Azure : https://azure.microsoft.com/en-us/ */
    AZURE
  }

  data class DataIngestion(
      /**
       * Number of seconds to wait after a scenario run workflow end time, before starting to check
       * ADX for data ingestion state. See https://bit.ly/3FXshzE for the rationale
       */
      val waitingTimeBeforeIngestionSeconds: Long = 15,

      /**
       * number of minutes after a scenario run workflow end time during which an ingestion failure
       * detected is considered linked to the current scenario run
       */
      val ingestionObservationWindowToBeConsideredAFailureMinutes: Long = 5,

      /** number of seconds to wait after the checking the scenario validation ingestion status */
      val sleepingTimeBeforeQueryingScenarioValidationStatusSeconds: Long = 5,

      /** number of retry to query the scenario validation status */
      val maxRetryAuthorized: Long = 5,

      /** Data ingestion state handling default behavior */
      val state: State = State()
  ) {
    data class State(
        /**
         * The timeout in second before considering no data in probes measures and control plane is
         * an issue
         */
        val noDataTimeOutSeconds: Long = 60,
    )
  }

  data class CsmIdentityProvider(
      /** okta|azure */
      val code: String,
      /**
       * entry sample :
       * - {"http://dev.api.cosmotech.com/platform" to "Platform scope"}
       * - {"default" to "Default scope"}
       */
      val defaultScopes: Map<String, String> = emptyMap(),
      /**
       * - "https://{yourOktaDomain}/oauth2/default/v1/authorize"
       * - "https://login.microsoftonline.com/common/oauth2/v2.0/authorize"
       */
      val authorizationUrl: String,
      /**
       * - "https://{yourOktaDomain}/oauth2/default/v1/token"
       * - "https://login.microsoftonline.com/common/oauth2/v2.0/token"
       */
      val tokenUrl: String,
      /**
       * entry sample :
       * - {"csm.read.scenario" to "Read access to scenarios"}
       */
      val containerScopes: Map<String, String> = emptyMap(),
      /** Custom group name used acted as Organization.Admin default: Platform.Admin */
      val adminGroup: String? = null,
      /** Custom group name used acted as Organization.User default: Organization.User */
      val userGroup: String? = null,
      /** Custom group name used acted as Organization.Viewer default: Organization.Viewer */
      val viewerGroup: String? = null,
  )

  data class CsmPlatformOkta(
      /** Okta Issuer */
      val issuer: String,

      /** Okta Application Id */
      val clientId: String,

      /** Okta Application Secret */
      val clientSecret: String,

      /** Okta Authorization Server Audience */
      val audience: String,
  )
}
