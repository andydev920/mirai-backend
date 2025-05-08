package jp.mirai.module.infra.dal.mysql.config;

import jp.mirai.framework.common.pojo.PageResult;
import jp.mirai.framework.mybatis.core.mapper.BaseMapperX;
import jp.mirai.framework.mybatis.core.query.LambdaQueryWrapperX;
import jp.mirai.module.infra.controller.admin.config.vo.ConfigPageReqVO;
import jp.mirai.module.infra.dal.dataobject.config.ConfigDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigMapper extends BaseMapperX<ConfigDO> {

    default ConfigDO selectByKey(String key) {
        return selectOne(ConfigDO::getConfigKey, key);
    }

    default PageResult<ConfigDO> selectPage(ConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ConfigDO>()
                .likeIfPresent(ConfigDO::getName, reqVO.getName())
                .likeIfPresent(ConfigDO::getConfigKey, reqVO.getKey())
                .eqIfPresent(ConfigDO::getType, reqVO.getType())
                .betweenIfPresent(ConfigDO::getCreateTime, reqVO.getCreateTime()));
    }

}
