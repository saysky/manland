package com.example.sens.controller.home;

import com.example.sens.controller.common.BaseController;
import com.example.sens.entity.*;
import com.example.sens.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

//    @GetMapping("/")
//    public String index(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
//                        @RequestParam(value = "size", defaultValue = "9") Integer pageSize,
//                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
//                        @RequestParam(value = "order", defaultValue = "desc") String order,
//                        @RequestParam(value = "startDate", required = false) String start,
//                        @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
//                        @RequestParam(value = "cateId", defaultValue = "0") Long cateId,
//                        Model model) {
//        DateFormat dateFormat = new SimpleDateFormat(DateUtil.FORMAT);
//
//        if (quantity == null || quantity < 1) {
//            quantity = 1;
//        }
//
//        Date today = new Date();
//
//        // 判断入住日期是否合法
//        if (StringUtils.isEmpty(start)) {
//            start = dateFormat.format(today);
//        } else {
//            try {
//                Date startDate = dateFormat.parse(start);
//                if (startDate.before(today)) {
//                    start = dateFormat.format(today);
//                }
//            } catch (ParseException e) {
//                start = dateFormat.format(today);
//                e.printStackTrace();
//            }
//        }
//
//
//        PostQueryCondition condition = new PostQueryCondition();
//        // 查询日期列表
//        List<String> dateList = DateUtil.getBetweenDates(start, quantity);
//            condition.setDateList(dateList);
//        condition.setCateId(cateId);
//        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
//        Page<Post> postPage = postService.findPostByCondition(condition, page);
//        model.addAttribute("posts", postPage);
//
//        // 分类列表
//        List<Category> categoryList = categoryService.findAll();
//        model.addAttribute("categoryList", categoryList);
//        model.addAttribute("quantity", quantity);
//        model.addAttribute("startDate", start);
//        model.addAttribute("cateId", cateId);
//        return "home/index";
//    }


}
