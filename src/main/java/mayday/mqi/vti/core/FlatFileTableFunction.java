/*

Derby - Class sun.javadb.vti.core.FlatFileTableFunction

Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package mayday.mqi.vti.core;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.SQLException;

/**
 * <p>
 * This Table Function makes a table out of the records in a flat file. This is an abstract
 * class. To extend this class, you must supply the following method, along with
 * a constructor and a public static method which returns an instance of this class.
 * The following method is responsible for parsing the next record and advancing the file:
 * </p>
 *
 * <ul>
 * <li>{@link #parseRow() parseRow()} - Parses the next record of the file into an array of Strings,
 * one for each column.</li>
 * </ul>
 *
 *    <img src="../../../../JavaDB.png"/>
 */
public    abstract  class   FlatFileTableFunction  extends StringColumnTableFunction
{
    ///////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTANTS
    //
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    //
    // STATE
    //
    ///////////////////////////////////////////////////////////////////////////////////

    private String              _textFileName;

    private File                _file;
    private FileReader          _fileReader;
    private LineNumberReader    _lineReader;

//    private boolean             _closed = false;

    private String[]            _row;

    private int                 _lineNumber = -1;

    ///////////////////////////////////////////////////////////////////////////////////
    //
    // FlatFileTableFunction BEHAVIOR TO BE IMPLEMENTED BY SUBCLASSES
    //
    ///////////////////////////////////////////////////////////////////////////////////

    /**
     * <p>
     * Parse the next chunk of text, using {@link #readLine() readLine()}, and return the next row.
     * Returns null if the file is exhausted.
     * </p>
     */
    protected  abstract String[]  parseRow( ) throws SQLException;
    
    ///////////////////////////////////////////////////////////////////////////////////
    //
    // StringColumnTableFunction BEHAVIOR TO BE IMPLEMENTED BY SUBCLASSES
    //
    ///////////////////////////////////////////////////////////////////////////////////

    /**
     * <p>
     * Get the string value of the column in the current row identified by the
     * 1-based columnNumber. This method is called by the <i>getXXX</i>()
     * methods in the <a href="StringColumnTableFunction.html">StringColumnTableFunction</a>
     * superclass.
     * </p>
     */
    protected  String  getRawColumn( int columnNumber ) throws SQLException
    {
        return _row[ columnNumber - 1 ];
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    //
    // CONSTRUCTORS
    //
    ///////////////////////////////////////////////////////////////////////////////////

    /**
     * <p>
     * Build a FlatFileTableFunction with the given column names. The function
     * will read from the given file.
     * </p>
     */
    public  FlatFileTableFunction( String[] columnNames, String textFileName )
    {
        super( columnNames );

        _textFileName = textFileName;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    //
    // ResultSet BEHAVIOR
    //
    ///////////////////////////////////////////////////////////////////////////////////

    /**
     * <p>
     * This method advances to the next record in the file. This method
     * calls {@link #parseRow() parseRow()} in order to turn the record into a
     * row. Subsequent calls to the <i>getXXX</i>() methods will return
     * individual columns in that row.
     * </p>
     */
    public  boolean next() throws SQLException
    {
        if ( _file == null )
        {
            try {
                _file = new File( _textFileName );
                _fileReader = new FileReader( _file );
                _lineReader = new LineNumberReader( _fileReader );
            } catch (Throwable t) { throw wrap( t ); }
        }

        _row = parseRow();

        if ( _row == null ) { return false; }
        else { return true; }
    }

    public  void close() throws SQLException
    {
        try {
            if ( _lineReader != null ) { _lineReader.close(); }
            if ( _fileReader != null ) { _fileReader.close(); }

        } catch (Throwable t) { throw wrap( t ); }
        finally
        {
             _lineReader = null;
             _fileReader = null;
            _file = null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //
    // MINIONS
    //
    ///////////////////////////////////////////////////////////////////////////////////

    /**
     * <p>
     * Get the current line number.
     * </p>
     */
    protected   int getLineNumber() { return _lineNumber; }
    
    /**
     * <p>
     * Get the name of the file being read.
     * </p>
     */
    protected   String getTextFileName() { return _textFileName; }
    
    /**
     * <p>
     * Read the next line from the file and return it. Return null if the file
     * is exhausted.
     * </p>
     */
    protected   String  readLine()  throws SQLException
    {
        try {
            String retval = _lineReader.readLine();

            _lineNumber++;
            return retval;
        } catch( Throwable t ) { throw wrap( t ); }
    }



}
