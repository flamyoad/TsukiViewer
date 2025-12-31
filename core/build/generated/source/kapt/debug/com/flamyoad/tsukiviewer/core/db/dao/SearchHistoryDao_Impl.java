package com.flamyoad.tsukiviewer.core.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.DataSource;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.paging.LimitOffsetDataSource;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.flamyoad.tsukiviewer.core.model.SearchHistory;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class SearchHistoryDao_Impl implements SearchHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SearchHistory> __insertionAdapterOfSearchHistory;

  private final EntityDeletionOrUpdateAdapter<SearchHistory> __deletionAdapterOfSearchHistory;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public SearchHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSearchHistory = new EntityInsertionAdapter<SearchHistory>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `search_history` (`id`,`title`,`tags`,`mustIncludeAllTags`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SearchHistory entity) {
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
        if (entity.getTags() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTags());
        }
        final int _tmp = entity.getMustIncludeAllTags() ? 1 : 0;
        statement.bindLong(4, _tmp);
      }
    };
    this.__deletionAdapterOfSearchHistory = new EntityDeletionOrUpdateAdapter<SearchHistory>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `search_history` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SearchHistory entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM search_history";
        return _query;
      }
    };
  }

  @Override
  public void insert(final SearchHistory searchHistory) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfSearchHistory.insert(searchHistory);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int delete(final SearchHistory searchHistory) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total += __deletionAdapterOfSearchHistory.handle(searchHistory);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public Object getLatestItem(final Continuation<? super SearchHistory> $completion) {
    final String _sql = "SELECT * FROM search_history ORDER BY id DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SearchHistory>() {
      @Override
      @Nullable
      public SearchHistory call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfMustIncludeAllTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustIncludeAllTags");
          final SearchHistory _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getInt(_cursorIndexOfId);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            final boolean _tmpMustIncludeAllTags;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfMustIncludeAllTags);
            _tmpMustIncludeAllTags = _tmp != 0;
            _result = new SearchHistory(_tmpId,_tmpTitle,_tmpTags,_tmpMustIncludeAllTags);
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
  public DataSource.Factory<Integer, SearchHistory> getAll() {
    final String _sql = "SELECT * FROM search_history ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new DataSource.Factory<Integer, SearchHistory>() {
      @Override
      @NonNull
      public LimitOffsetDataSource<SearchHistory> create() {
        return new LimitOffsetDataSource<SearchHistory>(__db, _statement, false, true, "search_history") {
          @Override
          @NonNull
          protected List<SearchHistory> convertRows(@NonNull final Cursor cursor) {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(cursor, "title");
            final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(cursor, "tags");
            final int _cursorIndexOfMustIncludeAllTags = CursorUtil.getColumnIndexOrThrow(cursor, "mustIncludeAllTags");
            final List<SearchHistory> _res = new ArrayList<SearchHistory>(cursor.getCount());
            while (cursor.moveToNext()) {
              final SearchHistory _item;
              final Integer _tmpId;
              if (cursor.isNull(_cursorIndexOfId)) {
                _tmpId = null;
              } else {
                _tmpId = cursor.getInt(_cursorIndexOfId);
              }
              final String _tmpTitle;
              if (cursor.isNull(_cursorIndexOfTitle)) {
                _tmpTitle = null;
              } else {
                _tmpTitle = cursor.getString(_cursorIndexOfTitle);
              }
              final String _tmpTags;
              if (cursor.isNull(_cursorIndexOfTags)) {
                _tmpTags = null;
              } else {
                _tmpTags = cursor.getString(_cursorIndexOfTags);
              }
              final boolean _tmpMustIncludeAllTags;
              final int _tmp;
              _tmp = cursor.getInt(_cursorIndexOfMustIncludeAllTags);
              _tmpMustIncludeAllTags = _tmp != 0;
              _item = new SearchHistory(_tmpId,_tmpTitle,_tmpTags,_tmpMustIncludeAllTags);
              _res.add(_item);
            }
            return _res;
          }
        };
      }
    };
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
