package org.elasticsearch.plugin.analysis.arirang.synonym;

import java.io.Reader;
import org.apache.lucene.analysis.synonym.SynonymMap;

public interface SynonymFile {
  SynonymMap reloadSynonymMap();
  Reader getReader();
}
