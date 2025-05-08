package jp.mirai.module.system.service.logger;

import jp.mirai.framework.common.pojo.PageResult;
import jp.mirai.module.system.api.logger.dto.OperateLogCreateReqDTO;
import jp.mirai.module.system.api.logger.dto.OperateLogPageReqDTO;
import jp.mirai.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import jp.mirai.module.system.dal.dataobject.logger.OperateLogDO;

/**
 * 操作日志 Service 接口
 *
 * @author mirai
 */
public interface OperateLogService {

    /**
     * 记录操作日志
     *
     * @param createReqDTO 创建请求
     */
    void createOperateLog(OperateLogCreateReqDTO createReqDTO);

    /**
     * 获得操作日志分页列表
     *
     * @param pageReqVO 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO);

    /**
     * 获得操作日志分页列表
     *
     * @param pageReqVO 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqVO);

}
