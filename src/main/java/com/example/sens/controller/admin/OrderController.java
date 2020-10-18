package com.example.sens.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.controller.common.BaseController;
import com.example.sens.dto.JsonResult;
import com.example.sens.entity.Order;
import com.example.sens.enums.OrderStatusEnum;
import com.example.sens.service.OrderService;
import com.example.sens.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * <pre>
 *     订单管理控制器
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    /**
     * 查询所有订单并渲染order页面
     *
     * @return 模板路径admin/admin_order
     */
    @GetMapping
    public String orders(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
                         @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                         @RequestParam(value = "sort", defaultValue = "id") String sort,
                         @RequestParam(value = "order", defaultValue = "desc") String order, Model model) {
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Order> orderPage = null;
        if (loginUserIsTenant()) {
            // 租客
            Order orderCondition = new Order();
            orderCondition.setUserId(getLoginUserId());
            orderPage = orderService.findAll(orderCondition, page);
        } else if (loginUserIsOwner()) {
            // 业主
            Order orderCondition = new Order();
            orderCondition.setOwnerUserId(getLoginUserId());
            orderPage = orderService.findAll(orderCondition, page);
        } else {
            // 管理员
            Order orderCondition = new Order();
            orderPage = orderService.findAll(orderCondition, page);
        }
        model.addAttribute("orders", orderPage.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        return "admin/admin_order";
    }


    /**
     * 删除订单
     *
     * @param id 订单Id
     * @return JsonResult
     */
    @DeleteMapping(value = "/delete")
    @ResponseBody
    public JsonResult delete(@RequestParam("id") Long id) {
        Order order = orderService.get(id);
        if (order == null) {
            return JsonResult.error("订单不存在");
        }

        orderService.delete(id);
        return JsonResult.success("删除成功");
    }

    /**
     * 完结订单
     *
     * @param id 订单Id
     * @return JsonResult
     */
    @PostMapping(value = "/finish")
    @ResponseBody
    public JsonResult finish(@RequestParam("id") Long id) {
        Order order = orderService.get(id);
        if (order == null) {
            return JsonResult.error("订单不存在");
        }

        order.setStatus(OrderStatusEnum.FINISHED.getCode());
        orderService.update(order);
        return JsonResult.success("完结成功");
    }

    /**
     * 关闭订单
     *
     * @param id 订单Id
     * @return JsonResult
     */
    @PostMapping(value = "/close")
    @ResponseBody
    @Transactional
    public JsonResult close(@RequestParam("id") Long id) {
        // 修改订单状态
        Order order = orderService.get(id);
        if (order == null) {
            return JsonResult.error("订单不存在");
        }

        order.setStatus(OrderStatusEnum.CLOSED.getCode());
        orderService.update(order);

        return JsonResult.success("关闭成功");
    }

    /**
     * 财务页面
     *
     * @param model
     * @return
     */
    @GetMapping("/finance")
    public String finance(@RequestParam(value = "startDate", required = false) String startDate,
                          @RequestParam(value = "endDate", required = false) String endDate,
                          @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
                          @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                          @RequestParam(value = "sort", defaultValue = "id") String sort,
                          @RequestParam(value = "order", defaultValue = "desc") String order,
                          Model model) throws ParseException {

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Order condition = new Order();
        Page<Order> orderPage = null;
        Order orderCondition = new Order();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(StringUtils.isNotEmpty(startDate)) {
            orderCondition.setStartDate(dateFormat.parse(startDate));
        }
        if(StringUtils.isNotEmpty(endDate)) {
            orderCondition.setEndDate(dateFormat.parse(endDate));
        }
        if (loginUserIsTenant()) {
            // 租客
            orderCondition.setUserId(getLoginUserId());
        } else if (loginUserIsOwner()) {
            // 业主
            orderCondition.setOwnerUserId(getLoginUserId());
        }
        orderPage = orderService.findAll(orderCondition, page);

        model.addAttribute("orders", orderPage.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));

        Integer totalPrice = orderService.getTotalPriceSum(condition);
        model.addAttribute("totalPrice", totalPrice == null ? 0 : totalPrice);
        return "admin/admin_finance";
    }


}
