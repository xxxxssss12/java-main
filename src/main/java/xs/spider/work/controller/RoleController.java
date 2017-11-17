package xs.spider.work.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.spider.base.bean.ResultInfo;
import xs.spider.work.bean.Role;
import xs.spider.work.service.RoleServiceImpl;

/**
 * Created by xs on 2017/11/10
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleServiceImpl roleService;
    @RequestMapping("/getAllRole")
    public ResultInfo getAllRole() {
        return ResultInfo.build(roleService.getList(new Role()));
    }
}
