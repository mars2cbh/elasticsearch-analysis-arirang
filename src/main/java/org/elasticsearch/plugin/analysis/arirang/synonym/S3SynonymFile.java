package org.elasticsearch.plugin.analysis.arirang.synonym;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.text.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.synonym.SolrSynonymParser;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.elasticsearch.SpecialPermission;
import org.elasticsearch.env.Environment;

public class S3SynonymFile implements SynonymFile {

  private static final Logger logger = LogManager.getLogger("arirang-synonym");

  private String format;
  private boolean expand;
  private Analyzer analyzer;
  private Environment env;
  private String region;
  private String bucket;
  private String key;

  public S3SynonymFile(Environment env, Analyzer analyzer,
      boolean expand, String format, String region, String bucket, String key) {
    this.analyzer = analyzer;
    this.expand = expand;
    this.format = format;
    this.env = env;
    this.bucket = bucket;
    this.key = key;
    this.region = region;
  }

  SynonymMap.Builder getSynonymParser(
      Reader rulesReader, String format, boolean expand, Analyzer analyzer
  ) throws IOException, ParseException {
    SynonymMap.Builder parser;
    if ("wordnet".equalsIgnoreCase(format)) {
      parser = new WordnetSynonymParser(true, expand, analyzer);
      ((WordnetSynonymParser) parser).parse(rulesReader);
    } else {
      parser = new SolrSynonymParser(true, expand, analyzer);
      ((SolrSynonymParser) parser).parse(rulesReader);
    }
    return parser;
  }

  @Override
  public SynonymMap reloadSynonymMap() {
    Reader rulesReader = null;
    try {
      logger.info("start reload s3 synonym from {}/{}.", bucket, key);
      rulesReader = getReader();

      SynonymMap.Builder parser;
      parser = getSynonymParser(rulesReader, format, expand, analyzer);
      return parser.build();

    } catch (Exception e) {
      logger.error("reload s3 synonym {}/{} error!", bucket, key, e);
      throw new IllegalArgumentException(
          "could not reload remote synonyms file to build synonyms", e);
    } finally {
      if (rulesReader != null) {
        try {
          rulesReader.close();
        } catch (Exception e) {
          logger.error("failed to close rulesReader", e);
        }
      }
    }
  }

  @Override
  public Reader getReader() {

    try {
      SpecialPermission.check();
      return AccessController.doPrivileged((PrivilegedExceptionAction<Reader>) () -> {
        AmazonS3 s3Client = getS3Client();
        return new InputStreamReader(s3Client.getObject(bucket, key).getObjectContent());
      });

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private AmazonS3 getS3Client() {
    Regions clientRegion = Regions.fromName(this.region.toLowerCase());
    return AmazonS3ClientBuilder.standard()
        .withRegion(clientRegion)
        .withCredentials(new DefaultAWSCredentialsProviderChain())
        .build();
  }
}
