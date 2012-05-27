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


import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.DOMElementImpl;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.GZIPInputStream;

public class ReutersProcessor  {

    private final String path;
    
    public ReutersProcessor(String path){this.path = path;}

    static Logger logger = Logger.getLogger(ReutersProcessor.class);

    private static String readNode(String name, Element parent){
        DOMElementImpl child = (DOMElementImpl ) parent.getElementsByTagName(name).item(0);
        if(child==null){
            return null;
        }
        return child.getFirstChild().getNodeValue();
    }

    private static Date parseDate(String input){
        String[] el = input.split("-");
        String month = el[1];
        String year = el[2].split(" ")[0];
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        if("JAN".equals(month)){
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
        }else if("FEB".equals(month)){
            calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        }else if("MAR".equals(month)){
            calendar.set(Calendar.MONTH, Calendar.MARCH);
        }else if("APR".equals(month)){
            calendar.set(Calendar.MONTH, Calendar.APRIL);
        }else if("MAY".equals(month)){
            calendar.set(Calendar.MONTH, Calendar.MAY);
        }else if("JUN".equals(month)){
            calendar.set(Calendar.MONTH, Calendar.JUNE);
        }else if("JUL".equals(month)){
            calendar.set(Calendar.MONTH, Calendar.JULY);
        }else if("AUG".equals(month)){
            calendar.set(Calendar.MONTH, Calendar.AUGUST);
        }else if("SEP".equals(month)){
            calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
        } else if("OCT".equals(month)){
            calendar.set(Calendar.MONTH, Calendar.OCTOBER);
        }else if("NOV".equals(month)){
            calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        }else if("DEC".equals(month)){
            calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        }else {
            throw new IllegalArgumentException("Unsupported month " + month);
        }
        return calendar.getTime();
    }

    public void process( IndexWriter indexWriter) throws IOException {
        FileInputStream fis=null;
        Tidy jt = new Tidy();
        jt.setXmlTags(true);

        GZIPInputStream gz=null;
        try{
            fis =new FileInputStream(path);
            gz = new GZIPInputStream(fis);
            TarArchiveInputStream is = new TarArchiveInputStream(gz);
            ArchiveEntry e ;
            int id=0;
            while((e  = is.getNextTarEntry())!=null){
                String name =e.getName();
                if(name.endsWith("sgm")){
                    logger.info("Processing "+ name);
                    int size =   (int) e.getSize();
                    byte[] content = new byte[size];
                    is.read(content) ;
                    ByteArrayInputStream bs = new ByteArrayInputStream(content);
                    NodeList nl = jt.parseDOM(bs, null).getElementsByTagName("REUTERS");
                    for(int n=0; n < nl.getLength(); ++n) {
                        Node node = nl.item(n);
                        Node b =((Element) node).getElementsByTagName("TEXT").item(0);
                        String text = readNode("BODY", (Element)b);
                        if(text != null){
                            Document document = new Document();
                            document.add(new Field("text", text, Field.Store.NO, Field.Index.ANALYZED ));
                            indexWriter.addDocument(document);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(gz);
        }
    }

}
