package com.tanhua.server.controller;


import com.tanhua.domain.vo.VoiceVo;
import com.tanhua.server.service.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 桃花传音控制层
 */
@RestController
@RequestMapping("/peachblossom")
public class VoiceController {

    @Autowired
    private VoiceService voiceService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity SaveVoice(MultipartFile soundFile){

        voiceService.SaveVoice(soundFile);
        return ResponseEntity.ok(null);
    }


    /**
     * 获取语音
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity QueryVoice(){
        VoiceVo voiceVo = voiceService.QueryVoice();
        return ResponseEntity.ok(voiceVo);
    }

}
