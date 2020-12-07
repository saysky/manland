package com.example.sens.controller.admin;

import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.controller.common.BaseController;
import com.example.sens.dto.JsonResult;
import com.example.sens.exception.MyBusinessException;
import com.example.sens.entity.*;
import com.example.sens.enums.*;
import com.example.sens.service.*;
import com.example.sens.util.PageUtil;
import com.example.sens.util.RegexUtil;
import com.example.sens.util.SensUtils;
import com.example.sens.vo.SearchVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     后台房屋管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2019/12/10
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/post")
public class PostController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private CityService cityService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    public static final String TITLE = "title";

    public static final String CONTENT = "content";


    /**
     * 处理后台获取房屋列表的请求
     *
     * @param model model
     * @return 模板路径admin/admin_post
     */
    @GetMapping
    public String posts(Model model,
                        @RequestParam(value = "status", defaultValue = "-1") Integer status,
                        @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        @ModelAttribute SearchVo searchVo) {

        Post condition = new Post();
        condition.setPostStatus(status);

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Post postCondition = new Post();
        Page<Post> postPage = postService.findPostByCondition(postCondition, page);
        List<Post> postList = postPage.getRecords();

        //封装分类和标签
        model.addAttribute("posts", postList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("status", status);
        model.addAttribute("order", order);
        model.addAttribute("sort", sort);

        model.addAttribute("type", "all");
        return "admin/admin_post";
    }

    /**
     * 我的出租
     *
     * @param model model
     * @return 模板路径admin/admin_post
     */
    @GetMapping("/lease")
    public String lease(Model model,
                        @RequestParam(value = "status", defaultValue = "-1") Integer status,
                        @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        @ModelAttribute SearchVo searchVo) {

        Post condition = new Post();
        condition.setPostStatus(status);
        condition.setUserId(getLoginUserId());

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Post> postPage = postService.findPostByCondition(condition, page);
        List<Post> postList = postPage.getRecords();

        //封装分类和标签
        model.addAttribute("posts", postList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("status", status);
        model.addAttribute("order", order);
        model.addAttribute("sort", sort);

        model.addAttribute("type", "lease");
        return "admin/admin_post";
    }

    /**
     * 我的租赁
     *
     * @param model model
     * @return 模板路径admin/admin_post
     */
    @GetMapping("/rent")
    public String rent(Model model,
                       @RequestParam(value = "status", defaultValue = "-1") Integer status,
                       @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                       @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                       @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                       @RequestParam(value = "order", defaultValue = "desc") String order,
                       @ModelAttribute SearchVo searchVo) {

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Post> postPage = postService.findByRentUserId(getLoginUserId(), page);
        List<Post> postList = postPage.getRecords();

        //封装分类和标签
        model.addAttribute("posts", postList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("status", status);
        model.addAttribute("order", order);
        model.addAttribute("sort", sort);

        model.addAttribute("type", "rent");
        return "admin/admin_post";
    }


    /**
     * 处理跳转到新建房屋页面
     *
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/new")
    public String newPost(Model model) {
        //所有分类
        List<Category> allCategories = categoryService.findAll();
        model.addAttribute("categories", allCategories);
        //所有城市
        List<City> cities = cityService.findAll();
        model.addAttribute("cities", cities);
        return "admin/admin_post_new";
    }


    /**
     * 添加/更新房屋
     *
     * @param post Post实体
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public JsonResult pushPost(@ModelAttribute Post post) {
        if (post.getId() != null) {
            // 只允许更新部分字段
            Post temp = new Post();
            temp.setId(post.getId());
            temp.setPostContent(post.getPostContent());
            temp.setPostSummary(post.getPostSummary());
            temp.setPostEditor(post.getPostEditor());
            post = temp;
        }
        // 1、提取摘要
        int postSummary = 100;
        //房屋摘要
        String summaryText = HtmlUtil.cleanHtmlTag(post.getPostContent());
        if (summaryText.length() > postSummary) {
            String summary = summaryText.substring(0, postSummary);
            post.setPostSummary(summary);
        } else {
            post.setPostSummary(summaryText);
        }

        // 2.处理imgUrl
        String postEditor = post.getPostEditor();
        if (StringUtils.isNotEmpty(postEditor)) {
            List<String> urlList = RegexUtil.getImgSrc(postEditor);
            String imgUrl = SensUtils.listToStr(urlList);
            post.setImgUrl(imgUrl);
        }

        // 2.添加/更新入库
        if (post.getId() == null) {
            post.setUserId(getLoginUserId());
        }
        postService.insertOrUpdate(post);
        return JsonResult.success("发布成功");
    }


    /**
     * 处理房屋为发布的状态
     *
     * @param postId 房屋编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/revert")
    @ResponseBody
    public JsonResult moveToPublish(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        if (post == null) {
            throw new MyBusinessException("房屋不存在");
        }
        post.setPostStatus(PostStatusEnum.ON_SALE.getCode());
        postService.update(post);
        return JsonResult.success("操作成功");
    }


    /**
     * 处理删除房屋的请求
     *
     * @param postId 房屋编号
     * @return 重定向到/admin/post
     */
    @DeleteMapping(value = "/delete")
    @ResponseBody
    public JsonResult removePost(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        if (post == null) {
            throw new MyBusinessException("房屋不存在");
        }
        if (PostStatusEnum.OFF_SALE.getCode().equals(post.getPostStatus())) {
            throw new MyBusinessException("该房屋已出租，不能删除");
        }
        // 判断是否有有效订单
        // 如果有有效订单，不能删除
        Order order = orderService.findByPostId(postId);
        if (order != null) {
            throw new MyBusinessException("该房屋已出租，不能删除");
        }

        // 判断权限
        if(!Objects.equals(post.getUserId(), getLoginUserId()) && !loginUserIsAdmin()) {
            throw new MyBusinessException("没有权限操作");
        }

        postService.delete(postId);
        return JsonResult.success("删除成功");
    }


    /**
     * 跳转到编辑房屋页面
     *
     * @param postId 房屋编号
     * @param model  model
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/edit")
    public String editPost(@RequestParam("id") Long postId, Model model) {
        Post post = postService.get(postId);
        if (post == null) {
            throw new MyBusinessException("房屋不存在");
        }

        //当前房屋分类
        Category category = categoryService.get(post.getCateId());
        post.setCategory(category);
        model.addAttribute("post", post);


        //所有分类
        List<Category> allCategories = categoryService.findAll();
        model.addAttribute("categories", allCategories);

        //所有城市
        List<City> cities = cityService.findAll();
        model.addAttribute("cities", cities);
        return "admin/admin_post_edit";
    }


}
