/*
 * The MIT License
 *
 * Copyright (c) 2009 The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mayday.wapiti.experiments.generic.reads.bamsam.samtools;

/**
 * A basic interface for querying BAM indices.
 *
 * @author mhanna
 * @version 0.1
 */
public interface BAMIndex {

    public static final String BAMIndexSuffix = ".bai";

    /**
     * Reports the maximum number of bins that can appear in a BAM file.
     */
    public static final int MAX_BINS = 37450;   // =(8^6-1)/7+1

    /**
     * Open the BAM file index.
     */
    public void open();

    /**
     * Close the BAM file index.
     */
    public void close();

    /**
     * Gets the compressed chunks which should be searched for the contents of records contained by the span
     * referenceIndex:startPos-endPos, inclusive.  See the BAM spec for more information on how a chunk is
     * represented.
     * 
     * @param referenceIndex The contig.
     * @param startPos Genomic start of query.
     * @param endPos Genomic end of query.
     * @return A file span listing the chunks in the BAM file.
     */
    BAMFileSpan getSpanOverlapping(final int referenceIndex, final int startPos, final int endPos);

    /**
     * Gets the start of the last linear bin in the index.
     * @return The chunk indicating the start of the last bin in the linear index.
     */
    long getStartOfLastLinearBin();    
}
