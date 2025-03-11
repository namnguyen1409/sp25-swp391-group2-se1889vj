package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
}
