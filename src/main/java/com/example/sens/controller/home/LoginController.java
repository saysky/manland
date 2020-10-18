package com.example.sens.controller.home;

import com.example.sens.common.constant.CommonConstant;
import com.example.sens.controller.common.BaseController;
import com.example.sens.entity.Role;
import com.example.sens.entity.User;
import com.example.sens.entity.UserRoleRef;
import com.example.sens.dto.JsonResult;
import com.example.sens.enums.RoleEnum;
import com.example.sens.enums.UserStatusEnum;
import com.example.sens.service.*;
import com.example.sens.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@Slf4j
public class LoginController extends BaseController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleRefService userRoleRefService;

    /**
     * 验证登录信息
     *
     * @param userName 登录名：身份证号码／账号
     * @param userPass password 密码
     * @return JsonResult JsonResult
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public JsonResult getLogin(@RequestParam("userName") String userName,
                               @RequestParam("userPass") String userPass) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userName, userPass);
        try {
            subject.login(token);
            if (subject.isAuthenticated()) {
                //登录成功，修改登录错误次数为0
                User user = (User) subject.getPrincipal();
                Set<String> permissionUrls = permissionService.findPermissionUrlsByUserId(user.getId());
                subject.getSession().setAttribute("permissionUrls", permissionUrls);
                return JsonResult.success("登录成功");
            }
        } catch (UnknownAccountException e) {
            return JsonResult.error("账号不存在");
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
            return JsonResult.error("账号或密码错误");
        } catch (LockedAccountException e) {
            return JsonResult.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return JsonResult.error("服务器内部错误");
    }


    /**
     * 退出登录
     *
     * @return 重定向到/login
     */
    @GetMapping(value = "/logout")
    public String logOut() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/";
    }

    /**
     * 退出登录
     *
     * @return 重定向到/login
     */
    @PostMapping(value = "/logout")
    @ResponseBody
    public JsonResult ajaxLogOut() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return JsonResult.success();
    }

    /**
     * 验证注册信息
     *
     * @param userName 账号
     * @param idCard   身份证号码
     * @return JsonResult JsonResult
     */
    @PostMapping(value = "/register")
    @ResponseBody
    @Transactional
    public JsonResult getRegister(@RequestParam("userName") String userName,
                                  @RequestParam("userPass") String userPass,
                                  @RequestParam("idCard") String idCard,
                                  @RequestParam("userDisplayName") String userDisplayName,
                                  @RequestParam("userType") String userType) {
        // 1.校验是否输入完整
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(userPass) || StringUtils.isEmpty(idCard)) {
            return JsonResult.error("请填写完整信息");
        }

        // 2.密码长度是否合法
        if (userPass.length() > 20 || userPass.length() < 6) {
            return JsonResult.error("用户密码长度为6-20位!");
        }

        // 3.密码长度是否合法
        Role role = roleService.findByRoleName(userType);
        if (RoleEnum.ADMIN.getValue().equals(userType) || role == null) {
            return JsonResult.error("用户类型不存在");
        }

        //3.创建用户
        User user = new User();
        user.setUserName(userName);
        user.setUserDisplayName(userDisplayName);
        user.setIdCard(idCard);
        user.setUserPass(Md5Util.toMd5(userPass, CommonConstant.PASSWORD_SALT, 10));
        user.setUserAvatar("/static/images/avatar/" + RandomUtils.nextInt(1, 41) + ".jpeg");
        user.setStatus(UserStatusEnum.NORMAL.getCode());
        userService.insert(user);

        //4.关联角色
        userRoleRefService.insert(new UserRoleRef(user.getId(), role.getId()));
        return JsonResult.success("注册成功");
    }


    /**
     * 检查用户是否登录
     *
     * @return JsonResult
     */
    @GetMapping(value = "/checkLogin")
    @ResponseBody
    public JsonResult checkLogin() {
        User user = getLoginUser();
        if (user == null) {
            return JsonResult.error("请先登录");
        } else {
            return JsonResult.success();
        }
    }


}
