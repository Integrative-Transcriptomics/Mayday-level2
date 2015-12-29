/*

   Derby - Class sun.javadb.vti.core.TemplateTableFunction

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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
   <p>
	An abstract implementation of the Java 6 <i>ResultSet</i>, that is useful
	when writing a Java DB Table Function.
    </p>

    <p>
	This class provides stubs for most of the methods of the Java 6 <i>java.sql.ResultSet</i>.
	Each stub throws a "not implemented" <i>SQLException</i>. 
	A concrete subclass can then just provide the methods not implemented here 
	and override any stubs that it needs. Typically, you will only need to override the <i>getXXX()</i>
	stubs corresponding to the datatypes of the actual columns in the
	<i>ResultSet</i> returned by your Table Function. That is, if your Table Function
	has character and integer columns, then you only need to override
	the <i>getString()</i> and <i>getInt()</i> stubs.
    </p>
    
	<p>
	The methods not implemented here are
    </p>
    
	<ul>
	<li><i>next()</i></li>
	<li><i>close()</i></li>
	</ul>

    <p>
    If you need to compile this class for jdk1.4 or Java 5, just remove the
    block of stubs in the section which begins with the comment "JAVA 6 COMPATIBLE BEHAVIOR".
    </p>

    <img src="../../../../JavaDB.png"/>
 */
public abstract class TemplateTableFunction implements ResultSet
{
    /////////////////////////////////////////////////////////////////////////
    //
    //  CONSTANTS
    //
    /////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////
    //
    //  STATE
    //
    /////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////
    //
    //  CONSTRUCTORS
    //
    /////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////
    //
    //  ResultSet BEHAVIOR
    //
    /////////////////////////////////////////////////////////////////////////

    //////////////////////////////////
    //
    //  JDK 1.4 COMPATIBLE BEHAVIOR
    //
    //////////////////////////////////

    public  boolean 	absolute(int row) throws SQLException { throw notImplemented(); }
    public  void 	afterLast() throws SQLException { throw notImplemented(); }
    public  void 	beforeFirst() throws SQLException { throw notImplemented(); }
    public  void 	cancelRowUpdates() throws SQLException { throw notImplemented(); }
    public  void 	clearWarnings() throws SQLException { throw notImplemented(); }
    //public  void 	close() throws SQLException { throw notImplemented(); }
    public  void 	deleteRow() throws SQLException { throw notImplemented(); }
    public  int 	findColumn(String columnLabel) throws SQLException { throw notImplemented(); }
    public  boolean 	first() throws SQLException { throw notImplemented(); }
    public  Array 	getArray(int columnIndex) throws SQLException { throw notImplemented(); }
    public  Array 	getArray(String columnLabel) throws SQLException { throw notImplemented(); }
    public  InputStream 	getAsciiStream(int columnIndex) throws SQLException { throw notImplemented(); }
    public  InputStream 	getAsciiStream(String columnLabel) throws SQLException { throw notImplemented(); }
    public  BigDecimal 	getBigDecimal(int columnIndex) throws SQLException { throw notImplemented(); }
    public  BigDecimal 	getBigDecimal(int columnIndex, int scale) throws SQLException { throw notImplemented(); }
    public  BigDecimal 	getBigDecimal(String columnLabel) throws SQLException { throw notImplemented(); }
    public  BigDecimal 	getBigDecimal(String columnLabel, int scale) throws SQLException { throw notImplemented(); }
    public  InputStream 	getBinaryStream(int columnIndex) throws SQLException { throw notImplemented(); }
    public  InputStream 	getBinaryStream(String columnLabel) throws SQLException { throw notImplemented(); }
    public  Blob 	getBlob(int columnIndex) throws SQLException { throw notImplemented(); }
    public  Blob 	getBlob(String columnLabel) throws SQLException { throw notImplemented(); }
    public  boolean 	getBoolean(int columnIndex) throws SQLException { throw notImplemented(); }
    public  boolean 	getBoolean(String columnLabel) throws SQLException { throw notImplemented(); }
    public  byte 	getByte(int columnIndex) throws SQLException { throw notImplemented(); }
    public  byte 	getByte(String columnLabel) throws SQLException { throw notImplemented(); }
    public  byte[] 	getBytes(int columnIndex) throws SQLException { throw notImplemented(); }
    public  byte[] 	getBytes(String columnLabel) throws SQLException { throw notImplemented(); }
    public  Reader 	getCharacterStream(int columnIndex) throws SQLException { throw notImplemented(); }
    public  Reader 	getCharacterStream(String columnLabel) throws SQLException { throw notImplemented(); }
    public  Clob 	getClob(int columnIndex) throws SQLException { throw notImplemented(); }
    public  Clob 	getClob(String columnLabel) throws SQLException { throw notImplemented(); }
    public  int 	getConcurrency() throws SQLException { throw notImplemented(); }
    public  String 	getCursorName() throws SQLException { throw notImplemented(); }
    public  Date 	getDate(int columnIndex) throws SQLException { throw notImplemented(); }
    public  Date 	getDate(int columnIndex, Calendar cal) throws SQLException { throw notImplemented(); }
    public  Date 	getDate(String columnLabel) throws SQLException { throw notImplemented(); }
    public  Date 	getDate(String columnLabel, Calendar cal) throws SQLException { throw notImplemented(); }
    public  double 	getDouble(int columnIndex) throws SQLException { throw notImplemented(); }
    public  double 	getDouble(String columnLabel) throws SQLException { throw notImplemented(); }
    public  int 	getFetchDirection() throws SQLException { throw notImplemented(); }
    public  int 	getFetchSize() throws SQLException { throw notImplemented(); }
    public  float 	getFloat(int columnIndex) throws SQLException { throw notImplemented(); }
    public  float 	getFloat(String columnLabel) throws SQLException { throw notImplemented(); }
    public  int 	getHoldability() throws SQLException { throw notImplemented(); }
    public  int 	getInt(int columnIndex) throws SQLException { throw notImplemented(); }
    public  int 	getInt(String columnLabel) throws SQLException { throw notImplemented(); }
    public  long 	getLong(int columnIndex) throws SQLException { throw notImplemented(); }
    public  long 	getLong(String columnLabel) throws SQLException { throw notImplemented(); }
    public  ResultSetMetaData 	getMetaData() throws SQLException { throw notImplemented(); }
    public  Reader 	getNCharacterStream(int columnIndex) throws SQLException { throw notImplemented(); }
    public  Reader 	getNCharacterStream(String columnLabel) throws SQLException { throw notImplemented(); }
    public  String 	getNString(int columnIndex) throws SQLException { throw notImplemented(); }
    public  String 	getNString(String columnLabel) throws SQLException { throw notImplemented(); }
    public  Object 	getObject(int columnIndex) throws SQLException { throw notImplemented(); }
    public  Object 	getObject(String columnLabel) throws SQLException { throw notImplemented(); }
    public  Ref 	getRef(int columnIndex) throws SQLException { throw notImplemented(); }
    public  Ref 	getRef(String columnLabel) throws SQLException { throw notImplemented(); }
    public  int 	getRow() throws SQLException { throw notImplemented(); }
    public  short 	getShort(int columnIndex) throws SQLException { throw notImplemented(); }
    public  short 	getShort(String columnLabel) throws SQLException { throw notImplemented(); }
    public  Statement 	getStatement() throws SQLException { throw notImplemented(); }
    public  String 	getString(int columnIndex) throws SQLException { throw notImplemented(); }
    public  String 	getString(String columnLabel) throws SQLException { throw notImplemented(); }
    public  Time 	getTime(int columnIndex) throws SQLException { throw notImplemented(); }
    public  Time 	getTime(int columnIndex, Calendar cal) throws SQLException { throw notImplemented(); }
    public  Time 	getTime(String columnLabel) throws SQLException { throw notImplemented(); }
    public  Time 	getTime(String columnLabel, Calendar cal) throws SQLException { throw notImplemented(); }
    public  Timestamp 	getTimestamp(int columnIndex) throws SQLException { throw notImplemented(); }
    public  Timestamp 	getTimestamp(int columnIndex, Calendar cal) throws SQLException { throw notImplemented(); }
    public  Timestamp 	getTimestamp(String columnLabel) throws SQLException { throw notImplemented(); }
    public  Timestamp 	getTimestamp(String columnLabel, Calendar cal) throws SQLException { throw notImplemented(); }
    public  int 	getType() throws SQLException { throw notImplemented(); }
    public  InputStream 	getUnicodeStream(int columnIndex) throws SQLException { throw notImplemented(); }
    public  InputStream 	getUnicodeStream(String columnLabel) throws SQLException { throw notImplemented(); }
    public  URL 	getURL(int columnIndex) throws SQLException { throw notImplemented(); }
    public  URL 	getURL(String columnLabel) throws SQLException { throw notImplemented(); }
    public  SQLWarning 	getWarnings() throws SQLException { throw notImplemented(); }
    public  void 	insertRow() throws SQLException { throw notImplemented(); }
    public  boolean 	isAfterLast() throws SQLException { throw notImplemented(); }
    public  boolean 	isBeforeFirst() throws SQLException { throw notImplemented(); }
    public  boolean 	isClosed() throws SQLException { throw notImplemented(); }
    public  boolean 	isFirst() throws SQLException { throw notImplemented(); }
    public  boolean 	isLast() throws SQLException { throw notImplemented(); }
    public  boolean 	last() throws SQLException { throw notImplemented(); }
    public  void 	moveToCurrentRow() throws SQLException { throw notImplemented(); }
    public  void 	moveToInsertRow() throws SQLException { throw notImplemented(); }
    //public  boolean 	next() throws SQLException { throw notImplemented(); }
    public  boolean 	previous() throws SQLException { throw notImplemented(); }
    public  void 	refreshRow() throws SQLException { throw notImplemented(); }
    public  boolean 	relative(int rows) throws SQLException { throw notImplemented(); }
    public  boolean 	rowDeleted() throws SQLException { throw notImplemented(); }
    public  boolean 	rowInserted() throws SQLException { throw notImplemented(); }
    public  boolean 	rowUpdated() throws SQLException { throw notImplemented(); }
    public  void 	setFetchDirection(int direction) throws SQLException { throw notImplemented(); }
    public  void 	setFetchSize(int rows) throws SQLException { throw notImplemented(); }
    public  void 	updateArray(int columnIndex, Array x) throws SQLException { throw notImplemented(); }
    public  void 	updateArray(String columnLabel, Array x) throws SQLException { throw notImplemented(); }
    public  void 	updateAsciiStream(int columnIndex, InputStream x) throws SQLException { throw notImplemented(); }
    public  void 	updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException { throw notImplemented(); }
    public  void 	updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateAsciiStream(String columnLabel, InputStream x) throws SQLException { throw notImplemented(); }
    public  void 	updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException { throw notImplemented(); }
    public  void 	updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException { throw notImplemented(); }
    public  void 	updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException { throw notImplemented(); }
    public  void 	updateBinaryStream(int columnIndex, InputStream x) throws SQLException { throw notImplemented(); }
    public  void 	updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException { throw notImplemented(); }
    public  void 	updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateBinaryStream(String columnLabel, InputStream x) throws SQLException { throw notImplemented(); }
    public  void 	updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException { throw notImplemented(); }
    public  void 	updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateBlob(int columnIndex, Blob x) throws SQLException { throw notImplemented(); }
    public  void 	updateBlob(int columnIndex, InputStream inputStream) throws SQLException { throw notImplemented(); }
    public  void 	updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateBlob(String columnLabel, Blob x) throws SQLException { throw notImplemented(); }
    public  void 	updateBlob(String columnLabel, InputStream inputStream) throws SQLException { throw notImplemented(); }
    public  void 	updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateBoolean(int columnIndex, boolean x) throws SQLException { throw notImplemented(); }
    public  void 	updateBoolean(String columnLabel, boolean x) throws SQLException { throw notImplemented(); }
    public  void 	updateByte(int columnIndex, byte x) throws SQLException { throw notImplemented(); }
    public  void 	updateByte(String columnLabel, byte x) throws SQLException { throw notImplemented(); }
    public  void 	updateBytes(int columnIndex, byte[] x) throws SQLException { throw notImplemented(); }
    public  void 	updateBytes(String columnLabel, byte[] x) throws SQLException { throw notImplemented(); }
    public  void 	updateCharacterStream(int columnIndex, Reader x) throws SQLException { throw notImplemented(); }
    public  void 	updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException { throw notImplemented(); }
    public  void 	updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateCharacterStream(String columnLabel, Reader reader) throws SQLException { throw notImplemented(); }
    public  void 	updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException { throw notImplemented(); }
    public  void 	updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateClob(int columnIndex, Clob x) throws SQLException { throw notImplemented(); }
    public  void 	updateClob(int columnIndex, Reader reader) throws SQLException { throw notImplemented(); }
    public  void 	updateClob(int columnIndex, Reader reader, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateClob(String columnLabel, Clob x) throws SQLException { throw notImplemented(); }
    public  void 	updateClob(String columnLabel, Reader reader) throws SQLException { throw notImplemented(); }
    public  void 	updateClob(String columnLabel, Reader reader, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateDate(int columnIndex, Date x) throws SQLException { throw notImplemented(); }
    public  void 	updateDate(String columnLabel, Date x) throws SQLException { throw notImplemented(); }
    public  void 	updateDouble(int columnIndex, double x) throws SQLException { throw notImplemented(); }
    public  void 	updateDouble(String columnLabel, double x) throws SQLException { throw notImplemented(); }
    public  void 	updateFloat(int columnIndex, float x) throws SQLException { throw notImplemented(); }
    public  void 	updateFloat(String columnLabel, float x) throws SQLException { throw notImplemented(); }
    public  void 	updateInt(int columnIndex, int x) throws SQLException { throw notImplemented(); }
    public  void 	updateInt(String columnLabel, int x) throws SQLException { throw notImplemented(); }
    public  void 	updateLong(int columnIndex, long x) throws SQLException { throw notImplemented(); }
    public  void 	updateLong(String columnLabel, long x) throws SQLException { throw notImplemented(); }
    public  void 	updateNCharacterStream(int columnIndex, Reader x) throws SQLException { throw notImplemented(); }
    public  void 	updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateNCharacterStream(String columnLabel, Reader reader) throws SQLException { throw notImplemented(); }
    public  void 	updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateNClob(int columnIndex, Reader reader) throws SQLException { throw notImplemented(); }
    public  void 	updateNClob(int columnIndex, Reader reader, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateNClob(String columnLabel, Reader reader) throws SQLException { throw notImplemented(); }
    public  void 	updateNClob(String columnLabel, Reader reader, long length) throws SQLException { throw notImplemented(); }
    public  void 	updateNString(int columnIndex, String nString) throws SQLException { throw notImplemented(); }
    public  void 	updateNString(String columnLabel, String nString) throws SQLException { throw notImplemented(); }
    public  void 	updateNull(int columnIndex) throws SQLException { throw notImplemented(); }
    public  void 	updateNull(String columnLabel) throws SQLException { throw notImplemented(); }
    public  void 	updateObject(int columnIndex, Object x) throws SQLException { throw notImplemented(); }
    public  void 	updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException { throw notImplemented(); }
    public  void 	updateObject(String columnLabel, Object x) throws SQLException { throw notImplemented(); }
    public  void 	updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException { throw notImplemented(); }
    public  void 	updateRef(int columnIndex, Ref x) throws SQLException { throw notImplemented(); }
    public  void 	updateRef(String columnLabel, Ref x) throws SQLException { throw notImplemented(); }
    public  void 	updateRow() throws SQLException { throw notImplemented(); }
    public  void 	updateShort(int columnIndex, short x) throws SQLException { throw notImplemented(); }
    public  void 	updateShort(String columnLabel, short x) throws SQLException { throw notImplemented(); }
    public  void 	updateString(int columnIndex, String x) throws SQLException { throw notImplemented(); }
    public  void 	updateString(String columnLabel, String x) throws SQLException { throw notImplemented(); }
    public  void 	updateTime(int columnIndex, Time x) throws SQLException { throw notImplemented(); }
    public  void 	updateTime(String columnLabel, Time x) throws SQLException { throw notImplemented(); }
    public  void 	updateTimestamp(int columnIndex, Timestamp x) throws SQLException { throw notImplemented(); }
    public  void 	updateTimestamp(String columnLabel, Timestamp x) throws SQLException { throw notImplemented(); }
    public  boolean 	wasNull() throws SQLException { throw notImplemented(); }

    //////////////////////////////////
    //
    //  JAVA 6 COMPATIBLE BEHAVIOR
    //
    //////////////////////////////////

    public  Object 	getObject(int columnIndex, Map<String,Class<?>> map) throws SQLException { throw notImplemented(); }
    public  Object 	getObject(String columnLabel, Map<String,Class<?>> map) throws SQLException { throw notImplemented(); }
    public  java.sql.NClob 	getNClob(int columnIndex) throws SQLException { throw notImplemented(); }
    public  java.sql.NClob 	getNClob(String columnLabel) throws SQLException { throw notImplemented(); }
    public  java.sql.RowId 	getRowId(int columnIndex) throws SQLException { throw notImplemented(); }
    public  java.sql.RowId 	getRowId(String columnLabel) throws SQLException { throw notImplemented(); }
    public  java.sql.SQLXML 	getSQLXML(int columnIndex) throws SQLException { throw notImplemented(); }
    public  java.sql.SQLXML 	getSQLXML(String columnLabel) throws SQLException { throw notImplemented(); }
    public  void 	updateNClob(int columnIndex, java.sql.NClob nClob) throws SQLException { throw notImplemented(); }
    public  void 	updateNClob(String columnLabel, java.sql.NClob nClob) throws SQLException { throw notImplemented(); }
    public  void 	updateRowId(int columnIndex, java.sql.RowId x) throws SQLException { throw notImplemented(); }
    public  void 	updateRowId(String columnLabel, java.sql.RowId x) throws SQLException { throw notImplemented(); }
    public  void 	updateSQLXML(int columnIndex, java.sql.SQLXML xmlObject) throws SQLException { throw notImplemented(); }
    public  void 	updateSQLXML(String columnLabel, java.sql.SQLXML xmlObject) throws SQLException { throw notImplemented(); }

    public  boolean 	isWrapperFor(Class<?> iface) throws SQLException { throw notImplemented(); }
    public  <T> T       unwrap(Class<T> iface) throws SQLException { throw notImplemented(); }

    /////////////////////////////////////////////////////////////////////////
    //
    //  MINIONS
    //
    /////////////////////////////////////////////////////////////////////////

    /**
     * <p>
     * Create a SQLException saying that the calling method is not implemented.
     * </p>
     */
    private SQLException    notImplemented()
    {
        StackTraceElement[]     stack = null;
        try {
            stack = (new Throwable()).getStackTrace();
         } catch (Throwable t) { return new SQLException( t.getMessage() ); }
        
        String      methodName = stack[ 0 ].getMethodName();

        return new SQLException( "Unimplemented method: " + methodName );
    }

}
