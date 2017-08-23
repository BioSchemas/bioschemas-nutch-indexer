# bioschemas-nutch-indexer
An Apache Nutch plugin to extract and index bioschemas data from websites

## Requirements
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
cd NUTCH\_HOME
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
