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
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter;
import com.flamyoad.tsukiviewer.core.model.IncludedPath;
import java.io.File;
import java.lang.Class;
import java.lang.Exception;
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
public final class IncludedPathDao_Impl implements IncludedPathDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IncludedPath> __insertionAdapterOfIncludedPath;

  private final FolderConverter __folderConverter = new FolderConverter();

  private final EntityDeletionOrUpdateAdapter<IncludedPath> __deletionAdapterOfIncludedPath;

  public IncludedPathDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIncludedPath = new EntityInsertionAdapter<IncludedPath>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `included_path` (`dir`) VALUES (?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncludedPath entity) {
        final String _tmp = __folderConverter.toString(entity.getDir());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
      }
    };
    this.__deletionAdapterOfIncludedPath = new EntityDeletionOrUpdateAdapter<IncludedPath>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `included_path` WHERE `dir` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncludedPath entity) {
        final String _tmp = __folderConverter.toString(entity.getDir());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
      }
    };
  }

  @Override
  public Object insert(final IncludedPath path, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIncludedPath.insert(path);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final IncludedPath path, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfIncludedPath.handle(path);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<List<IncludedPath>> getAll() {
    final String _sql = "SELECT dir FROM included_path";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"included_path"}, false, new Callable<List<IncludedPath>>() {
      @Override
      @Nullable
      public List<IncludedPath> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDir = 0;
          final List<IncludedPath> _result = new ArrayList<IncludedPath>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncludedPath _item;
            final File _tmpDir;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfDir)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfDir);
            }
            _tmpDir = __folderConverter.toFolder(_tmp);
            _item = new IncludedPath(_tmpDir);
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
  public Object getAllBlocking(final Continuation<? super List<? extends File>> $completion) {
    final String _sql = "SELECT dir FROM included_path";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<? extends File>>() {
      @Override
      @NonNull
      public List<? extends File> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<File> _result = new ArrayList<File>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final File _item;
            final String _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(0);
            }
            _item = __folderConverter.toFolder(_tmp);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
