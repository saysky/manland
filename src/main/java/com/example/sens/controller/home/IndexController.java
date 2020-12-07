package com.example.sens.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.controller.common.BaseController;
import com.example.sens.entity.*;
import com.example.sens.enums.PostStatusEnum;
import com.example.sens.service.*;
import com.example.sens.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2020/3/9 11:00 上午
 */

@Controller
public class IndexController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CityService cityService;

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/")
    public String index(Model model) {
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);

        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);

        List<Post> latestPostList = postService.getLatestPost(6);
        model.addAttribute("latestPostList", latestPostList);
        return "home/index";
    }

    @GetMapping("/contact")
    public String contact() {
        return "home/contact";
    }


    /**
     * 新闻列表
     *
     * @param model
     * @return
     */
    @GetMapping("/notice")
    public String noticeList(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                             @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                             @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                             @RequestParam(value = "order", defaultValue = "desc") String order,
                             Model model) {
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);


        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Notice> postPage = noticeService.findAll(page);
        model.addAttribute("page", postPage);
        model.addAttribute("noticeList", postPage.getRecords());

        return "home/noticeList";
    }


    /**
     * 新闻详情
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/notice/{id}")
    public String noticeDetails(@PathVariable("id") Long id,
                                @RequestParam(value = "startDate", required = false) String start,
                                @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                                Model model) {

        // 新闻
        Notice notice = noticeService.get(id);
        if (notice == null) {
            return renderNotFound();
        }

        model.addAttribute("notice", notice);

        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);

        return "home/notice";
    }


}
