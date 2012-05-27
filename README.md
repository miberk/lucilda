LUCILDA = LUCENE + LDA
======================

The goal is to provide APIs for extracting topics from Lucene index (or Solr). In a nutshell, topics are word clouds.

The method is based on Latent Dirichlet Allocation model [1]. Documents are represented as mixtures
of latent topics, and each topics is associated as a set of keywords. LDA defines probability distributions
for topics per document  as well as word distribution per document.

Here we implement the fast version of Gibbs Sampling algorithm as described in [2]. This algorithm scales well in
number of topics, and at least order of magnitude faster than 'conventional' Gibbs Samplers [3].
The input for the  sampler is a bag of words (vocabulary)  extracted from Lucene index.

The application require 4 input parameters:

1. List of Lucene fields we want to extract topics from;
2. Size of the vocabulary (set of keywords for topic building)  ;
3. Number of topics;
4. Location of Lucene Index.

Dictionary size, fields and Lucene index are input parameters to instantiate the WordProcessor class;
this instance, along with the number of topics, are used to create an instance of the TopicBuilder class.
Topics are created by calling the  extractTopics method of this class.

An example usage can be found in  vagueobjects.ir.lucene.demo.Reuters,
see the extractTopicsFromLuceneIndex method.


REFERENCES
----------

[1] http://en.wikipedia.org/wiki/Latent_Dirichlet_allocation
[2] http://www.cs.umass.edu/~mimno/papers/fast-topic-model.pdf
[3] In our implementation, in sample perplexity estimates are used to control convergence, see
    https://github.com/miberk/balda