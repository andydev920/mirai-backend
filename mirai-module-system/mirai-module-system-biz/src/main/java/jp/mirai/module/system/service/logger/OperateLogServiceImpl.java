package jp.mirai.module.system.service.logger;

import jp.mirai.framework.common.pojo.PageResult;
import jp.mirai.framework.common.util.object.BeanUtils;
import jp.mirai.module.system.api.logger.dto.OperateLogCreateReqDTO;
import jp.mirai.module.system.api.logger.dto.OperateLogPageReqDTO;
import jp.mirai.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import jp.mirai.module.system.dal.dataobject.logger.OperateLogDO;
import jp.mirai.module.system.dal.mysql.logger.OperateLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 操作日志 Service 实现类
 *
 * @author mirai
 */
@Service
@Validated
@Slf4j
public class OperateLogServiceImpl implements OperateLogService {

    @Resource
    private OperateLogMapper operateLogMapper;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        OperateLogDO log = BeanUtils.toBean(createReqDTO, OperateLogDO.class);
        operateLogMapper.insert(log);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO) {
        return operateLogMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqDTO) {
        return operateLogMapper.selectPage(pageReqDTO);
    }

}
