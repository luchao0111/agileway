package com.jn.agileway.web.rest;

import com.jn.langx.http.rest.RestRespBody;

/**
 * 通常用于消息格式化、国际化等等
 */
public interface RestErrorMessageHandler {
    void handler(RestRespBody restRespBody);
}
