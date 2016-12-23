# ukase
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.ukase/ukase/badge.svg?style=flat)](http://mvnrepository.com/artifact/com.github.ukase/ukase)

Ukase is renderer for `html`, `pdf` and simple `xlsx` files written as service with Java 8.
Releases provided as self-runnable `war` that bundled with Spring Boot. 

## Get&amp;configure

### Get
There are two ways:
- Download release [from GitHub](https://github.com/ukase/ukase/releases/latest) (ukase-x.x.x.war file)
- Extend/get/repack with maven build system:
```
<dependency>
    <groupId>com.github.ukase</groupId>
    <artifactId>ukase</artifactId>
    <version>LATEST</version>
</dependency>
```
- Download release using maven shell command (buggy method to get - work well only if you have copy in local repo, otherwise problems are possible):
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
- in your web application container (like tomcat or jetty)
- using included SpringBoot runner: `java -jar name_of_saved_war.war`

## Usage

We propogate next usage templates:
* prod
  - using by some subsystem of other application to generate pdf-s over [UKase API](#UKase_API)
* development
  - using by development environment of any application to generate pdf-s over [UKase API](#UKase_API)
  - using by developer to create and modify pdf templates. For these case we have UI that enables view and view's auto-refresh on any template change. 

## Xlsx rendering feature
[Xlsx rendering feature docs](docs/xlsx.md)

## UKase API

Short list [(more)](docs/api.md):
* [POST /api/html](docs/api.md#post-apihtml)
* [POST /api/pdf](docs/api.md#post-apipdf)
* [GET /api/pdf/{templateName}](docs/api.md#get-apipdftemplatename)
* [POST /api/bulk](docs/api.md#post-apibulk)
* [POST /api/bulk/sync](docs/api.md#post-apibulksync)
* [GET /api/bulk/status/{id}](docs/api.md#get-apibulkstatusid)
* [GET /api/bulk/{id}](docs/api.md#get-apibulkid)
* [POST /api/xlsx](docs/api.md#post-apixlsx-new)

## License :scroll:
Ukase is available over GNU Affero General Public License ([see more information here](http://www.gnu.org/licenses/));