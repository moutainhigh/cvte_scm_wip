package com.cvte.scm.wip.domain.common.user.service;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.base.commons.OperatingUserUnit;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.authorizations.InvalidTokenException;
import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.constants.MultiOrgSwitchConstants;
import com.cvte.scm.wip.domain.common.context.GlobalContext;
import com.cvte.scm.wip.domain.common.user.entity.*;
import com.cvte.scm.wip.domain.common.user.repository.AuthRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.cvte.csb.base.constants.CommonConstants.BOOLEAN_FALSE;
import static com.cvte.csb.base.constants.CommonConstants.BOOLEAN_TRUE;

/**
 * @Author: wufeng
 * @Date: 2019/10/15 14:42
 */
@Slf4j
@Service
public class BehaviorService {

    @Autowired
    private UserService sysUserService;

    @Autowired
    private MultiOrgSwitchService sysMultiOrgSwitchService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthRepository authRepository;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Value("${csb.redis.app_prefix}")
    private String redisAppPrefix;

    private static final String USER_SESSION_KEY_PREFIX = ":USER:SESSION:";
    private static final Long USER_SESSION_CACHE_TIMEMILLIS = 1 * 60 * 60 * 1000L;


    public void initUserContext(String account, String remoteAddress) {
        String userSessionKey = getUserSessionKey(account);
        Map<Object, Object> userSessionCacheMap = null;
        try {
            userSessionCacheMap = stringRedisTemplate.opsForHash().entries(userSessionKey);
        } catch (Exception e) {
            log.error("缓存中读取用户会话失败...", e);
        }

        if(MapUtils.isNotEmpty(userSessionCacheMap)) {
            initUserContextFromCache(userSessionCacheMap);
            log.info("从缓存中获取account:{}的用户会话【key={}】，成功设置用户上下文", account, userSessionKey);
            return;
        }
        /**
         * 缓存不命中，重新从数据库加载
         */
        UserBaseEntity user = sysUserService.getUserByAccount(account);
        if (ObjectUtils.isNull(user) || BOOLEAN_TRUE.equals(user.getIsLock()) || BOOLEAN_FALSE.equals(user.getIsEnabled())) {
            throw new InvalidTokenException("找不到用户，无效token");
        }
        /**
         * 设置当前用户身份
         */
        OperatingUser operatingUser = new OperatingUser();
        operatingUser.setId(user.getId());
        operatingUser.setAccount(user.getAccount());
        operatingUser.setName(user.getName());
        operatingUser.setEmail(user.getEmail());
        operatingUser.setHost(remoteAddress);
        CurrentContext.setCurrentOperatingUser(operatingUser);
        userSessionCacheMap.put("user", JSON.toJSONString(operatingUser));
        /**
         * 设置当前用户角色
         */
        List<RoleEntity> roleList = roleService.listSysRoleByUserId(user.getId());
        GlobalContext.setRoleList(roleList);
        userSessionCacheMap.put("roleList", JSON.toJSONString(roleList));
        /**
         * 设置当前用户所属岗位
         */
        List<PostEntity> postList = sysUserService.listUserPosts(user.getId());
        GlobalContext.setPostList(postList);
        userSessionCacheMap.put("postList", JSON.toJSONString(postList));
        /** TODO 设置具体业务系统相关的会话信息 **/

        // 写缓存
        stringRedisTemplate.opsForHash().putAll(userSessionKey, userSessionCacheMap);
        stringRedisTemplate.expire(userSessionKey, USER_SESSION_CACHE_TIMEMILLIS, TimeUnit.MILLISECONDS);
        log.info("已将用户{}的会话信息写入缓存, key={}", account, userSessionKey);
    }


    public void initUserUnitContext(String userOrgRelationId) {
        OrgRelationBaseEntity orgRelationBaseDTO = orgService.selectOrgRelationById(userOrgRelationId);
//        if(!multiOrgSwitchService.isUserExistInOrgRelationId(orgRelationBaseDTO)) {
//            throw new ParamsIncorrectException("当前用户不在" + userOrgRelationId + "组织架构下!");
//        }

        OperatingUserUnit operatingUserUnit = new OperatingUserUnit();

        OrgExtEntity orgExt = orgService.getOrgExtById(orgRelationBaseDTO.getOrgId());
        if(orgExt != null) {
            if(StringUtils.isNotBlank(orgExt.getEbsCode())) {
                operatingUserUnit.setCurrentUserEbsCode(orgExt.getEbsCode());
            }
        }
        if(StringUtils.isNotBlank(orgRelationBaseDTO.getOrgType()) && StringUtils.equals(MultiOrgSwitchConstants.BUSINESS, orgRelationBaseDTO.getOrgType())) {
            //初始化事业部信息
            operatingUserUnit.setCurrentUserBu(orgRelationBaseDTO.getRelationCode());
            operatingUserUnit.setCurrentUserBuId(orgRelationBaseDTO.getId());
            operatingUserUnit.setCurrentUserOrgRelationId(orgRelationBaseDTO.getId());
            operatingUserUnit.setCurrentUserOrgId(orgRelationBaseDTO.getId());
            operatingUserUnit.setCurrentUserOrg(orgRelationBaseDTO.getRelationCode());

            //初始化法人信息
            OrgRelationBaseEntity company = sysMultiOrgSwitchService.getParentOrgRelationByType(userOrgRelationId, MultiOrgSwitchConstants.COMPANY);
            if(company != null) {
                operatingUserUnit.setCurrentUserOu(company.getRelationCode());
                operatingUserUnit.setCurrentUserOuId(company.getId());
            }
        }
        CurrentContext.setCurrentOperatingUserUnit(operatingUserUnit);
    }


    public void doLogout() {
        String account = CurrentContext.getCurrentOperatingUser().getAccount();
        String userSessionKey = getUserSessionKey(account);
        stringRedisTemplate.delete(userSessionKey);
        log.info("用户{}注销，已删除会话缓存【key={}】", account, userSessionKey);
    }

    public RestResponse doLogin(String account, String password) {
        RestResponse restResponse = authRepository.login(account, password);
        return restResponse;
    }

    public RestResponse getCurrentUserInfo(String id) {
        RestResponse restResponse = authRepository.getCurrentUserInfo(id);
        return restResponse;
    }

    private void initUserContextFromCache(Map<Object, Object> userSessionCacheMap) {
        CurrentContext.setCurrentOperatingUser(JSON.parseObject((String) userSessionCacheMap.get("user"), OperatingUser.class));
        GlobalContext.setRoleList(JSON.parseArray((String) userSessionCacheMap.get("roleList"), RoleEntity.class));
        GlobalContext.setPostList(JSON.parseArray((String) userSessionCacheMap.get("postList"), PostEntity.class));
    }


    private String getUserSessionKey(String account) {
        return redisAppPrefix + USER_SESSION_KEY_PREFIX + account;
    }


}