package org.apache.nutch.parse.bioschemas;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.any23.extractor.html.TagSoupParser;
import org.apache.any23.extractor.microdata.ItemScope;
import org.apache.any23.extractor.microdata.MicrodataParserReport;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.protocol.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

public class BioschemasParserFilter implements HtmlParseFilter {
	
	private Configuration conf = null;
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public Configuration getConf() {
		return this.conf;
	}

	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
		
	}

	@Override
	public ParseResult filter(Content content, ParseResult parseResult, HTMLMetaTags metaTags, DocumentFragment doc) {    
		try {
			Map<String, StringBuilder> jsonItems = parse(nodeToString(doc), content.getUrl());
			Metadata metadata = parseResult.get(content.getUrl()).getData().getParseMeta();
			StringBuilder jsonOutput = new StringBuilder("{");
			jsonItems.forEach((key, value)-> jsonOutput.append(key + ": [" + value + "]"));
			jsonOutput.append("}");
			metadata.add("bioschemas", jsonOutput.toString());
		} catch (IOException e1) {
			LOG.error("Failed to parse microdata");
			e1.printStackTrace();
		}
		return parseResult;
	}
	

	private String nodeToString(Node node) {
	  StringWriter sw = new StringWriter();
	  try {
	    Transformer t = TransformerFactory.newInstance().newTransformer();
	    t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    t.transform(new DOMSource(node), new StreamResult(sw));
	  } catch (TransformerException te) {
	    System.out.println("nodeToString Transformer Exception");
	  }
	  return sw.toString();
	}
	
	
	private Map<String, StringBuilder> parse(String html, String uri) throws IOException {
		InputStream stream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        final TagSoupParser tagSoupParser = new TagSoupParser(stream, uri);
        
        final MicrodataParserReport report = org.apache.any23.extractor.microdata.MicrodataParser.getMicrodata(tagSoupParser.getDOM());
        final ItemScope[] itemScopes = report.getDetectedItemScopes();
        Map<String, StringBuilder> jsonItems = new HashMap<String, StringBuilder>();
        for(ItemScope item : itemScopes) {
        	String type = item.getType().toString();
        	type = type.substring(type.lastIndexOf('/') + 1);
        	if(jsonItems.containsKey(type)) {
        		jsonItems.get(type).append(", " + item.toJSON());
        	} else {
        		StringBuilder firstItem = new StringBuilder(item.toJSON());
        		jsonItems.put(type, firstItem);
        	}
        }
        
        return jsonItems;
	}
	

}
