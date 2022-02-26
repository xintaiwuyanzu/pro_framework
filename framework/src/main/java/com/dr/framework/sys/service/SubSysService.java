package com.dr.framework.sys.service;

import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.PermissionResourceService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.bo.PermissionResource;
import com.dr.framework.core.security.entity.SubSystem;
import com.dr.framework.core.security.query.SubSysQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 子系统增删改查service
 *
 * @author dr
 */
@Service
public class SubSysService extends PermissionResourceService<SubSystem> implements RelationHelper {

    @Transactional(rollbackFor = Exception.class)
    public long deleteById(String id) {
        Assert.isTrue(!StringUtils.isEmpty(id), "子系统id不能为空！");
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long delete(SqlQuery<SubSystem> sqlQuery) {
        //删除子系统
        //删除权限
        //删除菜单

        /**
         *    int count = 0;
         *         //删除子系统
         *         count += commonMapper.deleteById(SubSystem.class, id);
         *         //删除子系统对应的菜单
         *         List<SysMenu> sysMenus = selectMenuList(new SysMenuQuery.Builder().sysIdEqual(id).build());
         *         deleteMenu(sysMenus.stream()
         *                 .map(BaseEntity::getId)
         *                 .collect(Collectors.toList())
         *                 .toArray(new String[sysMenus.size()])
         *         );
         */
        return super.delete(sqlQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long insert(SubSystem entity) {
        if (StringUtils.isEmpty(entity.getStatus())) {
            entity.setStatus(StatusEntity.STATUS_ENABLE_STR);
        }
        return super.insert(entity);
    }

    @Override
    public long updateById(SubSystem entity) {
        if (!exists(entity.getId())) {
            return insert(entity);
        }
        return super.updateById(entity);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SubSystem> selectSubSysList(SubSysQuery query) {
        return commonMapper.selectByQuery(subSysQueryToSqlQuery(query));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<SubSystem> selectSubSysPage(SubSysQuery query, int start, int end) {
        return commonMapper.selectPageByQuery(subSysQueryToSqlQuery(query), start, end);
    }

    protected SqlQuery<SubSystem> subSysQueryToSqlQuery(SubSysQuery query) {
        SqlQuery<SubSystem> sqlQuery = SqlQuery.from(entityRelation);
        checkBuildInQuery(entityRelation, sqlQuery, SubSystem.ID_COLUMN_NAME, query.getIds());
        sqlQuery.orderBy(entityRelation.getColumn(SubSystem.ORDER_COLUMN_NAME));
        return sqlQuery;
    }

    @Override
    protected String getCacheName() {
        return "core.security.subsys";
    }


    @Override
    public List<? extends PermissionResource> getResources(String groupId) {
        return selectSubSysList(new SubSysQuery.Builder().build());
    }

    @Override
    public String getType() {
        return "subsys";
    }

    @Override
    public String getName() {
        return "子系统";
    }
}
