package com.flamyoad.tsukiviewer.core.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter;
import com.flamyoad.tsukiviewer.core.model.DoujinDetails;
import java.io.File;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CollectionDoujinDao_Impl implements CollectionDoujinDao {
  private final RoomDatabase __db;

  private final FolderConverter __folderConverter = new FolderConverter();

  public CollectionDoujinDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
  }

  @Override
  public Object searchIncludedOr(final List<Long> includedTagsId,
      final Continuation<? super List<DoujinDetails>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM doujin_details as details");
    _stringBuilder.append("\n");
    _stringBuilder.append("        INNER JOIN doujin_tags ON details.id = doujin_tags.doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE tagId IN (");
    final int _inputSize = includedTagsId.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") ");
    _stringBuilder.append("\n");
    _stringBuilder.append("        GROUP BY details.id");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (Long _item : includedTagsId) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindLong(_argIndex, _item);
      }
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DoujinDetails>>() {
      @Override
      @NonNull
      public List<DoujinDetails> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNukeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "nukeCode");
          final int _cursorIndexOfFullTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleEnglish");
          final int _cursorIndexOfFullTitleJapanese = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleJapanese");
          final int _cursorIndexOfShortTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "shortTitleEnglish");
          final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
          final int _cursorIndexOfFolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "folderName");
          final List<DoujinDetails> _result = new ArrayList<DoujinDetails>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DoujinDetails _item_1;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final int _tmpNukeCode;
            _tmpNukeCode = _cursor.getInt(_cursorIndexOfNukeCode);
            final String _tmpFullTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfFullTitleEnglish)) {
              _tmpFullTitleEnglish = null;
            } else {
              _tmpFullTitleEnglish = _cursor.getString(_cursorIndexOfFullTitleEnglish);
            }
            final String _tmpFullTitleJapanese;
            if (_cursor.isNull(_cursorIndexOfFullTitleJapanese)) {
              _tmpFullTitleJapanese = null;
            } else {
              _tmpFullTitleJapanese = _cursor.getString(_cursorIndexOfFullTitleJapanese);
            }
            final String _tmpShortTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfShortTitleEnglish)) {
              _tmpShortTitleEnglish = null;
            } else {
              _tmpShortTitleEnglish = _cursor.getString(_cursorIndexOfShortTitleEnglish);
            }
            final File _tmpAbsolutePath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAbsolutePath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAbsolutePath);
            }
            _tmpAbsolutePath = __folderConverter.toFolder(_tmp);
            final String _tmpFolderName;
            if (_cursor.isNull(_cursorIndexOfFolderName)) {
              _tmpFolderName = null;
            } else {
              _tmpFolderName = _cursor.getString(_cursorIndexOfFolderName);
            }
            _item_1 = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
            _result.add(_item_1);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchIncludedAnd(final List<Long> includedTagsId, final int includedTagsCount,
      final Continuation<? super List<DoujinDetails>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("        INNER JOIN doujin_details ON doujin_tags.doujinId = doujin_details.id");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE doujin_tags.tagId IN (");
    final int _inputSize = includedTagsId.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")  ----> Replace with list args");
    _stringBuilder.append("\n");
    _stringBuilder.append("        GROUP BY doujin_tags.doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        HAVING COUNT(*) = ");
    _stringBuilder.append("?");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 1 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (Long _item : includedTagsId) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindLong(_argIndex, _item);
      }
      _argIndex++;
    }
    _argIndex = 1 + _inputSize;
    _statement.bindLong(_argIndex, includedTagsCount);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DoujinDetails>>() {
      @Override
      @NonNull
      public List<DoujinDetails> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNukeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "nukeCode");
          final int _cursorIndexOfFullTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleEnglish");
          final int _cursorIndexOfFullTitleJapanese = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleJapanese");
          final int _cursorIndexOfShortTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "shortTitleEnglish");
          final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
          final int _cursorIndexOfFolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "folderName");
          final List<DoujinDetails> _result = new ArrayList<DoujinDetails>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DoujinDetails _item_1;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final int _tmpNukeCode;
            _tmpNukeCode = _cursor.getInt(_cursorIndexOfNukeCode);
            final String _tmpFullTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfFullTitleEnglish)) {
              _tmpFullTitleEnglish = null;
            } else {
              _tmpFullTitleEnglish = _cursor.getString(_cursorIndexOfFullTitleEnglish);
            }
            final String _tmpFullTitleJapanese;
            if (_cursor.isNull(_cursorIndexOfFullTitleJapanese)) {
              _tmpFullTitleJapanese = null;
            } else {
              _tmpFullTitleJapanese = _cursor.getString(_cursorIndexOfFullTitleJapanese);
            }
            final String _tmpShortTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfShortTitleEnglish)) {
              _tmpShortTitleEnglish = null;
            } else {
              _tmpShortTitleEnglish = _cursor.getString(_cursorIndexOfShortTitleEnglish);
            }
            final File _tmpAbsolutePath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAbsolutePath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAbsolutePath);
            }
            _tmpAbsolutePath = __folderConverter.toFolder(_tmp);
            final String _tmpFolderName;
            if (_cursor.isNull(_cursorIndexOfFolderName)) {
              _tmpFolderName = null;
            } else {
              _tmpFolderName = _cursor.getString(_cursorIndexOfFolderName);
            }
            _item_1 = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
            _result.add(_item_1);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchExcludedOr(final List<Long> excludedTagsId,
      final Continuation<? super List<DoujinDetails>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM doujin_details WHERE id IN (");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        SELECT doujinId FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t\t        EXCEPT");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        SELECT doujinId FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        WHERE doujin_tags.tagId IN (");
    final int _inputSize = excludedTagsId.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        GROUP BY doujin_tags.doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ) ");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (Long _item : excludedTagsId) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindLong(_argIndex, _item);
      }
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DoujinDetails>>() {
      @Override
      @NonNull
      public List<DoujinDetails> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNukeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "nukeCode");
          final int _cursorIndexOfFullTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleEnglish");
          final int _cursorIndexOfFullTitleJapanese = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleJapanese");
          final int _cursorIndexOfShortTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "shortTitleEnglish");
          final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
          final int _cursorIndexOfFolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "folderName");
          final List<DoujinDetails> _result = new ArrayList<DoujinDetails>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DoujinDetails _item_1;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final int _tmpNukeCode;
            _tmpNukeCode = _cursor.getInt(_cursorIndexOfNukeCode);
            final String _tmpFullTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfFullTitleEnglish)) {
              _tmpFullTitleEnglish = null;
            } else {
              _tmpFullTitleEnglish = _cursor.getString(_cursorIndexOfFullTitleEnglish);
            }
            final String _tmpFullTitleJapanese;
            if (_cursor.isNull(_cursorIndexOfFullTitleJapanese)) {
              _tmpFullTitleJapanese = null;
            } else {
              _tmpFullTitleJapanese = _cursor.getString(_cursorIndexOfFullTitleJapanese);
            }
            final String _tmpShortTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfShortTitleEnglish)) {
              _tmpShortTitleEnglish = null;
            } else {
              _tmpShortTitleEnglish = _cursor.getString(_cursorIndexOfShortTitleEnglish);
            }
            final File _tmpAbsolutePath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAbsolutePath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAbsolutePath);
            }
            _tmpAbsolutePath = __folderConverter.toFolder(_tmp);
            final String _tmpFolderName;
            if (_cursor.isNull(_cursorIndexOfFolderName)) {
              _tmpFolderName = null;
            } else {
              _tmpFolderName = _cursor.getString(_cursorIndexOfFolderName);
            }
            _item_1 = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
            _result.add(_item_1);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchExcludedAnd(final List<Long> excludedTagsId, final int excludedTagsCount,
      final Continuation<? super List<DoujinDetails>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM doujin_details WHERE id IN (");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        SELECT doujinId FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t\t        EXCEPT");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        SELECT doujinId FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        INNER JOIN doujin_details ON doujin_tags.doujinId = doujin_details.id");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        WHERE doujin_tags.tagId IN (");
    final int _inputSize = excludedTagsId.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") ");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        GROUP BY doujin_tags.doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        HAVING COUNT(*) = ");
    _stringBuilder.append("?");
    _stringBuilder.append("\n");
    _stringBuilder.append(") ");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 1 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (Long _item : excludedTagsId) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindLong(_argIndex, _item);
      }
      _argIndex++;
    }
    _argIndex = 1 + _inputSize;
    _statement.bindLong(_argIndex, excludedTagsCount);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DoujinDetails>>() {
      @Override
      @NonNull
      public List<DoujinDetails> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNukeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "nukeCode");
          final int _cursorIndexOfFullTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleEnglish");
          final int _cursorIndexOfFullTitleJapanese = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleJapanese");
          final int _cursorIndexOfShortTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "shortTitleEnglish");
          final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
          final int _cursorIndexOfFolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "folderName");
          final List<DoujinDetails> _result = new ArrayList<DoujinDetails>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DoujinDetails _item_1;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final int _tmpNukeCode;
            _tmpNukeCode = _cursor.getInt(_cursorIndexOfNukeCode);
            final String _tmpFullTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfFullTitleEnglish)) {
              _tmpFullTitleEnglish = null;
            } else {
              _tmpFullTitleEnglish = _cursor.getString(_cursorIndexOfFullTitleEnglish);
            }
            final String _tmpFullTitleJapanese;
            if (_cursor.isNull(_cursorIndexOfFullTitleJapanese)) {
              _tmpFullTitleJapanese = null;
            } else {
              _tmpFullTitleJapanese = _cursor.getString(_cursorIndexOfFullTitleJapanese);
            }
            final String _tmpShortTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfShortTitleEnglish)) {
              _tmpShortTitleEnglish = null;
            } else {
              _tmpShortTitleEnglish = _cursor.getString(_cursorIndexOfShortTitleEnglish);
            }
            final File _tmpAbsolutePath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAbsolutePath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAbsolutePath);
            }
            _tmpAbsolutePath = __folderConverter.toFolder(_tmp);
            final String _tmpFolderName;
            if (_cursor.isNull(_cursorIndexOfFolderName)) {
              _tmpFolderName = null;
            } else {
              _tmpFolderName = _cursor.getString(_cursorIndexOfFolderName);
            }
            _item_1 = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
            _result.add(_item_1);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchIncludedOrExcludedOr(final List<Long> includedTagsId,
      final List<Long> excludedTagsId,
      final Continuation<? super List<DoujinDetails>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM doujin_details ");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE id IN (");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        SELECT doujinId FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        WHERE doujin_tags.tagId IN (");
    final int _inputSize = includedTagsId.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") ");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        GROUP BY doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t\t        EXCEPT ");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        SELECT doujinId FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("            WHERE doujin_tags.tagId IN (");
    final int _inputSize_1 = excludedTagsId.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize_1);
    _stringBuilder.append(")");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        GROUP BY doujin_tags.doujinId)");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize + _inputSize_1;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (Long _item : includedTagsId) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindLong(_argIndex, _item);
      }
      _argIndex++;
    }
    _argIndex = 1 + _inputSize;
    for (Long _item_1 : excludedTagsId) {
      if (_item_1 == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindLong(_argIndex, _item_1);
      }
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DoujinDetails>>() {
      @Override
      @NonNull
      public List<DoujinDetails> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNukeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "nukeCode");
          final int _cursorIndexOfFullTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleEnglish");
          final int _cursorIndexOfFullTitleJapanese = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleJapanese");
          final int _cursorIndexOfShortTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "shortTitleEnglish");
          final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
          final int _cursorIndexOfFolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "folderName");
          final List<DoujinDetails> _result = new ArrayList<DoujinDetails>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DoujinDetails _item_2;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final int _tmpNukeCode;
            _tmpNukeCode = _cursor.getInt(_cursorIndexOfNukeCode);
            final String _tmpFullTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfFullTitleEnglish)) {
              _tmpFullTitleEnglish = null;
            } else {
              _tmpFullTitleEnglish = _cursor.getString(_cursorIndexOfFullTitleEnglish);
            }
            final String _tmpFullTitleJapanese;
            if (_cursor.isNull(_cursorIndexOfFullTitleJapanese)) {
              _tmpFullTitleJapanese = null;
            } else {
              _tmpFullTitleJapanese = _cursor.getString(_cursorIndexOfFullTitleJapanese);
            }
            final String _tmpShortTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfShortTitleEnglish)) {
              _tmpShortTitleEnglish = null;
            } else {
              _tmpShortTitleEnglish = _cursor.getString(_cursorIndexOfShortTitleEnglish);
            }
            final File _tmpAbsolutePath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAbsolutePath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAbsolutePath);
            }
            _tmpAbsolutePath = __folderConverter.toFolder(_tmp);
            final String _tmpFolderName;
            if (_cursor.isNull(_cursorIndexOfFolderName)) {
              _tmpFolderName = null;
            } else {
              _tmpFolderName = _cursor.getString(_cursorIndexOfFolderName);
            }
            _item_2 = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
            _result.add(_item_2);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchIncludedOrExcludedAnd(final List<Long> includedTagsId,
      final List<Long> excludedTagsId, final int excludedTagsCount,
      final Continuation<? super List<DoujinDetails>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM doujin_details ");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE id IN (");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        SELECT doujinId FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        WHERE doujin_tags.tagId IN (");
    final int _inputSize = includedTagsId.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") ");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        GROUP BY doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t\t        EXCEPT ");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        SELECT doujinId FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        WHERE doujin_tags.tagId IN (");
    final int _inputSize_1 = excludedTagsId.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize_1);
    _stringBuilder.append(")");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        GROUP BY doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        HAVING COUNT(*) = ");
    _stringBuilder.append("?");
    _stringBuilder.append(")  -----> Replace with list size");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 1 + _inputSize + _inputSize_1;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (Long _item : includedTagsId) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindLong(_argIndex, _item);
      }
      _argIndex++;
    }
    _argIndex = 1 + _inputSize;
    for (Long _item_1 : excludedTagsId) {
      if (_item_1 == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindLong(_argIndex, _item_1);
      }
      _argIndex++;
    }
    _argIndex = 1 + _inputSize + _inputSize_1;
    _statement.bindLong(_argIndex, excludedTagsCount);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DoujinDetails>>() {
      @Override
      @NonNull
      public List<DoujinDetails> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNukeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "nukeCode");
          final int _cursorIndexOfFullTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleEnglish");
          final int _cursorIndexOfFullTitleJapanese = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleJapanese");
          final int _cursorIndexOfShortTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "shortTitleEnglish");
          final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
          final int _cursorIndexOfFolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "folderName");
          final List<DoujinDetails> _result = new ArrayList<DoujinDetails>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DoujinDetails _item_2;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final int _tmpNukeCode;
            _tmpNukeCode = _cursor.getInt(_cursorIndexOfNukeCode);
            final String _tmpFullTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfFullTitleEnglish)) {
              _tmpFullTitleEnglish = null;
            } else {
              _tmpFullTitleEnglish = _cursor.getString(_cursorIndexOfFullTitleEnglish);
            }
            final String _tmpFullTitleJapanese;
            if (_cursor.isNull(_cursorIndexOfFullTitleJapanese)) {
              _tmpFullTitleJapanese = null;
            } else {
              _tmpFullTitleJapanese = _cursor.getString(_cursorIndexOfFullTitleJapanese);
            }
            final String _tmpShortTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfShortTitleEnglish)) {
              _tmpShortTitleEnglish = null;
            } else {
              _tmpShortTitleEnglish = _cursor.getString(_cursorIndexOfShortTitleEnglish);
            }
            final File _tmpAbsolutePath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAbsolutePath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAbsolutePath);
            }
            _tmpAbsolutePath = __folderConverter.toFolder(_tmp);
            final String _tmpFolderName;
            if (_cursor.isNull(_cursorIndexOfFolderName)) {
              _tmpFolderName = null;
            } else {
              _tmpFolderName = _cursor.getString(_cursorIndexOfFolderName);
            }
            _item_2 = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
            _result.add(_item_2);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchIncludedAndExcludedOr(final List<Long> includedTagsId,
      final List<Long> excludedTagsId, final int includedTagsCount,
      final Continuation<? super List<DoujinDetails>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM doujin_details ");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE id IN (");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        SELECT doujinId FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        WHERE doujin_tags.tagId IN (");
    final int _inputSize = includedTagsId.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") ");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        GROUP BY doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        HAVING COUNT(*) = ");
    _stringBuilder.append("?");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t\t        EXCEPT ");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        SELECT doujinId FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        WHERE doujin_tags.tagId IN (");
    final int _inputSize_1 = excludedTagsId.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize_1);
    _stringBuilder.append(")");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        GROUP BY doujinId)");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 1 + _inputSize + _inputSize_1;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (Long _item : includedTagsId) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindLong(_argIndex, _item);
      }
      _argIndex++;
    }
    _argIndex = 1 + _inputSize;
    _statement.bindLong(_argIndex, includedTagsCount);
    _argIndex = 2 + _inputSize;
    for (Long _item_1 : excludedTagsId) {
      if (_item_1 == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindLong(_argIndex, _item_1);
      }
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DoujinDetails>>() {
      @Override
      @NonNull
      public List<DoujinDetails> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNukeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "nukeCode");
          final int _cursorIndexOfFullTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleEnglish");
          final int _cursorIndexOfFullTitleJapanese = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleJapanese");
          final int _cursorIndexOfShortTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "shortTitleEnglish");
          final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
          final int _cursorIndexOfFolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "folderName");
          final List<DoujinDetails> _result = new ArrayList<DoujinDetails>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DoujinDetails _item_2;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final int _tmpNukeCode;
            _tmpNukeCode = _cursor.getInt(_cursorIndexOfNukeCode);
            final String _tmpFullTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfFullTitleEnglish)) {
              _tmpFullTitleEnglish = null;
            } else {
              _tmpFullTitleEnglish = _cursor.getString(_cursorIndexOfFullTitleEnglish);
            }
            final String _tmpFullTitleJapanese;
            if (_cursor.isNull(_cursorIndexOfFullTitleJapanese)) {
              _tmpFullTitleJapanese = null;
            } else {
              _tmpFullTitleJapanese = _cursor.getString(_cursorIndexOfFullTitleJapanese);
            }
            final String _tmpShortTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfShortTitleEnglish)) {
              _tmpShortTitleEnglish = null;
            } else {
              _tmpShortTitleEnglish = _cursor.getString(_cursorIndexOfShortTitleEnglish);
            }
            final File _tmpAbsolutePath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAbsolutePath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAbsolutePath);
            }
            _tmpAbsolutePath = __folderConverter.toFolder(_tmp);
            final String _tmpFolderName;
            if (_cursor.isNull(_cursorIndexOfFolderName)) {
              _tmpFolderName = null;
            } else {
              _tmpFolderName = _cursor.getString(_cursorIndexOfFolderName);
            }
            _item_2 = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
            _result.add(_item_2);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchIncludedAndExcludedAnd(final List<Long> includedTagsId,
      final List<Long> excludedTagsId, final int includedTagsCount, final int excludedTagsCount,
      final Continuation<? super List<DoujinDetails>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM doujin_details ");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE id IN (");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        SELECT doujinId FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        WHERE doujin_tags.tagId IN (");
    final int _inputSize = includedTagsId.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")  ");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        GROUP BY doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        HAVING COUNT(*) = ");
    _stringBuilder.append("?");
    _stringBuilder.append("   ");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t\t        EXCEPT ");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        SELECT doujinId FROM doujin_tags");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        WHERE doujin_tags.tagId IN (");
    final int _inputSize_1 = excludedTagsId.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize_1);
    _stringBuilder.append(")");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        GROUP BY doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("\t        HAVING COUNT(*) = ");
    _stringBuilder.append("?");
    _stringBuilder.append("  ");
    _stringBuilder.append("\n");
    _stringBuilder.append(")");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 2 + _inputSize + _inputSize_1;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (Long _item : includedTagsId) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindLong(_argIndex, _item);
      }
      _argIndex++;
    }
    _argIndex = 1 + _inputSize;
    _statement.bindLong(_argIndex, includedTagsCount);
    _argIndex = 2 + _inputSize;
    for (Long _item_1 : excludedTagsId) {
      if (_item_1 == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindLong(_argIndex, _item_1);
      }
      _argIndex++;
    }
    _argIndex = 2 + _inputSize + _inputSize_1;
    _statement.bindLong(_argIndex, excludedTagsCount);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DoujinDetails>>() {
      @Override
      @NonNull
      public List<DoujinDetails> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNukeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "nukeCode");
          final int _cursorIndexOfFullTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleEnglish");
          final int _cursorIndexOfFullTitleJapanese = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleJapanese");
          final int _cursorIndexOfShortTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "shortTitleEnglish");
          final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
          final int _cursorIndexOfFolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "folderName");
          final List<DoujinDetails> _result = new ArrayList<DoujinDetails>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DoujinDetails _item_2;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final int _tmpNukeCode;
            _tmpNukeCode = _cursor.getInt(_cursorIndexOfNukeCode);
            final String _tmpFullTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfFullTitleEnglish)) {
              _tmpFullTitleEnglish = null;
            } else {
              _tmpFullTitleEnglish = _cursor.getString(_cursorIndexOfFullTitleEnglish);
            }
            final String _tmpFullTitleJapanese;
            if (_cursor.isNull(_cursorIndexOfFullTitleJapanese)) {
              _tmpFullTitleJapanese = null;
            } else {
              _tmpFullTitleJapanese = _cursor.getString(_cursorIndexOfFullTitleJapanese);
            }
            final String _tmpShortTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfShortTitleEnglish)) {
              _tmpShortTitleEnglish = null;
            } else {
              _tmpShortTitleEnglish = _cursor.getString(_cursorIndexOfShortTitleEnglish);
            }
            final File _tmpAbsolutePath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAbsolutePath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAbsolutePath);
            }
            _tmpAbsolutePath = __folderConverter.toFolder(_tmp);
            final String _tmpFolderName;
            if (_cursor.isNull(_cursorIndexOfFolderName)) {
              _tmpFolderName = null;
            } else {
              _tmpFolderName = _cursor.getString(_cursorIndexOfFolderName);
            }
            _item_2 = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
            _result.add(_item_2);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
