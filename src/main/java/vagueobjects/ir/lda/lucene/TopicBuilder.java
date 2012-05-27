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

import vagueobjects.ir.lda.gibbs.Result;
import vagueobjects.ir.lda.gibbs.Sampler;

import java.io.IOException;
import java.io.Writer;

/**
 * Builds topic sets from the external data.
 */
public class TopicBuilder {
    /** Number of most significant terms that represent the topic*/
    public static final int NUM_TOKENS_PER_TOPIC = 10;

    /**
     * Tokens that occur in less than {@code minDocFreq} documents are considered too rare
     * to be included in vocabulary
     */
    private final Sampler sampler;

    private final WordProcessor processor;

    public TopicBuilder(WordProcessor processor,  int numTopics) {
        this.processor = processor;
        this.sampler = new Sampler(numTopics);
    }

    /**
     * Executes topic extraction
     * @return result of computation
     * @throws IOException thrown when low level IO Exception occurs
     */
    public Result extractTopics() throws IOException{
        processor.process();
        String[] vocabulary = processor.getVocabulary();

        int[][] termsInDocs = processor.getTermsInDocs();
        sampler.sample(termsInDocs, vocabulary.length);
        return new Result(sampler);
    }

    /**
     * Prints tokens to an external writer
     * @throws IOException when low level IO exception occurs.
     */
    public void printTokens(Writer writer) throws IOException{
        String[] vocabulary = processor.getVocabulary();
        int topicNumber = 0;
        for(String[] tokens:   sampler.topicsAndTerms(vocabulary)){
            writer.write(String.valueOf(++topicNumber));
            writer.write("\t");
            for(int i=0; i< NUM_TOKENS_PER_TOPIC;++i){
                writer.write(tokens[i]);
                writer.write(' ');
            }
            
            writer.write('\n');
        }
        writer.flush();
    }
   
}