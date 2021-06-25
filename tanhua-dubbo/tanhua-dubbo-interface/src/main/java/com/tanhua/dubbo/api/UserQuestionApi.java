package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Question;

/**
 * 陌生人问题接口
 */
public interface UserQuestionApi {
    //根据用户id查询问题
    Question findQuestion(Long userId);

    /**
     * 更新陌生人问题
     */
    void updateQuestion(Question question);

    /**
     * 添加陌生人问题
     * @param question
     *
     */
    void saveQuestions(Question question);
}
