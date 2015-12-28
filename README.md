# ukase

## UKase API

### method POST /api/html
body: [UkasePayload](#UkasePayload)
[possible errors](#PossibleErrors)
return type: text/html

This method applies transferred data to selected template and returns rendered html as body of answer  

### method POST /api/pdf
body: [UkasePayload](#UkasePayload)
[possible errors](#PossibleErrors)
return type: application/octet-stream

This method applies transferred data to selected template and applies rendered html with resources to flying saucer pdf generator.
Return generated pdf as array of bytes

#### UkasePayload
```
{
    "index": "template-name", // mandatory field, should contain real template name that exists in service templates path
    "data": { // data object that will be passed to handlebars template as context at render stage
    }
}
```

#### Possible errors
If "index" is null or contains wrong field value - `400 Bad Request` answer will be returned with collection of failed validations:
```
[{"field":"index","object":"ukasePayload","message":"No such HTML template"}]
```