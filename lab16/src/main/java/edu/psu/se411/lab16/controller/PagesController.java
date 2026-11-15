package edu.psu.se411.lab16.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class PagesController {
 @GetMapping("/") public String home(){ return "index"; }
 @GetMapping("/dashboard") public String dashboard(){ return "dashboard"; }
 @GetMapping("/admindashboard") public String admin(){ return "adminDashboard"; }
}
