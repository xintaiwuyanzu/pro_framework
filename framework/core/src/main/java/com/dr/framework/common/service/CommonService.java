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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;

/**
 * @author dr
 */
@Service
public class CommonService {
    @Autowired
    CommonMapper commonMapper;
    @Autowired(required = false)
    SecurityManager securityManager;

    @Transactional(rollbackFor = Exception.class)
    public <T extends IdEntity> void insert(T entity) {
        if (StringUtils.isEmpty(entity.getId())) {
            entity.setId(UUID.randomUUID().toString());
        }
        //保存创建人相关信息
        if (entity instanceof BaseCreateInfoEntity) {
            bindCreateInfo((BaseCreateInfoEntity) entity);
        }
        commonMapper.insert(entity);
    }

    public static void bindCreateInfo(BaseCreateInfoEntity entity) {
        if (StringUtils.isEmpty(entity.getId())) {
            entity.setId(UUID.randomUUID().toString());
        }
        SecurityHolder securityHolder = SecurityHolder.get();
        Person currentPerson = securityHolder.currentPerson();
        if (StringUtils.isEmpty(entity.getCreatePerson())) {
            if (currentPerson != null) {
                entity.setCreatePerson(currentPerson.getId());
            }
            if (StringUtils.isEmpty(entity.getCreateDate())) {
                entity.setCreateDate(System.currentTimeMillis());
            }
        }
        entity.setUpdateDate(System.currentTimeMillis());
        if (StringUtils.isEmpty(entity.getUpdatePerson()) && currentPerson != null) {
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
            if (StringUtils.isEmpty(entity.getId())) {
                entity.setId(UUID.randomUUID().toString());
                commonMapper.insert(entity);
            } else {
                if (!exists(entity)) {
                    insert(entity);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public <T extends IdEntity> void update(T entity) {
        //保存创建人相关信息
        if (entity instanceof BaseCreateInfoEntity) {
            BaseCreateInfoEntity createInfoEntity = (BaseCreateInfoEntity) entity;
            if (StringUtils.isEmpty(createInfoEntity.getCreatePerson())) {
                Person person = SecurityHolder.get().currentPerson();
                if (person != null) {
                    createInfoEntity.setUpdateDate(System.currentTimeMillis());
                    createInfoEntity.setUpdatePerson(person.getId());
                }
            }
        }
        commonMapper.updateById(entity);
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
        if (StringUtils.isEmpty(id)) {
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

    /**
     * 将list转换为tree
     *
     * @param treeList
     * @param parentId
     * @param labelFunction 获取label的函数
     * @param <T>
     * @return
     */
    public static <T extends TreeEntity> List<TreeNode> listToTree(List<T> treeList, String parentId, Function<T, String> labelFunction) {
        // 先将list转换成map
        Map<String, List<TreeNode>> pidMaps = new HashMap<>();
        for (T treeEntity : treeList) {
            TreeNode treeNode = new TreeNode(treeEntity.getId(), labelFunction.apply(treeEntity), treeEntity);
            String pid = treeEntity.getParentId();
            if (StringUtils.isEmpty(pid)) {
                pid = "$default_parentId";
            }
            treeNode.setParentId(pid);
            treeNode.setOrder(treeEntity.getOrder());
            List<TreeNode> treeNodeList;
            if (pidMaps.containsKey(pid)) {
                treeNodeList = pidMaps.get(pid);
            } else {
                treeNodeList = new ArrayList<>();
                pidMaps.put(pid, treeNodeList);
            }
            treeNodeList.add(treeNode);
        }
        return mapToTree(parentId, 0, pidMaps);
    }

    /**
     * 递归方法将map转换成tree
     *
     * @param parentId
     * @param pidMaps
     * @return
     */
    public static List<TreeNode> mapToTree(String parentId, int level, Map<String, List<TreeNode>> pidMaps) {
        if (pidMaps.containsKey(parentId)) {
            List<TreeNode> treeNodes = pidMaps.get(parentId);
            for (TreeNode treeNode : treeNodes) {
                treeNode.setLevel(level);
                List<TreeNode> children = mapToTree(treeNode.getId(), level + 1, pidMaps);
                if (children != null) {
                    treeNode.setChildren(children);
                }
            }
            Collections.sort(treeNodes);
            return treeNodes;
        }
        return null;
    }

}
