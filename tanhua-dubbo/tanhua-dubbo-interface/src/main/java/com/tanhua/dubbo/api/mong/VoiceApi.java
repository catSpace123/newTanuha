package com.tanhua.dubbo.api.mong;

import com.tanhua.domain.mongo2.Voice;

/**
 * 语音上传服务提供者接口
 */
public interface VoiceApi {
    /**
     * 上传语音
     * @param filePath  语音url
     * @param currendUserId  当前用户id
     * @param gender  用户性别
     */
    void SaveVoice(String filePath, Long currendUserId, String gender);

    Voice QueryVoice(Long currentUserId, String gender);
}
