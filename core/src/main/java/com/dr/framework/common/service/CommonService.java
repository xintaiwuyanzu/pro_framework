package com.dr.framework.common.service;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.entity.BaseCreateInfoEntity;
import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.entity.TreeEntity;
import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.SecurityHolder;
import com.dr.framework.core.security.service.SecurityManager;
import com.dr.framework.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dr
 */
@Service
public class CommonService {
    @Autowired
    CommonMapper commonMapper;
    @Lazy
    @Autowired(required = false)
    SecurityManager securityManager;

    @Transactional(rollbackFor = Exception.class)
    public <T extends IdEntity> long insert(T entity) {
        if (!StringUtils.hasText(entity.getId())) {
            entity.setId(UUID.randomUUID().toString());
        }
        //保存创建人相关信息
        if (entity instanceof BaseCreateInfoEntity) {
            bindCreateInfo((BaseCreateInfoEntity) entity);
        }
        return commonMapper.insert(entity);
    }

    public static void bindCreateInfo(BaseCreateInfoEntity entity) {
        if (!StringUtils.hasText(entity.getId())) {
            entity.setId(UUID.randomUUID().toString());
        }
        SecurityHolder securityHolder = SecurityHolder.get();
        Person currentPerson = securityHolder.currentPerson();
        if (!StringUtils.hasText(entity.getCreatePerson())) {
            if (currentPerson != null) {
                entity.setCreatePerson(currentPerson.getId());
            }
            if (ObjectUtils.isEmpty(entity.getCreateDate())) {
                entity.setCreateDate(System.currentTimeMillis());
            }
        }
        entity.setUpdateDate(System.currentTimeMillis());
        if (!StringUtils.hasText(entity.getUpdatePerson()) && currentPerson != null) {
            entity.setUpdatePerson(entity.getCreatePerson());
        }
    }

    /**
     * 批量插入数据
     *
     * @param entitys
     * @param <T>
     */
    @Transactional(rollbackFor = Exception.class)
    public <T extends IdEntity> void insertIfNotExist(T... entitys) {
        for (T entity : entitys) {
            if (!StringUtils.hasText(entity.getId())) {
                insert(entity);
            } else {
                if (!exists(entity)) {
                    insert(entity);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public <T extends IdEntity> long update(T entity) {
        //保存创建人相关信息
        if (entity instanceof BaseCreateInfoEntity) {
            BaseCreateInfoEntity createInfoEntity = (BaseCreateInfoEntity) entity;
            if (!StringUtils.hasText(createInfoEntity.getCreatePerson())) {
                Person person = SecurityHolder.get().currentPerson();
                if (person != null) {
                    createInfoEntity.setUpdateDate(System.currentTimeMillis());
                    createInfoEntity.setUpdatePerson(person.getId());
                }
            }
        }
        return commonMapper.updateById(entity);
    }

    /**
     * 根据sqlQuery查询数据
     *
     * @param sqlQuery
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public <E extends IdEntity> E selectOne(SqlQuery<E> sqlQuery) {
        return commonMapper.selectOneByQuery(sqlQuery);
    }

    /**
     * 根据sqlquery查询分页数据
     *
     * @param sqlQuery
     * @param pageIndex
     * @param pageSize
     * @param <E>
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public <E extends IdEntity> Page<E> selectPage(SqlQuery<E> sqlQuery, int pageIndex, int pageSize) {
        return commonMapper.selectPageByQuery(sqlQuery, pageIndex * pageSize, (pageIndex + 1) * pageSize);
    }

    /**
     * 根据sqlQuery查询列表数据
     *
     * @param eSqlQuery
     * @param <E>
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public <E extends IdEntity> List<E> selectList(SqlQuery<E> eSqlQuery) {
        return commonMapper.selectByQuery(eSqlQuery);
    }

    @Transactional(rollbackFor = Exception.class)
    public <E extends IdEntity> long delete(SqlQuery<E> sqlQuery) {
        return commonMapper.deleteByQuery(sqlQuery);
    }

    /**
     * 指定表的id的对象是否存在
     *
     * @param entityClass
     * @param id
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public boolean exists(Class entityClass, String id) {
        return commonMapper.exists(entityClass, id);
    }

    /**
     * 指定表的id的对象是否存在
     *
     * @param entity
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public boolean exists(IdEntity entity) {
        String id = entity.getId();
        if (!StringUtils.hasText(id)) {
            return false;
        }
        return exists(entity.getClass(), id);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public boolean exists(SqlQuery sqlQuery) {
        return commonMapper.existsByQuery(sqlQuery);
    }

    /**
     * 根据id查询表中的单条数据
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public <E> E findById(Class<E> entityClass, String id) {
        return commonMapper.selectById(entityClass, id);
    }

    /**
     * count查询
     *
     * @param query
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public long countByQuery(SqlQuery query) {
        return commonMapper.countByQuery(query);
    }

    public static <T extends TreeEntity> List<TreeNode> listToTree(List<T> treeList, String parentId, Function<T, String> labelFunction) {
        return listToTree(treeList, parentId, labelFunction, false);
    }

    public static <T extends TreeEntity> List<TreeNode> listToTree(List<T> treeList,
                                                                   String parentId,
                                                                   Function<T, String> labelFunction,
                                                                   boolean checkEmpty) {
        return listToTree(treeList, parentId, labelFunction, null, checkEmpty);
    }

    /**
     * 将list转换为tree
     *
     * @param treeList
     * @param parentId
     * @param labelFunction 获取label的函数
     * @param <T>
     * @param checkEmpty    是否检验children为空的情况
     * @return
     */
    public static <T extends TreeEntity> List<TreeNode> listToTree(List<T> treeList,
                                                                   String parentId,
                                                                   Function<T, String> labelFunction,
                                                                   Function<T, Boolean> leafFunction,
                                                                   boolean checkEmpty) {
        return listToTree(treeList, parentId, TreeEntity::getId, TreeEntity::getParentId, TreeEntity::getOrder, labelFunction, leafFunction, checkEmpty);
    }

    public static <T> List<TreeNode> listToTree(List<T> treeList,
                                                String parentId,
                                                Function<T, String> idFunction,
                                                Function<T, String> parentFunction,
                                                Function<T, Integer> orderFunction,
                                                Function<T, String> labelFunction,
                                                Function<T, Boolean> leafFunction,
                                                boolean checkEmpty) {
        // 先将list转换成map
        Map<String, List<TreeNode>> pidMaps = new HashMap<>();
        for (T treeEntity : treeList) {
            TreeNode treeNode = new TreeNode(idFunction.apply(treeEntity), labelFunction.apply(treeEntity), treeEntity);
            String pid = parentFunction.apply(treeEntity);
            if (!StringUtils.hasText(pid)) {
                //pid = "$default_parentId";
                pid = Constants.DEFAULT;
            }
            treeNode.setParentId(pid);
            if (orderFunction != null) {
                Integer order = orderFunction.apply(treeEntity);
                if (order == null) {
                    //这里会报空指针
                    order = 0;
                }
                treeNode.setOrder(order);
            }
            if (leafFunction != null) {
                treeNode.setLeaf(leafFunction.apply(treeEntity));
            }
            List<TreeNode> treeNodeList;
            if (pidMaps.containsKey(pid)) {
                treeNodeList = pidMaps.get(pid);
            } else {
                treeNodeList = new ArrayList<>();
                pidMaps.put(pid, treeNodeList);
            }
            treeNodeList.add(treeNode);
        }
        return mapToTree(parentId, 0, pidMaps, checkEmpty);
    }

    public static List<TreeNode> mapToTree(String parentId, int level, Map<String, List<TreeNode>> pidMaps) {
        return mapToTree(parentId, level, pidMaps, false);
    }

    /**
     * 递归方法将map转换成tree
     *
     * @param parentId
     * @param pidMaps
     * @param checkEmpty
     * @return
     */
    public static List<TreeNode> mapToTree(String parentId, int level, Map<String, List<TreeNode>> pidMaps, boolean checkEmpty) {
        if (pidMaps.containsKey(parentId)) {
            List<TreeNode> treeNodes = pidMaps.get(parentId)
                    .stream()
                    .filter(treeNode -> {
                        treeNode.setLevel(level);
                        List<TreeNode> children = mapToTree(treeNode.getId(), level + 1, pidMaps, checkEmpty);
                        if (children != null && !children.isEmpty()) {
                            treeNode.setChildren(children);
                        } else if (checkEmpty) {
                            return treeNode.isLeaf();
                        }
                        return true;
                    })
                    .sorted()
                    .collect(Collectors.toList());
            return treeNodes;
        }
        return Collections.EMPTY_LIST;
    }
}
