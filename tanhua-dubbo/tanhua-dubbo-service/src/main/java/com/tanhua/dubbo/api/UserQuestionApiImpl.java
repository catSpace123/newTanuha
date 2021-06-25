package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.Question;
import com.tanhua.dubbo.mapper.UserQuestionMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 陌生人问题服务提供者业务类
 */
@Service
public class UserQuestionApiImpl implements UserQuestionApi {

    @Autowired
    private UserQuestionMapper questionMapper;

    /**
     * 根据用户id查询问题对象
     */
    @Override
    public Question findQuestion(Long userId) {

        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",userId);
        return  questionMapper.selectOne(queryWrapper);

    }


    /**
     * 更新陌生人问题
     */
    @Override
    public void updateQuestion(Question question) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        //更具用户id修改
            queryWrapper.eq("user_id",question.getUserId());
            questionMapper.update(question,queryWrapper);
    }

    /**
     * 添加陌生人问题
     * @param question
     *
     */
    @Override
    public void saveQuestions(Question question) {
        questionMapper.insert(question);
    }
}
