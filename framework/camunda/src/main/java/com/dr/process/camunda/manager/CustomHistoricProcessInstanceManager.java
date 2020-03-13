package com.dr.process.camunda.manager;

import com.dr.process.camunda.query.CustomHistoricProcessInstanceQuery;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.impl.HistoricProcessInstanceQueryImpl;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricProcessInstanceManager;

import java.util.List;

/**
 * @author dr
 */
public class CustomHistoricProcessInstanceManager extends HistoricProcessInstanceManager {
    @Override
    public List<HistoricProcessInstance> findHistoricProcessInstancesByQueryCriteria(HistoricProcessInstanceQueryImpl historicProcessInstanceQuery, Page page) {
        if (isHistoryEnabled() && historicProcessInstanceQuery instanceof CustomHistoricProcessInstanceQuery) {
            configureQuery(historicProcessInstanceQuery);
            return getDbEntityManager().selectList("selectHistoricProcessInstancesByQueryCriteriaCustom", historicProcessInstanceQuery, page);
        }
        return super.findHistoricProcessInstancesByQueryCriteria(historicProcessInstanceQuery, page);
    }

    @Override
    public long findHistoricProcessInstanceCountByQueryCriteria(HistoricProcessInstanceQueryImpl historicProcessInstanceQuery) {
        if (isHistoryEnabled() && historicProcessInstanceQuery instanceof CustomHistoricProcessInstanceQuery) {
            configureQuery(historicProcessInstanceQuery);
            return (Long) getDbEntityManager().selectOne("selectHistoricProcessInstanceCountByQueryCriteriaCustom", historicProcessInstanceQuery);
        }
        return super.findHistoricProcessInstanceCountByQueryCriteria(historicProcessInstanceQuery);
    }
}
