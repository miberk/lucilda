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


/**
 * Implementations of this class define what tokens (words) can be accepted as candidates to
 * build topic vocabulary.
 * <p/>
 * A possible criteria - reject words that are too short (1 character) or start with digits
 * could be rejected or accept words from some pre-defined list.
 */
public interface WordFilter {

    /**
     * Implements accept/reject logic.
     * @param text  - token to check
     * @return  true if the token can be accepted as a candidate; false otherwise
     */
    boolean accept(String text);

    public static WordFilter ACCEPT_ALL = new WordFilter() {
        @Override public boolean accept(String text) { return true;  }
    } ;
}
