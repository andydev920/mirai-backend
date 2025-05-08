package jp.mirai.module.infra.dal.mysql.job;

import jp.mirai.framework.common.pojo.PageResult;
import jp.mirai.framework.mybatis.core.mapper.BaseMapperX;
import jp.mirai.framework.mybatis.core.query.LambdaQueryWrapperX;
import jp.mirai.module.infra.controller.admin.job.vo.job.JobPageReqVO;
import jp.mirai.module.infra.dal.dataobject.job.JobDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务 Mapper
 *
 * @author mirai
 */
@Mapper
public interface JobMapper extends BaseMapperX<JobDO> {

    default JobDO selectByHandlerName(String handlerName) {
        return selectOne(JobDO::getHandlerName, handlerName);
    }

    default PageResult<JobDO> selectPage(JobPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<JobDO>()
                .likeIfPresent(JobDO::getName, reqVO.getName())
                .eqIfPresent(JobDO::getStatus, reqVO.getStatus())
                .likeIfPresent(JobDO::getHandlerName, reqVO.getHandlerName())
        );
    }

}
