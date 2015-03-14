package com.ctc.wstx.api;

import javax.xml.stream.XMLResolver;

/**
 * Class that contains constant for property names used to configure
 * cursor and event readers produced by Wstx implementation of
 * {@link javax.xml.stream.XMLInputFactory}.
 *<p>
 * TODO:
 *
 * - CHECK_CHAR_VALIDITY (separate for white spaces?)
 * - CATALOG_RESOLVER? (or at least, ENABLE_CATALOGS)
 */
public final class WstxInputProperties
{
    /**
     * Constants used when no DTD handling is done, and we do not know the
     * 'real' type of an attribute. Seems like CDATA is the safe choice.
     */
    public final static String UNKNOWN_ATTR_TYPE = "CDATA";

    /*
    ///////////////////////////////////////////////////////
    // Simple on/off settings:
    ///////////////////////////////////////////////////////
     */

    // // // Normalization:


    /**
     * @deprecated In future versions of Woodstox, normalization will
     *   not be optional, and the default setting (true) will be the
     *   only supported way.
     *<p>
     * Whether non-standard linefeeds (\r, \r\n) need to be converted
     * to standard ones (\n) or not, as per XML specs.
     *<p>
     * Turning this option
     * off may help performance when processing content that has non-standard
     * linefeeds (Mac, Windows); otherwise effect is negligible.
     */
    public final static String P_NORMALIZE_LFS = "com.ctc.wstx.normalizeLFs";

    /**
     * @deprecated In future versions of Woodstox, normalization will
     *   not be optional, and the default setting (true) will be the
     *   only supported way.
     *<p>
     * Whether white space in attribute values should be normalized as
     * specified by XML specs or not.
     *<p>
     * Turning this option may help performance if attributes generally
     * have non-normalized white space; otherwise effect is negligible.
     */
    public final static String P_NORMALIZE_ATTR_VALUES = "com.ctc.wstx.normalizeAttrValues";


    // // // XML character validation:

    /**
     * Whether readers will verify that characters in text content are fully
     * valid XML characters (not just Unicode). If true, will check
     * that they are valid (including white space); if false, will not
     * check.
     *<p>
     * Turning this option off may improve parsing performance; leaving
     * it on guarantees compatibility with XML 1.0 specs regarding character
     * validity rules.
     */
    public final static String P_VALIDATE_TEXT_CHARS = "com.ctc.wstx.validateTextChars";


    // // // Caching:

    /**
     * Whether readers will try to cache parsed external DTD subsets or not.
     */

    public final static String P_CACHE_DTDS = "com.ctc.wstx.cacheDTDs";

    /**
     * Whether reader is to cache DTDs (when caching enabled) based on public id
     * or not: if not, system id will be primarily used. Although theoretically
     * public IDs should be unique, and should be good caching keys, sometimes
     * broken documents use 'wrong' public IDs, and such by default caching keys
     * are based on system id only.
     */
    public final static String P_CACHE_DTDS_BY_PUBLIC_ID = "com.ctc.wstx.cacheDTDsByPublicId";


    // // // Enabling/disabling lazy/incomplete parsing

    public final static String P_LAZY_PARSING = "com.ctc.wstx.lazyParsing";


    // // // Enabling/disabling support for dtd++

    /**
     * Whether the Reader will recognized DTD++ extensions when parsing
     * DTD subsets.
     *<p>
     * Note: not implemented as of 2.0.x
     */
    public final static String P_SUPPORT_DTDPP = "com.ctc.wstx.supportDTDPP";

    // // // Enabling alternate mode for parsing XML fragments instead
    // // // of full documents

    // Automatic W3C Schema support?
    /*
     * Whether W3C Schema hint attributes are recognized within document,
     * and used to locate Schema to use for validation.
     */
    //public final static String P_AUTOMATIC_W3C_SCHEMA = 0x00100000;

    /*
    ///////////////////////////////////////////////////////
    // More complex settings:
    ///////////////////////////////////////////////////////
     */

    // // // Buffer sizes;

    /**
     * Size of input buffer (in chars), to use for reading XML content
     * from input stream/reader.
     */
    public final static String P_INPUT_BUFFER_LENGTH = "com.ctc.wstx.inputBufferLength";

    // // // Constraints on sizes of text segments parsed:


    /**
     * Property to specify shortest non-complete text segment (part of
     * CDATA section or text content) that parser is allowed to return,
     * if not required to coalesce text.
     */
    public final static String P_MIN_TEXT_SEGMENT = "com.ctc.wstx.minTextSegment";

    // // // Entity handling

    /**
     * @deprecated This feature may be remove from future versions of
     *   Woodstox, since the same functionality can be achieved by using
     *   custom entity resolvers.
     *<p>
     * Property of type {@link java.util.Map}, that defines explicit set of
     * internal (generic) entities that will define of override any entities
     * defined in internal or external subsets; except for the 5 pre-defined
     * entities (lt, gt, amp, apos, quot). Can be used to explicitly define
     * entites that would normally come from a DTD.
     */
    public final static String P_CUSTOM_INTERNAL_ENTITIES = "com.ctc.wstx.customInternalEntities";

    /**
     * Property of type {@link XMLResolver}, that
     * will allow overriding of default DTD and external parameter entity
     * resolution.
     */
    public final static String P_DTD_RESOLVER = "com.ctc.wstx.dtdResolver";

    /**
     * Property of type {@link XMLResolver}, that
     * will allow overriding of default external general entity
     * resolution. Note that using this property overrides settings done
     * using {@link javax.xml.stream.XMLInputFactory#RESOLVER} (and vice versa).
     */
    public final static String P_ENTITY_RESOLVER = "com.ctc.wstx.entityResolver";
    
    /**
     * Property of type {@link XMLResolver}, that
     * will allow graceful handling of references to undeclared (general)
     * entities.
     */
    public final static String P_UNDECLARED_ENTITY_RESOLVER = "com.ctc.wstx.undeclaredEntityResolver";

    /**
     * Property of type {@link java.net.URL}, that will allow specifying
     * context URL to use when resolving relative references, for the
     * main-level entities (external DTD subset, references from the internal
     * DTD subset).
     */
    public final static String P_BASE_URL = "com.ctc.wstx.baseURL";

    // // // Alternate parsing modes

    /**
     * Three-valued property (one of
     * {@link #PARSING_MODE_DOCUMENT},
     * {@link #PARSING_MODE_FRAGMENT} or
     * {@link #PARSING_MODE_DOCUMENTS}; default being the document mode)
     * that can be used to handle "non-standard" XML content. The default
     * mode (<code>PARSING_MODE_DOCUMENT</code>) allows parsing of only
     * well-formed XML documents, but the other two modes allow more lenient
     * parsing. Fragment mode allows parsing of XML content that does not
     * have a single root element (can have zero or more), nor can have
     * XML or DOCTYPE declarations: this may be useful if parsing a subset
     * of a full XML document. Multi-document
     * (<code>PARSING_MODE_DOCUMENTS</code>) mode on the other hand allows
     * parsing of a stream that contains multiple consequtive well-formed
     * documents, with possibly multiple XML and DOCTYPE declarations.
     *<p>
     * The main difference from the API perspective is that in first two
     * modes, START_DOCUMENT and END_DOCUMENT are used as usual (as the first
     * and last events returned), whereas the multi-document mode can return
     * multiple pairs of these events: although it is still true that the
     * first event (one cursor points to when reader is instantiated or
     * returned by the event reader), there may be intervening pairs that
     * signal boundary between two adjacent enclosed documents.
     */
    public final static String P_INPUT_PARSING_MODE = "com.ctc.wstx.fragmentMode";

    // // // DTD defaulting, overriding

    /*
    ////////////////////////////////////////////////////////////////////
    // Helper classes, values enumerations
    ////////////////////////////////////////////////////////////////////
     */

    public final static ParsingMode PARSING_MODE_DOCUMENT = new ParsingMode();
    public final static ParsingMode PARSING_MODE_FRAGMENT = new ParsingMode();
    public final static ParsingMode PARSING_MODE_DOCUMENTS = new ParsingMode();

    /**
     * Inner class used for creating type-safe enumerations (prior to JDK 1.5).
     */
    public final static class ParsingMode
    {
        ParsingMode() { }
    }
}
