package de.fhg.fokus.odp.entities.service.persistence;

import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnmodifiableList;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.CacheModel;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;

import de.fhg.fokus.odp.entities.NoSuchMetadataCommentException;
import de.fhg.fokus.odp.entities.model.MetadataComment;
import de.fhg.fokus.odp.entities.model.impl.MetadataCommentImpl;
import de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl;
import de.fhg.fokus.odp.entities.service.persistence.MetadataCommentPersistence;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The persistence implementation for the metadata comment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MetadataCommentPersistence
 * @see MetadataCommentUtil
 * @generated
 */
public class MetadataCommentPersistenceImpl extends BasePersistenceImpl<MetadataComment>
    implements MetadataCommentPersistence {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never modify or reference this class directly. Always use {@link MetadataCommentUtil} to access the metadata comment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
     */
    public static final String FINDER_CLASS_NAME_ENTITY = MetadataCommentImpl.class.getName();
    public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
        ".List1";
    public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
        ".List2";
    public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentModelImpl.FINDER_CACHE_ENABLED,
            MetadataCommentImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
            "findAll", new String[0]);
    public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentModelImpl.FINDER_CACHE_ENABLED,
            MetadataCommentImpl.class,
            FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
    public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentModelImpl.FINDER_CACHE_ENABLED, Long.class,
            FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);
    public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_BY_UUID = new FinderPath(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentModelImpl.FINDER_CACHE_ENABLED,
            MetadataCommentImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
            "findByUuid",
            new String[] {
                String.class.getName(),
                
            Integer.class.getName(), Integer.class.getName(),
                OrderByComparator.class.getName()
            });
    public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID = new FinderPath(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentModelImpl.FINDER_CACHE_ENABLED,
            MetadataCommentImpl.class,
            FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
            new String[] { String.class.getName() },
            MetadataCommentModelImpl.UUID_COLUMN_BITMASK |
            MetadataCommentModelImpl.CREATED_COLUMN_BITMASK);
    public static final FinderPath FINDER_PATH_COUNT_BY_UUID = new FinderPath(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentModelImpl.FINDER_CACHE_ENABLED, Long.class,
            FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
            new String[] { String.class.getName() });
    private static final String _FINDER_COLUMN_UUID_UUID_1 = "metadataComment.uuid IS NULL";
    private static final String _FINDER_COLUMN_UUID_UUID_2 = "metadataComment.uuid = ?";
    private static final String _FINDER_COLUMN_UUID_UUID_3 = "(metadataComment.uuid IS NULL OR metadataComment.uuid = '')";
    public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_BY_USERLIFERAYID =
        new FinderPath(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentModelImpl.FINDER_CACHE_ENABLED,
            MetadataCommentImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
            "findByuserLiferayId",
            new String[] {
                Long.class.getName(),
                
            Integer.class.getName(), Integer.class.getName(),
                OrderByComparator.class.getName()
            });
    public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_USERLIFERAYID =
        new FinderPath(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentModelImpl.FINDER_CACHE_ENABLED,
            MetadataCommentImpl.class,
            FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByuserLiferayId",
            new String[] { Long.class.getName() },
            MetadataCommentModelImpl.USERLIFERAYID_COLUMN_BITMASK |
            MetadataCommentModelImpl.CREATED_COLUMN_BITMASK);
    public static final FinderPath FINDER_PATH_COUNT_BY_USERLIFERAYID = new FinderPath(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentModelImpl.FINDER_CACHE_ENABLED, Long.class,
            FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByuserLiferayId",
            new String[] { Long.class.getName() });
    private static final String _FINDER_COLUMN_USERLIFERAYID_USERLIFERAYID_2 = "metadataComment.userLiferayId = ?";
    public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_BY_METADATANAME =
        new FinderPath(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentModelImpl.FINDER_CACHE_ENABLED,
            MetadataCommentImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
            "findBymetadataName",
            new String[] {
                String.class.getName(),
                
            Integer.class.getName(), Integer.class.getName(),
                OrderByComparator.class.getName()
            });
    public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_METADATANAME =
        new FinderPath(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentModelImpl.FINDER_CACHE_ENABLED,
            MetadataCommentImpl.class,
            FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findBymetadataName",
            new String[] { String.class.getName() },
            MetadataCommentModelImpl.METADATANAME_COLUMN_BITMASK |
            MetadataCommentModelImpl.CREATED_COLUMN_BITMASK);
    public static final FinderPath FINDER_PATH_COUNT_BY_METADATANAME = new FinderPath(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentModelImpl.FINDER_CACHE_ENABLED, Long.class,
            FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countBymetadataName",
            new String[] { String.class.getName() });
    private static final String _FINDER_COLUMN_METADATANAME_METADATANAME_1 = "metadataComment.metadataName IS NULL";
    private static final String _FINDER_COLUMN_METADATANAME_METADATANAME_2 = "metadataComment.metadataName = ?";
    private static final String _FINDER_COLUMN_METADATANAME_METADATANAME_3 = "(metadataComment.metadataName IS NULL OR metadataComment.metadataName = '')";
    private static final String _SQL_SELECT_METADATACOMMENT = "SELECT metadataComment FROM MetadataComment metadataComment";
    private static final String _SQL_SELECT_METADATACOMMENT_WHERE = "SELECT metadataComment FROM MetadataComment metadataComment WHERE ";
    private static final String _SQL_COUNT_METADATACOMMENT = "SELECT COUNT(metadataComment) FROM MetadataComment metadataComment";
    private static final String _SQL_COUNT_METADATACOMMENT_WHERE = "SELECT COUNT(metadataComment) FROM MetadataComment metadataComment WHERE ";
    private static final String _ORDER_BY_ENTITY_ALIAS = "metadataComment.";
    private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No MetadataComment exists with the primary key ";
    private static final String _NO_SUCH_ENTITY_WITH_KEY = "No MetadataComment exists with the key {";
    private static final boolean _HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = GetterUtil.getBoolean(PropsUtil.get(
                PropsKeys.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
    private static Log _log = LogFactoryUtil.getLog(MetadataCommentPersistenceImpl.class);
    private static Set<String> _badColumnNames = SetUtil.fromArray(new String[] {
                "uuid", "text"
            });
    private static MetadataComment _nullMetadataComment = new MetadataCommentImpl() {
            @Override
            public Object clone() {
                return this;
            }

            @Override
            public CacheModel<MetadataComment> toCacheModel() {
                return _nullMetadataCommentCacheModel;
            }
        };

    private static CacheModel<MetadataComment> _nullMetadataCommentCacheModel = new CacheModel<MetadataComment>() {
            @Override
            public MetadataComment toEntityModel() {
                return _nullMetadataComment;
            }
        };

    public MetadataCommentPersistenceImpl() {
        setModelClass(MetadataComment.class);
    }

    /**
     * Returns all the metadata comments where uuid = &#63;.
     *
     * @param uuid the uuid
     * @return the matching metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public List<MetadataComment> findByUuid(String uuid)
        throws SystemException {
        return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
    }

    /**
     * Returns a range of all the metadata comments where uuid = &#63;.
     *
     * <p>
     * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
     * </p>
     *
     * @param uuid the uuid
     * @param start the lower bound of the range of metadata comments
     * @param end the upper bound of the range of metadata comments (not inclusive)
     * @return the range of matching metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public List<MetadataComment> findByUuid(String uuid, int start, int end)
        throws SystemException {
        return findByUuid(uuid, start, end, null);
    }

    /**
     * Returns an ordered range of all the metadata comments where uuid = &#63;.
     *
     * <p>
     * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
     * </p>
     *
     * @param uuid the uuid
     * @param start the lower bound of the range of metadata comments
     * @param end the upper bound of the range of metadata comments (not inclusive)
     * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
     * @return the ordered range of matching metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public List<MetadataComment> findByUuid(String uuid, int start, int end,
        OrderByComparator orderByComparator) throws SystemException {
        boolean pagination = true;
        FinderPath finderPath = null;
        Object[] finderArgs = null;

        if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
                (orderByComparator == null)) {
            pagination = false;
            finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID;
            finderArgs = new Object[] { uuid };
        } else {
            finderPath = FINDER_PATH_WITH_PAGINATION_FIND_BY_UUID;
            finderArgs = new Object[] { uuid, start, end, orderByComparator };
        }

        List<MetadataComment> list = (List<MetadataComment>) FinderCacheUtil.getResult(finderPath,
                finderArgs, this);

        if ((list != null) && !list.isEmpty()) {
            for (MetadataComment metadataComment : list) {
                if (!Validator.equals(uuid, metadataComment.getUuid())) {
                    list = null;

                    break;
                }
            }
        }

        if (list == null) {
            StringBundler query = null;

            if (orderByComparator != null) {
                query = new StringBundler(3 +
                        (orderByComparator.getOrderByFields().length * 3));
            } else {
                query = new StringBundler(3);
            }

            query.append(_SQL_SELECT_METADATACOMMENT_WHERE);

            boolean bindUuid = false;

            if (uuid == null) {
                query.append(_FINDER_COLUMN_UUID_UUID_1);
            } else if (uuid.equals(StringPool.BLANK)) {
                query.append(_FINDER_COLUMN_UUID_UUID_3);
            } else {
                bindUuid = true;

                query.append(_FINDER_COLUMN_UUID_UUID_2);
            }

            if (orderByComparator != null) {
                appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
                    orderByComparator);
            } else
             if (pagination) {
                query.append(MetadataCommentModelImpl.ORDER_BY_JPQL);
            }

            String sql = query.toString();

            Session session = null;

            try {
                session = openSession();

                Query q = session.createQuery(sql);

                QueryPos qPos = QueryPos.getInstance(q);

                if (bindUuid) {
                    qPos.add(uuid);
                }

                if (!pagination) {
                    list = (List<MetadataComment>) QueryUtil.list(q,
                            getDialect(), start, end, false);

                    Collections.sort(list);

                    list = new UnmodifiableList<MetadataComment>(list);
                } else {
                    list = (List<MetadataComment>) QueryUtil.list(q,
                            getDialect(), start, end);
                }

                cacheResult(list);

                FinderCacheUtil.putResult(finderPath, finderArgs, list);
            } catch (Exception e) {
                FinderCacheUtil.removeResult(finderPath, finderArgs);

                throw processException(e);
            } finally {
                closeSession(session);
            }
        }

        return list;
    }

    /**
     * Returns the first metadata comment in the ordered set where uuid = &#63;.
     *
     * @param uuid the uuid
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the first matching metadata comment
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a matching metadata comment could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment findByUuid_First(String uuid,
        OrderByComparator orderByComparator)
        throws NoSuchMetadataCommentException, SystemException {
        MetadataComment metadataComment = fetchByUuid_First(uuid,
                orderByComparator);

        if (metadataComment != null) {
            return metadataComment;
        }

        StringBundler msg = new StringBundler(4);

        msg.append(_NO_SUCH_ENTITY_WITH_KEY);

        msg.append("uuid=");
        msg.append(uuid);

        msg.append(StringPool.CLOSE_CURLY_BRACE);

        throw new NoSuchMetadataCommentException(msg.toString());
    }

    /**
     * Returns the first metadata comment in the ordered set where uuid = &#63;.
     *
     * @param uuid the uuid
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the first matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment fetchByUuid_First(String uuid,
        OrderByComparator orderByComparator) throws SystemException {
        List<MetadataComment> list = findByUuid(uuid, 0, 1, orderByComparator);

        if (!list.isEmpty()) {
            return list.get(0);
        }

        return null;
    }

    /**
     * Returns the last metadata comment in the ordered set where uuid = &#63;.
     *
     * @param uuid the uuid
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the last matching metadata comment
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a matching metadata comment could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment findByUuid_Last(String uuid,
        OrderByComparator orderByComparator)
        throws NoSuchMetadataCommentException, SystemException {
        MetadataComment metadataComment = fetchByUuid_Last(uuid,
                orderByComparator);

        if (metadataComment != null) {
            return metadataComment;
        }

        StringBundler msg = new StringBundler(4);

        msg.append(_NO_SUCH_ENTITY_WITH_KEY);

        msg.append("uuid=");
        msg.append(uuid);

        msg.append(StringPool.CLOSE_CURLY_BRACE);

        throw new NoSuchMetadataCommentException(msg.toString());
    }

    /**
     * Returns the last metadata comment in the ordered set where uuid = &#63;.
     *
     * @param uuid the uuid
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the last matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment fetchByUuid_Last(String uuid,
        OrderByComparator orderByComparator) throws SystemException {
        int count = countByUuid(uuid);

        if (count == 0) {
            return null;
        }

        List<MetadataComment> list = findByUuid(uuid, count - 1, count,
                orderByComparator);

        if (!list.isEmpty()) {
            return list.get(0);
        }

        return null;
    }

    /**
     * Returns the metadata comments before and after the current metadata comment in the ordered set where uuid = &#63;.
     *
     * @param _id the primary key of the current metadata comment
     * @param uuid the uuid
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the previous, current, and next metadata comment
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment[] findByUuid_PrevAndNext(long _id, String uuid,
        OrderByComparator orderByComparator)
        throws NoSuchMetadataCommentException, SystemException {
        MetadataComment metadataComment = findByPrimaryKey(_id);

        Session session = null;

        try {
            session = openSession();

            MetadataComment[] array = new MetadataCommentImpl[3];

            array[0] = getByUuid_PrevAndNext(session, metadataComment, uuid,
                    orderByComparator, true);

            array[1] = metadataComment;

            array[2] = getByUuid_PrevAndNext(session, metadataComment, uuid,
                    orderByComparator, false);

            return array;
        } catch (Exception e) {
            throw processException(e);
        } finally {
            closeSession(session);
        }
    }

    protected MetadataComment getByUuid_PrevAndNext(Session session,
        MetadataComment metadataComment, String uuid,
        OrderByComparator orderByComparator, boolean previous) {
        StringBundler query = null;

        if (orderByComparator != null) {
            query = new StringBundler(6 +
                    (orderByComparator.getOrderByFields().length * 6));
        } else {
            query = new StringBundler(3);
        }

        query.append(_SQL_SELECT_METADATACOMMENT_WHERE);

        boolean bindUuid = false;

        if (uuid == null) {
            query.append(_FINDER_COLUMN_UUID_UUID_1);
        } else if (uuid.equals(StringPool.BLANK)) {
            query.append(_FINDER_COLUMN_UUID_UUID_3);
        } else {
            bindUuid = true;

            query.append(_FINDER_COLUMN_UUID_UUID_2);
        }

        if (orderByComparator != null) {
            String[] orderByConditionFields = orderByComparator.getOrderByConditionFields();

            if (orderByConditionFields.length > 0) {
                query.append(WHERE_AND);
            }

            for (int i = 0; i < orderByConditionFields.length; i++) {
                query.append(_ORDER_BY_ENTITY_ALIAS);
                query.append(orderByConditionFields[i]);

                if ((i + 1) < orderByConditionFields.length) {
                    if (orderByComparator.isAscending() ^ previous) {
                        query.append(WHERE_GREATER_THAN_HAS_NEXT);
                    } else {
                        query.append(WHERE_LESSER_THAN_HAS_NEXT);
                    }
                } else {
                    if (orderByComparator.isAscending() ^ previous) {
                        query.append(WHERE_GREATER_THAN);
                    } else {
                        query.append(WHERE_LESSER_THAN);
                    }
                }
            }

            query.append(ORDER_BY_CLAUSE);

            String[] orderByFields = orderByComparator.getOrderByFields();

            for (int i = 0; i < orderByFields.length; i++) {
                query.append(_ORDER_BY_ENTITY_ALIAS);
                query.append(orderByFields[i]);

                if ((i + 1) < orderByFields.length) {
                    if (orderByComparator.isAscending() ^ previous) {
                        query.append(ORDER_BY_ASC_HAS_NEXT);
                    } else {
                        query.append(ORDER_BY_DESC_HAS_NEXT);
                    }
                } else {
                    if (orderByComparator.isAscending() ^ previous) {
                        query.append(ORDER_BY_ASC);
                    } else {
                        query.append(ORDER_BY_DESC);
                    }
                }
            }
        } else {
            query.append(MetadataCommentModelImpl.ORDER_BY_JPQL);
        }

        String sql = query.toString();

        Query q = session.createQuery(sql);

        q.setFirstResult(0);
        q.setMaxResults(2);

        QueryPos qPos = QueryPos.getInstance(q);

        if (bindUuid) {
            qPos.add(uuid);
        }

        if (orderByComparator != null) {
            Object[] values = orderByComparator.getOrderByConditionValues(metadataComment);

            for (Object value : values) {
                qPos.add(value);
            }
        }

        List<MetadataComment> list = q.list();

        if (list.size() == 2) {
            return list.get(1);
        } else {
            return null;
        }
    }

    /**
     * Removes all the metadata comments where uuid = &#63; from the database.
     *
     * @param uuid the uuid
     * @throws SystemException if a system exception occurred
     */
    @Override
    public void removeByUuid(String uuid) throws SystemException {
        for (MetadataComment metadataComment : findByUuid(uuid,
                QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {
            remove(metadataComment);
        }
    }

    /**
     * Returns the number of metadata comments where uuid = &#63;.
     *
     * @param uuid the uuid
     * @return the number of matching metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public int countByUuid(String uuid) throws SystemException {
        FinderPath finderPath = FINDER_PATH_COUNT_BY_UUID;

        Object[] finderArgs = new Object[] { uuid };

        Long count = (Long) FinderCacheUtil.getResult(finderPath, finderArgs,
                this);

        if (count == null) {
            StringBundler query = new StringBundler(2);

            query.append(_SQL_COUNT_METADATACOMMENT_WHERE);

            boolean bindUuid = false;

            if (uuid == null) {
                query.append(_FINDER_COLUMN_UUID_UUID_1);
            } else if (uuid.equals(StringPool.BLANK)) {
                query.append(_FINDER_COLUMN_UUID_UUID_3);
            } else {
                bindUuid = true;

                query.append(_FINDER_COLUMN_UUID_UUID_2);
            }

            String sql = query.toString();

            Session session = null;

            try {
                session = openSession();

                Query q = session.createQuery(sql);

                QueryPos qPos = QueryPos.getInstance(q);

                if (bindUuid) {
                    qPos.add(uuid);
                }

                count = (Long) q.uniqueResult();

                FinderCacheUtil.putResult(finderPath, finderArgs, count);
            } catch (Exception e) {
                FinderCacheUtil.removeResult(finderPath, finderArgs);

                throw processException(e);
            } finally {
                closeSession(session);
            }
        }

        return count.intValue();
    }

    /**
     * Returns all the metadata comments where userLiferayId = &#63;.
     *
     * @param userLiferayId the user liferay ID
     * @return the matching metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public List<MetadataComment> findByuserLiferayId(long userLiferayId)
        throws SystemException {
        return findByuserLiferayId(userLiferayId, QueryUtil.ALL_POS,
            QueryUtil.ALL_POS, null);
    }

    /**
     * Returns a range of all the metadata comments where userLiferayId = &#63;.
     *
     * <p>
     * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
     * </p>
     *
     * @param userLiferayId the user liferay ID
     * @param start the lower bound of the range of metadata comments
     * @param end the upper bound of the range of metadata comments (not inclusive)
     * @return the range of matching metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public List<MetadataComment> findByuserLiferayId(long userLiferayId,
        int start, int end) throws SystemException {
        return findByuserLiferayId(userLiferayId, start, end, null);
    }

    /**
     * Returns an ordered range of all the metadata comments where userLiferayId = &#63;.
     *
     * <p>
     * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
     * </p>
     *
     * @param userLiferayId the user liferay ID
     * @param start the lower bound of the range of metadata comments
     * @param end the upper bound of the range of metadata comments (not inclusive)
     * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
     * @return the ordered range of matching metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public List<MetadataComment> findByuserLiferayId(long userLiferayId,
        int start, int end, OrderByComparator orderByComparator)
        throws SystemException {
        boolean pagination = true;
        FinderPath finderPath = null;
        Object[] finderArgs = null;

        if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
                (orderByComparator == null)) {
            pagination = false;
            finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_USERLIFERAYID;
            finderArgs = new Object[] { userLiferayId };
        } else {
            finderPath = FINDER_PATH_WITH_PAGINATION_FIND_BY_USERLIFERAYID;
            finderArgs = new Object[] {
                    userLiferayId,
                    
                    start, end, orderByComparator
                };
        }

        List<MetadataComment> list = (List<MetadataComment>) FinderCacheUtil.getResult(finderPath,
                finderArgs, this);

        if ((list != null) && !list.isEmpty()) {
            for (MetadataComment metadataComment : list) {
                if ((userLiferayId != metadataComment.getUserLiferayId())) {
                    list = null;

                    break;
                }
            }
        }

        if (list == null) {
            StringBundler query = null;

            if (orderByComparator != null) {
                query = new StringBundler(3 +
                        (orderByComparator.getOrderByFields().length * 3));
            } else {
                query = new StringBundler(3);
            }

            query.append(_SQL_SELECT_METADATACOMMENT_WHERE);

            query.append(_FINDER_COLUMN_USERLIFERAYID_USERLIFERAYID_2);

            if (orderByComparator != null) {
                appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
                    orderByComparator);
            } else
             if (pagination) {
                query.append(MetadataCommentModelImpl.ORDER_BY_JPQL);
            }

            String sql = query.toString();

            Session session = null;

            try {
                session = openSession();

                Query q = session.createQuery(sql);

                QueryPos qPos = QueryPos.getInstance(q);

                qPos.add(userLiferayId);

                if (!pagination) {
                    list = (List<MetadataComment>) QueryUtil.list(q,
                            getDialect(), start, end, false);

                    Collections.sort(list);

                    list = new UnmodifiableList<MetadataComment>(list);
                } else {
                    list = (List<MetadataComment>) QueryUtil.list(q,
                            getDialect(), start, end);
                }

                cacheResult(list);

                FinderCacheUtil.putResult(finderPath, finderArgs, list);
            } catch (Exception e) {
                FinderCacheUtil.removeResult(finderPath, finderArgs);

                throw processException(e);
            } finally {
                closeSession(session);
            }
        }

        return list;
    }

    /**
     * Returns the first metadata comment in the ordered set where userLiferayId = &#63;.
     *
     * @param userLiferayId the user liferay ID
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the first matching metadata comment
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a matching metadata comment could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment findByuserLiferayId_First(long userLiferayId,
        OrderByComparator orderByComparator)
        throws NoSuchMetadataCommentException, SystemException {
        MetadataComment metadataComment = fetchByuserLiferayId_First(userLiferayId,
                orderByComparator);

        if (metadataComment != null) {
            return metadataComment;
        }

        StringBundler msg = new StringBundler(4);

        msg.append(_NO_SUCH_ENTITY_WITH_KEY);

        msg.append("userLiferayId=");
        msg.append(userLiferayId);

        msg.append(StringPool.CLOSE_CURLY_BRACE);

        throw new NoSuchMetadataCommentException(msg.toString());
    }

    /**
     * Returns the first metadata comment in the ordered set where userLiferayId = &#63;.
     *
     * @param userLiferayId the user liferay ID
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the first matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment fetchByuserLiferayId_First(long userLiferayId,
        OrderByComparator orderByComparator) throws SystemException {
        List<MetadataComment> list = findByuserLiferayId(userLiferayId, 0, 1,
                orderByComparator);

        if (!list.isEmpty()) {
            return list.get(0);
        }

        return null;
    }

    /**
     * Returns the last metadata comment in the ordered set where userLiferayId = &#63;.
     *
     * @param userLiferayId the user liferay ID
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the last matching metadata comment
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a matching metadata comment could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment findByuserLiferayId_Last(long userLiferayId,
        OrderByComparator orderByComparator)
        throws NoSuchMetadataCommentException, SystemException {
        MetadataComment metadataComment = fetchByuserLiferayId_Last(userLiferayId,
                orderByComparator);

        if (metadataComment != null) {
            return metadataComment;
        }

        StringBundler msg = new StringBundler(4);

        msg.append(_NO_SUCH_ENTITY_WITH_KEY);

        msg.append("userLiferayId=");
        msg.append(userLiferayId);

        msg.append(StringPool.CLOSE_CURLY_BRACE);

        throw new NoSuchMetadataCommentException(msg.toString());
    }

    /**
     * Returns the last metadata comment in the ordered set where userLiferayId = &#63;.
     *
     * @param userLiferayId the user liferay ID
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the last matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment fetchByuserLiferayId_Last(long userLiferayId,
        OrderByComparator orderByComparator) throws SystemException {
        int count = countByuserLiferayId(userLiferayId);

        if (count == 0) {
            return null;
        }

        List<MetadataComment> list = findByuserLiferayId(userLiferayId,
                count - 1, count, orderByComparator);

        if (!list.isEmpty()) {
            return list.get(0);
        }

        return null;
    }

    /**
     * Returns the metadata comments before and after the current metadata comment in the ordered set where userLiferayId = &#63;.
     *
     * @param _id the primary key of the current metadata comment
     * @param userLiferayId the user liferay ID
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the previous, current, and next metadata comment
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment[] findByuserLiferayId_PrevAndNext(long _id,
        long userLiferayId, OrderByComparator orderByComparator)
        throws NoSuchMetadataCommentException, SystemException {
        MetadataComment metadataComment = findByPrimaryKey(_id);

        Session session = null;

        try {
            session = openSession();

            MetadataComment[] array = new MetadataCommentImpl[3];

            array[0] = getByuserLiferayId_PrevAndNext(session, metadataComment,
                    userLiferayId, orderByComparator, true);

            array[1] = metadataComment;

            array[2] = getByuserLiferayId_PrevAndNext(session, metadataComment,
                    userLiferayId, orderByComparator, false);

            return array;
        } catch (Exception e) {
            throw processException(e);
        } finally {
            closeSession(session);
        }
    }

    protected MetadataComment getByuserLiferayId_PrevAndNext(Session session,
        MetadataComment metadataComment, long userLiferayId,
        OrderByComparator orderByComparator, boolean previous) {
        StringBundler query = null;

        if (orderByComparator != null) {
            query = new StringBundler(6 +
                    (orderByComparator.getOrderByFields().length * 6));
        } else {
            query = new StringBundler(3);
        }

        query.append(_SQL_SELECT_METADATACOMMENT_WHERE);

        query.append(_FINDER_COLUMN_USERLIFERAYID_USERLIFERAYID_2);

        if (orderByComparator != null) {
            String[] orderByConditionFields = orderByComparator.getOrderByConditionFields();

            if (orderByConditionFields.length > 0) {
                query.append(WHERE_AND);
            }

            for (int i = 0; i < orderByConditionFields.length; i++) {
                query.append(_ORDER_BY_ENTITY_ALIAS);
                query.append(orderByConditionFields[i]);

                if ((i + 1) < orderByConditionFields.length) {
                    if (orderByComparator.isAscending() ^ previous) {
                        query.append(WHERE_GREATER_THAN_HAS_NEXT);
                    } else {
                        query.append(WHERE_LESSER_THAN_HAS_NEXT);
                    }
                } else {
                    if (orderByComparator.isAscending() ^ previous) {
                        query.append(WHERE_GREATER_THAN);
                    } else {
                        query.append(WHERE_LESSER_THAN);
                    }
                }
            }

            query.append(ORDER_BY_CLAUSE);

            String[] orderByFields = orderByComparator.getOrderByFields();

            for (int i = 0; i < orderByFields.length; i++) {
                query.append(_ORDER_BY_ENTITY_ALIAS);
                query.append(orderByFields[i]);

                if ((i + 1) < orderByFields.length) {
                    if (orderByComparator.isAscending() ^ previous) {
                        query.append(ORDER_BY_ASC_HAS_NEXT);
                    } else {
                        query.append(ORDER_BY_DESC_HAS_NEXT);
                    }
                } else {
                    if (orderByComparator.isAscending() ^ previous) {
                        query.append(ORDER_BY_ASC);
                    } else {
                        query.append(ORDER_BY_DESC);
                    }
                }
            }
        } else {
            query.append(MetadataCommentModelImpl.ORDER_BY_JPQL);
        }

        String sql = query.toString();

        Query q = session.createQuery(sql);

        q.setFirstResult(0);
        q.setMaxResults(2);

        QueryPos qPos = QueryPos.getInstance(q);

        qPos.add(userLiferayId);

        if (orderByComparator != null) {
            Object[] values = orderByComparator.getOrderByConditionValues(metadataComment);

            for (Object value : values) {
                qPos.add(value);
            }
        }

        List<MetadataComment> list = q.list();

        if (list.size() == 2) {
            return list.get(1);
        } else {
            return null;
        }
    }

    /**
     * Removes all the metadata comments where userLiferayId = &#63; from the database.
     *
     * @param userLiferayId the user liferay ID
     * @throws SystemException if a system exception occurred
     */
    @Override
    public void removeByuserLiferayId(long userLiferayId)
        throws SystemException {
        for (MetadataComment metadataComment : findByuserLiferayId(
                userLiferayId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {
            remove(metadataComment);
        }
    }

    /**
     * Returns the number of metadata comments where userLiferayId = &#63;.
     *
     * @param userLiferayId the user liferay ID
     * @return the number of matching metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public int countByuserLiferayId(long userLiferayId)
        throws SystemException {
        FinderPath finderPath = FINDER_PATH_COUNT_BY_USERLIFERAYID;

        Object[] finderArgs = new Object[] { userLiferayId };

        Long count = (Long) FinderCacheUtil.getResult(finderPath, finderArgs,
                this);

        if (count == null) {
            StringBundler query = new StringBundler(2);

            query.append(_SQL_COUNT_METADATACOMMENT_WHERE);

            query.append(_FINDER_COLUMN_USERLIFERAYID_USERLIFERAYID_2);

            String sql = query.toString();

            Session session = null;

            try {
                session = openSession();

                Query q = session.createQuery(sql);

                QueryPos qPos = QueryPos.getInstance(q);

                qPos.add(userLiferayId);

                count = (Long) q.uniqueResult();

                FinderCacheUtil.putResult(finderPath, finderArgs, count);
            } catch (Exception e) {
                FinderCacheUtil.removeResult(finderPath, finderArgs);

                throw processException(e);
            } finally {
                closeSession(session);
            }
        }

        return count.intValue();
    }

    /**
     * Returns all the metadata comments where metadataName = &#63;.
     *
     * @param metadataName the metadata name
     * @return the matching metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public List<MetadataComment> findBymetadataName(String metadataName)
        throws SystemException {
        return findBymetadataName(metadataName, QueryUtil.ALL_POS,
            QueryUtil.ALL_POS, null);
    }

    /**
     * Returns a range of all the metadata comments where metadataName = &#63;.
     *
     * <p>
     * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
     * </p>
     *
     * @param metadataName the metadata name
     * @param start the lower bound of the range of metadata comments
     * @param end the upper bound of the range of metadata comments (not inclusive)
     * @return the range of matching metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public List<MetadataComment> findBymetadataName(String metadataName,
        int start, int end) throws SystemException {
        return findBymetadataName(metadataName, start, end, null);
    }

    /**
     * Returns an ordered range of all the metadata comments where metadataName = &#63;.
     *
     * <p>
     * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
     * </p>
     *
     * @param metadataName the metadata name
     * @param start the lower bound of the range of metadata comments
     * @param end the upper bound of the range of metadata comments (not inclusive)
     * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
     * @return the ordered range of matching metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public List<MetadataComment> findBymetadataName(String metadataName,
        int start, int end, OrderByComparator orderByComparator)
        throws SystemException {
        boolean pagination = true;
        FinderPath finderPath = null;
        Object[] finderArgs = null;

        if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
                (orderByComparator == null)) {
            pagination = false;
            finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_METADATANAME;
            finderArgs = new Object[] { metadataName };
        } else {
            finderPath = FINDER_PATH_WITH_PAGINATION_FIND_BY_METADATANAME;
            finderArgs = new Object[] {
                    metadataName,
                    
                    start, end, orderByComparator
                };
        }

        List<MetadataComment> list = (List<MetadataComment>) FinderCacheUtil.getResult(finderPath,
                finderArgs, this);

        if ((list != null) && !list.isEmpty()) {
            for (MetadataComment metadataComment : list) {
                if (!Validator.equals(metadataName,
                            metadataComment.getMetadataName())) {
                    list = null;

                    break;
                }
            }
        }

        if (list == null) {
            StringBundler query = null;

            if (orderByComparator != null) {
                query = new StringBundler(3 +
                        (orderByComparator.getOrderByFields().length * 3));
            } else {
                query = new StringBundler(3);
            }

            query.append(_SQL_SELECT_METADATACOMMENT_WHERE);

            boolean bindMetadataName = false;

            if (metadataName == null) {
                query.append(_FINDER_COLUMN_METADATANAME_METADATANAME_1);
            } else if (metadataName.equals(StringPool.BLANK)) {
                query.append(_FINDER_COLUMN_METADATANAME_METADATANAME_3);
            } else {
                bindMetadataName = true;

                query.append(_FINDER_COLUMN_METADATANAME_METADATANAME_2);
            }

            if (orderByComparator != null) {
                appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
                    orderByComparator);
            } else
             if (pagination) {
                query.append(MetadataCommentModelImpl.ORDER_BY_JPQL);
            }

            String sql = query.toString();

            Session session = null;

            try {
                session = openSession();

                Query q = session.createQuery(sql);

                QueryPos qPos = QueryPos.getInstance(q);

                if (bindMetadataName) {
                    qPos.add(metadataName);
                }

                if (!pagination) {
                    list = (List<MetadataComment>) QueryUtil.list(q,
                            getDialect(), start, end, false);

                    Collections.sort(list);

                    list = new UnmodifiableList<MetadataComment>(list);
                } else {
                    list = (List<MetadataComment>) QueryUtil.list(q,
                            getDialect(), start, end);
                }

                cacheResult(list);

                FinderCacheUtil.putResult(finderPath, finderArgs, list);
            } catch (Exception e) {
                FinderCacheUtil.removeResult(finderPath, finderArgs);

                throw processException(e);
            } finally {
                closeSession(session);
            }
        }

        return list;
    }

    /**
     * Returns the first metadata comment in the ordered set where metadataName = &#63;.
     *
     * @param metadataName the metadata name
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the first matching metadata comment
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a matching metadata comment could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment findBymetadataName_First(String metadataName,
        OrderByComparator orderByComparator)
        throws NoSuchMetadataCommentException, SystemException {
        MetadataComment metadataComment = fetchBymetadataName_First(metadataName,
                orderByComparator);

        if (metadataComment != null) {
            return metadataComment;
        }

        StringBundler msg = new StringBundler(4);

        msg.append(_NO_SUCH_ENTITY_WITH_KEY);

        msg.append("metadataName=");
        msg.append(metadataName);

        msg.append(StringPool.CLOSE_CURLY_BRACE);

        throw new NoSuchMetadataCommentException(msg.toString());
    }

    /**
     * Returns the first metadata comment in the ordered set where metadataName = &#63;.
     *
     * @param metadataName the metadata name
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the first matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment fetchBymetadataName_First(String metadataName,
        OrderByComparator orderByComparator) throws SystemException {
        List<MetadataComment> list = findBymetadataName(metadataName, 0, 1,
                orderByComparator);

        if (!list.isEmpty()) {
            return list.get(0);
        }

        return null;
    }

    /**
     * Returns the last metadata comment in the ordered set where metadataName = &#63;.
     *
     * @param metadataName the metadata name
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the last matching metadata comment
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a matching metadata comment could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment findBymetadataName_Last(String metadataName,
        OrderByComparator orderByComparator)
        throws NoSuchMetadataCommentException, SystemException {
        MetadataComment metadataComment = fetchBymetadataName_Last(metadataName,
                orderByComparator);

        if (metadataComment != null) {
            return metadataComment;
        }

        StringBundler msg = new StringBundler(4);

        msg.append(_NO_SUCH_ENTITY_WITH_KEY);

        msg.append("metadataName=");
        msg.append(metadataName);

        msg.append(StringPool.CLOSE_CURLY_BRACE);

        throw new NoSuchMetadataCommentException(msg.toString());
    }

    /**
     * Returns the last metadata comment in the ordered set where metadataName = &#63;.
     *
     * @param metadataName the metadata name
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the last matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment fetchBymetadataName_Last(String metadataName,
        OrderByComparator orderByComparator) throws SystemException {
        int count = countBymetadataName(metadataName);

        if (count == 0) {
            return null;
        }

        List<MetadataComment> list = findBymetadataName(metadataName,
                count - 1, count, orderByComparator);

        if (!list.isEmpty()) {
            return list.get(0);
        }

        return null;
    }

    /**
     * Returns the metadata comments before and after the current metadata comment in the ordered set where metadataName = &#63;.
     *
     * @param _id the primary key of the current metadata comment
     * @param metadataName the metadata name
     * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
     * @return the previous, current, and next metadata comment
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment[] findBymetadataName_PrevAndNext(long _id,
        String metadataName, OrderByComparator orderByComparator)
        throws NoSuchMetadataCommentException, SystemException {
        MetadataComment metadataComment = findByPrimaryKey(_id);

        Session session = null;

        try {
            session = openSession();

            MetadataComment[] array = new MetadataCommentImpl[3];

            array[0] = getBymetadataName_PrevAndNext(session, metadataComment,
                    metadataName, orderByComparator, true);

            array[1] = metadataComment;

            array[2] = getBymetadataName_PrevAndNext(session, metadataComment,
                    metadataName, orderByComparator, false);

            return array;
        } catch (Exception e) {
            throw processException(e);
        } finally {
            closeSession(session);
        }
    }

    protected MetadataComment getBymetadataName_PrevAndNext(Session session,
        MetadataComment metadataComment, String metadataName,
        OrderByComparator orderByComparator, boolean previous) {
        StringBundler query = null;

        if (orderByComparator != null) {
            query = new StringBundler(6 +
                    (orderByComparator.getOrderByFields().length * 6));
        } else {
            query = new StringBundler(3);
        }

        query.append(_SQL_SELECT_METADATACOMMENT_WHERE);

        boolean bindMetadataName = false;

        if (metadataName == null) {
            query.append(_FINDER_COLUMN_METADATANAME_METADATANAME_1);
        } else if (metadataName.equals(StringPool.BLANK)) {
            query.append(_FINDER_COLUMN_METADATANAME_METADATANAME_3);
        } else {
            bindMetadataName = true;

            query.append(_FINDER_COLUMN_METADATANAME_METADATANAME_2);
        }

        if (orderByComparator != null) {
            String[] orderByConditionFields = orderByComparator.getOrderByConditionFields();

            if (orderByConditionFields.length > 0) {
                query.append(WHERE_AND);
            }

            for (int i = 0; i < orderByConditionFields.length; i++) {
                query.append(_ORDER_BY_ENTITY_ALIAS);
                query.append(orderByConditionFields[i]);

                if ((i + 1) < orderByConditionFields.length) {
                    if (orderByComparator.isAscending() ^ previous) {
                        query.append(WHERE_GREATER_THAN_HAS_NEXT);
                    } else {
                        query.append(WHERE_LESSER_THAN_HAS_NEXT);
                    }
                } else {
                    if (orderByComparator.isAscending() ^ previous) {
                        query.append(WHERE_GREATER_THAN);
                    } else {
                        query.append(WHERE_LESSER_THAN);
                    }
                }
            }

            query.append(ORDER_BY_CLAUSE);

            String[] orderByFields = orderByComparator.getOrderByFields();

            for (int i = 0; i < orderByFields.length; i++) {
                query.append(_ORDER_BY_ENTITY_ALIAS);
                query.append(orderByFields[i]);

                if ((i + 1) < orderByFields.length) {
                    if (orderByComparator.isAscending() ^ previous) {
                        query.append(ORDER_BY_ASC_HAS_NEXT);
                    } else {
                        query.append(ORDER_BY_DESC_HAS_NEXT);
                    }
                } else {
                    if (orderByComparator.isAscending() ^ previous) {
                        query.append(ORDER_BY_ASC);
                    } else {
                        query.append(ORDER_BY_DESC);
                    }
                }
            }
        } else {
            query.append(MetadataCommentModelImpl.ORDER_BY_JPQL);
        }

        String sql = query.toString();

        Query q = session.createQuery(sql);

        q.setFirstResult(0);
        q.setMaxResults(2);

        QueryPos qPos = QueryPos.getInstance(q);

        if (bindMetadataName) {
            qPos.add(metadataName);
        }

        if (orderByComparator != null) {
            Object[] values = orderByComparator.getOrderByConditionValues(metadataComment);

            for (Object value : values) {
                qPos.add(value);
            }
        }

        List<MetadataComment> list = q.list();

        if (list.size() == 2) {
            return list.get(1);
        } else {
            return null;
        }
    }

    /**
     * Removes all the metadata comments where metadataName = &#63; from the database.
     *
     * @param metadataName the metadata name
     * @throws SystemException if a system exception occurred
     */
    @Override
    public void removeBymetadataName(String metadataName)
        throws SystemException {
        for (MetadataComment metadataComment : findBymetadataName(
                metadataName, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {
            remove(metadataComment);
        }
    }

    /**
     * Returns the number of metadata comments where metadataName = &#63;.
     *
     * @param metadataName the metadata name
     * @return the number of matching metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public int countBymetadataName(String metadataName)
        throws SystemException {
        FinderPath finderPath = FINDER_PATH_COUNT_BY_METADATANAME;

        Object[] finderArgs = new Object[] { metadataName };

        Long count = (Long) FinderCacheUtil.getResult(finderPath, finderArgs,
                this);

        if (count == null) {
            StringBundler query = new StringBundler(2);

            query.append(_SQL_COUNT_METADATACOMMENT_WHERE);

            boolean bindMetadataName = false;

            if (metadataName == null) {
                query.append(_FINDER_COLUMN_METADATANAME_METADATANAME_1);
            } else if (metadataName.equals(StringPool.BLANK)) {
                query.append(_FINDER_COLUMN_METADATANAME_METADATANAME_3);
            } else {
                bindMetadataName = true;

                query.append(_FINDER_COLUMN_METADATANAME_METADATANAME_2);
            }

            String sql = query.toString();

            Session session = null;

            try {
                session = openSession();

                Query q = session.createQuery(sql);

                QueryPos qPos = QueryPos.getInstance(q);

                if (bindMetadataName) {
                    qPos.add(metadataName);
                }

                count = (Long) q.uniqueResult();

                FinderCacheUtil.putResult(finderPath, finderArgs, count);
            } catch (Exception e) {
                FinderCacheUtil.removeResult(finderPath, finderArgs);

                throw processException(e);
            } finally {
                closeSession(session);
            }
        }

        return count.intValue();
    }

    /**
     * Caches the metadata comment in the entity cache if it is enabled.
     *
     * @param metadataComment the metadata comment
     */
    @Override
    public void cacheResult(MetadataComment metadataComment) {
        EntityCacheUtil.putResult(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentImpl.class, metadataComment.getPrimaryKey(),
            metadataComment);

        metadataComment.resetOriginalValues();
    }

    /**
     * Caches the metadata comments in the entity cache if it is enabled.
     *
     * @param metadataComments the metadata comments
     */
    @Override
    public void cacheResult(List<MetadataComment> metadataComments) {
        for (MetadataComment metadataComment : metadataComments) {
            if (EntityCacheUtil.getResult(
                        MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
                        MetadataCommentImpl.class,
                        metadataComment.getPrimaryKey()) == null) {
                cacheResult(metadataComment);
            } else {
                metadataComment.resetOriginalValues();
            }
        }
    }

    /**
     * Clears the cache for all metadata comments.
     *
     * <p>
     * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
     * </p>
     */
    @Override
    public void clearCache() {
        if (_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
            CacheRegistryUtil.clear(MetadataCommentImpl.class.getName());
        }

        EntityCacheUtil.clearCache(MetadataCommentImpl.class.getName());

        FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
        FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
        FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
    }

    /**
     * Clears the cache for the metadata comment.
     *
     * <p>
     * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
     * </p>
     */
    @Override
    public void clearCache(MetadataComment metadataComment) {
        EntityCacheUtil.removeResult(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentImpl.class, metadataComment.getPrimaryKey());

        FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
        FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
    }

    @Override
    public void clearCache(List<MetadataComment> metadataComments) {
        FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
        FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

        for (MetadataComment metadataComment : metadataComments) {
            EntityCacheUtil.removeResult(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
                MetadataCommentImpl.class, metadataComment.getPrimaryKey());
        }
    }

    /**
     * Creates a new metadata comment with the primary key. Does not add the metadata comment to the database.
     *
     * @param _id the primary key for the new metadata comment
     * @return the new metadata comment
     */
    @Override
    public MetadataComment create(long _id) {
        MetadataComment metadataComment = new MetadataCommentImpl();

        metadataComment.setNew(true);
        metadataComment.setPrimaryKey(_id);

        String uuid = PortalUUIDUtil.generate();

        metadataComment.setUuid(uuid);

        return metadataComment;
    }

    /**
     * Removes the metadata comment with the primary key from the database. Also notifies the appropriate model listeners.
     *
     * @param _id the primary key of the metadata comment
     * @return the metadata comment that was removed
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment remove(long _id)
        throws NoSuchMetadataCommentException, SystemException {
        return remove((Serializable) _id);
    }

    /**
     * Removes the metadata comment with the primary key from the database. Also notifies the appropriate model listeners.
     *
     * @param primaryKey the primary key of the metadata comment
     * @return the metadata comment that was removed
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment remove(Serializable primaryKey)
        throws NoSuchMetadataCommentException, SystemException {
        Session session = null;

        try {
            session = openSession();

            MetadataComment metadataComment = (MetadataComment) session.get(MetadataCommentImpl.class,
                    primaryKey);

            if (metadataComment == null) {
                if (_log.isWarnEnabled()) {
                    _log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
                }

                throw new NoSuchMetadataCommentException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
                    primaryKey);
            }

            return remove(metadataComment);
        } catch (NoSuchMetadataCommentException nsee) {
            throw nsee;
        } catch (Exception e) {
            throw processException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    protected MetadataComment removeImpl(MetadataComment metadataComment)
        throws SystemException {
        metadataComment = toUnwrappedModel(metadataComment);

        Session session = null;

        try {
            session = openSession();

            if (!session.contains(metadataComment)) {
                metadataComment = (MetadataComment) session.get(MetadataCommentImpl.class,
                        metadataComment.getPrimaryKeyObj());
            }

            if (metadataComment != null) {
                session.delete(metadataComment);
            }
        } catch (Exception e) {
            throw processException(e);
        } finally {
            closeSession(session);
        }

        if (metadataComment != null) {
            clearCache(metadataComment);
        }

        return metadataComment;
    }

    @Override
    public MetadataComment updateImpl(
        de.fhg.fokus.odp.entities.model.MetadataComment metadataComment)
        throws SystemException {
        metadataComment = toUnwrappedModel(metadataComment);

        boolean isNew = metadataComment.isNew();

        MetadataCommentModelImpl metadataCommentModelImpl = (MetadataCommentModelImpl) metadataComment;

        if (Validator.isNull(metadataComment.getUuid())) {
            String uuid = PortalUUIDUtil.generate();

            metadataComment.setUuid(uuid);
        }

        Session session = null;

        try {
            session = openSession();

            if (metadataComment.isNew()) {
                session.save(metadataComment);

                metadataComment.setNew(false);
            } else {
                session.merge(metadataComment);
            }
        } catch (Exception e) {
            throw processException(e);
        } finally {
            closeSession(session);
        }

        FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

        if (isNew || !MetadataCommentModelImpl.COLUMN_BITMASK_ENABLED) {
            FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
        }
        else {
            if ((metadataCommentModelImpl.getColumnBitmask() &
                    FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID.getColumnBitmask()) != 0) {
                Object[] args = new Object[] {
                        metadataCommentModelImpl.getOriginalUuid()
                    };

                FinderCacheUtil.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
                FinderCacheUtil.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
                    args);

                args = new Object[] { metadataCommentModelImpl.getUuid() };

                FinderCacheUtil.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
                FinderCacheUtil.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
                    args);
            }

            if ((metadataCommentModelImpl.getColumnBitmask() &
                    FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_USERLIFERAYID.getColumnBitmask()) != 0) {
                Object[] args = new Object[] {
                        metadataCommentModelImpl.getOriginalUserLiferayId()
                    };

                FinderCacheUtil.removeResult(FINDER_PATH_COUNT_BY_USERLIFERAYID,
                    args);
                FinderCacheUtil.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_USERLIFERAYID,
                    args);

                args = new Object[] { metadataCommentModelImpl.getUserLiferayId() };

                FinderCacheUtil.removeResult(FINDER_PATH_COUNT_BY_USERLIFERAYID,
                    args);
                FinderCacheUtil.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_USERLIFERAYID,
                    args);
            }

            if ((metadataCommentModelImpl.getColumnBitmask() &
                    FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_METADATANAME.getColumnBitmask()) != 0) {
                Object[] args = new Object[] {
                        metadataCommentModelImpl.getOriginalMetadataName()
                    };

                FinderCacheUtil.removeResult(FINDER_PATH_COUNT_BY_METADATANAME,
                    args);
                FinderCacheUtil.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_METADATANAME,
                    args);

                args = new Object[] { metadataCommentModelImpl.getMetadataName() };

                FinderCacheUtil.removeResult(FINDER_PATH_COUNT_BY_METADATANAME,
                    args);
                FinderCacheUtil.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_METADATANAME,
                    args);
            }
        }

        EntityCacheUtil.putResult(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
            MetadataCommentImpl.class, metadataComment.getPrimaryKey(),
            metadataComment);

        return metadataComment;
    }

    protected MetadataComment toUnwrappedModel(MetadataComment metadataComment) {
        if (metadataComment instanceof MetadataCommentImpl) {
            return metadataComment;
        }

        MetadataCommentImpl metadataCommentImpl = new MetadataCommentImpl();

        metadataCommentImpl.setNew(metadataComment.isNew());
        metadataCommentImpl.setPrimaryKey(metadataComment.getPrimaryKey());

        metadataCommentImpl.setUuid(metadataComment.getUuid());
        metadataCommentImpl.set_id(metadataComment.get_id());
        metadataCommentImpl.setUserLiferayId(metadataComment.getUserLiferayId());
        metadataCommentImpl.setMetadataName(metadataComment.getMetadataName());
        metadataCommentImpl.setText(metadataComment.getText());
        metadataCommentImpl.setCreated(metadataComment.getCreated());

        return metadataCommentImpl;
    }

    /**
     * Returns the metadata comment with the primary key or throws a {@link com.liferay.portal.NoSuchModelException} if it could not be found.
     *
     * @param primaryKey the primary key of the metadata comment
     * @return the metadata comment
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment findByPrimaryKey(Serializable primaryKey)
        throws NoSuchMetadataCommentException, SystemException {
        MetadataComment metadataComment = fetchByPrimaryKey(primaryKey);

        if (metadataComment == null) {
            if (_log.isWarnEnabled()) {
                _log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
            }

            throw new NoSuchMetadataCommentException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
                primaryKey);
        }

        return metadataComment;
    }

    /**
     * Returns the metadata comment with the primary key or throws a {@link de.fhg.fokus.odp.entities.NoSuchMetadataCommentException} if it could not be found.
     *
     * @param _id the primary key of the metadata comment
     * @return the metadata comment
     * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment findByPrimaryKey(long _id)
        throws NoSuchMetadataCommentException, SystemException {
        return findByPrimaryKey((Serializable) _id);
    }

    /**
     * Returns the metadata comment with the primary key or returns <code>null</code> if it could not be found.
     *
     * @param primaryKey the primary key of the metadata comment
     * @return the metadata comment, or <code>null</code> if a metadata comment with the primary key could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment fetchByPrimaryKey(Serializable primaryKey)
        throws SystemException {
        MetadataComment metadataComment = (MetadataComment) EntityCacheUtil.getResult(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
                MetadataCommentImpl.class, primaryKey);

        if (metadataComment == _nullMetadataComment) {
            return null;
        }

        if (metadataComment == null) {
            Session session = null;

            try {
                session = openSession();

                metadataComment = (MetadataComment) session.get(MetadataCommentImpl.class,
                        primaryKey);

                if (metadataComment != null) {
                    cacheResult(metadataComment);
                } else {
                    EntityCacheUtil.putResult(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
                        MetadataCommentImpl.class, primaryKey,
                        _nullMetadataComment);
                }
            } catch (Exception e) {
                EntityCacheUtil.removeResult(MetadataCommentModelImpl.ENTITY_CACHE_ENABLED,
                    MetadataCommentImpl.class, primaryKey);

                throw processException(e);
            } finally {
                closeSession(session);
            }
        }

        return metadataComment;
    }

    /**
     * Returns the metadata comment with the primary key or returns <code>null</code> if it could not be found.
     *
     * @param _id the primary key of the metadata comment
     * @return the metadata comment, or <code>null</code> if a metadata comment with the primary key could not be found
     * @throws SystemException if a system exception occurred
     */
    @Override
    public MetadataComment fetchByPrimaryKey(long _id)
        throws SystemException {
        return fetchByPrimaryKey((Serializable) _id);
    }

    /**
     * Returns all the metadata comments.
     *
     * @return the metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public List<MetadataComment> findAll() throws SystemException {
        return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
    }

    /**
     * Returns a range of all the metadata comments.
     *
     * <p>
     * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
     * </p>
     *
     * @param start the lower bound of the range of metadata comments
     * @param end the upper bound of the range of metadata comments (not inclusive)
     * @return the range of metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public List<MetadataComment> findAll(int start, int end)
        throws SystemException {
        return findAll(start, end, null);
    }

    /**
     * Returns an ordered range of all the metadata comments.
     *
     * <p>
     * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
     * </p>
     *
     * @param start the lower bound of the range of metadata comments
     * @param end the upper bound of the range of metadata comments (not inclusive)
     * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
     * @return the ordered range of metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public List<MetadataComment> findAll(int start, int end,
        OrderByComparator orderByComparator) throws SystemException {
        boolean pagination = true;
        FinderPath finderPath = null;
        Object[] finderArgs = null;

        if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
                (orderByComparator == null)) {
            pagination = false;
            finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL;
            finderArgs = FINDER_ARGS_EMPTY;
        } else {
            finderPath = FINDER_PATH_WITH_PAGINATION_FIND_ALL;
            finderArgs = new Object[] { start, end, orderByComparator };
        }

        List<MetadataComment> list = (List<MetadataComment>) FinderCacheUtil.getResult(finderPath,
                finderArgs, this);

        if (list == null) {
            StringBundler query = null;
            String sql = null;

            if (orderByComparator != null) {
                query = new StringBundler(2 +
                        (orderByComparator.getOrderByFields().length * 3));

                query.append(_SQL_SELECT_METADATACOMMENT);

                appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
                    orderByComparator);

                sql = query.toString();
            } else {
                sql = _SQL_SELECT_METADATACOMMENT;

                if (pagination) {
                    sql = sql.concat(MetadataCommentModelImpl.ORDER_BY_JPQL);
                }
            }

            Session session = null;

            try {
                session = openSession();

                Query q = session.createQuery(sql);

                if (!pagination) {
                    list = (List<MetadataComment>) QueryUtil.list(q,
                            getDialect(), start, end, false);

                    Collections.sort(list);

                    list = new UnmodifiableList<MetadataComment>(list);
                } else {
                    list = (List<MetadataComment>) QueryUtil.list(q,
                            getDialect(), start, end);
                }

                cacheResult(list);

                FinderCacheUtil.putResult(finderPath, finderArgs, list);
            } catch (Exception e) {
                FinderCacheUtil.removeResult(finderPath, finderArgs);

                throw processException(e);
            } finally {
                closeSession(session);
            }
        }

        return list;
    }

    /**
     * Removes all the metadata comments from the database.
     *
     * @throws SystemException if a system exception occurred
     */
    @Override
    public void removeAll() throws SystemException {
        for (MetadataComment metadataComment : findAll()) {
            remove(metadataComment);
        }
    }

    /**
     * Returns the number of metadata comments.
     *
     * @return the number of metadata comments
     * @throws SystemException if a system exception occurred
     */
    @Override
    public int countAll() throws SystemException {
        Long count = (Long) FinderCacheUtil.getResult(FINDER_PATH_COUNT_ALL,
                FINDER_ARGS_EMPTY, this);

        if (count == null) {
            Session session = null;

            try {
                session = openSession();

                Query q = session.createQuery(_SQL_COUNT_METADATACOMMENT);

                count = (Long) q.uniqueResult();

                FinderCacheUtil.putResult(FINDER_PATH_COUNT_ALL,
                    FINDER_ARGS_EMPTY, count);
            } catch (Exception e) {
                FinderCacheUtil.removeResult(FINDER_PATH_COUNT_ALL,
                    FINDER_ARGS_EMPTY);

                throw processException(e);
            } finally {
                closeSession(session);
            }
        }

        return count.intValue();
    }

    @Override
    protected Set<String> getBadColumnNames() {
        return _badColumnNames;
    }

    /**
     * Initializes the metadata comment persistence.
     */
    public void afterPropertiesSet() {
        String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
                    com.liferay.util.service.ServiceProps.get(
                        "value.object.listener.de.fhg.fokus.odp.entities.model.MetadataComment")));

        if (listenerClassNames.length > 0) {
            try {
                List<ModelListener<MetadataComment>> listenersList = new ArrayList<ModelListener<MetadataComment>>();

                for (String listenerClassName : listenerClassNames) {
                    listenersList.add((ModelListener<MetadataComment>) InstanceFactory.newInstance(
                            getClassLoader(), listenerClassName));
                }

                listeners = listenersList.toArray(new ModelListener[listenersList.size()]);
            } catch (Exception e) {
                _log.error(e);
            }
        }
    }

    public void destroy() {
        EntityCacheUtil.removeCache(MetadataCommentImpl.class.getName());
        FinderCacheUtil.removeCache(FINDER_CLASS_NAME_ENTITY);
        FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
        FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
    }
}
