# POST /api/html

body: [UkasePayload](#ukasepayload)

[possible errors](#possible-errors)

return type: text/html

This method applies transferred data to selected template and returns rendered html as body of answer  

# POST /api/pdf

body: [UkasePayload](#ukasepayload)

[possible errors](#possible-errors)

return type: application/octet-stream

This method applies transferred data to selected template and applies rendered html with resources to flying saucer pdf generator.
Return generated pdf as array of bytes

# GET /api/pdf/{templateName}

{templateName} - template name you interested in or ANY 

[possible errors](#possible-errors)

return type: none

return code:
- 304 Modified (if some template modified in long-polling request)
- some other (not checked real return code for now) in case of time out 

This method is for long-polling request to check whether it is template or resource changed or not. In UI it is used to
refresh pdf in view at any updates.
In case of `ANY` template name - it works for every template and resources.
In case of specified template name - it works for only specified template (no updates on partials) and resources.

# POST /api/bulk/sync

body: array of [UkasePayload](#ukasepayload)

return type: byte array of bundled pdf

return code:
- 200 Ok - pdf file generated
- 400 Bad Request - request were failed to be added to queue or processed 
- 500 Internal Server Error - request were interrupted (possible that server stopped at moment) 

Synchronous method to process bulk of PDFs.

# POST /api/xlsx :new:

body: [UkasePayload](#ukasepayload)

[possible errors](#possible-errors)

return type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

Processes request in order:
* render xhtml according requested template (powered by 
* parse xthml and css styles to DOM (powered by Flying Saucer)
* render xlsx file (powered by Apache POI:SXSSF)
* return rendered xlsx as bytes array

# Async API

## POST /api/async/pdf/bulk
 moved at 4.0 from `POST /api/bulk`

body: array of [UkasePayload](#ukasepayload)

return type: UUID in string mode

return code:
- 200 Ok - task were applied to work
- 400 Bad Request - request were failed to be added to queue 

Asynchronous method to add task (building pdf bulk) in queue. Answers contains string with UUID that assigned
to the task.

## POST /api/async/xlsx
since 4.0

body: array of [UkasePayload](#ukasepayload)

return type: UUID in string mode

return code:
- 200 Ok - task were applied to work
- 400 Bad Request - request were failed to be added to queue 

Asynchronous method to add task (building pdf bulk) in queue. Answers contains string with UUID that assigned
to the task.

## GET /api/async/{id}/status
 moved at 4.0 from `GET /api/bulk/status/{id}`
 
`{id}` - UUID of queued task

body: none

return type: [Status](#status)

return code:
* in case of option 'ukase.bulk.statusCodes' {true}:
  - 200 Ok - task finished
  - 404 Not Found - task not finished yet 
  - 400 Bad Request - task were failed 
* in case of option 'ukase.bulk.statusCodes' {false}:
  - 200 Ok - status provided only in answer body

Check status for task in queue 

## GET /api/async/{id}
moved at 4.0 from `GET /api/bulk/{id}`

`{id}` - UUID of queued task

body: none

return type: rendered in task byte array

return code:
- 200 Ok - pdf file generated
- 400 Bad Request - pdf bulk request not ready yet or already failed 

Return byte array if pdf already rendered.


# Defined types

## UkasePayload
```
{
    "index": "template-name", // mandatory field, should contain real template name that exists in service templates path
    "sample": true, // non-mandatory field (defaults - false), turns on configured 'sample' watermark
    "data": { // data object that will be passed to handlebars template as context at render stage
    }
}
```

## Status
```
{
    "status": "error", // possible values:
                       //     error - pdf bulk weren't bundled - some error while processing,
                       //     processing - pdf bulk in order to be processed or still processing,
                       //     ready - pdf bulk were bundled
}
```

## Possible errors
If "index" is null or contains wrong field value - `400 Bad Request` answer will be returned with collection of failed validations:
```
[{"field":"index","object":"ukasePayload","message":"No such HTML template"}]
```
