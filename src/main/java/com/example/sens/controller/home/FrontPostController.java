package com.example.sens.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.controller.common.BaseController;
import com.example.sens.dto.JsonResult;
import com.example.sens.entity.*;
import com.example.sens.enums.OrderStatusEnum;
import com.example.sens.enums.PostStatusEnum;
import com.example.sens.service.*;
import com.example.sens.util.DateUtil;
import com.example.sens.util.FileUtil;
import com.example.sens.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
                           @RequestParam(value = "area", defaultValue = "") String area,
                           @RequestParam(value = "price", defaultValue = "") String price,
                           @RequestParam(value = "status", defaultValue = "-1") Integer status,
                           HttpSession session,
                           Model model) {
        Post condition = new Post();

        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);


        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);

        model.addAttribute("postCount", postService.count(null));

        try {
            if (StringUtils.isNotEmpty(price)) {
                String[] priceArr = price.split("-");
                if (priceArr.length == 2) {
                    condition.setMinPrice(Integer.valueOf(priceArr[0]));
                    condition.setMaxPrice(Integer.valueOf(priceArr[1]));
                }
            }
            if (StringUtils.isNotEmpty(area)) {
                String[] areaArr = price.split("-");
                if (areaArr.length == 2) {
                    condition.setMinArea(Integer.valueOf(areaArr[0]));
                    condition.setMaxArea(Integer.valueOf(areaArr[1]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 查询日期列表
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
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
        model.addAttribute("area", area);
        model.addAttribute("price", price);

        // 侧边栏
        model.addAttribute("onCount", postService.countByStatus(PostStatusEnum.ON_SALE.getCode()));
        model.addAttribute("offCount", postService.countByStatus(PostStatusEnum.OFF_SALE.getCode()));

        if (cityId != null && cityId != -1) {
            City city = cityService.get(cityId);
            if (city != null) {
                session.setAttribute("city", city);
            }
        } else {
            session.removeAttribute("city");
        }
        return "home/postList";
    }


    /**
     * 房屋详情
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/post/{id}")
    public String postDetails(@PathVariable("id") Long id,
                              @RequestParam(value = "startDate", required = false) String start,
                              @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                              HttpSession session,
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
        model.addAttribute("allowEdit", allowEdit);

        String[] imgUrlList = post.getImgUrl().split(",");
        model.addAttribute("imgUrlList", imgUrlList);

        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);
        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);

        City citySession = (City) session.getAttribute("city");
        Long cityId = citySession == null ? null : citySession.getId();
        List<Post> latestPostList = postService.getLatestPost(cityId, 6);
        model.addAttribute("latestPostList", latestPostList);

        // 可以考虑优化下，暂时没有时间优化
        List<Post> unionRentPost = postService.getUnionRentPost(post);
        List<Order> orderList = new ArrayList<>();
        for (Post temp : unionRentPost) {
            Order order = orderService.findByPostId(temp.getId());
            if (order == null) {
                order = new Order();
            } else {
                order.setUser(userService.get(order.getUserId()));
            }
            order.setPost(temp);
            orderList.add(order);
        }
        model.addAttribute("orderList", orderList);
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
        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);
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

        if (Objects.equals(post.getUserId(), user.getId())) {
            return JsonResult.error("不能租赁自己的房子哦");
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
        order.setPrice(post.getPrice() * quantity + post.getDeposit());
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
        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);
        model.addAttribute("user", userService.get(order.getUserId()));
        return "home/order";
    }

    /**
     * 电子合同
     *
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
//        User user = getLoginUser();
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        if (!Objects.equals(user.getId(), order.getUserId()) && !Objects.equals(user.getId(), order.getOwnerUserId()) && !loginUserIsAdmin()) {
//            return this.renderNotAllowAccess();
//        }

        model.addAttribute("order", order);
        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);
        return "home/agreement";
    }


    /**
     * 下载合同
     *
     * @param orderId
     * @param response
     */
    @GetMapping("/agreement/download")
    public void agreementDownload(@RequestParam("orderId") Long orderId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {


        StringBuffer requestURL = request.getRequestURL();
        String tempContextUrl = requestURL.delete(requestURL.length() - request.getRequestURI().length(), requestURL.length()).append("/").toString();
        ServletOutputStream out = null;
        InputStream inputStream = null;
        try {
            Order order = orderService.get(orderId);

            User user = userService.get(order.getUserId());
            User ownerUser = userService.get(order.getOwnerUserId());
            String pdfName = ownerUser.getUserDisplayName() + "&" + user.getUserDisplayName() + "租房合同.html";
            // 获取外部文件流
            URL url = new URL(tempContextUrl + "agreement?orderId=" + orderId);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            inputStream = conn.getInputStream();
            int len = 0;
            // 输出 下载的响应头，如果下载的文件是中文名，文件名需要经过url编码
            response.setContentType("text/html;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(pdfName, "UTF-8"));
            response.setHeader("Cache-Control", "no-cache");
            out = response.getOutputStream();
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
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

        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);
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
        User user = userService.get(getLoginUserId());
        if (user == null) {
            return JsonResult.error("请先登录");
        }

        Order order = orderService.get(orderId);
        if (order == null) {
            return JsonResult.error("订单不存在");
        }

        if (user.getMoney() < order.getPrice()) {
            return JsonResult.error("余额不足，请充值");
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

        // 这里暂不用乐观锁实现，忽略并发问题
        // 我的余额减少
        user.setMoney(user.getMoney() - order.getPrice());
        userService.update(user);

        // 对方的余额增加
        User ownerUser = userService.get(order.getOwnerUserId());
        ownerUser.setMoney(ownerUser.getMoney() + order.getPrice());
        userService.update(ownerUser);


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
