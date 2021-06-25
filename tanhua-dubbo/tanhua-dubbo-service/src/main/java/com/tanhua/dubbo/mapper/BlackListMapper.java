package com.tanhua.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.BlackList;
import com.tanhua.domain.db.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 黑名单列表dao接口
 */
public interface BlackListMapper extends BaseMapper<BlackList> {
    /**
     * 查询黑名单分页
     * @param page1
     * @param userId
     * @return
     */
    @Select("select tui.id,tui.avatar,tui.nickname,tui.gender,tui.age\n" +
            "from tb_user_info tui , tb_black_list tbl where tui.id = tbl.black_user_id and  tbl.user_id = #{userId}")
    Page<UserInfo> findPage(Page page1, @Param("userId") Long userId);
}
