package com.example.sens.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.controller.common.BaseController;
import com.example.sens.dto.JsonResult;
import com.example.sens.entity.RechargeRecord;
import com.example.sens.entity.User;
import com.example.sens.service.RechargeRecordService;
import com.example.sens.service.UserService;
import com.example.sens.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author 言曌
 * @date 2020/3/17 11:39 下午
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/rechargeRecord")
public class RechargeRecordController extends BaseController {

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private UserService userService;

    /**
     * 查询所有充值记录并渲染rechargeRecord页面
     *
     * @return 模板路径admin/admin_rechargeRecord
     */
    @GetMapping
    public String rechargeRecords(@RequestParam(value = "startDate", defaultValue = "") String startDate,
                                  @RequestParam(value = "endDate", defaultValue = "") String endDate,
                                  @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                                  @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                                  @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                                  @RequestParam(value = "order", defaultValue = "desc") String order, Model model) {
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<RechargeRecord> rechargeRecords = null;

        if (loginUserIsAdmin()) {
            rechargeRecords = rechargeRecordService.findAll(startDate, endDate, page);
        } else {
            rechargeRecords = rechargeRecordService.findByUserId(startDate, endDate, getLoginUserId(), page);
        }

        model.addAttribute("rechargeRecords", rechargeRecords.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        return "admin/admin_rechargeRecord";
    }


    /**
     * 删除充值记录
     *
     * @param rechargeRecordId 充值记录Id
     * @return JsonResult
     */
    @DeleteMapping(value = "/delete")
    @ResponseBody
    public JsonResult checkDelete(@RequestParam("id") Long rechargeRecordId) {
        rechargeRecordService.delete(rechargeRecordId);
        return JsonResult.success("删除充值记录成功");
    }


    /**
     * 充值界面
     *
     * @return JsonResult
     */
    @GetMapping(value = "/new")
    public String rechargePage(Model model) {
        User user = userService.get(getLoginUserId());
        model.addAttribute("currentMoney", user.getMoney());
        return "admin/admin_rechargeRecord_new";
    }

    /**
     * 充值保存
     *
     * @param money
     * @return
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public JsonResult recharge(@RequestParam("money") Long money) {
        if (money > 10000 || money < 10) {
            return JsonResult.error("充值金额不合法(最少10元，最多1万元)");
        }
        // 充值操作
        // 忽略，假设直接充值成功
        // 修改余额
        User loginUser = getLoginUser();
        User user = userService.get(loginUser.getId());
        user.setMoney(user.getMoney() + money);
        userService.insertOrUpdate(user);

        // 添加充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUserId(loginUser.getId());
        rechargeRecord.setMoney(money);
        rechargeRecord.setCreateTime(new Date());
        rechargeRecordService.insert(rechargeRecord);
        return JsonResult.success("充值成功", user.getMoney());
    }
}
