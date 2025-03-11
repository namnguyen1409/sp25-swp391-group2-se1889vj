package com.group2.sp25swp391group2se1889vj.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/invoice")
@AllArgsConstructor
public class InvoiceController {


    @GetMapping("/add")
    public String addInvoice(){
        return "invoice/add";
    }

    @GetMapping("/{type}")
    public String getInvoiceTemplate(@PathVariable String type,
                                     @RequestParam String tabId,
                                     Model model) {
        model.addAttribute("tabId", tabId);
        if ("purchase".equals(type)) {
            return "invoice/purchase"; // Trả về file templates/invoice/purchase.html
        } else if ("sales".equals(type)) {
            return "invoice/sales"; // Trả về file templates/invoice/sales.html
        } else {
            return "error/invalid-invoice"; // Trả về trang lỗi nếu type không hợp lệ
        }
    }

}
