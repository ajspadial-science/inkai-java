package com.spadial.inkay;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;

@Controller
public class RootController {

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("foo", "Hello, Inkay!");
		return "index";
	}

}