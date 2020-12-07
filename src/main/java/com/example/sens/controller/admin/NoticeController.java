package com.example.sens.controller.admin;

import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.controller.common.BaseController;
import com.example.sens.dto.JsonResult;
import com.example.sens.entity.Notice;
import com.example.sens.exception.MyBusinessException;
import com.example.sens.service.NoticeService;
import com.example.sens.util.PageUtil;
import com.example.sens.vo.SearchVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *     后台公告管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2020/12/6
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/notice")
public class NoticeController extends BaseController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 处理后台获取公告列表的请求
     *
     * @param model model
     * @return 模板路径admin/admin_notice
     */
    @GetMapping
    public String notices(Model model,
                          @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                          @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                          @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                          @RequestParam(value = "order", defaultValue = "desc") String order,
                          @ModelAttribute SearchVo searchVo) {


        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Notice> noticePage = noticeService.findAll(page);
        List<Notice> noticeList = noticePage.getRecords();

        model.addAttribute("notices", noticeList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("order", order);
        model.addAttribute("sort", sort);

        return "admin/admin_notice";
    }


    /**
     * 处理跳转到新建公告页面
     *
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/new")
    public String newNotice(Model model) {
        return "admin/admin_notice_new";
    }


    /**
     * 添加/更新公告
     *
     * @param notice Notice实体
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public JsonResult pushNotice(@ModelAttribute Notice notice) {
        // 1、提取摘要
        int postSummary = 100;
        //房屋摘要
        String summaryText = HtmlUtil.cleanHtmlTag(notice.getContent());
        if (summaryText.length() > postSummary) {
            String summary = summaryText.substring(0, postSummary);
            notice.setSummary(summary);
        } else {
            notice.setSummary(summaryText);
        }
        noticeService.insertOrUpdate(notice);
        return JsonResult.success("发布成功");
    }


    /**
     * 处理删除公告的请求
     *
     * @param noticeId 公告编号
     * @return 重定向到/admin/notice
     */
    @DeleteMapping(value = "/delete")
    @ResponseBody
    public JsonResult removeNotice(@RequestParam("id") Long noticeId) {
        noticeService.delete(noticeId);
        return JsonResult.success("删除成功");
    }


    /**
     * 跳转到编辑公告页面
     *
     * @param noticeId 公告编号
     * @param model    model
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/edit")
    public String editNotice(@RequestParam("id") Long noticeId, Model model) {
        Notice notice = noticeService.get(noticeId);
        if (notice == null) {
            throw new MyBusinessException("公告不存在");
        }

        model.addAttribute("notice", notice);

        return "admin/admin_notice_edit";
    }


}
