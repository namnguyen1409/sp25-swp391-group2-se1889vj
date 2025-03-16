package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.InvoiceFilterDTO;
import com.group2.sp25swp391group2se1889vj.dto.InvoiceDetailDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.exception.Http404;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/invoice")
@AllArgsConstructor
public class InvoiceController {

    private User getUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

    private Long getWarehouseId() {
        var currentUser = getUser();
        if(currentUser.getRole() == RoleType.OWNER) return currentUser.getWarehouse().getId();
        else return currentUser.getAssignedWarehouse().getId();
    }

    private Map<String, String> createPairs(List<String> fields, List<String> fieldTitles) {
        Map<String, String> pairs = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            pairs.put(fields.get(i), fieldTitles.get(i));
        }
        return pairs;
    }

    private final InvoiceService invoiceService;

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
            return "invoice/sale"; // Trả về file templates/invoice/sales.html
        } else {
            return "error"; // Trả về trang lỗi nếu type không hợp lệ
        }
    }

    @GetMapping({"/list", "", "/"})
    public String list(
            Model model,
            @ModelAttribute(value = "invoiceFilterDTO", binding = false) InvoiceFilterDTO invoiceFilterDTO
    ) {
        if (invoiceFilterDTO == null) {
            invoiceFilterDTO = new InvoiceFilterDTO();
        }
        Sort sortDirection = "asc".equalsIgnoreCase(invoiceFilterDTO.getDirection())
                ? Sort.by(invoiceFilterDTO.getOrderBy()).ascending()
                : Sort.by(invoiceFilterDTO.getOrderBy()).descending();

        List<String> fields = Arrays.asList("type", "totalPrice", "totalDiscount", "customerBalance", "totalPayable", "totalPaid", "totalDebt", "customerFullName", "customerPhone");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Loại", "Tổng niêm yết", "Tổng sau giảm", "Số dư khách hàng", "Số tiền phải trả", "Số tiền đã trả", "Số nợ còn lại", "Tên khách hàng", "Số điện thoại"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("invoiceType", "price", "price", "price", "price", "price", "price","", ""));
        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);

        Pageable pageable = PageRequest.of(invoiceFilterDTO.getPage() - 1, invoiceFilterDTO.getSize(), sortDirection);

        Long warehouseId = getWarehouseId();

        Page<InvoiceDetailDTO> invoices = invoiceService.searchInvoices(warehouseId, invoiceFilterDTO, pageable);

        model.addAttribute("invoices", invoices);
        model.addAttribute("invoiceFilterDTO", invoiceFilterDTO);
        return "invoice/list";
    }

    @PostMapping({"/list", "", "/"})
    public String list(
            @ModelAttribute(value = "invoiceFilterDTO") InvoiceFilterDTO invoiceFilterDTO,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("invoiceFilterDTO", invoiceFilterDTO);
        return "redirect:/invoice/list";
    }


    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        InvoiceDetailDTO invoice = invoiceService.findInvoiceBywarehouseIdAndId(getWarehouseId(), id);
        if (invoice == null) {
            throw new Http404("Không tìm thấy hóa đơn");
        }
        model.addAttribute("invoice", invoice);
        return "invoice/detail";
    }


}
