package com.flamyoad.tsukiviewer.core.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter;
import com.flamyoad.tsukiviewer.core.model.BookmarkItem;
import java.io.File;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
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
public final class BookmarkItemDao_Impl implements BookmarkItemDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BookmarkItem> __insertionAdapterOfBookmarkItem;

  private final FolderConverter __folderConverter = new FolderConverter();

  private final EntityDeletionOrUpdateAdapter<BookmarkItem> __deletionAdapterOfBookmarkItem;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  private final SharedSQLiteStatement __preparedStmtOfDeleteFromAllGroups;

  public BookmarkItemDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBookmarkItem = new EntityInsertionAdapter<BookmarkItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `bookmark_item` (`id`,`absolutePath`,`parentName`,`dateAdded`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookmarkItem entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        final String _tmp = __folderConverter.toString(entity.getAbsolutePath());
        if (_tmp == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp);
        }
        if (entity.getParentName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getParentName());
        }
        statement.bindLong(4, entity.getDateAdded());
      }
    };
    this.__deletionAdapterOfBookmarkItem = new EntityDeletionOrUpdateAdapter<BookmarkItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `bookmark_item` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookmarkItem entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmark_item WHERE absolutePath = ? AND parentName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteFromAllGroups = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmark_item WHERE absolutePath = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final BookmarkItem item, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBookmarkItem.insertAndReturnId(item);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insert(final List<BookmarkItem> items,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfBookmarkItem.insertAndReturnIdsList(items);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final BookmarkItem item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBookmarkItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final List<BookmarkItem> items,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        int _total = 0;
        __db.beginTransaction();
        try {
          _total += __deletionAdapterOfBookmarkItem.handleMultiple(items);
          __db.setTransactionSuccessful();
          return _total;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final File absolutePath, final String groupName,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        final String _tmp = __folderConverter.toString(absolutePath);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
        }
        _argIndex = 2;
        if (groupName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, groupName);
        }
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteFromAllGroups(final File path,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteFromAllGroups.acquire();
        int _argIndex = 1;
        final String _tmp = __folderConverter.toString(path);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
        }
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteFromAllGroups.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<List<BookmarkItem>> selectAll() {
    final String _sql = "SELECT * FROM bookmark_item";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"bookmark_item"}, false, new Callable<List<BookmarkItem>>() {
      @Override
      @Nullable
      public List<BookmarkItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
          final int _cursorIndexOfParentName = CursorUtil.getColumnIndexOrThrow(_cursor, "parentName");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final List<BookmarkItem> _result = new ArrayList<BookmarkItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BookmarkItem _item;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final File _tmpAbsolutePath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAbsolutePath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAbsolutePath);
            }
            _tmpAbsolutePath = __folderConverter.toFolder(_tmp);
            final String _tmpParentName;
            if (_cursor.isNull(_cursorIndexOfParentName)) {
              _tmpParentName = null;
            } else {
              _tmpParentName = _cursor.getString(_cursorIndexOfParentName);
            }
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            _item = new BookmarkItem(_tmpId,_tmpAbsolutePath,_tmpParentName,_tmpDateAdded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<BookmarkItem>> from(final String groupName) {
    final String _sql = "SELECT * FROM bookmark_item WHERE parentName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (groupName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, groupName);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"bookmark_item"}, false, new Callable<List<BookmarkItem>>() {
      @Override
      @Nullable
      public List<BookmarkItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
          final int _cursorIndexOfParentName = CursorUtil.getColumnIndexOrThrow(_cursor, "parentName");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final List<BookmarkItem> _result = new ArrayList<BookmarkItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BookmarkItem _item;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final File _tmpAbsolutePath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAbsolutePath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAbsolutePath);
            }
            _tmpAbsolutePath = __folderConverter.toFolder(_tmp);
            final String _tmpParentName;
            if (_cursor.isNull(_cursorIndexOfParentName)) {
              _tmpParentName = null;
            } else {
              _tmpParentName = _cursor.getString(_cursorIndexOfParentName);
            }
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            _item = new BookmarkItem(_tmpId,_tmpAbsolutePath,_tmpParentName,_tmpDateAdded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object selectFrom(final String groupName,
      final Continuation<? super List<BookmarkItem>> $completion) {
    final String _sql = "SELECT * FROM bookmark_item WHERE parentName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (groupName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, groupName);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BookmarkItem>>() {
      @Override
      @NonNull
      public List<BookmarkItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAbsolutePath = CursorUtil.getColumnIndexOrThrow(_cursor, "absolutePath");
          final int _cursorIndexOfParentName = CursorUtil.getColumnIndexOrThrow(_cursor, "parentName");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final List<BookmarkItem> _result = new ArrayList<BookmarkItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BookmarkItem _item;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final File _tmpAbsolutePath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAbsolutePath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAbsolutePath);
            }
            _tmpAbsolutePath = __folderConverter.toFolder(_tmp);
            final String _tmpParentName;
            if (_cursor.isNull(_cursorIndexOfParentName)) {
              _tmpParentName = null;
            } else {
              _tmpParentName = _cursor.getString(_cursorIndexOfParentName);
            }
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            _item = new BookmarkItem(_tmpId,_tmpAbsolutePath,_tmpParentName,_tmpDateAdded);
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
  public Object exists(final File folderPath, final String groupName,
      final Continuation<? super Boolean> $completion) {
    final String _sql = "\n"
            + "        SELECT EXISTS(SELECT * FROM bookmark_item \n"
            + "                      WHERE parentName = ? AND absolutePath = ?)\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (groupName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, groupName);
    }
    _argIndex = 2;
    final String _tmp = __folderConverter.toString(folderPath);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
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
            final Integer _tmp_1;
            if (_cursor.isNull(0)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getInt(0);
            }
            _result = _tmp_1 == null ? null : _tmp_1 != 0;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
