package jp.mirai.module.infra.dal.mysql.file;

import jp.mirai.framework.common.pojo.PageResult;
import jp.mirai.framework.mybatis.core.mapper.BaseMapperX;
import jp.mirai.framework.mybatis.core.query.LambdaQueryWrapperX;
import jp.mirai.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import jp.mirai.module.infra.dal.dataobject.file.FileDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件操作 Mapper
 *
 * @author mirai
 */
@Mapper
public interface FileMapper extends BaseMapperX<FileDO> {

    default PageResult<FileDO> selectPage(FilePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FileDO>()
                .likeIfPresent(FileDO::getPath, reqVO.getPath())
                .likeIfPresent(FileDO::getType, reqVO.getType())
                .betweenIfPresent(FileDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FileDO::getId));
    }

}
