package com.group2.sp25swp391group2se1889vj.controller;


import com.group2.sp25swp391group2se1889vj.dto.CustomerDTO;
import com.group2.sp25swp391group2se1889vj.dto.DebtDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.messaging.DebtProducer;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.service.CustomerService;
import com.group2.sp25swp391group2se1889vj.service.DebtService;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/customer")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final DebtService debtService;
    private final DebtProducer debtProducer;

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
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "searchBy", required = false, defaultValue = "name") String searchBy,
            @RequestParam(value = "orderBy", required = false, defaultValue = "createdAt") String orderBy,
            @RequestParam(value = "direction", required = false, defaultValue = "desc") String direction
    ) {
        List<String> fields = Arrays.asList("fullName", "phone", "email", "address", "balance");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Họ và tên", "Số điện thoại", "Email", "Địa chỉ", "số dư"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("", "phone", "", "", "price"));
        List<String> searchAbleFields = Arrays.asList("fullName", "phone", "email", "address");
        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);
        model.addAttribute("searchAbleFields", searchAbleFields);
        if (!fields.contains(searchBy)) {
            searchBy = "fullName";
        }
        if (!fields.contains(orderBy)) {
            orderBy = "createdAt";
        }
        Sort sortDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.by(orderBy).ascending()
                : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sortDirection);
        Page<CustomerDTO> customers;
        if (search != null && !search.isEmpty()) {
            customers = switch (searchBy) {
                case "fullName" ->
                        customerService.findPaginatedCustomersByCreatedByIdAndFullNameContaining(getUser().getId(), search, pageable);
                case "phone" ->
                        customerService.findPaginatedCustomersByCreatedByIdAndPhoneContaining(getUser().getId(), search, pageable);
                case "email" ->
                        customerService.findPaginatedCustomersByCreatedByIdAndEmailContaining(getUser().getId(), search, pageable);
                case "address" ->
                        customerService.findPaginatedCustomersByCreatedByIdAndAddressContaining(getUser().getId(), search, pageable);
                default -> customerService.findPaginatedCustomersByCreatedById(getUser().getId(), pageable);
            };
        } else {
            customers = customerService.findPaginatedCustomersByCreatedById(getUser().getId(), pageable);
        }

        model.addAttribute("customers", customers);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("direction", direction);
        return "customer/list";
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
        if(Boolean.TRUE.equals(customerService.existByEmail(customerDTO.getEmail()))) {
            bindingResult.rejectValue("email", "error.email", "Email đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "customer/add";
        }
        customerService.saveCustomer(customerDTO);
        return "redirect:/customer/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        CustomerDTO customerDTO = customerService.findCustomerById(id);
        model.addAttribute("customer", customerDTO);
        return "customer/edit";
    }

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

    @GetMapping("{id}/debt")
    public String debt(@PathVariable Long id,
                       Model model,
                       @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                       @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                       @RequestParam(value = "search", required = false) String search,
                       @RequestParam(value = "searchBy", required = false, defaultValue = "name") String searchBy,
                       @RequestParam(value = "orderBy", required = false, defaultValue = "createdAt") String orderBy,
                       @RequestParam(value = "direction", required = false, defaultValue = "desc") String direction
    ) {
        CustomerDTO customer = customerService.findCustomerById(id);

        List<String> fields = Arrays.asList("type", "description", "amount", "debtAt", "createdAt");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Loại", "Mô tả", "Số tiền", "Ngày nợ", "Ngày tạo"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("", "", "price", "", ""));
        List<String> searchAbleFields = Arrays.asList("description");
        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);
        model.addAttribute("searchAbleFields", searchAbleFields);
        if (!fields.contains(searchBy)) {
            searchBy = "description";
        }
        if (!fields.contains(orderBy)) {
            orderBy = "createdAt";
        }
        Sort sortDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.by(orderBy).ascending()
                : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sortDirection);
        Page<DebtDTO> debts;
        if (search != null && !search.isEmpty()) {
            debts = switch (searchBy) {
                case "description" ->
                        debtService.findPaginatedDebtsByCustomerIdAndDescriptionContainingIgnoreCase(id, search, pageable);
                default -> debtService.findPaginatedDebtsByCustomerId(id, pageable);
            };
        } else {
            debts = debtService.findPaginatedDebtsByCustomerId(id, pageable);
        }

        model.addAttribute("customer", customer);
        model.addAttribute("debts", debts);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("direction", direction);
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
        debtDTO.setCustomerId(id);
        debtProducer.addDebtToQueue(debtDTO);
        return "redirect:/customer/" + id + "/debt";
    }


}
