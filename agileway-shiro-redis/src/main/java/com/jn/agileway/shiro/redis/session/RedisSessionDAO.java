package com.jn.agileway.shiro.redis.session;

import com.jn.agileway.redis.redistemplate.RedisTemplate;
import com.jn.agileway.redis.key.RedisKeyWrapper;
import com.jn.langx.IdGenerator;
import com.jn.langx.text.StringTemplates;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;

public class RedisSessionDAO extends AbstractSessionDAO {
    private static final Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);
    private RedisTemplate<String, Session> redisTemplate;

    private RedisKeyWrapper sessionKeyWrapper = new RedisKeyWrapper().prefix("shiro:session");

    @Override
    protected Serializable doCreate(Session session) {
        if (session == null) {
            logger.error("session is null");
            throw new UnknownSessionException("session is null");
        }
        Serializable sessionId = generateSessionId(session);
        String sessionIdRedisKey = getSessionIdRedisKey(sessionId);
        this.assignSessionId(session, sessionId);
        redisTemplate.opsForValue().set(sessionIdRedisKey, session);
        return sessionId;
    }

    private String getSessionIdRedisKey(Serializable sessionId) {
        if (sessionId == null) {
            logger.error("sessionId is null");
            throw new UnknownSessionException("session is null");
        }
        return sessionKeyWrapper.wrap(sessionId.toString());
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        String sessionIdRedisKey = getSessionIdRedisKey(sessionId);
        return redisTemplate.opsForValue().get(sessionIdRedisKey);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null) {
            logger.error("session is null");
            throw new UnknownSessionException("session is null");
        }
        Serializable sessionId = session.getId();
        if (sessionId == null) {
            throw new UnknownSessionException(StringTemplates.formatWithPlaceholder("unknown session: {}", session.toString()));
        }
        String sessionIdRedisKey = getSessionIdRedisKey(sessionId);
        redisTemplate.opsForValue().set(sessionIdRedisKey, session);
    }

    @Override
    public void delete(Session session) {
        if (session == null) {
            logger.error("session is null");
            throw new UnknownSessionException("session is null");
        }
        Serializable sessionId = session.getId();
        String sessionIdRedisKey = getSessionIdRedisKey(sessionId);
        redisTemplate.delete(sessionIdRedisKey);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return null;
    }

    public void setSessionIdGenerator(IdGenerator sessionIdGenerator) {
        setSessionIdGenerator(new SessionIdGeneratorAdapter(sessionIdGenerator));
    }

    public void setSessionIdGenerator(org.springframework.util.IdGenerator uuidGenerator) {
        setSessionIdGenerator(new SessionIdGeneratorAdapter(uuidGenerator));
    }

    public void setRedisTemplate(RedisTemplate<String, Session> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setSessionKeyWrapper(RedisKeyWrapper sessionKeyWrapper) {
        this.sessionKeyWrapper = sessionKeyWrapper;
    }
}
