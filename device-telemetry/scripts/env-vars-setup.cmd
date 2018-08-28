:: Prepare the environment variables used by the application.

:: Url for the PCS Auth Webservice
SETX PCS_AUTH_WEBSERVICE_URL "..."

:: Connection information for Azure DocumentDb
SETX PCS_TELEMETRY_DOCUMENTDB_CONNSTRING "..."

:: Url for the PCS Storage Adapter Webservice
SETX PCS_STORAGEADAPTER_WEBSERVICE_URL "..."

:: The OpenId tokens issuer URL, e.g. https://sts.windows.net/12000000-3400-5600-0000-780000000000/
SETX PCS_AUTH_ISSUER "{enter the token issuer URL here}"

:: The intended audience of the tokens, e.g. your Client Id
SETX PCS_AUTH_AUDIENCE "{enter the tokens audience here}"

:: The tenant for the Azure Active Directory application
:: see: Azure Portal => Azure Active Directory => Properties => Directory ID
SETX PCS_AAD_TENANT="{enter the Azure Active Directory Tenant for the application here}"

:: The Application ID registered with Azure Active Directory
:: see: Azure Portal => Azure Active Directory => App Registrations => Your App => Application ID
SETX PCS_AAD_APPID="{enter Azure Active Directory application ID here}"

:: The Application Secret for your Azure Active Directory Application
:: see: Azure Portal => Azure Active Directory => App Registrations => Your App => Settings => Passwords
SETX PCS_AAD_APPSECRET="{enter your application secret here}"

:: The storage type for telemetry messages. Default is "tsi". Allowed values: ["cosmosdb", "tsi"]
SETX PCS_TELEMETRY_STORAGE_TYPE="tsi"

:: The FQDN (Fully Qualified Domain Name) for the Time Series endpoint
:: see: Azure Portal => Your Resource Group => Time Series Insights Environment => Data Access FQDN
SETX PCS_TSI_FQDN="{enter your Time Series FQDN here}"