package com.heima.freemarker.controller;

import com.heima.freemarker.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;


/**
 * @author CFJG
 */
@Controller
public class FreemarkerController {

    @GetMapping("/test")
    public String test(Model model){
        model.addAttribute("name","cy");
        Student stu = Student.builder().name("常岩").age(19).build();
        model.addAttribute("stu",stu);
        return "01-basic";
    }
}
