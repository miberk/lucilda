package vagueobjects.ir.lda.lucene;

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


import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * A utility that builds a new Lucene index using some existing Lucene index.
 * E.g., one may want to work with documents of a certain category or most recent documents.
 * Thw new index includes a single field, named "text" , that combines tokens from the source index.
 */
public class Indexer {
    static Logger logger = Logger.getLogger(Indexer.class);

    public static final String FIELD = "text";
    private final Version version; 
    private final Directory directory;
    private final Analyzer analyzer;

    
    public Indexer(Version version, String path) throws IOException {
        File dir = new File(path);
        if(dir.exists()){
            throw new IllegalArgumentException("Directory " + dir + " already exists");
        }
        this.directory = new NIOFSDirectory(dir);
        this.version = version;
        this.analyzer = new StandardAnalyzer(version);

    }
    
    public void makeSubIndex(IndexReader reader, Term term, String...fieldNames )
            throws ParseException, IOException {
        if(fieldNames.length<2){
            throw new IllegalArgumentException("At least 1 field name is expected");
        }
 
        IndexWriter writer =new IndexWriter(directory,
                new IndexWriterConfig(version, analyzer));

        collect(term, reader, writer, fieldNames); 
        writer.commit();
    }
 
    /**
     * Creates {@code WordProcessor} from extracted index
     * @param vocabSize  - size of the vocabulary to use
     * @return     new instance of {@code WordProcessor}
     * @throws IOException - if low level IO error occurred
     */
    public WordProcessor create(int vocabSize, WordFilter filter) throws IOException {
        IndexReader reader = IndexReader.open(directory);
        return new WordProcessor(reader, vocabSize, filter,   FIELD);
    }
    
    private void collect(Term term, IndexReader reader, IndexWriter writer, String[] fieldNames) throws IOException {
        int numDocs = reader.numDocs();
        String field = term.field();
        String value = term.text();
        int count =0;
        for(int d=0; d< numDocs; ++d){

            Document source = reader.document(d);
            if(!reader.isDeleted(d) && value.equals(source.get(field))){
                ++count;
                if(count%100000==0){
                    logger.debug("Passed " +  count+ "  documents");
                }
                Document document = new Document();
                for(String fieldName: fieldNames){
                    String v  = source.get(fieldName);
                    if(v !=null){
                        document.add(new Field(FIELD, v , Field.Store.YES, Field.Index.ANALYZED));
                    }
                }
                writer.addDocument(document);
            }
        }
        if(count==0){
            throw new IllegalStateException("No matching documents found");
        }
    }
    

}
