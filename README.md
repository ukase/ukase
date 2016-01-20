# ukase

## Get&amp;configure
Download release from [from GitHub](https://github.com/ukase/ukase/releases/download/Ukase-1.0/ukase-1.0.war)

Download using maven:
```
mvn dependency:get -Dartifact=com.github.ukase:ukase:1.0:war -Dtransitive=false -Ddest=ukase.jar
```

After downloading create `config` directory and place here `application.yml` file, prod sample:
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
  jar: absolute-path-to-jar-with-templates_resources_fonts/templates_fonts_resources.jar
```
dev sample:
```
spring:
  main:
    banner-mode: off
server:
  port: 10080
logging:
  config: classpath:logback-dev.xml
ukase:
  project-root: .
  resources: absolute-path-to-dir-with-templates_fonts
  templates: absolute-path-to-dir-with-templates
  jar: absolute-path-to-jar-with-templates_resources_fonts/templates_fonts_resources.jar
```
*note*: it is possible to use both - absolute and relative paths.

## Usage

In case of prod - propagated that main usage goes over UKase API

In Case of dev - propagated usage of UI that enables in real time see what changes made to current template
(index.html is available over configured port at your localhost)

Application can be started in some container (for example - tomcat) or started with SpringBoot option:
```
java -jar ukase-1.0.war
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

### method GET /api/pdf/templateName

templateName - template name you interested in or ANY 

[possible errors](#possible-errors)

return type: none

return code:
- 304 Modified (if some template modified in long-polling request)
- some other (not checked real return code for now) in case of time out 

This method is for long-polling request to check whether it is template or resource changed or not. In UI it is used to
refresh pdf in view at any updates.
In case of `ANY` template name - it works for every template and resources.
In case of specified template name - it works for only specified template (no updates on partials) and resources.

#### UkasePayload
```
{
    "index": "template-name", // mandatory field, should contain real template name that exists in service templates path
    "sample": true, // non-mandatory field (defaults - false), turns on configured 'sample' watermark
    "data": { // data object that will be passed to handlebars template as context at render stage
    }
}
```

#### Possible errors
If "index" is null or contains wrong field value - `400 Bad Request` answer will be returned with collection of failed validations:
```
[{"field":"index","object":"ukasePayload","message":"No such HTML template"}]
```

## License
Ukase is available over GNU Affero General Public License ([see more information here](http://www.gnu.org/licenses/));