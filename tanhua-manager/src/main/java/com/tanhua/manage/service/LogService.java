package com.tanhua.manage.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanhua.manage.domain.Log;
import com.tanhua.manage.mapper.LogMapper;
import org.springframework.stereotype.Service;

/**
 * 日志记录业务处理层
 */
@Service
public class LogService extends ServiceImpl<LogMapper,Log> {
}
