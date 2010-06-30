/*******************************************************************************
 * Copyright 2010 Omnidroid - http://code.google.com/p/omnidroid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package edu.nyu.cs.omnidroid.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import edu.nyu.cs.omnidroid.app.controller.util.Logger;
import edu.nyu.cs.omnidroid.app.model.db.DbHelper;
import edu.nyu.cs.omnidroid.app.model.db.LogDbAdapter;

/**
 * Abstract base class that provides general access to the Log DB layer. 
 *
 */
abstract public class CoreLogsDbHelper {
  // Class identifier
  private static final String TAG = CoreLogsDbHelper.class.getSimpleName();

  // DB Management
  protected Context context;
  protected DbHelper dbHelper;
  protected SQLiteDatabase database;
  protected LogDbAdapter logDbAdapter;

  /**
   * Creates a new CoreLogsDbHelper within the current context and initializes all necessary
   * database adapters.
   * 
   * @param context
   *          context for the application database resource
   */
  public CoreLogsDbHelper(Context context) {
    this.context = context;
    dbHelper = new DbHelper(context);
    database = dbHelper.getWritableDatabase();
  }

  /**
   * Close this database helper object. Attempting to use this object after this call will cause an
   * {@link IllegalStateException} being raised.
   */
  public void close() {
    Logger.i(TAG, "closing database.");
    database.close();
    
    // Not necessary, but also close all dbHelper databases just in case.
    dbHelper.close();
  }

  /**
   * 
   * @param log is a cursor to the current log to return
   * @return the {@code Log} at the cursor position
   */
  abstract public Log getLog(Cursor log);

  /*
   * Retrieves a cursor to the log that matched the ID passed in.
   * 
   * @param id
   *            - the ID for the Log that is desired.
   * @return a cursor to the event log requested.
   * 
   */
  public Cursor getLogMatchingID(long id) {
    return logDbAdapter.fetch(id);
  }

  /**
   * Insert a new Log record into DB
   * 
   * @param log
   *          log to store in the DB
   * @return id of the record inserted, -1 if unsuccessful
   */
  public long insert(Log log) {
    log.setTimestamp((new Date()).getTime());
    return logDbAdapter.insert(log);
  }

  /**
   * @return a List of {@code nLog}s that are stored in the DB.
   */
  public List<Log> getLogs() {
    ArrayList<Log> logs = new ArrayList<Log>();

    // Fetch all rules that match this event and are enabled
    Cursor logTable = logDbAdapter.fetchAll();

    // Build a rule for each row in the database and add it to the rule list
    while (logTable.moveToNext()) {
      logs.add(getLog(logTable));
    }
    logTable.close();
    return logs;
  }

}
