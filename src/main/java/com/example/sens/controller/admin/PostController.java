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
        Page<Post> postPage = null;
        Post postCondition = new Post();
        if (loginUserIsAdmin()) {
            // 管理员
            postPage = postService.findPostByCondition(postCondition, page);
        } else if(loginUserIsOwner()) {
            // 业主
            postCondition.setUserId(getLoginUserId());
            postPage = postService.findPostByCondition(postCondition, page);
        }
        List<Post> postList = postPage.getRecords();
        for (Post post : postList) {
            post.setCategory(categoryService.get(post.getCateId()));
        }
        //封装分类和标签
        model.addAttribute("posts", postList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("status", status);
        model.addAttribute("order", order);
        model.addAttribute("sort", sort);
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
     * 处理移至回收站的请求
     *
     * @param postId 房屋编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/throw")
    @ResponseBody
    public JsonResult moveToTrash(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        if (post == null) {
            throw new MyBusinessException("房屋不存在");
        }
        post.setPostStatus(PostStatusEnum.RECYCLE.getCode());
        postService.update(post);
        return JsonResult.success("操作成功");

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
        postService.delete(postId);
        return JsonResult.success("删除成功");
    }

    /**
     * 批量删除
     *
     * @param ids 房屋ID列表
     * @return 重定向到/admin/post
     */
    @DeleteMapping(value = "/batchDelete")
    @ResponseBody
    public JsonResult batchDelete(@RequestParam("ids") List<Long> ids) {
        //批量操作
        //1、防止恶意操作
        if (ids == null || ids.size() == 0 || ids.size() >= 100) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), "参数不合法!");
        }
        //2、检查用户权限
        //房屋作者才可以删除
        List<Post> postList = postService.findByBatchIds(ids);
        //3、如果当前状态为回收站，则删除；否则，移到回收站
        for (Post post : postList) {
            if (Objects.equals(post.getPostStatus(), PostStatusEnum.RECYCLE.getCode())) {
                postService.delete(post.getId());
            } else {
                post.setPostStatus(PostStatusEnum.RECYCLE.getCode());
                postService.update(post);
            }
        }
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
