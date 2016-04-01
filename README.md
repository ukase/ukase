# ukase

## Get&amp;configure

### Get
There are two ways:
- Download release [from GitHub](https://github.com/ukase/ukase/releases/download/Ukase-1.3/ukase-1.3.war)
- Download release using maven:
```
mvn dependency:get -Dartifact=com.github.ukase:ukase:LATEST:war -Dtransitive=false -Ddest=ukase.jar
```

### Configure
1. Create directory for this application
2. Move there downloaded `war` file
3. Create `config` subfolder
4. Create `application.yml` file there:
  - [sample for development environment](/samples/dev/application.yml)
  - [sample for prod environment](/samples/prod/application.yml)
  
### Start

You can start application:
- in your web application container (such as tomcat or jetty)
- using included SpringBoot runner: `java -jar name_of_saved_war.war`

## Usage

We propogate next usage templates:
* prod
  - using by some subsystem of other application to generate pdf-s over [UKase API](#UKase_API)
* development
  - using by development environment of any application to generate pdf-s over [UKase API](#UKase_API)
  - using by developer to create and modify pdf templates. For these case we have UI that enables view and view's auto-refresh on any template change. 

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

### :new: method POST /api/bulk

body:
:boom: not finished yet

### :new: method POST /api/bulk/sync

body:
:boom: not finished yet

### :new: method HEAD /api/bulk/$id

:boom: not finished yet

### :new: method GET /api/bulk/$id

:boom: not finished yet


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

## License :scroll:
Ukase is available over GNU Affero General Public License ([see more information here](http://www.gnu.org/licenses/));