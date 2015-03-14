package test;

import java.io.*;

import javax.xml.stream.*;

import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamProperties;

import com.ctc.wstx.api.WstxOutputProperties;

/**
 * Simple non-automated unit test for outputting namespace-aware XML
 * documents.
 */
public class TestNsStreamWriter
{
    private TestNsStreamWriter() {
    }

    protected XMLOutputFactory getFactory()
    {
        System.setProperty("javax.xml.stream.XMLOutputFactory",
                           "com.ctc.wstx.stax.WstxOutputFactory");
        return XMLOutputFactory.newInstance();
    }

    final String ENCODING = "ISO-8859-1";
    //final String ENCODING = "UTF-8";

    protected void test()
        throws Exception
    {
        XMLOutputFactory f = getFactory();
        f.setProperty(XMLStreamProperties.XSP_NAMESPACE_AWARE,
                      Boolean.TRUE);
        //Boolean.FALSE);
        f.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES,
                      //Boolean.TRUE);
                      Boolean.FALSE);
        f.setProperty(XMLOutputFactory2.P_AUTOMATIC_EMPTY_ELEMENTS,
                      //Boolean.TRUE);
                      Boolean.FALSE);

        f.setProperty(WstxOutputProperties.P_OUTPUT_VALIDATE_CONTENT,
                      Boolean.TRUE);
        f.setProperty(WstxOutputProperties.P_OUTPUT_FIX_CONTENT,
                      Boolean.TRUE);
                      //Boolean.FALSE);

        //Writer w = new PrintWriter(System.out);
        //XMLStreamWriter sw = f.createXMLStreamWriter(w);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //XMLStreamWriter sw = f.createXMLStreamWriter(bos, ENCODING);
        XMLStreamWriter sw = f.createXMLStreamWriter(bos);

        sw.writeStartDocument();

        {
            final String TEXT =
                /*
" Table of types of doubts\n"
+"doubt: specific error or issue with the test case\n"
+"extension: uses an extension feature\n"
+"gray-area: the spec does not give enough precision to distinguish correct behavior on the indicated detail\n"
+"processor-specific: processors are required to provide a unique value (should be marked as \"manual\" compare in catalog)\n"
+"serial: processor has options regarding serialization (This doubt only used for detail issues, not general discretion about encoding.)"
                */
" test of very very long comments and buffer limits\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
+"01234567890123456789012345678901234567890123456789\n"
                ;
            sw.writeComment(TEXT);
        }

        sw.writeCharacters("\n");
        sw.writeStartElement("root");

        sw.writeCharacters("Need to quote this too: ]]>");
        /*
        sw.writeEmptyElement("alpha");
        sw.writeNamespace("ns", "uri:foo");
        sw.writeAttribute("atpr", "http://attr-prefix", "attr", "a<b");

        sw.writeStartElement("bravo");

        sw.writeCharacters("Text: & \n");
        */

        sw.writeCData("Test: ]]>x");
        sw.writeProcessingInstruction("p", "i");

        sw.writeEndElement(); // exception here

        sw.writeCharacters("\n"); // to get linefeed
        sw.writeEndDocument();

        sw.flush();
        sw.close();

        //w.close();

        System.err.println("DOC -> '"+new String(bos.toByteArray(), ENCODING)+"'");
    }

    public static void main(String[] args)
        throws Exception
    {
        new TestNsStreamWriter().test();
    }
}
