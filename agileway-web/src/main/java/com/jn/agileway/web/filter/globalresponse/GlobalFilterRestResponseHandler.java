package com.jn.agileway.web.filter.globalresponse;

import com.jn.agileway.web.rest.GlobalRestHandlers;
import com.jn.agileway.web.rest.GlobalRestResponseBodyHandler;
import com.jn.agileway.web.rest.GlobalRestResponseBodyHandlerConfiguration;
import com.jn.agileway.web.servlet.Servlets;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.langx.http.HttpStatus;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class GlobalFilterRestResponseHandler implements GlobalRestResponseBodyHandler<Method> {

    private static final Logger logger = LoggerFactory.getLogger(GlobalFilterRestResponseHandler.class);
    private GlobalRestResponseBodyHandlerConfiguration configuration = new GlobalRestResponseBodyHandlerConfiguration();
    private JSONFactory jsonFactory = JsonFactorys.getJSONFactory(JsonScope.SINGLETON);

    @Override
    public void setConfiguration(GlobalRestResponseBodyHandlerConfiguration configuration) {
        if (configuration != null) {
            this.configuration = configuration;
        }
    }

    @Override
    public void setJsonFactory(JSONFactory jsonFactory) {
        if (jsonFactory != null) {
            this.jsonFactory = jsonFactory;
        }
    }

    @Override
    public JSONFactory getJsonFactory() {
        return jsonFactory;
    }

    @Override
    public RestRespBody handleResponseBody(HttpServletRequest request, HttpServletResponse response, Method method, Object actionReturnValue) {
        int statusCode = response.getStatus();
        long contentLength = Servlets.getContentLength(response);
        // 这个==0的判断其实没啥用
        if (contentLength == 0) {
            boolean error = HttpStatus.is4xxClientError(statusCode) || HttpStatus.is5xxServerError(statusCode);
            if (error) {
                Boolean responseBodyWritten= (Boolean) request.getAttribute(GlobalRestHandlers.GLOBAL_REST_RESPONSE_HAD_WRITTEN);
                if(responseBodyWritten==null || !responseBodyWritten) {
                    RestRespBody respBody = new RestRespBody(!error, statusCode, "", null, null);
                    String json = jsonFactory.get().toJson(respBody);
                    try {
                        Servlets.writeToResponse(response, GlobalRestHandlers.RESPONSE_CONTENT_TYPE_JSON_UTF8, json);
                    } catch (IOException ex) {
                        Throwables.throwAsRuntimeException(ex);
                    }
                    return respBody;
                }
            }
        }
        return null;
    }
}
