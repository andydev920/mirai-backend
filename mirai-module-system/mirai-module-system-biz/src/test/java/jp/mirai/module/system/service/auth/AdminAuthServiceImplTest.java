package jp.mirai.module.system.service.auth;

import cn.hutool.core.util.ReflectUtil;
import jp.mirai.framework.common.enums.CommonStatusEnum;
import jp.mirai.framework.common.enums.UserTypeEnum;
import jp.mirai.framework.test.core.ut.BaseDbUnitTest;
import jp.mirai.module.system.api.sms.SmsCodeApi;
import jp.mirai.module.system.api.social.dto.SocialUserBindReqDTO;
import jp.mirai.module.system.api.social.dto.SocialUserRespDTO;
import jp.mirai.module.system.controller.admin.auth.vo.*;
import jp.mirai.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import jp.mirai.module.system.dal.dataobject.user.AdminUserDO;
import jp.mirai.module.system.enums.logger.LoginLogTypeEnum;
import jp.mirai.module.system.enums.logger.LoginResultEnum;
import jp.mirai.module.system.enums.sms.SmsSceneEnum;
import jp.mirai.module.system.enums.social.SocialTypeEnum;
import jp.mirai.module.system.service.logger.LoginLogService;
import jp.mirai.module.system.service.member.MemberService;
import jp.mirai.module.system.service.oauth2.OAuth2TokenService;
import jp.mirai.module.system.service.social.SocialUserService;
import jp.mirai.module.system.service.user.AdminUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import javax.validation.Validation;
import javax.validation.Validator;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static jp.mirai.framework.test.core.util.AssertUtils.assertPojoEquals;
import static jp.mirai.framework.test.core.util.AssertUtils.assertServiceException;
import static jp.mirai.framework.test.core.util.RandomUtils.randomPojo;
import static jp.mirai.framework.test.core.util.RandomUtils.randomString;
import static jp.mirai.module.system.enums.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Import(AdminAuthServiceImpl.class)
public class AdminAuthServiceImplTest extends BaseDbUnitTest {

    @Resource
    private AdminAuthServiceImpl authService;

    @MockBean
    private AdminUserService userService;

    @MockBean
    private LoginLogService loginLogService;
    @MockBean
    private SocialUserService socialUserService;
    @MockBean
    private SmsCodeApi smsCodeApi;
    @MockBean
    private OAuth2TokenService oauth2TokenService;
    @MockBean
    private MemberService memberService;
    @MockBean
    private Validator validator;

    @BeforeEach
    public void setUp() {
        authService.setCaptchaEnable(true);
        // 注入一个 Validator 对象
        ReflectUtil.setFieldValue(authService, "validator",
                Validation.buildDefaultValidatorFactory().getValidator());
    }

    @Test
    public void testAuthenticate_success() {
        // 准备参数
        String username = randomString();
        String password = randomString();
        // mock user 数据
        AdminUserDO user = randomPojo(AdminUserDO.class, o -> o.setUsername(username)
                .setPassword(password).setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(userService.getUserByUsername(eq(username))).thenReturn(user);
        // mock password 匹配
        when(userService.isPasswordMatch(eq(password), eq(user.getPassword()))).thenReturn(true);

        // 调用
        AdminUserDO loginUser = authService.authenticate(username, password);
        // 校验
        assertPojoEquals(user, loginUser);
    }

    @Test
    public void testAuthenticate_userNotFound() {
        // 准备参数
        String username = randomString();
        String password = randomString();

        // 调用, 并断言异常
        assertServiceException(() -> authService.authenticate(username, password),
                AUTH_LOGIN_BAD_CREDENTIALS);
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_USERNAME.getType())
                        && o.getResult().equals(LoginResultEnum.BAD_CREDENTIALS.getResult())
                        && o.getUserId() == null)
        );
    }

    @Test
    public void testAuthenticate_badCredentials() {
        // 准备参数
        String username = randomString();
        String password = randomString();
        // mock user 数据
        AdminUserDO user = randomPojo(AdminUserDO.class, o -> o.setUsername(username)
                .setPassword(password).setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(userService.getUserByUsername(eq(username))).thenReturn(user);

        // 调用, 并断言异常
        assertServiceException(() -> authService.authenticate(username, password),
                AUTH_LOGIN_BAD_CREDENTIALS);
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_USERNAME.getType())
                        && o.getResult().equals(LoginResultEnum.BAD_CREDENTIALS.getResult())
                        && o.getUserId().equals(user.getId()))
        );
    }

    @Test
    public void testAuthenticate_userDisabled() {
        // 准备参数
        String username = randomString();
        String password = randomString();
        // mock user 数据
        AdminUserDO user = randomPojo(AdminUserDO.class, o -> o.setUsername(username)
                .setPassword(password).setStatus(CommonStatusEnum.DISABLE.getStatus()));
        when(userService.getUserByUsername(eq(username))).thenReturn(user);
        // mock password 匹配
        when(userService.isPasswordMatch(eq(password), eq(user.getPassword()))).thenReturn(true);

        // 调用, 并断言异常
        assertServiceException(() -> authService.authenticate(username, password),
                AUTH_LOGIN_USER_DISABLED);
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_USERNAME.getType())
                        && o.getResult().equals(LoginResultEnum.USER_DISABLED.getResult())
                        && o.getUserId().equals(user.getId()))
        );
    }

    @Test
    public void testLogin_success() {
        // 准备参数
        AuthLoginReqVO reqVO = randomPojo(AuthLoginReqVO.class, o ->
                o.setUsername("test_username").setPassword("test_password")
                        .setSocialType(randomEle(SocialTypeEnum.values()).getType()));

        // mock 验证码正确
        authService.setCaptchaEnable(false);
        // mock user 数据
        AdminUserDO user = randomPojo(AdminUserDO.class, o -> o.setId(1L).setUsername("test_username")
                .setPassword("test_password").setStatus(CommonStatusEnum.ENABLE.getStatus()));
        when(userService.getUserByUsername(eq("test_username"))).thenReturn(user);
        // mock password 匹配
        when(userService.isPasswordMatch(eq("test_password"), eq(user.getPassword()))).thenReturn(true);
        // mock 缓存登录用户到 Redis
        OAuth2AccessTokenDO accessTokenDO = randomPojo(OAuth2AccessTokenDO.class, o -> o.setUserId(1L)
                .setUserType(UserTypeEnum.ADMIN.getValue()));
        when(oauth2TokenService.createAccessToken(eq(1L), eq(UserTypeEnum.ADMIN.getValue()), eq("default"), isNull()))
                .thenReturn(accessTokenDO);

        // 调用，并校验
        AuthLoginRespVO loginRespVO = authService.login(reqVO);
        assertPojoEquals(accessTokenDO, loginRespVO);
        // 校验调用参数
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_USERNAME.getType())
                        && o.getResult().equals(LoginResultEnum.SUCCESS.getResult())
                        && o.getUserId().equals(user.getId()))
        );
        verify(socialUserService).bindSocialUser(eq(new SocialUserBindReqDTO(
                user.getId(), UserTypeEnum.ADMIN.getValue(),
                reqVO.getSocialType(), reqVO.getSocialCode(), reqVO.getSocialState())));
    }

    @Test
    public void testSendSmsCode() {
        // 准备参数
        String mobile = randomString();
        Integer scene = randomEle(SmsSceneEnum.values()).getScene();
        AuthSmsSendReqVO reqVO = new AuthSmsSendReqVO(mobile, scene);
        // mock 方法（用户信息）
        AdminUserDO user = randomPojo(AdminUserDO.class);
        when(userService.getUserByMobile(eq(mobile))).thenReturn(user);

        // 调用
        //authService.sendSmsCode(reqVO);
        // 断言
        verify(smsCodeApi).sendSmsCode(argThat(sendReqDTO -> {
            assertEquals(mobile, sendReqDTO.getMobile());
            assertEquals(scene, sendReqDTO.getScene());
            return true;
        }));
    }

    @Test
    public void testSmsLogin_success() {
        // 准备参数
        String mobile = randomString();
        String code = randomString();
        AuthSmsLoginReqVO reqVO = new AuthSmsLoginReqVO(mobile, code);
        // mock 方法（验证码）
        doNothing().when(smsCodeApi).useSmsCode((argThat(smsCodeUseReqDTO -> {
            assertEquals(mobile, smsCodeUseReqDTO.getMobile());
            assertEquals(code, smsCodeUseReqDTO.getCode());
            assertEquals(SmsSceneEnum.ADMIN_MEMBER_LOGIN.getScene(), smsCodeUseReqDTO.getScene());
            return true;
        })));
        // mock 方法（用户信息）
        AdminUserDO user = randomPojo(AdminUserDO.class, o -> o.setId(1L));
        when(userService.getUserByMobile(eq(mobile))).thenReturn(user);
        // mock 缓存登录用户到 Redis
        OAuth2AccessTokenDO accessTokenDO = randomPojo(OAuth2AccessTokenDO.class, o -> o.setUserId(1L)
                .setUserType(UserTypeEnum.ADMIN.getValue()));
        when(oauth2TokenService.createAccessToken(eq(1L), eq(UserTypeEnum.ADMIN.getValue()), eq("default"), isNull()))
                .thenReturn(accessTokenDO);

        // 调用，并断言
        AuthLoginRespVO loginRespVO = authService.smsLogin(reqVO);
        assertPojoEquals(accessTokenDO, loginRespVO);
        // 断言调用
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_MOBILE.getType())
                        && o.getResult().equals(LoginResultEnum.SUCCESS.getResult())
                        && o.getUserId().equals(user.getId()))
        );
    }

    @Test
    public void testSocialLogin_success() {
        // 准备参数
        AuthSocialLoginReqVO reqVO = randomPojo(AuthSocialLoginReqVO.class);
        // mock 方法（绑定的用户编号）
        Long userId = 1L;
        when(socialUserService.getSocialUserByCode(eq(UserTypeEnum.ADMIN.getValue()), eq(reqVO.getType()),
                eq(reqVO.getCode()), eq(reqVO.getState()))).thenReturn(new SocialUserRespDTO(randomString(), randomString(), randomString(), userId));
        // mock（用户）
        AdminUserDO user = randomPojo(AdminUserDO.class, o -> o.setId(userId));
        when(userService.getUser(eq(userId))).thenReturn(user);
        // mock 缓存登录用户到 Redis
        OAuth2AccessTokenDO accessTokenDO = randomPojo(OAuth2AccessTokenDO.class, o -> o.setUserId(1L)
                .setUserType(UserTypeEnum.ADMIN.getValue()));
        when(oauth2TokenService.createAccessToken(eq(1L), eq(UserTypeEnum.ADMIN.getValue()), eq("default"), isNull()))
                .thenReturn(accessTokenDO);

        // 调用，并断言
        AuthLoginRespVO loginRespVO = authService.socialLogin(reqVO);
        assertPojoEquals(accessTokenDO, loginRespVO);
        // 断言调用
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGIN_SOCIAL.getType())
                        && o.getResult().equals(LoginResultEnum.SUCCESS.getResult())
                        && o.getUserId().equals(user.getId()))
        );
    }




    @Test
    public void testRefreshToken() {
        // 准备参数
        String refreshToken = randomString();
        // mock 方法
        OAuth2AccessTokenDO accessTokenDO = randomPojo(OAuth2AccessTokenDO.class);
        when(oauth2TokenService.refreshAccessToken(eq(refreshToken), eq("default")))
                .thenReturn(accessTokenDO);

        // 调用
        AuthLoginRespVO loginRespVO = authService.refreshToken(refreshToken);
        // 断言
        assertPojoEquals(accessTokenDO, loginRespVO);
    }

    @Test
    public void testLogout_success() {
        // 准备参数
        String token = randomString();
        // mock
        OAuth2AccessTokenDO accessTokenDO = randomPojo(OAuth2AccessTokenDO.class, o -> o.setUserId(1L)
                .setUserType(UserTypeEnum.ADMIN.getValue()));
        when(oauth2TokenService.removeAccessToken(eq(token))).thenReturn(accessTokenDO);

        // 调用
        authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        // 校验调用参数
        verify(loginLogService).createLoginLog(
                argThat(o -> o.getLogType().equals(LoginLogTypeEnum.LOGOUT_SELF.getType())
                        && o.getResult().equals(LoginResultEnum.SUCCESS.getResult()))
        );
        // 调用，并校验

    }

    @Test
    public void testLogout_fail() {
        // 准备参数
        String token = randomString();

        // 调用
        authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        // 校验调用参数
        verify(loginLogService, never()).createLoginLog(any());
    }

}
