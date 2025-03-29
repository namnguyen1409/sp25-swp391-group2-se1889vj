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
import org.springframework.security.access.prepost.PreAuthorize;
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


@PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
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
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("", "phone", "", "", "price text-right", "date"));
        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);

        Pageable pageable = PageRequest.of(customerFilterDTO.getPage() - 1, customerFilterDTO.getSize(), sortDirection);

        Long warehouseId = getWarehouseId();

        Page<CustomerDTO> customers = customerService.searchCustomers(warehouseId, customerFilterDTO, pageable);
        System.out.println(customerFilterDTO);

        model.addAttribute("customers", customers);
        model.addAttribute("customerFilterDTO", customerFilterDTO);

        int n1 = customers.getNumber() * customers.getSize() + 1;
        if (customers.getTotalElements() == 0) {
            n1 = 0;
        }
        int n2 = Math.min((customers.getNumber() + 1) * customers.getSize(), (int) customers.getTotalElements());

        model.addAttribute("n1", n1);
        model.addAttribute("n2", n2);
        model.addAttribute("total", customers.getTotalElements());

        return "customer/list";
    }

    @PostMapping({"/list", "", "/"})
    public String list(
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
        Long warehouseId = getWarehouseId();
        if(Boolean.TRUE.equals(customerService.existByPhoneAndWarehouseId(customerDTO.getPhone(), warehouseId))) {
            bindingResult.rejectValue("phone", "error.phone", "Số điện thoại đã tồn tại");
        }
        if(Boolean.TRUE.equals(customerService.existByEmailAndWarehouseId(customerDTO.getEmail(), warehouseId)) && !customerDTO.getEmail().isEmpty()) {
            bindingResult.rejectValue("email", "error.email", "Email đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "customer/add";
        }
        customerDTO.setWarehouseId(warehouseId);
        customerService.saveCustomer(customerDTO);
        return "redirect:/customer/list";
    }


    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        CustomerDTO customerDTO = customerService.findCustomerById(id);

        if (!Objects.equals(customerDTO.getWarehouseId(), getWarehouseId())) {
            return "redirect:/customer/list";
        }
        model.addAttribute("customer", customerDTO);
        return "customer/edit";
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    @PostMapping("/edit")
    public String edit(
            @Validated @ModelAttribute("customer") CustomerDTO customerDTO,
            BindingResult bindingResult
    ) {
        if(Boolean.TRUE.equals(customerService.existByPhoneAndWarehouseIdAndIdNot(customerDTO.getPhone() ,getWarehouseId(), customerDTO.getId()))) {
            bindingResult.rejectValue("phone", "error.phone", "Số điện thoại đã tồn tại");
        }
        if(Boolean.TRUE.equals(customerService.existByEmailAndWarehouseIdAndIdNot(customerDTO.getEmail(), getWarehouseId(), customerDTO.getId())) && !customerDTO.getEmail().isEmpty()) {
            bindingResult.rejectValue("email", "error.email", "Email đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "customer/edit";
        }

        var check = customerService.findCustomerById(customerDTO.getId());
        if (!Objects.equals(check.getWarehouseId(), getWarehouseId())) {
            return "redirect:/customer/list";
        }

        customerService.updateCustomer(customerDTO);
        return "redirect:/customer/list";
    }


    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        CustomerDTO customerDTO = customerService.findCustomerById(id);
        if (!Objects.equals(customerDTO.getWarehouseId(), getWarehouseId())) {
            return "redirect:/customer/list";
        }
        model.addAttribute("customer", customerDTO);
        return "customer/detail";
    }

    @GetMapping({"{id}/debt", "{id}/debt/"})
    public String debt(@PathVariable Long id,
                       Model model,
                       @ModelAttribute(value = "debtFilterDTO", binding = false) DebtFilterDTO debtFilterDTO
    ) {
        CustomerDTO customer = customerService.findCustomerById(id);

        if (!Objects.equals(customer.getWarehouseId(), getWarehouseId())) {
            return "redirect:/customer/list";
        }
        if (debtFilterDTO == null) {
            debtFilterDTO = new DebtFilterDTO();
        }
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

        int n1 = debts.getNumber() * debts.getSize() + 1;
        if (debts.getTotalElements() == 0) {
            n1 = 0;
        }
        int n2 = Math.min((debts.getNumber() + 1) * debts.getSize(), (int) debts.getTotalElements());

        model.addAttribute("n1", n1);
        model.addAttribute("n2", n2);
        model.addAttribute("total", debts.getTotalElements());

        return "customer/debt/list";
    }


    @PostMapping({"{id}/debt", "{id}/debt/"})
    public String debt(@PathVariable Long id,
                       Model model,
                       @ModelAttribute("debtFilterDTO") DebtFilterDTO debtFilterDTO,
                          RedirectAttributes redirectAttributes
    ) {
        CustomerDTO customer = customerService.findCustomerById(id);
        redirectAttributes.addFlashAttribute("debtFilterDTO", debtFilterDTO);
        return "redirect:/customer/" + id + "/debt";
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
