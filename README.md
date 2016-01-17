# ukase

## Get&amp;install
Download release from [from GitHub](https://github.com/ukase/ukase/releases/download/Ukase-1.0/ukase-1.0.war)

Download using maven:
```
mvn dependency:get -Dartifact=com.github.ukase:ukase:1.0:war -Dtransitive=false -Ddest=ukase.jar
```

After downloading create `config` directory and place here `application.yml` file, sample:
```
spring:
  main:
    banner-mode: off
server:
  port: 10080
logging:
  config: classpath:logback-prod.xml
ukase:
  project-root: .
  resources: 
  templates: 
  jar: absolut-path-to-jar-with-templates_resources_fonts/templates_fonts_resources.jar
```

## UKase API

### method POST /api/html

body: [UkasePayload](#ukasepayload)

[possible errors](#possible-errors)

return type: text/html

This method applies transferred data to selected template and returns rendered html as body of answer  

### method POST /api/pdf

body: [UkasePayload](#ukasepayload)

[possible errors](#possible-errors)

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