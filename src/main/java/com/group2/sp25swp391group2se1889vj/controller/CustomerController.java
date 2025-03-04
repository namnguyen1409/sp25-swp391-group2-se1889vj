package com.group2.sp25swp391group2se1889vj.controller;


import com.group2.sp25swp391group2se1889vj.dto.CustomerDTO;
import com.group2.sp25swp391group2se1889vj.dto.CustomerFilterDTO;
import com.group2.sp25swp391group2se1889vj.dto.DebtDTO;
import com.group2.sp25swp391group2se1889vj.dto.DebtFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.service.CustomerService;
import com.group2.sp25swp391group2se1889vj.service.DebtService;
import com.group2.sp25swp391group2se1889vj.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;


@Controller
@RequestMapping("/customer")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final DebtService debtService;
    private final StorageService storageService;

    private User getUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

    private Map<String, String> createPairs(List<String> fields, List<String> fieldTitles) {
        Map<String, String> pairs = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            pairs.put(fields.get(i), fieldTitles.get(i));
        }
        return pairs;
    }

    @GetMapping({"/list", "", "/"})
    public String list(
            Model model,
            @ModelAttribute(value = "customerFilterDTO", binding = false) CustomerFilterDTO customerFilterDTO
    ) {
        if (customerFilterDTO == null) {
            customerFilterDTO = new CustomerFilterDTO();
        }
        Sort sortDirection = "asc".equalsIgnoreCase(customerFilterDTO.getDirection())
                ? Sort.by(customerFilterDTO.getOrderBy()).ascending()
                : Sort.by(customerFilterDTO.getOrderBy()).descending();

        List<String> fields = Arrays.asList("fullName", "phone", "email", "address", "balance", "createdAt");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Họ và tên", "Số điện thoại", "Email", "Địa chỉ", "số dư", "Ngày tạo"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("", "phone", "", "", "price", "dateTime"));
        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);

        Pageable pageable = PageRequest.of(customerFilterDTO.getPage() - 1, customerFilterDTO.getSize(), sortDirection);

        Long ownerId = getUser().getId();
        if (getUser().getRole().equals(RoleType.STAFF)) {
            ownerId = getUser().getOwner().getId();
        }

        Page<CustomerDTO> customers = customerService.searchCustomers(ownerId, customerFilterDTO, pageable);

        model.addAttribute("customers", customers);
        model.addAttribute("customerFilterDTO", customerFilterDTO);
        return "customer/list";
    }

    @PostMapping({"/list", "", "/"})
    public String list(
            Model model,
            @ModelAttribute("customerFilterDTO") CustomerFilterDTO customerFilterDTO,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("customerFilterDTO", customerFilterDTO);
        return "redirect:/customer/list";
    }


    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        return "customer/add";
    }

    @PostMapping("/add")
    public String add(
            @Validated @ModelAttribute("customer") CustomerDTO customerDTO,
            BindingResult bindingResult
    ) {

        if(Boolean.TRUE.equals(customerService.existByPhone(customerDTO.getPhone()))) {
            bindingResult.rejectValue("phone", "error.phone", "Số điện thoại đã tồn tại");
        }
        if(Boolean.TRUE.equals(customerService.existByEmail(customerDTO.getEmail())) && !customerDTO.getEmail().isEmpty()) {
            bindingResult.rejectValue("email", "error.email", "Email đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "customer/add";
        }
        customerDTO.setOwnerId(getUser().getId());
        customerService.saveCustomer(customerDTO);
        return "redirect:/customer/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        CustomerDTO customerDTO = customerService.findCustomerById(id);
        model.addAttribute("customer", customerDTO);
        return "customer/edit";
    }

    @Transactional
    @PostMapping("/edit")
    public String edit(
            @Validated @ModelAttribute("customer") CustomerDTO customerDTO,
            BindingResult bindingResult
    ) {
        if(Boolean.TRUE.equals(customerService.existByPhoneAndIdNot(customerDTO.getPhone(), customerDTO.getId()))) {
            bindingResult.rejectValue("phone", "error.phone", "Số điện thoại đã tồn tại");
        }
        if(Boolean.TRUE.equals(customerService.existByEmailAndIdNot(customerDTO.getEmail(), customerDTO.getId()))) {
            bindingResult.rejectValue("email", "error.email", "Email đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "customer/edit";
        }
        customerService.updateCustomer(customerDTO);
        return "redirect:/customer/list";
    }


    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        CustomerDTO customerDTO = customerService.findCustomerById(id);
        model.addAttribute("customer", customerDTO);
        return "customer/detail";
    }

    @GetMapping({"{id}/debt", "{id}/debt/"})
    public String debt(@PathVariable Long id,
                       Model model
    ) {
        CustomerDTO customer = customerService.findCustomerById(id);
        DebtFilterDTO debtFilterDTO = new DebtFilterDTO();
        Sort sortDirection = "asc".equalsIgnoreCase(debtFilterDTO.getDirection())
                ? Sort.by(debtFilterDTO.getOrderBy()).ascending()
                : Sort.by(debtFilterDTO.getOrderBy()).descending();

        List<String> fields = Arrays.asList("type", "description", "amount", "debtAt", "createdAt");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Loại", "Mô tả", "Số tiền", "Ngày nợ", "Ngày tạo"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("debtType", "html", "price", "dateTime", "dateTime"));
        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);

        Pageable pageable = PageRequest.of(debtFilterDTO.getPage() - 1, debtFilterDTO.getSize(), sortDirection);

        Page<DebtDTO> debts = debtService.searchDebts(id, debtFilterDTO, pageable);

        model.addAttribute("customer", customer);
        model.addAttribute("debts", debts);
        model.addAttribute("debtFilterDTO", debtFilterDTO);
        return "customer/debt/list";
    }


    @PostMapping({"{id}/debt", "{id}/debt/"})
    public String debt(@PathVariable Long id,
                       Model model,
                       @ModelAttribute("debtFilterDTO") DebtFilterDTO debtFilterDTO,
                       BindingResult bindingResult
    ) {
        CustomerDTO customer = customerService.findCustomerById(id);
        Sort sortDirection = "asc".equalsIgnoreCase(debtFilterDTO.getDirection())
                ? Sort.by(debtFilterDTO.getOrderBy()).ascending()
                : Sort.by(debtFilterDTO.getOrderBy()).descending();

        List<String> fields = Arrays.asList("type", "description", "amount", "debtAt", "createdAt");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Loại", "Mô tả", "Số tiền", "Ngày nợ", "Ngày tạo"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("debtType", "html", "price", "dateTime", "dateTime"));
        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);

        Pageable pageable = PageRequest.of(debtFilterDTO.getPage() - 1, debtFilterDTO.getSize(), sortDirection);

        Page<DebtDTO> debts = debtService.searchDebts(id, debtFilterDTO, pageable);

        model.addAttribute("customer", customer);
        model.addAttribute("debts", debts);
        model.addAttribute("debtFilterDTO", debtFilterDTO);
        return "customer/debt/list";
    }

    @GetMapping("/{id}/debt/add")
    public String addDebt(@PathVariable Long id, Model model) {
        CustomerDTO customer = customerService.findCustomerById(id);
        model.addAttribute("customer", customer);
        model.addAttribute("debt", new DebtDTO());
        return "customer/debt/add";
    }

    @PostMapping("/{id}/debt/add")
    public String addDebt(
            @PathVariable Long id,
            Model model,
            @Validated @ModelAttribute("debt") DebtDTO debtDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            CustomerDTO customer = customerService.findCustomerById(id);
            model.addAttribute("customer", customer);
            return "customer/debt/add";
        }
        if(debtDTO.getDebtAt() == null) {
            debtDTO.setDebtAt(LocalDateTime.now());
        }
        if(debtDTO.getImage() != null && !debtDTO.getImage().isEmpty()) {
            String image = storageService.moveToUploads(debtDTO.getImage());
            debtDTO.setImage(image);
        }
        debtDTO.setCustomerId(id);
        debtService.saveDebt(debtDTO);
        return "redirect:/customer/" + id + "/debt";
    }

    @GetMapping("/{id}/debt/{debtId}/detail")
    public String detailDebt(@PathVariable Long id, @PathVariable Long debtId, Model model) {
        CustomerDTO customer = customerService.findCustomerById(id);
        DebtDTO debt = debtService.findDebtById(debtId);
        model.addAttribute("customer", customer);
        model.addAttribute("debt", debt);
        return "customer/debt/detail";
    }


}
