package jp.mirai.module.infra.dal.mysql.demo.demo01;

import jp.mirai.framework.common.pojo.PageResult;
import jp.mirai.framework.mybatis.core.query.LambdaQueryWrapperX;
import jp.mirai.framework.mybatis.core.mapper.BaseMapperX;
import jp.mirai.module.infra.controller.admin.demo.demo01.vo.Demo01ContactPageReqVO;
import jp.mirai.module.infra.dal.dataobject.demo.demo01.Demo01ContactDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 示例联系人 Mapper
 *
 * @author mirai
 */
@Mapper
public interface Demo01ContactMapper extends BaseMapperX<Demo01ContactDO> {

    default PageResult<Demo01ContactDO> selectPage(Demo01ContactPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<Demo01ContactDO>()
                .likeIfPresent(Demo01ContactDO::getName, reqVO.getName())
                .eqIfPresent(Demo01ContactDO::getSex, reqVO.getSex())
                .betweenIfPresent(Demo01ContactDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(Demo01ContactDO::getId));
    }

}