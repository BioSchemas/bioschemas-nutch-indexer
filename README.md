# bioschemas-nutch-indexer
An Apache Nutch plugin to extract and index bioschemas data from websites

## Installation
### Downloading and Running ElasticSearch 2.3.3
1. Download ES 2.3.3 from [here](https://www.elastic.co/downloads/past-releases/elasticsearch-2-3-3).
2. Extract the elasticsearch-2.3.3.zip
```shell
./elasticsearch
```

### Apache Nutch Install and Setup
1. Download the binary package (apache-nutch-1.13-bin.zip) of Apache Nutch 1.13 from [here](http://www.apache.org/dyn/closer.cgi/nutch/).
2. Extract apache-nutch-1.13-bin.zip to your favorite location (from now on NUTCH\_HOME)
3. Change the http.agent.name property in the NUTCH\_HOME/conf/nutch-site.xml file. It should look something like this:
```xml
<property>
 <name>http.agent.name</name>
 <value>My Bioschemas Spider</value>
</property>
```
4. Change the elastic.host and elastic.port properties in the NUTCH\_HOME/conf/nutch-site.xml file. It should look something like this:
```xml
<property>
  <name>elastic.host</name>
  <value>127.0.0.1</value>
  <description>Comma-separated list of hostnames to send documents to using
  TransportClient. Either host and port must be defined or cluster.</description>
</property>

<property> 
  <name>elastic.port</name>
  <value>9300</value>
  <description>The port to connect to using TransportClient.</description>
</property>
```

### Download and install the plugin
1. Download the binary distribution for the index-bioschemas plugin available at [the releases page](https://github.com/BioSchemas/bioschemas-nutch-indexer/releases) in this repo.
2. Copy the plugin folder to NUTCH\_HOME/plugins
3. Copy the mimetypes.jar file from [here](https://github.com/BioSchemas/bioschemas-nutch-indexer/blob/master/lib/mimetypes.jar) to NUTCH\_HOME/lib
4. Edit the plugin.includes property in the NUTCH\_HOME/conf/nutch-site.xml file so it uses the index-bioschemas plugin:
```xml
<property>
  <name>plugin.includes</name>
  <value>protocol-http|urlfilter-regex|parse-(html|tika)|index-(basic|anchor|bioschemas)|scoring-opic|urlnormalizer-(pass|regex|basic)</value>
  <description>Regular expression naming plugin directory names to
  include.  Any plugin not matching this expression is excluded.
  In any case you need at least include the nutch-extensionpoints plugin. By
  default Nutch includes crawling just HTML and plain text via HTTP,
  and basic indexing and search plugins.
  </description>
</property>
```

### Testing your installation
```shell
cd NUTCH_HOME
mkdir urls
cd urls
touch seed.txt
echo "https://tess.elixir-europe.org/events/uppmax-introductory-course-summer-2017" >> seed.txt
echo "https://tess.elixir-europe.org/events/the-genomics-era-the-future-of-genetics-in-medicine-6c93a777-54db-49f8-86f9-3be4bd91f87a" >> seed.txt
cd ..
bin/crawl -i urls tess-events/ 1
```
Then go to http://localhost:9200/nutch/_search?pretty=true&q=*:*&size=100

The documents body have several fields coming from the data crawled by Nutch, such as the plain text content (content), the crawling time stamp (tstamp), the source url (id) and the page title (title) among others. Inside the 'bioschemas' field you will find a JSON String containing the JSON representation of the microdata extraction result. This result is a JSON object, each field have the name of one item type coming from the extracted microdata, in this example we have "BreadCrumbList" and "Event". In those fields you will find JSON arrays with the JSON Object representation of the collected items.

## Apache Nutch documentation
Please find more information about how to use Apache Nutch in order to crawl websites [here](https://wiki.apache.org/nutch/NutchTutorial)

## Setting up the development environment

In order to build Apache Nutch with the plugin you will [Apache Ant](http://ant.apache.org/).

1. Download the apache-nutch-1.13-src.tar.gz file from [here](http://www.apache.org/dyn/closer.cgi/nutch/)
2. Extract the content to your favorite location (NUTCH\_SRC\_HOME)
3. Make sure you can build Apache Nutch source out of the box
```shell
cd NUTCH_SRC_HOME
ant
```
Now you will see a ready to use binary install for Nutch in the path NUTCH\_SRC\_HOME/runtime/local
3. Clone this repo and copy the [index-bioschemas](https://github.com/BioSchemas/bioschemas-nutch-indexer/tree/master/index-bioschemas) folder into NUTCH\_SRC\_HOME/src/plugin
4. Edit the NUTCH\_SRC\_HOME/src/plugin/build.xml in order to add the plugin so Ant can deploy it, test it and clean it.
Insert:
```xml
<ant dir="index-bioschemas" target="deploy"/>
```
In line 33:
```xml
<ant dir="index-bioschemas" target="test"/>
```
In line 104 and:
```xml
<ant dir="index-bioschemas" target="clean"/>
```
5. Edit the NUTCH\_SRC\_HOME/build.xml to make available the plugin packageset to Ant.

Insert:
```xml
<packageset dir="${plugins.dir}/index-bioschemas/src/java"/>
```

In lines 181 and 627.

6. Edit the NUTCH\_SRC\_HOME/build.xml to make available the plugin source path to Ant.
Insert:
```xml
<source path="${plugins.dir}/index-bioschemas/src/java"/>
```
In line 1031.

7. Edit the NUTCH\_SRC\_HOME/default.properties to tell Apache Nutch that we want our plugin included in the build process.
Insert:
```java
org.apache.nutch.parse.bioschemas*:\
```
In line 149 and Insert:
```java
org.apache.nutch.indexer.bioschemas*:\
```
In line 160.

8. There is a [missing dependency for Apache Any23](https://issues.apache.org/jira/browse/ANY23-170) in the Maven Repositories so you will have to add it manually to your Ivy local repo.

Download the missing jar from [here](http://svn.apache.org/repos/asf/any23/repo-ext/org/apache/commons/commons-csv/1.0-SNAPSHOT-rev1148315/). Change its name to commons-csv.jar and put it in HOME/.ivy2/local/org.apache.commons/commons-csv/1.0-SNAPSHOT-rev1148315/jars/

9. Got to NUTCH\_SRC\_HOME/ and run ant.

10. Now the NUTCH\_SRC\_HOME/runtime/local installation will include the plugin. In order to make it work correctly you will have to copy the [mimetypes.jar](https://github.com/BioSchemas/bioschemas-nutch-indexer/blob/master/lib/mimetypes.jar) to NUTCH\_SRC\_HOME/runtime/local/lib/

11. You can now test your installation. Execute steps 3 and 4 of the [Apache Nutch Install and Setup](https://github.com/BioSchemas/bioschemas-nutch-indexer#apache-nutch-install-and-setup) section and step 4 from the [plugin install](https://github.com/BioSchemas/bioschemas-nutch-indexer#download-and-install-the-plugin) section.

12. Run the commands in the [Test your installation](https://github.com/BioSchemas/bioschemas-nutch-indexer#testing-your-installation) section using NUTCH\_SRC\_HOME/runtime/local insted of NUTCH\_HOME.

Remember to run ant from the NUTCH\_SRC\_HOME every time you make a change in the code.

13. [OPTIONAL] From NUTCH\_SRC\_HOME run:
```shell
ant eclipse
```
In order to generate the Eclipse project files so you can later import the project to the IDE. If you want to run Nutch from Eclipse you will have to follow this [guide](https://wiki.apache.org/nutch/RunNutchInEclipse).
