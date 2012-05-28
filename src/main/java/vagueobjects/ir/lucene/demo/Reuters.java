package vagueobjects.ir.lucene.demo;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import vagueobjects.ir.lda.lucene.TopicBuilder;
import vagueobjects.ir.lda.lucene.WordFilter;
import vagueobjects.ir.lda.lucene.WordProcessor;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Reuters {
    static Logger logger = Logger.getLogger(Reuters.class);
    final static String FILE = "reuters21578.tar.gz";
    final static String  SEPARATOR = System.getProperty("file.separator");
    final static String SOURCE=  "http://kdd.ics.uci.edu/databases/reuters21578/" + FILE;
    final String  indexPath;
    final int vocabularySize;
    final int numTopics;

    Reuters(String indexPath, int vocabularySize, int numTopics){
        this.indexPath = indexPath;
        this.vocabularySize = vocabularySize;
        this.numTopics = numTopics;
    }

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        if(args.length!=3){
            System.out.println("Expected arguments: output path, vocabulary size and number of topics");
            return;
        }
        String path = args[0];
        int vocabularySize = Integer.parseInt(args[1]);
        int numTopics = Integer.parseInt(args[2]);
        logger.info("Output path: '" + path + "', vocabulary size ="
                + vocabularySize + ", number of topics=" + numTopics);
        String indexPath = path  + SEPARATOR + "index";

        Reuters reuters = new Reuters(indexPath, vocabularySize, numTopics);
        
        if(!new File(indexPath).exists()){
            String source= reuters.download(path);
            reuters.buildLuceneIndex(source, indexPath);
        } else {
            logger.info("Index already exists");
        }
        reuters.extractTopicsFromLuceneIndex(indexPath, vocabularySize, numTopics);

    }

    String download(String dir) throws IOException {
        logger.info("downloading source...");
        URL url = new URL(SOURCE);
        FileOutputStream fos = null;
        BufferedInputStream in = null;
        String destination = dir + SEPARATOR + FILE;
        try{
            in = new BufferedInputStream(url.openStream());
            fos = new FileOutputStream(destination);
            byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fos.write(data, 0, count);
            }
            logger.info("download complete");
            return destination;
        }finally {
            if(fos != null) fos.close();
            if(in != null) in.close();
        }
    }

    /**
     * Given Lucene index, extracts topics.
     */
    
    void extractTopicsFromLuceneIndex(String indexPath,
            int vocabSize, int numTopics) throws IOException{
        //This index includes a single field, named "text"
        String[] fieldNames = {"text"};
        WordFilter filter = new AlphaFilter();
        WordProcessor processor = new WordProcessor(indexPath, vocabSize,filter, fieldNames)
                .withRequiredTerms();
        TopicBuilder builder = new TopicBuilder( processor, numTopics);
        builder.extractTopics();
        //Display topics
        Writer writer = new OutputStreamWriter(System.out);
        builder.printTokens(writer);
        writer.flush();

    }
    
    
    /**
     * Indexes Reuters data.
     */
    void buildLuceneIndex(String src, String indexPath) throws IOException {
        Version version = Version.LUCENE_36;
        Directory directory = new NIOFSDirectory(new File(indexPath));
        ReutersProcessor processor = new ReutersProcessor(src);
        IndexWriterConfig config = new IndexWriterConfig(version, new StandardAnalyzer(version));
        IndexWriter indexWriter = new IndexWriter(directory, config);
        processor.process(indexWriter);
        indexWriter.commit();
    }
    

  
}