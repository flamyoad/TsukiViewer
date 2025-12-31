package com.flamyoad.tsukiviewer.core.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter;
import com.flamyoad.tsukiviewer.core.model.IncludedFolder;
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
public final class IncludedFolderDao_Impl implements IncludedFolderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IncludedFolder> __insertionAdapterOfIncludedFolder;

  private final FolderConverter __folderConverter = new FolderConverter();

  public IncludedFolderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIncludedFolder = new EntityInsertionAdapter<IncludedFolder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `included_folders` (`dir`,`parentDir`,`lastName`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncludedFolder entity) {
        final String _tmp = __folderConverter.toString(entity.getDir());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        final String _tmp_1 = __folderConverter.toString(entity.getParentDir());
        if (_tmp_1 == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp_1);
        }
        if (entity.getLastName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getLastName());
        }
      }
    };
  }

  @Override
  public Object insert(final IncludedFolder folder, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIncludedFolder.insert(folder);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insert(final List<IncludedFolder> list,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIncludedFolder.insert(list);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<List<IncludedFolder>> getAll() {
    final String _sql = "SELECT * FROM included_folders";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"included_folders"}, false, new Callable<List<IncludedFolder>>() {
      @Override
      @Nullable
      public List<IncludedFolder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDir = CursorUtil.getColumnIndexOrThrow(_cursor, "dir");
          final int _cursorIndexOfParentDir = CursorUtil.getColumnIndexOrThrow(_cursor, "parentDir");
          final int _cursorIndexOfLastName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastName");
          final List<IncludedFolder> _result = new ArrayList<IncludedFolder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncludedFolder _item;
            final File _tmpDir;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfDir)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfDir);
            }
            _tmpDir = __folderConverter.toFolder(_tmp);
            final File _tmpParentDir;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfParentDir)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfParentDir);
            }
            _tmpParentDir = __folderConverter.toFolder(_tmp_1);
            final String _tmpLastName;
            if (_cursor.isNull(_cursorIndexOfLastName)) {
              _tmpLastName = null;
            } else {
              _tmpLastName = _cursor.getString(_cursorIndexOfLastName);
            }
            _item = new IncludedFolder(_tmpDir,_tmpParentDir,_tmpLastName);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
