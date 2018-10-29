API Specification - Action Settings
======================================

## Get a list of actions settings

The list of configuration settings for all action types.

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