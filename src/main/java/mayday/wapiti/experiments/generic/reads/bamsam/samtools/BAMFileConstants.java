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
 * Constants used in reading & writing BAM files
 */
class BAMFileConstants {
    /**
     * The beginning of a BAMRecord is a fixed-size block of 8 int32s
     */
    static final int FIXED_BLOCK_SIZE = 8 * 4;

    /**
     * Sanity check -- we never expect BAMRecords to be as big as this.
     */
    static final int MAXIMUM_RECORD_LENGTH = 1024 * 1024;

    /**
     * BAM file magic number.  This is what is present in the gunzipped version of the file,
     * which never exists on disk.
     */

    static final byte[] BAM_MAGIC = "BAM\1".getBytes();
    /**
     * BAM index file magic number.
     */
    static final byte[] BAM_INDEX_MAGIC = "BAI\1".getBytes();
}
