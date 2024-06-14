最新消息，博主已开通B站账号：[Java刘哥](https://space.bilibili.com/160340478)
<hr/> 



# 租房系统
基于SpringBoot实现的租房系统，包括三种角色：管理员、房东、租客。
- 详细介绍：[https://liuyanzhao.com/shop/manland.html](https://liuyanzhao.com/shop/manland.html) <br/>
- 预览地址：[http://manland.liuyanzhao.com](http://manland.liuyanzhao.com)  <br/> <br/>


## 博主开发的其他租房或房屋交易项目全部在这里 <br/>
[https://liuyanzhao.com/shop.html?k=房屋](https://liuyanzhao.com/shop.html?k=房屋)
- [基于SpringBoot+Vue房屋租赁系统 租房 Verio的Vue版本](https://liuyanzhao.com/shop/verio-vue.html)
- [基于SpringBoot的房屋租赁平台 房屋展示平台 留学生房屋租赁平台](https://liuyanzhao.com/shop/housekey.html)
- [基于SpringBoot/SSM的最新最轻量级最漂亮的的二手房屋交易系统RentUP](https://liuyanzhao.com/shop/rentup.html)
- [基于SpringBoot/SSM的房屋租赁系统租房系统Rello](https://liuyanzhao.com/shop/rello.html)
- [基于SpringBoot/SSM房屋租赁系统 verio3.0/协同过滤，房屋合租系统 租房系统](https://liuyanzhao.com/shop/verio.html)
- [基于SpringBoot房屋租赁系统manland4.0](https://liuyanzhao.com/shop/manland.html)

  
# 功能介绍
#### 管理员功能：
- 房屋管理
- 租房类型管理(合租/整租)
- 房屋管理
- 订单管理
- 新闻公告管理
- 收支明细
- 用户管理
- 角色管理、权限管理

#### 房东功能
- 房屋管理
- 订单管理：取消订单、审核退租、查看合同、下载打印合同
- 收支明细
- 个人信息修改、账号密码修改

## 租客功能
订单管理：创建订单、确认合同、支付订单、取消订单、退租、电子合同查看、下载打印合同
收支明细
个人信息修改、账号密码修改



## 技术组成
- SpringBoot
- MyBatis
- Shiro
- Thymeleaf
- Bootstrap + jQuery
- MySQL
- Maven


## 预览
1-首页1.png
![1-首页1.png](img/1-首页1.png)
2-首页2.png
![2-首页2.png](img/2-首页2.png)
3-房屋列表1.png
![3-房屋列表1.png](img/3-房屋列表1.png)
4-房屋列表2.png
![4-房屋列表2.png](img/4-房屋列表2.png)
5-房屋详情1.png
![5-房屋详情1.png](img/5-房屋详情1.png)
6-房屋详情2.png
![6-房屋详情2.png](img/6-房屋详情2.png)
7-新闻公告列表.png
![7-新闻公告列表.png](img/7-新闻公告列表.png)
8-新闻公告详情.png
![8-新闻公告详情.png](img/8-新闻公告详情.png)
10-注册页面.png
![10-注册页面.png](img/10-注册页面.png)
10-登录页面.png
![10-登录页面.png](img/10-登录页面.png)
11-点击预定.png
![11-点击预定.png](img/11-点击预定.png)
12-签订合同页面.png
![12-签订合同页面.png](img/12-签订合同页面.png)
13-支付订单页面.png
![13-支付订单页面.png](img/13-支付订单页面.png)
14-租客订单列表.png
![14-租客订单列表.png](img/14-租客订单列表.png)
15-租客的房屋信息.png
![15-租客的房屋信息.png](img/15-租客的房屋信息.png)
16-充值管理.png
![16-充值管理.png](img/16-充值管理.png)
17-个人信息.png
![17-个人信息.png](img/17-个人信息.png)
18-出租者的房屋信息列表.png
![18-出租者的房屋信息列表.png](img/18-出租者的房屋信息列表.png)
19-出租者的订单管理.png
![19-出租者的订单管理.png](img/19-出租者的订单管理.png)
20-管理员房屋管理.png
![20-管理员房屋管理.png](img/20-管理员房屋管理.png)
21-出租分类.png
![21-出租分类.png](img/21-出租分类.png)
22-财务统计.png
![22-财务统计.png](img/22-财务统计.png)
23-公告管理.png
![23-公告管理.png](img/23-公告管理.png)
24-编辑发布新闻公告.png
![24-编辑发布新闻公告.png](img/24-编辑发布新闻公告.png)
25-用户管理.png
![25-用户管理.png](img/25-用户管理.png)
26-房屋信息发布.png
![26-房屋信息发布.png](img/26-房屋信息发布.png)
27-房屋信息编辑.png
![27-房屋信息编辑.png](img/27-房屋信息编辑.png)



其他页面，请直接通过演示网站访问 <br/>
管理员账号admin/123456，房东mayun/123456，租客zhangsan/123456


## 联系方式
目前只开源后端代码，需要前端和sql等完整代码请联系博主 <br/>
同时也提供部署或讲解服务  <br/>
微信/QQ：847064370 <br/>
[博主博客主页](https://liuyanzhao.com) <br/>

## 日志

- 2021/11/13 4.0版本
    - 新增中介角色，功能跟房东类似，需要填写房东信息
    - 注册登录增加验证码，不区分大小写

- 2021/3/17  3.0版本
    - 大改
    - 角色改成 管理员、房东和租客三种，将原来用户角色拆分成房东和租客
    - 租房时间单位由月改成日，租房时可以选择租到具体哪一天
    - 新增退租功能
    - 取消充值，用户余额字段，新增收支明细
    - 管理员可以对订单做任何操作操作

- 2020/12/14 2.0版本
    - 根据部分同学要求，重构代码
    — 修改导航菜单，加入图标
    - 固定顶部导航
    - 添加城市切换切换卡
    - 添加合租室友信息
    
- 2020/12/06
    - 根据部分同学要求，重构代码
    - 把房东和租客两种角色合二为一，用户既可以租房也可以发布出租信息。 
    - 把租房分类改成了整租和合租。
    - 新增余额充值和收支明细，以及付款后租客余额减少，出租人越增加，定时返回押金
    - 修改了合同内容，新增合同下载和打印
    - 新增新闻公告
    - 新增联系我们页面
    - 新增押金字段
    - 新增支持租金和面积检索
<hr/>

- 2020/10/18 1.0 版本
- 完成初步开发
- 管理员功能：登录，房屋管理、房屋类型管理、订单管理、房东管理，租客管理、财务统计，个人信息等。还要角色管理和权限管理，这里隐藏了。
- 房东功能：注册，登录，房屋管理(房屋添加修改删除上架等)、订单管理、查看电子合同。
- 租客功能：注册，登录，房屋检索，房屋租赁，查看订单，查看电子合同。

