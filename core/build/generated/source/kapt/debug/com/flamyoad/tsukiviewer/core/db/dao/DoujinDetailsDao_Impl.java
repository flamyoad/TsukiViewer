package com.flamyoad.tsukiviewer.core.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter;
import com.flamyoad.tsukiviewer.core.model.DoujinDetails;
import com.flamyoad.tsukiviewer.core.model.DoujinDetailsWithTags;
import com.flamyoad.tsukiviewer.core.model.ShortTitle;
import com.flamyoad.tsukiviewer.core.model.Tag;
import java.io.File;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DoujinDetailsDao_Impl implements DoujinDetailsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DoujinDetails> __insertionAdapterOfDoujinDetails;

  private final FolderConverter __folderConverter = new FolderConverter();

  private final EntityDeletionOrUpdateAdapter<DoujinDetails> __deletionAdapterOfDoujinDetails;

  private final EntityDeletionOrUpdateAdapter<DoujinDetails> __updateAdapterOfDoujinDetails;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public DoujinDetailsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDoujinDetails = new EntityInsertionAdapter<DoujinDetails>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `doujin_details` (`id`,`nukeCode`,`fullTitleEnglish`,`fullTitleJapanese`,`shortTitleEnglish`,`absolutePath`,`folderName`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DoujinDetails entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        statement.bindLong(2, entity.getNukeCode());
        if (entity.getFullTitleEnglish() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getFullTitleEnglish());
        }
        if (entity.getFullTitleJapanese() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFullTitleJapanese());
        }
        if (entity.getShortTitleEnglish() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getShortTitleEnglish());
        }
        final String _tmp = __folderConverter.toString(entity.getAbsolutePath());
        if (_tmp == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp);
        }
        if (entity.getFolderName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getFolderName());
        }
      }
    };
    this.__deletionAdapterOfDoujinDetails = new EntityDeletionOrUpdateAdapter<DoujinDetails>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `doujin_details` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DoujinDetails entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfDoujinDetails = new EntityDeletionOrUpdateAdapter<DoujinDetails>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `doujin_details` SET `id` = ?,`nukeCode` = ?,`fullTitleEnglish` = ?,`fullTitleJapanese` = ?,`shortTitleEnglish` = ?,`absolutePath` = ?,`folderName` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DoujinDetails entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        statement.bindLong(2, entity.getNukeCode());
        if (entity.getFullTitleEnglish() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getFullTitleEnglish());
        }
        if (entity.getFullTitleJapanese() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFullTitleJapanese());
        }
        if (entity.getShortTitleEnglish() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getShortTitleEnglish());
        }
        final String _tmp = __folderConverter.toString(entity.getAbsolutePath());
        if (_tmp == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp);
        }
        if (entity.getFolderName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getFolderName());
        }
        if (entity.getId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getId());
        }
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM doujin_details";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DoujinDetails doujinDetails,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDoujinDetails.insertAndReturnId(doujinDetails);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final DoujinDetails doujinDetails,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDoujinDetails.handle(doujinDetails);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final DoujinDetails doujinDetails,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDoujinDetails.handle(doujinDetails);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object existsByTitle(final String fullTitleEnglish,
      final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT * FROM doujin_details WHERE fullTitleEnglish = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (fullTitleEnglish == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, fullTitleEnglish);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp == null ? null : _tmp != 0;
          } else {
            _result = null;
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
  public Object existsByAbsolutePath(final String absolutePath,
      final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT * FROM doujin_details WHERE absolutePath = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (absolutePath == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, absolutePath);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp == null ? null : _tmp != 0;
          } else {
            _result = null;
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
  public Object getAllShortTitles(final Continuation<? super List<ShortTitle>> $completion) {
    final String _sql = "SELECT shortTitleEnglish, absolutePath FROM doujin_details";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ShortTitle>>() {
      @Override
      @NonNull
      public List<ShortTitle> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfShortTitleEnglish = 0;
          final int _cursorIndexOfPath = 1;
          final List<ShortTitle> _result = new ArrayList<ShortTitle>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ShortTitle _item;
            final String _tmpShortTitleEnglish;
            if (_cursor.isNull(_cursorIndexOfShortTitleEnglish)) {
              _tmpShortTitleEnglish = null;
            } else {
              _tmpShortTitleEnglish = _cursor.getString(_cursorIndexOfShortTitleEnglish);
            }
            final File _tmpPath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfPath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfPath);
            }
            _tmpPath = __folderConverter.toFolder(_tmp);
            _item = new ShortTitle(_tmpShortTitleEnglish,_tmpPath);
            _result.add(_item);
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
  public Object findByTitle(final String query,
      final Continuation<? super List<DoujinDetails>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM doujin_details \n"
            + "        WHERE fullTitleEnglish LIKE '%' || ? || '%' OR \n"
            + "        fullTitleJapanese LIKE '%' || ? || '%'\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    _argIndex = 2;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
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
            final DoujinDetails _item;
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
            _item = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
            _result.add(_item);
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
  public Object findByAbsolutePath(final String absolutePath,
      final Continuation<? super List<DoujinDetails>> $completion) {
    final String _sql = "SELECT * FROM doujin_details WHERE absolutePath = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (absolutePath == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, absolutePath);
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
            final DoujinDetails _item;
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
            _item = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
            _result.add(_item);
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
  public List<String> findShortTitleByPath(final String absolutePath) {
    final String _sql = "SELECT shortTitleEnglish FROM doujin_details WHERE absolutePath = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (absolutePath == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, absolutePath);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final List<String> _result = new ArrayList<String>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final String _item;
        if (_cursor.isNull(0)) {
          _item = null;
        } else {
          _item = _cursor.getString(0);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Object findByTags(final List<String> tags, final int tagCount,
      final Continuation<? super List<DoujinDetails>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM doujin_tags as dt");
    _stringBuilder.append("\n");
    _stringBuilder.append("        INNER JOIN doujin_details ON doujin_details.id = dt.doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        INNER JOIN tags ON tags.tagId = dt.tagId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE name IN (");
    final int _inputSize = tags.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    _stringBuilder.append("\n");
    _stringBuilder.append("        GROUP BY doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        HAVING COUNT(doujinId) = ");
    _stringBuilder.append("?");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 1 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : tags) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindString(_argIndex, _item);
      }
      _argIndex++;
    }
    _argIndex = 1 + _inputSize;
    _statement.bindLong(_argIndex, tagCount);
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
  public Object findByTags(final List<String> tags,
      final Continuation<? super List<DoujinDetails>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM doujin_tags as dt");
    _stringBuilder.append("\n");
    _stringBuilder.append("        INNER JOIN doujin_details ON doujin_details.id = dt.doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        INNER JOIN tags ON tags.tagId = dt.tagId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE name IN (");
    final int _inputSize = tags.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    _stringBuilder.append("\n");
    _stringBuilder.append("        GROUP BY doujinId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        HAVING COUNT(doujinId) = 1");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : tags) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindString(_argIndex, _item);
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
  public Object getAllShortDetails(final Continuation<? super List<DoujinDetails>> $completion) {
    final String _sql = "SELECT * FROM doujin_details";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
            final DoujinDetails _item;
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
            _item = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
            _result.add(_item);
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
  public Object getAllLongDetails(
      final Continuation<? super List<DoujinDetailsWithTags>> $completion) {
    final String _sql = "SELECT * FROM doujin_details";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<List<DoujinDetailsWithTags>>() {
      @Override
      @NonNull
      public List<DoujinDetailsWithTags> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfNukeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "nukeCode");
            final int _cursorIndexOfFullTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleEnglish");
            final int _cursorIndexOfFullTitleJapanese = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleJapanese");
            final int _cursorIndexOfShortTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "shortTitleEnglish");
            final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
            final int _cursorIndexOfFolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "folderName");
            final LongSparseArray<ArrayList<Tag>> _collectionTags = new LongSparseArray<ArrayList<Tag>>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfId);
              }
              if (_tmpKey != null) {
                if (!_collectionTags.containsKey(_tmpKey)) {
                  _collectionTags.put(_tmpKey, new ArrayList<Tag>());
                }
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiptagsAscomFlamyoadTsukiviewerCoreModelTag(_collectionTags);
            final List<DoujinDetailsWithTags> _result = new ArrayList<DoujinDetailsWithTags>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final DoujinDetailsWithTags _item;
              final DoujinDetails _tmpDoujinDetails;
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
              _tmpDoujinDetails = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
              final ArrayList<Tag> _tmpTagsCollection;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              }
              if (_tmpKey_1 != null) {
                _tmpTagsCollection = _collectionTags.get(_tmpKey_1);
              } else {
                _tmpTagsCollection = new ArrayList<Tag>();
              }
              _item = new DoujinDetailsWithTags(_tmpDoujinDetails,_tmpTagsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<DoujinDetailsWithTags> getLongDetailsByFullTitle(final String fullTitle) {
    final String _sql = "SELECT * FROM doujin_details WHERE fullTitleEnglish = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (fullTitle == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, fullTitle);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"doujin_tags", "tags",
        "doujin_details"}, true, new Callable<DoujinDetailsWithTags>() {
      @Override
      @Nullable
      public DoujinDetailsWithTags call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfNukeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "nukeCode");
            final int _cursorIndexOfFullTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleEnglish");
            final int _cursorIndexOfFullTitleJapanese = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleJapanese");
            final int _cursorIndexOfShortTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "shortTitleEnglish");
            final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
            final int _cursorIndexOfFolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "folderName");
            final LongSparseArray<ArrayList<Tag>> _collectionTags = new LongSparseArray<ArrayList<Tag>>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfId);
              }
              if (_tmpKey != null) {
                if (!_collectionTags.containsKey(_tmpKey)) {
                  _collectionTags.put(_tmpKey, new ArrayList<Tag>());
                }
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiptagsAscomFlamyoadTsukiviewerCoreModelTag(_collectionTags);
            final DoujinDetailsWithTags _result;
            if (_cursor.moveToFirst()) {
              final DoujinDetails _tmpDoujinDetails;
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
              _tmpDoujinDetails = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
              final ArrayList<Tag> _tmpTagsCollection;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              }
              if (_tmpKey_1 != null) {
                _tmpTagsCollection = _collectionTags.get(_tmpKey_1);
              } else {
                _tmpTagsCollection = new ArrayList<Tag>();
              }
              _result = new DoujinDetailsWithTags(_tmpDoujinDetails,_tmpTagsCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<DoujinDetailsWithTags> getLongDetailsByPath(final String absolutePath) {
    final String _sql = "SELECT * FROM doujin_details WHERE absolutePath = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (absolutePath == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, absolutePath);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"doujin_tags", "tags",
        "doujin_details"}, true, new Callable<DoujinDetailsWithTags>() {
      @Override
      @Nullable
      public DoujinDetailsWithTags call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfNukeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "nukeCode");
            final int _cursorIndexOfFullTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleEnglish");
            final int _cursorIndexOfFullTitleJapanese = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleJapanese");
            final int _cursorIndexOfShortTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "shortTitleEnglish");
            final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
            final int _cursorIndexOfFolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "folderName");
            final LongSparseArray<ArrayList<Tag>> _collectionTags = new LongSparseArray<ArrayList<Tag>>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfId);
              }
              if (_tmpKey != null) {
                if (!_collectionTags.containsKey(_tmpKey)) {
                  _collectionTags.put(_tmpKey, new ArrayList<Tag>());
                }
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiptagsAscomFlamyoadTsukiviewerCoreModelTag(_collectionTags);
            final DoujinDetailsWithTags _result;
            if (_cursor.moveToFirst()) {
              final DoujinDetails _tmpDoujinDetails;
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
              _tmpDoujinDetails = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
              final ArrayList<Tag> _tmpTagsCollection;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              }
              if (_tmpKey_1 != null) {
                _tmpTagsCollection = _collectionTags.get(_tmpKey_1);
              } else {
                _tmpTagsCollection = new ArrayList<Tag>();
              }
              _result = new DoujinDetailsWithTags(_tmpDoujinDetails,_tmpTagsCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getLongDetailsByPathBlocking(final String absolutePath,
      final Continuation<? super DoujinDetailsWithTags> $completion) {
    final String _sql = "SELECT * FROM doujin_details WHERE absolutePath = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (absolutePath == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, absolutePath);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<DoujinDetailsWithTags>() {
      @Override
      @Nullable
      public DoujinDetailsWithTags call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfNukeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "nukeCode");
            final int _cursorIndexOfFullTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleEnglish");
            final int _cursorIndexOfFullTitleJapanese = CursorUtil.getColumnIndexOrThrow(_cursor, "fullTitleJapanese");
            final int _cursorIndexOfShortTitleEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "shortTitleEnglish");
            final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
            final int _cursorIndexOfFolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "folderName");
            final LongSparseArray<ArrayList<Tag>> _collectionTags = new LongSparseArray<ArrayList<Tag>>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfId);
              }
              if (_tmpKey != null) {
                if (!_collectionTags.containsKey(_tmpKey)) {
                  _collectionTags.put(_tmpKey, new ArrayList<Tag>());
                }
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiptagsAscomFlamyoadTsukiviewerCoreModelTag(_collectionTags);
            final DoujinDetailsWithTags _result;
            if (_cursor.moveToFirst()) {
              final DoujinDetails _tmpDoujinDetails;
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
              _tmpDoujinDetails = new DoujinDetails(_tmpId,_tmpNukeCode,_tmpFullTitleEnglish,_tmpFullTitleJapanese,_tmpShortTitleEnglish,_tmpAbsolutePath,_tmpFolderName);
              final ArrayList<Tag> _tmpTagsCollection;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              }
              if (_tmpKey_1 != null) {
                _tmpTagsCollection = _collectionTags.get(_tmpKey_1);
              } else {
                _tmpTagsCollection = new ArrayList<Tag>();
              }
              _result = new DoujinDetailsWithTags(_tmpDoujinDetails,_tmpTagsCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshiptagsAscomFlamyoadTsukiviewerCoreModelTag(
      @NonNull final LongSparseArray<ArrayList<Tag>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshiptagsAscomFlamyoadTsukiviewerCoreModelTag(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `tags`.`tagId` AS `tagId`,`tags`.`type` AS `type`,`tags`.`name` AS `name`,`tags`.`url` AS `url`,`tags`.`count` AS `count`,_junction.`doujinId` FROM `doujin_tags` AS _junction INNER JOIN `tags` ON (_junction.`tagId` = `tags`.`tagId`) WHERE _junction.`doujinId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      // _junction.doujinId;
      final int _itemKeyIndex = 5;
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfTagId = 0;
      final int _cursorIndexOfType = 1;
      final int _cursorIndexOfName = 2;
      final int _cursorIndexOfUrl = 3;
      final int _cursorIndexOfCount = 4;
      while (_cursor.moveToNext()) {
        final Long _tmpKey;
        if (_cursor.isNull(_itemKeyIndex)) {
          _tmpKey = null;
        } else {
          _tmpKey = _cursor.getLong(_itemKeyIndex);
        }
        if (_tmpKey != null) {
          final ArrayList<Tag> _tmpRelation = _map.get(_tmpKey);
          if (_tmpRelation != null) {
            final Tag _item_1;
            final Long _tmpTagId;
            if (_cursor.isNull(_cursorIndexOfTagId)) {
              _tmpTagId = null;
            } else {
              _tmpTagId = _cursor.getLong(_cursorIndexOfTagId);
            }
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpUrl;
            if (_cursor.isNull(_cursorIndexOfUrl)) {
              _tmpUrl = null;
            } else {
              _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            }
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item_1 = new Tag(_tmpTagId,_tmpType,_tmpName,_tmpUrl,_tmpCount);
            _tmpRelation.add(_item_1);
          }
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
