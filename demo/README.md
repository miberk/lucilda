This directory includes pre-compiled demo application. Before running this application, please create an empty
directory. The demo application will download the Reuters file into this directory and build Lucene index.
To run the application, from the demo directory execute

java -jar demo.jar [DOWNLOAD-DIRECTORY-PATH] [DICTIONARY-SIZE] [NUMBER-OF-TOPICS]

For example, you may choose the dictionary size=10000 and number of topics=100.
After downloading the Reuters data and creating index, the application will run the Gibbs sampler for LDA and
print all topics (with leading terms) on the console.