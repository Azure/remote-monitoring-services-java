API Specification - Action Settings
======================================

## Get a list of actions settings

The list of configuration settings for all action types.

The ApplicationPermissionsAssigned attribute indicates whether or not the application has
been given "Contributor" permissions in order to make management API calls. If the application
does not have "Contributor" permissions, then users will need to check on resources manually from
the Azure portal. This can happen when the user deploying the application does not have "Owner"
permissions in order to assign the application the necessry permissions.

Request:
```
GET /v1/solution-settings/actions
```

Response:
```
200 OK
Content-Type: application/json
```
```json
{
    "Items": [
        {
            "Type": "Email",
            "Settings": {
                "IsEnabled": false,
                "ApplicationPermissionsAssigned": false,
                "Office365ConnectorUrl": "https://portal.azure.com/#@{tenant}/resource/subscriptions/{subscription}/resourceGroups/{resource-group}/providers/Microsoft.Web/connections/office365-connector/edit"
            }
        }
    ],
    "$metadata": {
        "$type": "ActionSettingsList;1",
        "$url": "/v1/solution-settings/actions"
    }
}
```
