/* Woodstox XML processor
 *
 * Copyright (c) 2004- Tatu Saloranta, tatu.saloranta@iki.fi
 *
 * Licensed under the License specified in file LICENSE, included with
 * the source code.
 * You may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ctc.wstx.sr;

import java.io.IOException;
import java.io.Writer;

import javax.xml.XMLConstants;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.sw.XmlWriter;
import com.ctc.wstx.util.StringVector;
import com.ctc.wstx.util.TextBuilder;
import com.ctc.wstx.util.WordResolver;

/**
 * Shared base class that defines API stream reader uses to communicate
 * with the attribute collector implementation, independent of whether it's
 * operating in namespace-aware or non-namespace modes.
 * Collector class is used to build up attribute lists; for the most part
 * will just hold references to few specialized {@link TextBuilder}s that
 * are used to create efficient semi-shared value Strings.
 */
public abstract class AttributeCollector
{
    /**
     * Threshold value that indicates minimum length for lists instances
     * that need a Map structure, for fast attribute access by fully-qualified
     * name.
     */
    protected final static int LONG_ATTR_LIST_LEN = 4;

    /**
     * Expected typical maximum number of attributes for any element;
     * chosen to minimize need to resize, while trying not to waste space.
     * Dynamically grown; better not to set too high to avoid excessive
     * overhead for small attribute-less documents.
     */
    protected final static int EXP_ATTR_COUNT = 16;

    /**
     * This value is used to indicate that we shouldn't keep track
     * of index of xml:id attribute -- generally done when Xml:id
     * support is disabled
     */
    protected final static int XMLID_IX_DISABLED = -2;

    protected final static int XMLID_IX_NONE = -1;

    /*
    //////////////////////////////////////////
    // Collected attribute information:
    //////////////////////////////////////////
     */

    /**
     * Actual number of attributes collected, including attributes
     * added via default values.
     */
    protected int mAttrCount;

    /**
     * Number of attribute values actually parsed, not including
     * ones created via default value expansion. Equal to or less than
     * {@link #mAttrCount}.
     */
    protected int mNonDefCount;

    /**
     * TextBuilder into which values of all attributes are appended
     * to, including default valued ones (defaults are added after
     * explicit ones).
     * Constructed lazily, if and when needed (not needed
     * for short attribute-less docs)
     */
    protected TextBuilder mValueBuffer = null;

    /**
     * Vector in which attribute names are added; exact number of elements
     * per attribute depends on whether namespace support is enabled or
     * not (non-namespace mode only needs one entry; namespace mode two,
     * one for prefix, one for local name).
     * Constructed lazily, if and when needed (not needed
     * for short attribute-less docs)
     */
    protected StringVector mAttrNames = null;

    /**
     * Index of "xml:id" attribute, if one exists for the current
     * element; {@link #XMLID_IX_NONE} if none.
     */
    protected int mXmlIdAttrIndex;

    /*
    //////////////////////////////////////////
    // Resolved (derived) attribute information:
    //////////////////////////////////////////
     */

    /**
     * Array in which attribute value Strings are added, first time they
     * are requested. Values are first added to <code>mValueBuffer</code>,
     * from which a String is created, and finally substring created as
     * needed and added to this array.
     */
    protected String[] mAttrValues = null;

    /*
    //////////////////////////////////////////////////////////////
    // Information that defines "Map-like" data structure used for
    // quick access to attribute values by fully-qualified name
    //////////////////////////////////////////////////////////////
     */

    /**
     * Encoding of a data structure that contains mapping from
     * attribute names to attribute index in main attribute name arrays.
     *<p>
     * Data structure contains two separate areas; main hash area (with
     * size <code>mAttrHashSize</code>), and remaining spillover area
     * that follows hash area up until (but not including)
     * <code>mAttrSpillEnd</code> index.
     * Main hash area only contains indexes (index+1; 0 signifying empty slot)
     * to actual attributes; spillover area has both hash and index for
     * any spilled entry. Spilled entries are simply stored in order
     * added, and need to be searched using linear search. In case of both
     * primary hash hits and spills, eventual comparison with the local
     * name needs to be done with actual name array.
     */
    protected int[] mAttrMap = null;

    /**
     * Size of hash area in <code>mAttrMap</code>; generally at least 20%
     * more than number of attributes (<code>mAttrCount</code>).
     */
    protected int mAttrHashSize;

    /**
     * Pointer to int slot right after last spill entr, in
     * <code>mAttrMap</code> array.
     */
    protected int mAttrSpillEnd;

    /*
    ///////////////////////////////////////////////
    // Life-cycle:
    ///////////////////////////////////////////////
     */

    protected AttributeCollector(ReaderConfig cfg)
    {
        mXmlIdAttrIndex = cfg.willDoXmlIdTyping() ? XMLID_IX_NONE : XMLID_IX_DISABLED;
    }

    /**
     * Method called to allow reusing of collector, usually right before
     * starting collecting attributes for a new start tag.
     */
    protected abstract void reset();

    /*
    ///////////////////////////////////////////////
    // Public accesors (for stream reader)
    ///////////////////////////////////////////////
     */

    /**
     * @return Number of namespace declarations collected, including
     *   possible default namespace declaration
     */
    public abstract int getNsCount();

    public abstract String getNsPrefix(int index);

    public abstract String getNsURI(int index);

    // // // Direct access to attribute/NS prefixes/localnames/URI

    public final int getCount() {
        return mAttrCount;
    }

    /**
     * @return Number of attributes that were explicitly specified; may
     *  be less than the total count due to attributes created using
     *  attribute default values
     */
    public int getSpecifiedCount() {
        return mNonDefCount;
    }

    public abstract String getPrefix(int index);

    public abstract String getLocalName(int index);

    public abstract String getURI(int index);

    public abstract QName getQName(int index);

    /**
     *<p>
     * Note: the main reason this method is defined at this level, and
     * made final, is performance. JIT may be able to fully inline this
     * method, even when reference is via this base class. This is important
     * since this is likely to be the most often called method of the
     * collector instances.
     */
    public final String getValue(int index)
    {
        if (index < 0 || index >= mAttrCount) {
            throwIndex(index);
        }
        /* Note: array has been properly (re)sized by sub-classes
         * resolveXxx() method, so it's either null or properly sized
         * by now
         */
        if (mAttrValues == null) {
            mAttrValues = new String[mAttrCount];
        }
        String str = mAttrValues[index];
        if (str == null) {
            str = mValueBuffer.getEntry(index);
            mAttrValues[index] = str;
        }
        return str;
    }

    public abstract String getValue(String nsURI, String localName);

    public final boolean isSpecified(int index) {
        return (index < mNonDefCount);
    }

    public final int getXmlIdAttrIndex() {
        return mXmlIdAttrIndex;
    }

    /*
    ///////////////////////////////////////////////
    // Accessors for accessing helper objects
    ///////////////////////////////////////////////
     */

    public abstract TextBuilder getDefaultNsBuilder();

    public abstract TextBuilder getNsBuilder(String localName);

    public abstract TextBuilder getAttrBuilder(String attrPrefix, String attrLocalName);

    /**
     * Method needed by event builder code; called to build a non-transient
     * attribute container to use by a start element event.
     */
    public abstract ElemAttrs buildAttrOb();

    /*
    ///////////////////////////////////////////////
    // Validation methods:
    ///////////////////////////////////////////////
     */

    /**
     * Low-level accessor method that attribute validation code may call
     * for certain types of attributes; generally only for id and idref/idrefs
     * attributes. It returns the underlying 'raw' attribute value buffer
     * for direct access.
     */
    public final TextBuilder getAttrBuilder()
    {
        return mValueBuffer;
    }

    /**
     * Low-level mutator method that attribute validation code may call
     * for certain types of attributes, when it wants to handle the whole
     * validation and normalization process by itself. It is generally
     * only called for id and idref/idrefs attributes, as those values
     * are usually normalized.
     */
    public final void setNormalizedValue(int index, String value) {
        if (mAttrValues == null) {
            mAttrValues = new String[mAttrCount];
        }
        mAttrValues[index] = value;
    }

    /*
    ///////////////////////////////////////////////
    // Package/core methods:
    ///////////////////////////////////////////////
     */

    protected void throwIndex(int index) {
        throw new IllegalArgumentException("Invalid index "+index+"; current element has only "+getCount()+" attributes");
    }

    /**
     * Method called by {@link InputElementStack} instance that "owns" this
     * attribute collector; 
     */
    public final StringVector getNameList() {
        return mAttrNames;
    }

    /**
     * Method that basically serializes the specified (read-in) attribute
     * using Writers provided. Serialization is done by
     * writing out (fully-qualified) name
     * of the attribute, followed by the equals sign and quoted value.
     */
    public abstract void writeAttribute(int index, XmlWriter xw)
        throws IOException, XMLStreamException;

    /**
     * Method called to initialize buffers that need not be immediately
     * initialized
     */
    protected final void allocBuffers()
    {
        if (mValueBuffer == null) {
            mValueBuffer = new TextBuilder(EXP_ATTR_COUNT);
        }
        if (mAttrNames == null) {
            mAttrNames = new StringVector(EXP_ATTR_COUNT);
        }
    }

    /*
    ///////////////////////////////////////////////
    // Internal methods:
    ///////////////////////////////////////////////
     */


    /**
     * Method that can be used to get the specified attribute value,
     * by getting it written using Writer passed in. Can potentially
     * save one String allocation, since no (temporary) Strings need
     * to be created.
     */
    /*
    protected final void writeValue(int index, Writer w)
        throws IOException
    {
        mValueBuffer.getEntry(index, w);
    }
    */

    protected static String[] resize(String[] old) {
        int len = old.length;
        String[] result = new String[len];
        System.arraycopy(old, 0, result, 0, len);
        return result;
    }

    protected void throwDupAttr(InputProblemReporter rep, int index)
        throws XMLStreamException
    {
        rep.throwParseError("Duplicate attribute '"+getQName(index)+"'.");
    }
}
