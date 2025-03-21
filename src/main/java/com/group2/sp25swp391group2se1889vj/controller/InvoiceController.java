package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.*;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.enums.InvoiceType;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.exception.Http404;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.service.InvoiceItemService;
import com.group2.sp25swp391group2se1889vj.service.InvoiceService;
import com.group2.sp25swp391group2se1889vj.service.WarehouseService;
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

    private final InvoiceItemService invoiceItemService;
    private final WarehouseService warehouseService;

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

    @GetMapping("/import")
    public String importInvoice(
            @ModelAttribute(value = "invoiceFilterDTO", binding = false) InvoiceFilterDTO invoiceFilterDTO,
            Model model
    ){
        if (invoiceFilterDTO == null) {
            invoiceFilterDTO = new InvoiceFilterDTO();
        }
        Sort sortDirection = "asc".equalsIgnoreCase(invoiceFilterDTO.getDirection())
                ? Sort.by(invoiceFilterDTO.getOrderBy()).ascending()
                : Sort.by(invoiceFilterDTO.getOrderBy()).descending();
        List<String> fields = Arrays.asList("totalPrice", "customerBalance", "totalPayable", "totalPaid", "totalDebt", "customerFullName", "customerPhone", "createdAt", "createdByUsername");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Tổng tiền", "Số dư khách hàng", "Tổng phải trả", "Tổng đã trả", "Tổng nợ khách", "Tên khách hàng", "Số điện thoại", "Ngày tạo", "Người tạo"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("price", "price", "price", "price", "price", "", "", "date", ""));
        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);
        Pageable pageable = PageRequest.of(invoiceFilterDTO.getPage() - 1, invoiceFilterDTO.getSize(), sortDirection);
        Long warehouseId = getWarehouseId();
        invoiceFilterDTO.setType(InvoiceType.PURCHASE);
        Page<InvoiceDetailDTO> invoices = invoiceService.searchInvoices(warehouseId, invoiceFilterDTO, pageable);
        model.addAttribute("invoices", invoices);
        model.addAttribute("invoiceFilterDTO", invoiceFilterDTO);
        return "invoice/import";
    }

    @PostMapping("/import")
    public String importInvoice(
            @ModelAttribute(value = "invoiceFilterDTO") InvoiceFilterDTO invoiceFilterDTO,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("invoiceFilterDTO", invoiceFilterDTO);
        return "redirect:/invoice/import";
    }

    @GetMapping("/export")
    public String exportInvoice(
            @ModelAttribute(value = "invoiceFilterDTO", binding = false) InvoiceFilterDTO invoiceFilterDTO,
            Model model
    ){
        if (invoiceFilterDTO == null) {
            invoiceFilterDTO = new InvoiceFilterDTO();
        }
        Sort sortDirection = "asc".equalsIgnoreCase(invoiceFilterDTO.getDirection())
                ? Sort.by(invoiceFilterDTO.getOrderBy()).ascending()
                : Sort.by(invoiceFilterDTO.getOrderBy()).descending();
        List<String> fields = Arrays.asList("totalPrice", "totalDiscount","customerBalance", "totalPayable", "totalPaid", "totalDebt", "customerFullName", "customerPhone", "createdAt", "createdByUsername");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Tổng tiền đề xuất", "Tổng tiền thực","Số dư khách hàng", "Tổng phải trả", "Tổng đã trả", "Tổng khách nợ", "Tên khách hàng", "Số điện thoại", "Ngày tạo", "Người tạo"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("price", "price","price", "price", "price", "price", "", "", "date", ""));
        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);
        Pageable pageable = PageRequest.of(invoiceFilterDTO.getPage() - 1, invoiceFilterDTO.getSize(), sortDirection);
        Long warehouseId = getWarehouseId();
        invoiceFilterDTO.setType(InvoiceType.SALES);
        Page<InvoiceDetailDTO> invoices = invoiceService.searchInvoices(warehouseId, invoiceFilterDTO, pageable);
        model.addAttribute("invoices", invoices);
        model.addAttribute("invoiceFilterDTO", invoiceFilterDTO);
        return "invoice/export";
    }

    @PostMapping("/export")
    public String exportInvoice(
            @ModelAttribute(value = "invoiceFilterDTO") InvoiceFilterDTO invoiceFilterDTO,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("invoiceFilterDTO", invoiceFilterDTO);
        return "redirect:/invoice/export";
    }


    @GetMapping("/detail/import/{id}")
    public String detail(@PathVariable Long id, Model model) {
        InvoiceDataDTO invoice = invoiceService.findInvoiceDataBywarehouseIdAndId(getWarehouseId(), id);
        if (invoice == null) {
            throw new Http404("Không tìm thấy hóa đơn");
        }
        model.addAttribute("invoice", invoice);
        return "invoice/detail-import";
    }

    @GetMapping("/detail/import/print/{id}")
    public String printDetail(@PathVariable Long id, Model model) {
        InvoiceDataDTO invoice = invoiceService.findInvoiceDataBywarehouseIdAndId(getWarehouseId(), id);
        if (invoice == null) {
            throw new Http404("Không tìm thấy hóa đơn");
        }
        model.addAttribute("invoice", invoice);
        return "invoice/invoice-import-print";
    }

    @GetMapping("/detail/export/{id}")
    public String detailExport(@PathVariable Long id, Model model) {
        InvoiceDataDTO invoice = invoiceService.findInvoiceDataBywarehouseIdAndId(getWarehouseId(), id);
        if (invoice == null) {
            throw new Http404("Không tìm thấy hóa đơn");
        }
        model.addAttribute("invoice", invoice);
        return "invoice/detail-export";
    }

    @GetMapping("/detail/export/print/{id}")
    public String printDetailExport(@PathVariable Long id, Model model) {
        InvoiceDataDTO invoice = invoiceService.findInvoiceDataBywarehouseIdAndId(getWarehouseId(), id);
        if (invoice == null) {
            throw new Http404("Không tìm thấy hóa đơn");
        }
        model.addAttribute("invoice", invoice);
        return "invoice/invoice-export-print";
    }

}
