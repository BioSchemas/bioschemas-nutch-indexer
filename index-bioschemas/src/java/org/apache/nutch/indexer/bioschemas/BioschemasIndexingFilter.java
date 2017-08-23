package org.apache.nutch.indexer.bioschemas;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.parse.Parse;

public class BioschemasIndexingFilter implements IndexingFilter{
	
	Configuration conf = null;

	@Override
	public Configuration getConf() {
		return this.conf;
	}

	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	@Override
	public NutchDocument filter(NutchDocument doc, Parse parse, Text url, CrawlDatum datum, Inlinks inlinks)
			throws IndexingException {
		doc.add("bioschemas", parse.getData().getParseMeta().get("bioschemas"));
		return doc;
	}
	
}