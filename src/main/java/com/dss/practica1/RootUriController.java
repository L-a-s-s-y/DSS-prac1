package com.dss.practica1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootUriController {
	@RequestMapping(value = "/login")
	public String index() {
		return "login";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
}