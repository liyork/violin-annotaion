package com.wolf.base.apply;

import com.alibaba.fastjson.JSON;
import com.wolf.annotation.CacheResult;
import com.wolf.annotation.WipeCache;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> Description: 缓存aop切入点
 * <p/>
 * Date: 2015/12/22
 * Time: 8:56
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Aspect  //注掉，用于测试其他
@Component
public class CacheAspect {

    private static ExpressionParser parser = new SpelExpressionParser();
    private static LocalVariableTableParameterNameDiscoverer parameterNameDiscovere =
            new LocalVariableTableParameterNameDiscoverer();

    //key:方法长字符,value:方法参数名称
    private static Map<String, String[]> parameterNamesCache = new ConcurrentHashMap<String, String[]>();

    //key:方法长字符,value:方法短字符，包含名和参数组成的字符串,getCity(String s,String y)
    private static Map<String, String> methodSignature = new ConcurrentHashMap<String, String>();

//    JedisPool pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1");
//    Jedis jedis = null;//pool.getResource();

    private static final Logger LOG = LoggerFactory.getLogger(CacheAspect.class);

    //定义:缓存切入点
    @Pointcut("@annotation(com.wolf.annotation.CacheResult)")
    public void cacheResultAspect() {
    }

    //定义:清除缓存切入点
    @Pointcut("@annotation(com.wolf.annotation.WipeCache)")
    public void wipeCacheAspect() {
    }

    //使用:缓存结果集环绕逻辑
    @Around("cacheResultAspect()")
    public Object cacheResult(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("cacheResult");
        try {
            Method method = getMethod(joinPoint);
            List<CacheResult> annotations = getCacheResultAnnoation(method);
            if (annotations == null || annotations.isEmpty()) {
                return joinPoint.proceed();
            }
            CacheResult annotation = annotations.get(0);
            String domain = annotation.domain();
            long ttl = annotation.TTL();
            String[] keyTemplate = annotation.key();
            Object[] keys = parseExpression(keyTemplate, joinPoint);
            String redisKey = domain + "@";
            for (Object o : keys) {
                redisKey += ("_" + o);
            }
            Object redisResult = getWithSwitch(redisKey);
            if (redisResult != null) {
                LOG.info("[cache hit]:{}", domain);
                return redisResult;
            }
            Object result = joinPoint.proceed();
            if (result != null) {
                setWithSwitch(redisKey, result);
                LOG.info("[cache miss]:{}", domain);
            }
            return result;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return joinPoint.proceed();
        }

    }

    private void setWithSwitch(String redisKey, Object result) {
        //TODO 从配置中心查看是否使用缓存
        //TODO 暂时使用string待完善
        System.out.println("jedis.set(redisKey.getBytes(), result.toString().getBytes())");
    }

    private Object getWithSwitch(String redisKey) {
        //TODO 从配置中心查看是否开启缓存
        System.out.println("jedis.get(redisKey);");
        return null;
    }

    //使用:清除缓存结果集后置逻辑
    @After("wipeCacheAspect()")
    public void wipeCache(JoinPoint joinPoint) {

        Method method = getMethod(joinPoint);
        List<WipeCache> annotations = getWipeCacheAnnoation(method);
        for (WipeCache annotation : annotations) {
            String domain = annotation.domain();
            String[] keyTemplate = annotation.key();
            Object[] keys = parseExpression(keyTemplate, joinPoint);
            String redisKey = domain + "@";
            for (Object o : keys) {
                redisKey += ("_" + o);
            }
            deleteWithSwitch(redisKey);
        }
    }

    private void deleteWithSwitch(String redisKey) {
        //从配置中心查看是否开启缓存
        System.out.println("jedis.del(redisKey);");
    }


    //转换key为指定的值
    private Object[] parseExpression(String[] template, JoinPoint joinPoint) {

        //获取方法所有参数名称
        String methodLongName = joinPoint.getSignature().toLongString();
        String[] parameterNames = parameterNamesCache.get(methodLongName);
        if (parameterNames == null) {
            Method method = getMethod(joinPoint);
            parameterNames = parameterNameDiscovere.getParameterNames(method);
            parameterNamesCache.put(methodLongName, parameterNames);//缓存参数名称
        }

        // add args to expression context
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        if (args.length == parameterNames.length) {
            for (int i = 0, len = args.length; i < len; i++)
                context.setVariable(parameterNames[i], args[i]);
        }

        Object[] objects = new Object[template.length];

        for (int i = 0; i < template.length; i++) {
            Expression expression = parser.parseExpression(template[i]);
            Object value = expression.getValue(context, Object.class);
            objects[i] = value;
        }
        return objects;
    }

    /**
     * 获取当前执行的方法
     * TODO 有待改善，可以直接通过反射获取，这样把所有方法都缓存一遍，可能有其他目的
     *
     * @param joinPoint
     * @return
     */
    private Method getMethod(JoinPoint joinPoint) {

        String methodLongName = joinPoint.getSignature().toLongString();

        //缓存当前调用方法methodLongName
        String methodSignature = CacheAspect.methodSignature.get(methodLongName);
        if (null == methodSignature || "".equals(methodSignature)) {
            methodSignature = getMethodSignature(methodLongName);
            CacheAspect.methodSignature.put(methodLongName, methodSignature);
        }
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        Method method = null;
        for (int i = 0, len = methods.length; i < len; i++) {
            String targetMethodLongName = methods[i].toString();
            //缓存类中所有方法methodLongName
            String targetMethodAndParam = CacheAspect.methodSignature.get(targetMethodLongName);
            if (null == targetMethodAndParam || "".equals(targetMethodAndParam)) {
                targetMethodAndParam = getMethodSignature(targetMethodLongName);
                CacheAspect.methodSignature.put(targetMethodLongName, targetMethodAndParam);
            }

            if (methodSignature.equals(targetMethodAndParam)) {
                method = methods[i];
                break;
            }
        }
        return method;
    }

    private boolean getRealMethodName(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature1;
        if (!(signature instanceof MethodSignature)) {
            LOG.error("only support on method,{}", JSON.toJSONString(signature));
            return true;
        }

        methodSignature1 = (MethodSignature) signature;
        Object target = joinPoint.getTarget();
        String methodName = null;
        try {
            Method currentMethod = target.getClass().getMethod(methodSignature1.getName(), methodSignature1.getParameterTypes());
            methodName = currentMethod.getName();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取方法上的CacheResult注解
     *
     * @param method
     * @return
     */
    private List<CacheResult> getCacheResultAnnoation(Method method) {
        Annotation[] annotations = method.getAnnotations();
        List<CacheResult> cacheResults = new LinkedList<CacheResult>();
        for (Annotation a : annotations) {
            if (a instanceof CacheResult) {
                cacheResults.add((CacheResult) a);
            }
        }
        return cacheResults;
    }

    private List<WipeCache> getWipeCacheAnnoation(Method method) {
        Annotation[] annotations = method.getAnnotations();
        List<WipeCache> wipeCaches = new LinkedList<WipeCache>();
        for (Annotation a : annotations) {
            if (a instanceof WipeCache) {
                wipeCaches.add((WipeCache) a);
            }
        }
        return wipeCaches;
    }

    /**
     * 获取方法和参数字符串，截取---》方法名称+(String s,String s)
     *
     * @param longName
     * @return
     */
    public String getMethodSignature(String longName) {
        int leftBracketIndex = longName.indexOf("(");
        int lastDotBeforeMethod = 0;
        for (int i = leftBracketIndex; i >= 0; i--) {
            if (longName.charAt(i) == '.') {
                lastDotBeforeMethod = i;
                break;
            }
        }
        return longName.substring(++lastDotBeforeMethod);
    }


}
