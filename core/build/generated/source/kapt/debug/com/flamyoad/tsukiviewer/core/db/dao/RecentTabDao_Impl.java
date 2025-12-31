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
import com.flamyoad.tsukiviewer.core.model.RecentTab;
import java.io.File;
import java.lang.Class;
import java.lang.Exception;
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
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RecentTabDao_Impl implements RecentTabDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RecentTab> __insertionAdapterOfRecentTab;

  private final FolderConverter __folderConverter = new FolderConverter();

  private final EntityDeletionOrUpdateAdapter<RecentTab> __deletionAdapterOfRecentTab;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllExcept;

  public RecentTabDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRecentTab = new EntityInsertionAdapter<RecentTab>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `recent_tabs` (`id`,`title`,`dirPath`,`thumbnail`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecentTab entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        final String _tmp = __folderConverter.toString(entity.getDirPath());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        final String _tmp_1 = __folderConverter.toString(entity.getThumbnail());
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp_1);
        }
      }
    };
    this.__deletionAdapterOfRecentTab = new EntityDeletionOrUpdateAdapter<RecentTab>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `recent_tabs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecentTab entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__preparedStmtOfDeleteAllExcept = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM recent_tabs WHERE id != ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final RecentTab tab) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfRecentTab.insertAndReturnId(tab);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final RecentTab tab) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfRecentTab.handle(tab);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int deleteAllExcept(final long tabId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllExcept.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, tabId);
    try {
      __db.beginTransaction();
      try {
        final int _result = _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
        return _result;
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAllExcept.release(_stmt);
    }
  }

  @Override
  public LiveData<List<RecentTab>> getAll() {
    final String _sql = "SELECT * FROM recent_tabs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"recent_tabs"}, false, new Callable<List<RecentTab>>() {
      @Override
      @Nullable
      public List<RecentTab> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDirPath = CursorUtil.getColumnIndexOrThrow(_cursor, "dirPath");
          final int _cursorIndexOfThumbnail = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnail");
          final List<RecentTab> _result = new ArrayList<RecentTab>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RecentTab _item;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final File _tmpDirPath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfDirPath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfDirPath);
            }
            _tmpDirPath = __folderConverter.toFolder(_tmp);
            final File _tmpThumbnail;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfThumbnail)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfThumbnail);
            }
            _tmpThumbnail = __folderConverter.toFolder(_tmp_1);
            _item = new RecentTab(_tmpId,_tmpTitle,_tmpDirPath,_tmpThumbnail);
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
  public Object get(final long id, final Continuation<? super RecentTab> $completion) {
    final String _sql = "SELECT * FROM recent_tabs WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RecentTab>() {
      @Override
      @NonNull
      public RecentTab call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDirPath = CursorUtil.getColumnIndexOrThrow(_cursor, "dirPath");
          final int _cursorIndexOfThumbnail = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnail");
          final RecentTab _result;
          if (_cursor.moveToFirst()) {
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final File _tmpDirPath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfDirPath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfDirPath);
            }
            _tmpDirPath = __folderConverter.toFolder(_tmp);
            final File _tmpThumbnail;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfThumbnail)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfThumbnail);
            }
            _tmpThumbnail = __folderConverter.toFolder(_tmp_1);
            _result = new RecentTab(_tmpId,_tmpTitle,_tmpDirPath,_tmpThumbnail);
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
  public Object getByPath(final String path, final Continuation<? super RecentTab> $completion) {
    final String _sql = "SELECT * FROM recent_tabs WHERE dirPath = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (path == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, path);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RecentTab>() {
      @Override
      @Nullable
      public RecentTab call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDirPath = CursorUtil.getColumnIndexOrThrow(_cursor, "dirPath");
          final int _cursorIndexOfThumbnail = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnail");
          final RecentTab _result;
          if (_cursor.moveToFirst()) {
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final File _tmpDirPath;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfDirPath)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfDirPath);
            }
            _tmpDirPath = __folderConverter.toFolder(_tmp);
            final File _tmpThumbnail;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfThumbnail)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfThumbnail);
            }
            _tmpThumbnail = __folderConverter.toFolder(_tmp_1);
            _result = new RecentTab(_tmpId,_tmpTitle,_tmpDirPath,_tmpThumbnail);
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
  public boolean existsByPath(final String path) {
    final String _sql = "SELECT EXISTS (SELECT * FROM recent_tabs WHERE dirPath = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (path == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, path);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final boolean _result;
      if (_cursor.moveToFirst()) {
        final int _tmp;
        _tmp = _cursor.getInt(0);
        _result = _tmp != 0;
      } else {
        _result = false;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
