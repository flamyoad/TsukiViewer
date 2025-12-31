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
import com.flamyoad.tsukiviewer.core.model.Collection;
import com.flamyoad.tsukiviewer.core.model.CollectionCriteria;
import com.flamyoad.tsukiviewer.core.model.CollectionWithCriterias;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CollectionDao_Impl implements CollectionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Collection> __insertionAdapterOfCollection;

  private final FolderConverter __folderConverter = new FolderConverter();

  private final EntityDeletionOrUpdateAdapter<Collection> __deletionAdapterOfCollection;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  private final SharedSQLiteStatement __preparedStmtOfUpdateThumbnail;

  private final SharedSQLiteStatement __preparedStmtOfDeleteThumbnail;

  public CollectionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCollection = new EntityInsertionAdapter<Collection>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `collection` (`id`,`name`,`coverPhoto`,`mustHaveAllTitles`,`mustHaveAllIncludedTags`,`mustHaveAllExcludedTags`,`minNumPages`,`maxNumPages`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Collection entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        final String _tmp = __folderConverter.toString(entity.getCoverPhoto());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        final int _tmp_1 = entity.getMustHaveAllTitles() ? 1 : 0;
        statement.bindLong(4, _tmp_1);
        final int _tmp_2 = entity.getMustHaveAllIncludedTags() ? 1 : 0;
        statement.bindLong(5, _tmp_2);
        final int _tmp_3 = entity.getMustHaveAllExcludedTags() ? 1 : 0;
        statement.bindLong(6, _tmp_3);
        statement.bindLong(7, entity.getMinNumPages());
        statement.bindLong(8, entity.getMaxNumPages());
      }
    };
    this.__deletionAdapterOfCollection = new EntityDeletionOrUpdateAdapter<Collection>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `collection` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Collection entity) {
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
        final String _query = "DELETE FROM collection WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateThumbnail = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE collection \n"
                + "        SET coverPhoto = ? \n"
                + "        WHERE id = ?\n"
                + "        ";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteThumbnail = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE collection SET coverPhoto = '' WHERE id = ? ";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Collection collection, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCollection.insertAndReturnId(collection);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Collection collection, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCollection.handle(collection);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateThumbnail(final long collectionId, final File thumbnail,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateThumbnail.acquire();
        int _argIndex = 1;
        final String _tmp = __folderConverter.toString(thumbnail);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, collectionId);
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
          __preparedStmtOfUpdateThumbnail.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteThumbnail(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteThumbnail.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeleteThumbnail.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<List<Collection>> getAll() {
    final String _sql = "SELECT * FROM collection";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"collection"}, false, new Callable<List<Collection>>() {
      @Override
      @Nullable
      public List<Collection> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCoverPhoto = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPhoto");
          final int _cursorIndexOfMustHaveAllTitles = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllTitles");
          final int _cursorIndexOfMustHaveAllIncludedTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllIncludedTags");
          final int _cursorIndexOfMustHaveAllExcludedTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllExcludedTags");
          final int _cursorIndexOfMinNumPages = CursorUtil.getColumnIndexOrThrow(_cursor, "minNumPages");
          final int _cursorIndexOfMaxNumPages = CursorUtil.getColumnIndexOrThrow(_cursor, "maxNumPages");
          final List<Collection> _result = new ArrayList<Collection>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Collection _item;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final File _tmpCoverPhoto;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfCoverPhoto)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCoverPhoto);
            }
            _tmpCoverPhoto = __folderConverter.toFolder(_tmp);
            final boolean _tmpMustHaveAllTitles;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfMustHaveAllTitles);
            _tmpMustHaveAllTitles = _tmp_1 != 0;
            final boolean _tmpMustHaveAllIncludedTags;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfMustHaveAllIncludedTags);
            _tmpMustHaveAllIncludedTags = _tmp_2 != 0;
            final boolean _tmpMustHaveAllExcludedTags;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfMustHaveAllExcludedTags);
            _tmpMustHaveAllExcludedTags = _tmp_3 != 0;
            final int _tmpMinNumPages;
            _tmpMinNumPages = _cursor.getInt(_cursorIndexOfMinNumPages);
            final int _tmpMaxNumPages;
            _tmpMaxNumPages = _cursor.getInt(_cursorIndexOfMaxNumPages);
            _item = new Collection(_tmpId,_tmpName,_tmpCoverPhoto,_tmpMustHaveAllTitles,_tmpMustHaveAllIncludedTags,_tmpMustHaveAllExcludedTags,_tmpMinNumPages,_tmpMaxNumPages);
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
  public Object getAllBlocking(final Continuation<? super List<Collection>> $completion) {
    final String _sql = "SELECT * FROM collection";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Collection>>() {
      @Override
      @NonNull
      public List<Collection> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCoverPhoto = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPhoto");
          final int _cursorIndexOfMustHaveAllTitles = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllTitles");
          final int _cursorIndexOfMustHaveAllIncludedTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllIncludedTags");
          final int _cursorIndexOfMustHaveAllExcludedTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllExcludedTags");
          final int _cursorIndexOfMinNumPages = CursorUtil.getColumnIndexOrThrow(_cursor, "minNumPages");
          final int _cursorIndexOfMaxNumPages = CursorUtil.getColumnIndexOrThrow(_cursor, "maxNumPages");
          final List<Collection> _result = new ArrayList<Collection>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Collection _item;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final File _tmpCoverPhoto;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfCoverPhoto)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCoverPhoto);
            }
            _tmpCoverPhoto = __folderConverter.toFolder(_tmp);
            final boolean _tmpMustHaveAllTitles;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfMustHaveAllTitles);
            _tmpMustHaveAllTitles = _tmp_1 != 0;
            final boolean _tmpMustHaveAllIncludedTags;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfMustHaveAllIncludedTags);
            _tmpMustHaveAllIncludedTags = _tmp_2 != 0;
            final boolean _tmpMustHaveAllExcludedTags;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfMustHaveAllExcludedTags);
            _tmpMustHaveAllExcludedTags = _tmp_3 != 0;
            final int _tmpMinNumPages;
            _tmpMinNumPages = _cursor.getInt(_cursorIndexOfMinNumPages);
            final int _tmpMaxNumPages;
            _tmpMaxNumPages = _cursor.getInt(_cursorIndexOfMaxNumPages);
            _item = new Collection(_tmpId,_tmpName,_tmpCoverPhoto,_tmpMustHaveAllTitles,_tmpMustHaveAllIncludedTags,_tmpMustHaveAllExcludedTags,_tmpMinNumPages,_tmpMaxNumPages);
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
  public LiveData<List<CollectionWithCriterias>> getAllWithCriterias() {
    final String _sql = "SELECT * FROM collection";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"collection_criteria",
        "collection"}, false, new Callable<List<CollectionWithCriterias>>() {
      @Override
      @Nullable
      public List<CollectionWithCriterias> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCoverPhoto = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPhoto");
          final int _cursorIndexOfMustHaveAllTitles = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllTitles");
          final int _cursorIndexOfMustHaveAllIncludedTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllIncludedTags");
          final int _cursorIndexOfMustHaveAllExcludedTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllExcludedTags");
          final int _cursorIndexOfMinNumPages = CursorUtil.getColumnIndexOrThrow(_cursor, "minNumPages");
          final int _cursorIndexOfMaxNumPages = CursorUtil.getColumnIndexOrThrow(_cursor, "maxNumPages");
          final LongSparseArray<ArrayList<CollectionCriteria>> _collectionCriteriaList = new LongSparseArray<ArrayList<CollectionCriteria>>();
          while (_cursor.moveToNext()) {
            final Long _tmpKey;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpKey = null;
            } else {
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
            }
            if (_tmpKey != null) {
              if (!_collectionCriteriaList.containsKey(_tmpKey)) {
                _collectionCriteriaList.put(_tmpKey, new ArrayList<CollectionCriteria>());
              }
            }
          }
          _cursor.moveToPosition(-1);
          __fetchRelationshipcollectionCriteriaAscomFlamyoadTsukiviewerCoreModelCollectionCriteria(_collectionCriteriaList);
          final List<CollectionWithCriterias> _result = new ArrayList<CollectionWithCriterias>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CollectionWithCriterias _item;
            final Collection _tmpCollection;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final File _tmpCoverPhoto;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfCoverPhoto)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCoverPhoto);
            }
            _tmpCoverPhoto = __folderConverter.toFolder(_tmp);
            final boolean _tmpMustHaveAllTitles;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfMustHaveAllTitles);
            _tmpMustHaveAllTitles = _tmp_1 != 0;
            final boolean _tmpMustHaveAllIncludedTags;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfMustHaveAllIncludedTags);
            _tmpMustHaveAllIncludedTags = _tmp_2 != 0;
            final boolean _tmpMustHaveAllExcludedTags;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfMustHaveAllExcludedTags);
            _tmpMustHaveAllExcludedTags = _tmp_3 != 0;
            final int _tmpMinNumPages;
            _tmpMinNumPages = _cursor.getInt(_cursorIndexOfMinNumPages);
            final int _tmpMaxNumPages;
            _tmpMaxNumPages = _cursor.getInt(_cursorIndexOfMaxNumPages);
            _tmpCollection = new Collection(_tmpId,_tmpName,_tmpCoverPhoto,_tmpMustHaveAllTitles,_tmpMustHaveAllIncludedTags,_tmpMustHaveAllExcludedTags,_tmpMinNumPages,_tmpMaxNumPages);
            final ArrayList<CollectionCriteria> _tmpCriteriaListCollection;
            final Long _tmpKey_1;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpKey_1 = null;
            } else {
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
            }
            if (_tmpKey_1 != null) {
              _tmpCriteriaListCollection = _collectionCriteriaList.get(_tmpKey_1);
            } else {
              _tmpCriteriaListCollection = new ArrayList<CollectionCriteria>();
            }
            _item = new CollectionWithCriterias(_tmpCollection,_tmpCriteriaListCollection);
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
  public LiveData<List<CollectionWithCriterias>> getAllWithCriterias(final String keyword) {
    final String _sql = "\n"
            + "        SELECT * FROM collection \n"
            + "        WHERE name LIKE '%' || ? || '%'\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (keyword == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, keyword);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"collection_criteria",
        "collection"}, false, new Callable<List<CollectionWithCriterias>>() {
      @Override
      @Nullable
      public List<CollectionWithCriterias> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCoverPhoto = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPhoto");
          final int _cursorIndexOfMustHaveAllTitles = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllTitles");
          final int _cursorIndexOfMustHaveAllIncludedTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllIncludedTags");
          final int _cursorIndexOfMustHaveAllExcludedTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllExcludedTags");
          final int _cursorIndexOfMinNumPages = CursorUtil.getColumnIndexOrThrow(_cursor, "minNumPages");
          final int _cursorIndexOfMaxNumPages = CursorUtil.getColumnIndexOrThrow(_cursor, "maxNumPages");
          final LongSparseArray<ArrayList<CollectionCriteria>> _collectionCriteriaList = new LongSparseArray<ArrayList<CollectionCriteria>>();
          while (_cursor.moveToNext()) {
            final Long _tmpKey;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpKey = null;
            } else {
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
            }
            if (_tmpKey != null) {
              if (!_collectionCriteriaList.containsKey(_tmpKey)) {
                _collectionCriteriaList.put(_tmpKey, new ArrayList<CollectionCriteria>());
              }
            }
          }
          _cursor.moveToPosition(-1);
          __fetchRelationshipcollectionCriteriaAscomFlamyoadTsukiviewerCoreModelCollectionCriteria(_collectionCriteriaList);
          final List<CollectionWithCriterias> _result = new ArrayList<CollectionWithCriterias>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CollectionWithCriterias _item;
            final Collection _tmpCollection;
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final File _tmpCoverPhoto;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfCoverPhoto)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCoverPhoto);
            }
            _tmpCoverPhoto = __folderConverter.toFolder(_tmp);
            final boolean _tmpMustHaveAllTitles;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfMustHaveAllTitles);
            _tmpMustHaveAllTitles = _tmp_1 != 0;
            final boolean _tmpMustHaveAllIncludedTags;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfMustHaveAllIncludedTags);
            _tmpMustHaveAllIncludedTags = _tmp_2 != 0;
            final boolean _tmpMustHaveAllExcludedTags;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfMustHaveAllExcludedTags);
            _tmpMustHaveAllExcludedTags = _tmp_3 != 0;
            final int _tmpMinNumPages;
            _tmpMinNumPages = _cursor.getInt(_cursorIndexOfMinNumPages);
            final int _tmpMaxNumPages;
            _tmpMaxNumPages = _cursor.getInt(_cursorIndexOfMaxNumPages);
            _tmpCollection = new Collection(_tmpId,_tmpName,_tmpCoverPhoto,_tmpMustHaveAllTitles,_tmpMustHaveAllIncludedTags,_tmpMustHaveAllExcludedTags,_tmpMinNumPages,_tmpMaxNumPages);
            final ArrayList<CollectionCriteria> _tmpCriteriaListCollection;
            final Long _tmpKey_1;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpKey_1 = null;
            } else {
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
            }
            if (_tmpKey_1 != null) {
              _tmpCriteriaListCollection = _collectionCriteriaList.get(_tmpKey_1);
            } else {
              _tmpCriteriaListCollection = new ArrayList<CollectionCriteria>();
            }
            _item = new CollectionWithCriterias(_tmpCollection,_tmpCriteriaListCollection);
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
  public LiveData<Collection> get(final long collectionId) {
    final String _sql = "SELECT * FROM collection WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, collectionId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"collection"}, false, new Callable<Collection>() {
      @Override
      @Nullable
      public Collection call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCoverPhoto = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPhoto");
          final int _cursorIndexOfMustHaveAllTitles = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllTitles");
          final int _cursorIndexOfMustHaveAllIncludedTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllIncludedTags");
          final int _cursorIndexOfMustHaveAllExcludedTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllExcludedTags");
          final int _cursorIndexOfMinNumPages = CursorUtil.getColumnIndexOrThrow(_cursor, "minNumPages");
          final int _cursorIndexOfMaxNumPages = CursorUtil.getColumnIndexOrThrow(_cursor, "maxNumPages");
          final Collection _result;
          if (_cursor.moveToFirst()) {
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final File _tmpCoverPhoto;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfCoverPhoto)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCoverPhoto);
            }
            _tmpCoverPhoto = __folderConverter.toFolder(_tmp);
            final boolean _tmpMustHaveAllTitles;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfMustHaveAllTitles);
            _tmpMustHaveAllTitles = _tmp_1 != 0;
            final boolean _tmpMustHaveAllIncludedTags;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfMustHaveAllIncludedTags);
            _tmpMustHaveAllIncludedTags = _tmp_2 != 0;
            final boolean _tmpMustHaveAllExcludedTags;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfMustHaveAllExcludedTags);
            _tmpMustHaveAllExcludedTags = _tmp_3 != 0;
            final int _tmpMinNumPages;
            _tmpMinNumPages = _cursor.getInt(_cursorIndexOfMinNumPages);
            final int _tmpMaxNumPages;
            _tmpMaxNumPages = _cursor.getInt(_cursorIndexOfMaxNumPages);
            _result = new Collection(_tmpId,_tmpName,_tmpCoverPhoto,_tmpMustHaveAllTitles,_tmpMustHaveAllIncludedTags,_tmpMustHaveAllExcludedTags,_tmpMinNumPages,_tmpMaxNumPages);
          } else {
            _result = null;
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
  public Object getBlocking(final long collectionId,
      final Continuation<? super Collection> $completion) {
    final String _sql = "SELECT * FROM collection WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, collectionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Collection>() {
      @Override
      @NonNull
      public Collection call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCoverPhoto = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPhoto");
          final int _cursorIndexOfMustHaveAllTitles = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllTitles");
          final int _cursorIndexOfMustHaveAllIncludedTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllIncludedTags");
          final int _cursorIndexOfMustHaveAllExcludedTags = CursorUtil.getColumnIndexOrThrow(_cursor, "mustHaveAllExcludedTags");
          final int _cursorIndexOfMinNumPages = CursorUtil.getColumnIndexOrThrow(_cursor, "minNumPages");
          final int _cursorIndexOfMaxNumPages = CursorUtil.getColumnIndexOrThrow(_cursor, "maxNumPages");
          final Collection _result;
          if (_cursor.moveToFirst()) {
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final File _tmpCoverPhoto;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfCoverPhoto)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCoverPhoto);
            }
            _tmpCoverPhoto = __folderConverter.toFolder(_tmp);
            final boolean _tmpMustHaveAllTitles;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfMustHaveAllTitles);
            _tmpMustHaveAllTitles = _tmp_1 != 0;
            final boolean _tmpMustHaveAllIncludedTags;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfMustHaveAllIncludedTags);
            _tmpMustHaveAllIncludedTags = _tmp_2 != 0;
            final boolean _tmpMustHaveAllExcludedTags;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfMustHaveAllExcludedTags);
            _tmpMustHaveAllExcludedTags = _tmp_3 != 0;
            final int _tmpMinNumPages;
            _tmpMinNumPages = _cursor.getInt(_cursorIndexOfMinNumPages);
            final int _tmpMaxNumPages;
            _tmpMaxNumPages = _cursor.getInt(_cursorIndexOfMaxNumPages);
            _result = new Collection(_tmpId,_tmpName,_tmpCoverPhoto,_tmpMustHaveAllTitles,_tmpMustHaveAllIncludedTags,_tmpMustHaveAllExcludedTags,_tmpMinNumPages,_tmpMaxNumPages);
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

  private void __fetchRelationshipcollectionCriteriaAscomFlamyoadTsukiviewerCoreModelCollectionCriteria(
      @NonNull final LongSparseArray<ArrayList<CollectionCriteria>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipcollectionCriteriaAscomFlamyoadTsukiviewerCoreModelCollectionCriteria(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`collectionId`,`type`,`value`,`valueName` FROM `collection_criteria` WHERE `collectionId` IN (");
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
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "collectionId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfCollectionId = 1;
      final int _cursorIndexOfType = 2;
      final int _cursorIndexOfValue = 3;
      final int _cursorIndexOfValueName = 4;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<CollectionCriteria> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final CollectionCriteria _item_1;
          final Long _tmpId;
          if (_cursor.isNull(_cursorIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _cursor.getLong(_cursorIndexOfId);
          }
          final long _tmpCollectionId;
          _tmpCollectionId = _cursor.getLong(_cursorIndexOfCollectionId);
          final String _tmpType;
          if (_cursor.isNull(_cursorIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _cursor.getString(_cursorIndexOfType);
          }
          final String _tmpValue;
          if (_cursor.isNull(_cursorIndexOfValue)) {
            _tmpValue = null;
          } else {
            _tmpValue = _cursor.getString(_cursorIndexOfValue);
          }
          final String _tmpValueName;
          if (_cursor.isNull(_cursorIndexOfValueName)) {
            _tmpValueName = null;
          } else {
            _tmpValueName = _cursor.getString(_cursorIndexOfValueName);
          }
          _item_1 = new CollectionCriteria(_tmpId,_tmpCollectionId,_tmpType,_tmpValue,_tmpValueName);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
