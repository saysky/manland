package com.example.sens.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.controller.common.BaseController;
import com.example.sens.dto.JsonResult;
import com.example.sens.entity.*;
import com.example.sens.enums.OrderStatusEnum;
import com.example.sens.enums.PostStatusEnum;
import com.example.sens.service.*;
import com.example.sens.util.DateUtil;
import com.example.sens.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2020/3/11 4:59 下午
 */
@Controller
public class FrontPostController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PostService postService;

    @Autowired
    private CityService cityService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    /**
     * 房屋列表
     *
     * @param model
     * @return
     */
    @GetMapping("/post")
    public String postList(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                           @RequestParam(value = "size", defaultValue = "6") Integer pageSize,
                           @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                           @RequestParam(value = "order", defaultValue = "desc") String order,
                           @RequestParam(value = "postTitle", defaultValue = "") String postTitle,
                           @RequestParam(value = "cityId", defaultValue = "-1") Long cityId,
                           @RequestParam(value = "cateId", defaultValue = "-1") Long cateId,
                           @RequestParam(value = "status", defaultValue = "-1") Integer status,
                           Model model) {
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);


        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);

        model.addAttribute("postCount", postService.count(null));

        // 查询日期列表
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Post condition = new Post();
        condition.setPostTitle(postTitle);
        condition.setPostStatus(status);
        condition.setCateId(cateId);
        condition.setCityId(cityId);

        Page<Post> postPage = postService.findPostByCondition(condition, page);
        model.addAttribute("page", postPage);
        model.addAttribute("postTitle", postTitle);
        model.addAttribute("cityId", cityId);
        model.addAttribute("cateId", cateId);
        model.addAttribute("status", status);

        // 侧边栏
        model.addAttribute("onCount", postService.countByStatus(PostStatusEnum.ON_SALE.getCode()));
        model.addAttribute("offCount", postService.countByStatus(PostStatusEnum.OFF_SALE.getCode()));

        return "home/postList";
    }


    /**
     * 帖子详情
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/post/{id}")
    public String postDetails(@PathVariable("id") Long id,
                              @RequestParam(value = "startDate", required = false) String start,
                              @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                              Model model) {

        // 房屋
        Post post = postService.get(id);
        if (post == null) {
            return renderNotFound();
        }
        // 分类和城市
        Category category = categoryService.get(post.getCateId());
        City city = cityService.get(post.getCityId());
        User user = userService.get(post.getUserId());

        post.setCategory(category);
        post.setCity(city);
        post.setUser(user);
        model.addAttribute("post", post);

        boolean allowEdit = getLoginUser() != null && (loginUserIsAdmin() || Objects.equals(post.getUserId(), getLoginUserId()));
        model.addAttribute("allowEdit", allowEdit );

        String[] imgUrlList = post.getImgUrl().split(",");
        model.addAttribute("imgUrlList", imgUrlList);

        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);

        List<Post> latestPostList = postService.getLatestPost(6);
        model.addAttribute("latestPostList", latestPostList);
        return "home/post";
    }


    /**
     * 结算页面
     *
     * @param postId
     * @param start
     * @param quantity
     * @param model
     * @return
     */
    @GetMapping("/checkout")
    public String checkout(@RequestParam("postId") Long postId,
                           @RequestParam(value = "startDate", required = false) String start,
                           @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                           Model model) {
        DateFormat dateFormat = new SimpleDateFormat(DateUtil.FORMAT);

        if (quantity == null || quantity < 1) {
            quantity = 1;
        }

        Date today = new Date();

        // 判断入住日期是否合法
        if (StringUtils.isEmpty(start)) {
            start = dateFormat.format(today);
        } else {
            try {
                Date startDate = dateFormat.parse(start);
                if (startDate.before(today)) {
                    start = dateFormat.format(today);
                }
            } catch (ParseException e) {
                start = dateFormat.format(today);
                e.printStackTrace();
            }
        }

        Post post = postService.get(postId);
        if (post == null) {
            return this.renderNotFound();
        }

        User user = getLoginUser();
        if (user == null) {
            return "redirect:/";
        }

        // 分类列表
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);

        model.addAttribute("post", post);
        model.addAttribute("startDate", start);
        model.addAttribute("quantity", quantity);
        model.addAttribute("user", user);
        return "home/checkout";
    }


    /**
     * 创建订单
     *
     * @param postId
     * @param quantity
     * @return
     */
    @PostMapping("/order")
    @Transactional
    @ResponseBody
    public JsonResult addOrder(@RequestParam(value = "postId") Long postId,
                               @RequestParam(value = "quantity") Integer quantity) {
        User user = getLoginUser();
        if (user == null) {
            return JsonResult.error("请先登录");
        }

        if (quantity == null || quantity < 1 || quantity > 12) {
            return JsonResult.error("月数不合法");
        }

        Post post = postService.get(postId);
        if (post == null) {
            return JsonResult.error("房屋不存在");
        }

        if (!PostStatusEnum.ON_SALE.getCode().equals(post.getPostStatus())) {
            return JsonResult.error("房屋已租出，暂时无法预定");
        }

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.MONTH, quantity);

        // 添加订单
        Order order = new Order();
        order.setPostId(postId);
        order.setQuantity(quantity);
        order.setStartDate(today);
        order.setEndDate(cal.getTime());
        order.setUserId(user.getId());
        order.setOwnerUserId(post.getUserId());
        order.setStatus(OrderStatusEnum.NOT_PAY.getCode());
        order.setPrice(post.getPrice() * quantity);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        orderService.insert(order);

        return JsonResult.success("订单创建成功", order.getId());
    }

    @GetMapping("/order/{id}")
    public String order(@PathVariable("id") Long id, Model model) {
        Order order = orderService.get(id);
        if (order == null) {
            return this.renderNotFound();
        }

        User user = getLoginUser();
        if (user == null) {
            return "redirect:/login";
        }

        if (!Objects.equals(user.getId(), order.getUserId()) && !Objects.equals(user.getId(), order.getOwnerUserId()) && !loginUserIsAdmin()) {
            return this.renderNotAllowAccess();
        }
        model.addAttribute("order", order);


        // 分类列表
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);

        model.addAttribute("user", userService.get(order.getUserId()));
        return "home/order";
    }

    /**
     * 电子合同
     * @param orderId
     * @return
     */
    @GetMapping("/agreement")
    public String agreement(@RequestParam("orderId") Long orderId, Model model) {
        Order order = orderService.get(orderId);

        if (order == null) {
            return this.renderNotFound();
        }

        Post post = postService.get(order.getPostId());
        order.setPost(post);

        order.setUser(userService.get(order.getUserId()));
        order.setOwnerUser(userService.get(order.getOwnerUserId()));
        User user = getLoginUser();
        if (user == null) {
            return "redirect:/login";
        }

        if (!Objects.equals(user.getId(), order.getUserId()) && !Objects.equals(user.getId(), order.getOwnerUserId()) && !loginUserIsAdmin()) {
            return this.renderNotAllowAccess();
        }

        model.addAttribute("order", order);

        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);
        return "home/agreement";
    }


        /**
         * 支付页面
         *
         * @param orderId
         * @param model
         * @return
         */
    @GetMapping("/pay")
    public String pay(@RequestParam("orderId") Long orderId, Model model) {
        Order order = orderService.get(orderId);

        if (order == null) {
            return this.renderNotFound();
        }

        Post post = postService.get(order.getPostId());
        order.setPost(post);

        User user = getLoginUser();
        if (user == null) {
            return "redirect:/login";
        }

        if (!Objects.equals(user.getId(), order.getUserId()) && !Objects.equals(user.getId(), order.getOwnerUserId()) && !loginUserIsAdmin()) {
            return this.renderNotAllowAccess();
        }


        if (!Objects.equals(OrderStatusEnum.NOT_PAY.getCode(), order.getStatus())) {
            return this.renderNotAllowAccess();
        }

        model.addAttribute("order", order);


        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);
        return "home/pay";
    }


    /**
     * 支付
     *
     * @return orderId
     */
    @PostMapping("/pay")
    @Transactional
    @ResponseBody
    public JsonResult paySuccess(@RequestParam(value = "orderId") Long orderId) {
        User user = getLoginUser();
        if (user == null) {
            return JsonResult.error("请先登录");
        }

        Order order = orderService.get(orderId);
        if (order == null) {
            return JsonResult.error("订单不存在");
        }

        if (!Objects.equals(user.getId(), order.getUserId())) {
            return JsonResult.error("没有权限");
        }


        Post post = postService.get(order.getPostId());
        if (post == null || !Objects.equals(post.getPostStatus(), PostStatusEnum.ON_SALE.getCode())) {
            return JsonResult.error("房屋已租出，暂时无法预定");
        }

        order.setStatus(OrderStatusEnum.HAS_PAY.getCode());
        orderService.update(order);

        post.setPostStatus(PostStatusEnum.OFF_SALE.getCode());
        postService.update(post);

        return JsonResult.success("支付成功", order.getId());
    }


    @GetMapping("/login")
    public String login() {
        return "home/login";
    }


    @GetMapping("/register")
    public String register() {
        return "home/register";
    }


}
