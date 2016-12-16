# Extended API
This is a Fisheye/Crucible plugin. In FeCru version 4.2.0 the comment resolution status was introduced. There is a Java API to query the status but there is no REST API endpoint. This plugin establishes a new REST API endpoint:

## Endpoint
`(...)/rest/extended-api/1/comments/{cId}/resolutionStatus`
- [com.atlassian.crucible.spi.data.CommentResolutionData](https://docs.atlassian.com/fisheye-crucible/4.2.0/javadoc/com/atlassian/crucible/spi/data/CommentResolutionData.html)
- [com.atlassian.crucible.spi.data.CommentResolutionStatus](https://docs.atlassian.com/fisheye-crucible/4.2.0/javadoc/com/atlassian/crucible/spi/data/CommentResolutionStatus.html)

You can communicate with the API per `JSON` and `XML`.

## Example Response
```json
{
  "created": 1481552398827,
  "status": "UNRESOLVED",
  "user": {
    "userName": "admin",
    "displayName": "A. D. Ministrator",
    "avatarUrl": "http://MBP1.local:3990/fecru/avatar/admin?s=48",
    "url": "/fecru/user/admin"
  }
}
```
Get familiar with `javax.ws.rs.client.*` to consume `de.willert.crucible.extendedapi.CommentResolutionData` or use any other JSON/XML parser.

The client must be authenticated to access this resource.

## TODOS
- [ ] Response errors should be json/xml encoded